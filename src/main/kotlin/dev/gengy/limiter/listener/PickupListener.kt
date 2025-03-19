package dev.gengy.limiter.listener

import dev.gengy.limiter.Limiter
import dev.gengy.limiter.config.Configs
import dev.gengy.limiter.config.Configs.item
import dev.gengy.limiter.config.ItemConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import kotlin.system.exitProcess

class PickupListener : Listener {
    private fun sendMaxMessage(player: Player, item: ItemConfig.ItemData) {
        if (!Configs.lang.enableMaxMessage) return
        Limiter.adventure.player(player).sendMessage(
            Configs.lang.mm.deserialize(
                Configs.lang.maxMessage,
                Placeholder.unparsed("amount", item.maxAmount.toString()),
                Placeholder.component("item",
                    if (item.item.itemTranslationKey != null) {
                        Component.translatable(item.item.itemTranslationKey!!)
                    } else Component.text(item.item.name)
                )
            )
        )
    }

    @EventHandler
    fun onPickup(e: EntityPickupItemEvent) {
        if (e.entity !is Player) return
        val player = e.entity as Player
        val item = e.item.itemStack
        val inConfig = Configs.item.limitedItems.find {
            it.item == item.type
        }
        if (inConfig == null) return
        val amount = getPlayerInventoryCount(player.inventory, inConfig)
        if (amount >= inConfig.maxAmount) {
            // Amount is already above
            sendMaxMessage(player, inConfig)
            e.isCancelled = true
            return
        }

        if (amount + item.amount <= inConfig.maxAmount) {
            // Amount is allowed
            return
        }
        e.isCancelled = true
        sendMaxMessage(player, inConfig)
        val excess = (amount + item.amount) - inConfig.maxAmount
        val newAmount = item.clone()
        newAmount.amount = item.amount - excess
        player.inventory.addItem(newAmount)

        val newDrop = item.clone()
        newDrop.amount = excess
        e.item.itemStack = newDrop
        return
    }

    @EventHandler
    fun onInventoryMove(e: InventoryClickEvent) {
        val chestInventory = e.view.topInventory
        val playerInventory = e.whoClicked.inventory

        if (e.isShiftClick) {
            if (e.clickedInventory is PlayerInventory) return
            val item = e.currentItem ?: return
            val configItem = Configs.item.limitedItems.find { it.item == item.type } ?: return
            val playerInventoryAmount = getPlayerInventoryCount(e.whoClicked.inventory, configItem)
            if (playerInventoryAmount + item.amount > configItem.maxAmount) {
                sendMaxMessage(e.whoClicked as Player, configItem)
                e.isCancelled = true
                val excess = (playerInventoryAmount + item.amount) - configItem.maxAmount
                val playerInvItem = item.clone()
                playerInvItem.amount = item.amount - excess
                e.whoClicked.inventory.addItem(playerInvItem)
                e.currentItem!!.amount = excess
            }
            moveOutOfPlayerInventory(playerInventory, chestInventory, configItem)
            return
        }
        if (e.cursor == null || e.clickedInventory !is PlayerInventory) return
        val configItem = Configs.item.limitedItems.find { it.item == e.cursor!!.type } ?: return
        val playerInventoryAmount = getPlayerInventoryCount(e.whoClicked.inventory, configItem)

        if (e.cursor!!.amount + playerInventoryAmount > configItem.maxAmount) {
            e.isCancelled = true
            sendMaxMessage(e.whoClicked as Player, configItem)

            val excess = (e.cursor!!.amount + playerInventoryAmount) - configItem.maxAmount
            e.currentItem!!.amount += (e.cursor!!.amount - excess)
            e.cursor!!.amount = excess
        }

            moveOutOfPlayerInventory(playerInventory, chestInventory, configItem)
    }

    @EventHandler
    fun onDragClick(e: InventoryDragEvent) {
        val item = e.oldCursor
        val configItem = Configs.item.limitedItems.find { it.item == item.type } ?: return
        val playerInventoryAmount = getPlayerInventoryCount(e.whoClicked.inventory, configItem)
        if (playerInventoryAmount + item.amount > configItem.maxAmount) {
            e.isCancelled = true
        }

        moveOutOfPlayerInventory(e.whoClicked.inventory, e.view.topInventory, configItem)
    }

    private fun getPlayerInventoryCount(inventory: PlayerInventory, item: ItemConfig.ItemData): Int {
        return inventory.contents.filterNotNull().filter { it.type == item.item }.sumOf { it.amount } +
                inventory.extraContents.filterNotNull().filter { it.type == item.item }.sumOf { it.amount }
    }

    private fun moveOutOfPlayerInventory(playerInventory: PlayerInventory, dest: Inventory, configItem: ItemConfig.ItemData) {
        val excessItems: MutableList<ItemStack> = mutableListOf()
        var amount = 0
        playerInventory.forEachIndexed { index, itemStack ->
            if (itemStack == null) return@forEachIndexed
            if (itemStack.type != configItem.item) return@forEachIndexed
            if (amount >= configItem.maxAmount) {
                excessItems.add(itemStack)
                playerInventory.setItem(index, null)
                return@forEachIndexed
            }
            if (amount + itemStack.amount <= configItem.maxAmount) {
                amount += itemStack.amount
                return@forEachIndexed
            }

            val excess = (amount + itemStack.amount) - configItem.maxAmount
            if (itemStack.amount - excess > 0) {
                val playerInv = itemStack.clone()
                playerInv.amount = itemStack.amount - excess
                playerInventory.setItem(index, playerInv)
            }
            val excessItem = itemStack.clone()
            excessItem.amount = excess
            excessItems.add(excessItem)
        }

        excessItems.forEach {
            dest.addItem(it)
        }
    }
}