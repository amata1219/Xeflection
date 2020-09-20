package amata1219.xeflection

import java.lang.reflect.{AccessibleObject, Constructor, Field, Method}

class AnyReflected(val clazz: Class[_], val instance: Any) {

  def as[T]: T = instance.asInstanceOf[T]

  def get[T](name: String): T = accessibleField(name).get(instance).asInstanceOf[T]

  def flatMap(mapper: Any => AnyReflected): AnyReflected = mapper(as[Any])

  def map(mapper: Any => Any): AnyReflected = flatMap(mapper.andThen(Reflect.on))

  def set(name: String, value: Any): AnyReflected = {
    accessibleField(name).set(instance, value)
    this
  }

  def set(name: String, value: Any => Any): AnyReflected = {
    set(name, value(get[Any](name)))
    this
  }

  def field(name: String): AnyReflected = {
    val field: Field = accessibleField(name)
    Reflect.on(field.getType, field.get(instance))
  }

  private def accessibleField(name: String): Field = searchUpward4Member[NoSuchFieldException, Field](_.getDeclaredField(name))

  def call(name: String, args: Any*): AnyReflected = {
    val types: Array[Class[_]] = args.map(_.getClass).toArray
    accessibleMethod(name, types:_*).invoke(instance, args:_*) match {
      case null => AnyReflected.NONE
      case result => Reflect.on(result.getClass, result)
    }
  }

  private def accessibleMethod(name: String, types: Class[_]*): Method = searchUpward4Member[NoSuchMethodException, Method](_.getDeclaredMethod(name, types:_*))

  private def searchUpward4Member[E <: Exception, M >: Null <: AccessibleObject](getter: Class[_] => M): M = {
    var current: Class[_] = clazz
    var result: M = null
    try {
      result = getter(current)
    } catch {
      case ex: E =>
        do {
          try {
            result = getter(current)
          } catch {
            case _: E =>
          }
          current = current.getSuperclass
        } while (current != null)

        throw ex
    }
    result.setAccessible(true)
    result
  }

  def create(args: Any*): AnyReflected = {
    val types: Array[Class[_]] = args.map(_.getClass).toArray
    val result = accessibleConstructor(types:_*).newInstance(args:_*)
    Reflect.on(clazz, result)
  }

  private def accessibleConstructor(types: Class[_]*): Constructor[_] = {
    val constructor: Constructor[_] = clazz.getDeclaredConstructor(types:_*)
    constructor.setAccessible(true)
    constructor
  }

  private object AnyReflected {

    val NONE: AnyReflected = Reflect.on(classOf[Unit], ())

  }

}
