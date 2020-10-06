package amata1219.xeflection.kotlin

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

class AnyReflected(val clazz: Class<*>, private val instance: Any?) {

    companion object {
        val NONE: AnyReflected = Reflect.on(Void.TYPE, null)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> cast2(): T? = when (instance) {
        null -> null
        else -> instance as T
    }

    fun <T> value(name: String): T? = field(name).cast2<T>()

    fun flatMap(mapper: (Any?) -> AnyReflected): AnyReflected = mapper(instance)

    fun map(mapper: (Any?) -> Any): AnyReflected = flatMap { Reflect.on(mapper(it)) }

    fun set(name: String, newValue: Any): AnyReflected {
        accessibleField(name).set(instance, newValue)
        return this
    }

    fun update(name: String, function: (Any) -> Any): AnyReflected {
        set(name, function(name))
        return this
    }

    fun field(name: String): AnyReflected {
        val field: Field = accessibleField(name)
        return Reflect.on(field.type, field.get(name))
    }

    private fun accessibleField(name: String): Field = searchUpward4Member<Field> { it.getDeclaredField(name) }

    fun call(name: String, vararg args: Any): AnyReflected {
        val types: Array<Class<*>> = args.map(Any::javaClass).toTypedArray()
        return when (val result: Any? = accessibleMethod(name, *types).invoke(instance, *args)) {
            null -> NONE
            else -> Reflect.on(result)
        }
    }

    private fun accessibleMethod(name: String, vararg types: Class<*>): Method = searchUpward4Member<Method> { it.getDeclaredMethod(name, *types) }

    private fun <M : AccessibleObject> searchUpward4Member(getter: (Class<*>) -> M): M {
        var current: Class<*> = clazz
        try {
            return getter(current).accessblized()
        } catch (ex: Exception) {
            while (true) {
                current = current.superclass ?: throw ex
                try {
                    return getter(current).accessblized()
                } catch (_: Exception) {

                }
            }
        }
    }

    fun create(vararg args: Any): AnyReflected {
        val types: Array<Class<*>> = args.map(Any::javaClass).toTypedArray()
        val result = accessibleConstructor(*types).newInstance(*args)
        return Reflect.on(result.javaClass, result)
    }

    private fun accessibleConstructor(vararg types: Class<*>): Constructor<*> {
        val constructor: Constructor<*> = clazz.getDeclaredConstructor(*types)
        return constructor.accessblized()
    }

    private fun <A : AccessibleObject> A.accessblized(): A {
        isAccessible = true
        return this
    }

}