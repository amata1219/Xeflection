package amata1219.xeflection

object Reflect {

  def on(instance: Any): AnyReflected = on(instance.getClass, instance)

  def on(clazzName: String, instance: Any): AnyReflected = on(Class.forName(clazzName), instance)

  def on(clazz: Class[_], instance: Any): AnyReflected = new AnyReflected(clazz, instance)

  def onNetMinecraftServer(simpleClazzName: String, instance: Any): AnyReflected = on(s"${NetMinecraftServer.path}.$simpleClazzName", instance)

  def onOrgBukkitCraftbukkit(simpleClazzName: String, instance: Any): AnyReflected = on(s"${OrgBukkitCraftbukkit.path}.$simpleClazzName", instance)

}
