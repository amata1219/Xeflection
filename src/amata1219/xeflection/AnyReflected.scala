package amata1219.xeflection

import java.lang.reflect.{Constructor, Field, Method}

class AnyReflected(val clazz: Class[_], val instance: Any) {

  object AnyReflected {

    val NONE: AnyReflected = Reflect.on(().getClass, ())

  }

  def get[T](): T = instance.asInstanceOf[T]

  def get[T](name: String): T = field(name).get()

  def set(name: String, value: Any): AnyReflected = {
    accessibleField(name).set(instance, value)
    this
  }

  def field(name: String): AnyReflected = {
    val field: Field = accessibleField(name)
    Reflect.on(field.getClass, field.get(instance))
  }

  private def accessibleField(name: String): Field = {
    val field: Field = clazz.getDeclaredField(name)
    field.setAccessible(true)
    field
  }

  def call(name: String, args: Any*): AnyReflected = {
    val types: Array[Class[_]] = args.map(_.getClass).toArray
    accessibleMethod(name, types:_*).invoke(instance, args) match {
      case null => AnyReflected.NONE
      case result => Reflect.on(result.getClass, result)
    }
  }

  private def accessibleMethod(name: String, types: Class[_]*): Method = {
    val method: Method = clazz.getDeclaredMethod(name, types:_*)
    method.setAccessible(true)
    method
  }

  def create(args: Any*): AnyReflected = {
    val types: Array[Class[_]] = args.map(_.getClass).toArray
    val result = accessibleConstructor(types:_*).newInstance(args)
    Reflect.on(clazz, result)
  }

  private def accessibleConstructor(types: Class[_]*): Constructor[_] = {
    val constructor: Constructor[_] = clazz.getDeclaredConstructor(types:_*)
    constructor.setAccessible(true)
    constructor
  }


}
