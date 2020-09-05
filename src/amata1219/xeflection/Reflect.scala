package amata1219.xeflection

object Reflect {

  def on(clazz: Class[_], instance: Any): AnyReflected = new AnyReflected(clazz, instance)

}
