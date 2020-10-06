package amata1219.xeflection.scala

object Reflect {

  def on(target: Any): AnyReflected = target match {
    case clazzName: String => on(clazzName, null)
    case clazz: Class[_] => on(clazz, null)
    case instance => on(instance.getClass, instance)
  }

  def on(clazzName: String, instance: Any): AnyReflected = on(Class.forName(clazzName), instance)

  def on(clazz: Class[_], instance: Any): AnyReflected = new AnyReflected(clazz, instance)

}
