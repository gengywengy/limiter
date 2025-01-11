package dev.gengy.limiter

import dev.gengy.limiter.config.Configs
import dev.gengy.limiter.listener.Listeners
import org.bukkit.plugin.java.JavaPlugin

class Limiter : JavaPlugin() {
    companion object {
        lateinit var plugin: Limiter
    }

    override fun onEnable() {
        plugin = this
        Listeners.load()
        Configs.load()
    }

    override fun onDisable() {
        Listeners.disable()
    }
}
