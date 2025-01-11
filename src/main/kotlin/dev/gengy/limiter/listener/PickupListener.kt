package dev.gengy.limiter.listener

import dev.gengy.limiter.config.Configs
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

class PickupListener : Listener {
    @EventHandler
    fun onPickup(e: EntityPickupItemEvent) {
        if (e.entity !is Player) return
        val player = e.entity as Player
        val item = e.item.itemStack
        val inConfig = Configs.item.limitedItems.find {
            it.item == item.type
        }
        if (inConfig == null) return
        val amount = player.inventory.contents.filter { it != null && it.type == item.type }.sumOf { it.amount }
        if (amount >= inConfig.maxAmount) {
            // Amount is already above
            e.isCancelled = true
            return
        }

        if (amount + item.amount <= inConfig.maxAmount) {
            // Amount is allowed
            return
        }
        e.isCancelled = true
        val excess = (amount + item.amount) - inConfig.maxAmount
        val newAmount = item.clone()
        newAmount.amount = item.amount - excess
        player.inventory.addItem(newAmount)

        val newDrop = item.clone()
        newDrop.amount = excess
        e.item.itemStack = newDrop
        return
    }
}