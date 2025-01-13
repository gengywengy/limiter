package dev.gengy.limiter.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

@Serializable
data class LanguageConfig(
    val enableMaxMessage: Boolean = false,
    val maxMessage: String = "<red>You can't have more than <amount>x of <item>",
    @Transient
    val mm: MiniMessage = MiniMessage.builder().tags(TagResolver.builder()
        .resolver(StandardTags.defaults())
        .build()
    ).build()
)