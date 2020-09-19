package amata1219.xeflection

import org.bukkit.Bukkit

sealed class MinecraftPackage(packageName: String) {

  val path: String = s"$packageName.v${
    Bukkit.getServer.getClass.getPackage.getName.replaceFirst(
      ".*(\\d+_\\d+_R\\d+).*",
      "$1"
    )
  }"

  override def toString: String = path

}
case object NetMinecraftServer extends MinecraftPackage("net.minecraft.server")
case object OrgBukkitCraftbukkit extends MinecraftPackage("org.bukkit.craftbukkit")