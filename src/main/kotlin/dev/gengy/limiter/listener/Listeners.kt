package dev.gengy.limiter.listener

import dev.gengy.limiter.Limiter
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

object Listeners {
    fun load() {
        val pm = Bukkit.getServer().pluginManager

        pm.registerEvents(PickupListener(), Limiter.plugin)
    }
    fun disable() {
        HandlerList.unregisterAll(PickupListener())
    }
}