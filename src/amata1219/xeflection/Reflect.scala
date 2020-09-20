package amata1219.xeflection

object Reflect {

  def on(target: Any): AnyReflected = target match {
    case clazz: Class[_] => on(clazz, null)
    case clazzName: String => on(Class.forName(clazzName), null)
    case instance => on(instance.getClass, instance)
  }

  def on(clazzName: String, instance: Any): AnyReflected = on(Class.forName(clazzName), instance)

  def on(clazz: Class[_], instance: Any): AnyReflected = new AnyReflected(clazz, instance)

}
