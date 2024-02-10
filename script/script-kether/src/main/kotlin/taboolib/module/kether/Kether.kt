package taboolib.module.kether

import taboolib.common.Inject
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Awake
import taboolib.library.kether.QuestAction
import taboolib.library.kether.QuestActionParser
import taboolib.module.kether.action.ActionLiteral
import taboolib.module.lang.Language

@Inject
@RuntimeDependencies(
    RuntimeDependency(
        "!org.apache.commons:commons-jexl3:3.2.1",
        test = "!org.apache.commons.jexl3_3_2_1.JexlEngine",
        relocate = ["!org.apache.commons.jexl3", "!org.apache.commons.jexl3_3_2_1"],
        transitive = false
    ),
    RuntimeDependency(
        "!com.mojang:datafixerupper:4.0.26",
        test = "!com.mojang.datafixers.kinds.App",
        repository = "http://ptms.ink:8081/repository/releases"
    )
)
object Kether {

    @Awake(LifeCycle.INIT)
    fun init() {
        try {
            Language.textTransfer += KetherTransfer
        } catch (_: NoClassDefFoundError) {
        }
    }

    /**
     * 是否启用宽容解析器
     * 禁用时：
     * tell literal "HelloWorld!" 或 tell *"HelloWorld!"
     * 启用时：
     * tell "HelloWorld!"
     */
    var isAllowToleranceParser = true

    val scriptService by lazy {
        ScriptService
    }

    val scriptRegistry by lazy {
        try {
            ScriptService.registry.registerAction("noop", QuestActionParser.of { QuestAction.noop<Any>() })
            ScriptService.registry.registerAction("literal", ActionLiteral.parser())
        } catch (ex: Throwable) {
            ex.printStackTrace()
            error(ex.toString())
        }
        ScriptService.registry
    }

    val registeredScriptProperty = HashMap<Class<*>, MutableMap<String, ScriptProperty<*>>>()

    val registeredPlayerOperator = LinkedHashMap<String, PlayerOperator>()

    // Edited 2023/10/1 14:38 - Fix "NoSuchMethodException" by "@JvmName"
    @JvmName("addAction\$module_kether")
    fun addAction(name: Array<String>, parser: QuestActionParser) {
        name.forEach { addAction(it, parser) }
    }

    @JvmName("addAction\$module_kether")
    fun addAction(name: String, parser: QuestActionParser, namespace: String? = null) {
        scriptRegistry.registerAction(namespace ?: "kether", name, parser)
    }

    @JvmName("removeAction\$module_kether")
    fun removeAction(name: String, namespace: String? = null) {
        scriptRegistry.unregisterAction(namespace ?: "kether", name)
    }

    @JvmName("addPlayerOperator\$module_kether")
    fun addPlayerOperator(name: String, operator: PlayerOperator) {
        registeredPlayerOperator[name] = operator
    }

    @JvmName("addScriptProperty\$module_kether")
    fun addScriptProperty(clazz: Class<*>, property: ScriptProperty<*>) {
        registeredScriptProperty.computeIfAbsent(clazz) { HashMap() }[property.id] = property
    }

    @JvmName("removeScriptProperty\$module_kether")
    fun removeScriptProperty(clazz: Class<*>, id: String) {
        registeredScriptProperty[clazz]?.remove(id)
    }

}