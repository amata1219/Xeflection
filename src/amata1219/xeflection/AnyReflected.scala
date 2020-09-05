package amata1219.xeflection

import java.lang.reflect.Field

class AnyReflected(val clazz: Class[_], val instance: Any) {

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


}
