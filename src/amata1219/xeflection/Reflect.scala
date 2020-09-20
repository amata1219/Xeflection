package amata1219.xeflection

object Reflect {

  def on(target: Any): AnyReflected = target match {
    case clazz: Class[_] => new AnyReflected(clazz, null)
    case clazzName: String => new AnyReflected(Class.forName(clazzName), null)
    case instance => new AnyReflected(null, instance)
  }

}
