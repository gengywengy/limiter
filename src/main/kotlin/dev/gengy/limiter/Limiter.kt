package dev.gengy.limiter

import dev.gengy.limiter.config.Configs
import dev.gengy.limiter.listener.Listeners
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.plugin.java.JavaPlugin

class Limiter : JavaPlugin() {
    companion object {
        lateinit var plugin: Limiter
        lateinit var adventure: BukkitAudiences
    }

    override fun onEnable() {
        plugin = this
        adventure = BukkitAudiences.create(this)
        Listeners.load()
        Configs.load()
    }

    override fun onDisable() {
        Listeners.disable()
    }
}
