package amata1219.xeflection.kotlin

import org.bukkit.Bukkit

sealed class MinecraftPackage(packageName: String) {
    val path: String

    init {
        val version: String = Bukkit.getServer().javaClass.`package`.name.replaceFirst(
                Regex(".*(\\d+_\\d+_R\\d+).*"),
                "$1"
        )
        path = "$packageName.v$version"
    }

    override fun toString(): String = path
}
class NetMinecraftServer : MinecraftPackage("net.minecraft.server")
class OrgBukkitCraftbukkit: MinecraftPackage("org.bukkit.craftbukkit")