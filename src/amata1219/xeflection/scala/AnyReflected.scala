package amata1219.xeflection.scala

import java.lang.reflect.{AccessibleObject, Constructor, Field, Method}

class AnyReflected(val clazz: Class[_], private val instance: Any) {

  private object AnyReflected {
    val NONE: AnyReflected = Reflect.on(classOf[Unit], ())
  }

  def value[T]: T = instance.asInstanceOf[T]

  def value[T](name: String): T = accessibleField(name).get(instance).asInstanceOf[T]

  def flatMap(mapper: Any => AnyReflected): AnyReflected = mapper(value[Any])

  def map(mapper: Any => Any): AnyReflected = flatMap(mapper.andThen(Reflect.on))

  def set(name: String, newValue: Any): AnyReflected = {
    accessibleField(name).set(instance, newValue)
    this
  }

  def update(name: String, function: Any => Any): AnyReflected = {
    set(name, function(value[Any](name)))
    this
  }

  def field(name: String): AnyReflected = {
    val field: Field = accessibleField(name)
    Reflect.on(field.getType, field.get(instance))
  }

  private def accessibleField(name: String): Field = searchUpward4Member[Field](_.getDeclaredField(name))

  def call(name: String, args: Any*): AnyReflected = {
    val types: Array[Class[_]] = args.map(_.getClass).toArray
    accessibleMethod(name, types:_*).invoke(instance, args:_*) match {
      case null => AnyReflected.NONE
      case result => Reflect.on(result)
    }
  }

  private def accessibleMethod(name: String, types: Class[_]*): Method = searchUpward4Member[Method](_.getDeclaredMethod(name, types:_*))

  private def searchUpward4Member[M <: AccessibleObject](getter: Class[_] => M): M = {
    var current: Class[_] = clazz
    try {
      getter(current)
    } catch {
      case ex: Exception =>
        while (true) {
          current = current.getSuperclass
          if (current == null) throw ex
          try {
            return getter(current).accessiblized()
          } catch {
            case _: Exception =>
          }
        }
        throw new Exception("Not found specified member")
    }
  }

  def create(args: Any*): AnyReflected = {
    val types: Array[Class[_]] = args.map(_.getClass).toArray
    val result = accessibleConstructor(types:_*).newInstance(args:_*)
    Reflect.on(result.getClass, result)
  }

  private def accessibleConstructor(types: Class[_]*): Constructor[_] = {
    val constructor: Constructor[_] = clazz.getDeclaredConstructor(types:_*)
    constructor.accessiblized()
  }

  implicit class XAccessibleObject[A <: AccessibleObject](val obj: A) {
    def accessiblized(): A = {
      obj.setAccessible(true)
      obj
    }
  }

}
