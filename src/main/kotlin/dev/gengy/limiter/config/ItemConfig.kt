package dev.gengy.limiter.config

import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class ItemConfig(
    val limitedItems: List<ItemData> = listOf(
        ItemData(
            item = Material.COBWEB,
            maxAmount = 16
        )
    )
) {
    @Serializable
    data class ItemData(
        val item: Material,
        val maxAmount: Int
    )
}
