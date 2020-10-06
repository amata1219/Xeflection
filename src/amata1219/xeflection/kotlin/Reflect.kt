package amata1219.xeflection.kotlin

object Reflect {

    fun on(target: Any): AnyReflected = when (target) {
        is String -> on(target, null)
        is Class<*> -> on(target, null)
        else -> on(target.javaClass, target)
    }

    fun on(clazzName: String, instance: Any?): AnyReflected = on(Class.forName(clazzName), instance)

    fun on(clazz: Class<*>, instance: Any?) = AnyReflected(clazz, instance)

}