package dev.gengy.limiter.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment

object EnchantmentSerializer : KSerializer<Enchantment> {
    override val descriptor: SerialDescriptor
        = PrimitiveSerialDescriptor("SpigotEnchantment", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Enchantment {
        val key = NamespacedKey.fromString(decoder.decodeString()) ?: throw Exception("Invalid namespaced key in enchant config.")
        return Registry.ENCHANTMENT.get(key) ?: throw Exception("Invalid enchantment in enchant config.")
    }
    override fun serialize(encoder: Encoder, value: Enchantment) {
        encoder.encodeString(value.key.toString())
    }
}