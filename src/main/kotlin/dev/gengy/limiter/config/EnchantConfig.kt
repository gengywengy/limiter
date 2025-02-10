package dev.gengy.limiter.config

import dev.gengy.limiter.serializer.EnchantmentSerializer
import kotlinx.serialization.Serializable
import org.bukkit.enchantments.Enchantment

@Serializable
data class EnchantConfig(
    val levelLimits: Map<@Serializable(with = EnchantmentSerializer::class) Enchantment, Int> = mapOf(
        Enchantment.SHARPNESS to 2
    )
)