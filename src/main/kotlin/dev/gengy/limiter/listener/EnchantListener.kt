package dev.gengy.limiter.listener

import dev.gengy.limiter.config.Configs
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.PrepareAnvilEvent


class EnchantListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPrepareAnvil(event: PrepareAnvilEvent) {
        if (event.result == null) return
        val result = event.result
        var modified = false

        val enchants: Map<Enchantment, Int> = HashMap(result!!.enchantments)
        for ((enchant, value) in enchants.entries) {
            val maxLevel = Configs.enchant.levelLimits[enchant]
            if (maxLevel != null) {
                if (value > maxLevel) {
                    result.removeEnchantment(enchant)
                    result.addUnsafeEnchantment(enchant, maxLevel)
                    modified = true
                }
            }
        }

        if (modified) {
            event.result = result
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEnchant(event: EnchantItemEvent) {
        for ((enchant, value) in event.enchantsToAdd) {
            val maxLevel = Configs.enchant.levelLimits[enchant]
            if (maxLevel != null) {
                if (value > maxLevel) {
                    event.enchantsToAdd[enchant] = maxLevel
                }
            }
        }
    }
}