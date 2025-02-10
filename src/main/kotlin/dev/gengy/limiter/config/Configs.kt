package dev.gengy.limiter.config

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import dev.gengy.limiter.Limiter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.EmptySerializersModule
import java.io.File
import kotlin.io.path.createDirectory

object Configs {
    private val kaml = Yaml(
        EmptySerializersModule(),
        YamlConfiguration(
            strictMode = false,
            encodeDefaults = true,
            polymorphismStyle = PolymorphismStyle.Property,
        )
    )
    lateinit var item: ItemConfig
    lateinit var enchant: EnchantConfig
    lateinit var lang: LanguageConfig

    fun load() {
        item = loadConfig(ItemConfig.serializer(), "items.yaml", ItemConfig())
        enchant = loadConfig(EnchantConfig.serializer(), "enchants.yaml", EnchantConfig())
        lang = loadConfig(LanguageConfig.serializer(), "lang.yaml", LanguageConfig())
    }

    private fun <T> loadConfig(serializer: KSerializer<T>, name: String, default: T): T {
        if (!Limiter.plugin.dataFolder.exists()) Limiter.plugin.dataFolder.toPath().createDirectory()
        val file = File(Limiter.plugin.dataFolder, name)
        if (!file.exists()) {
            file.createNewFile()
            file.writeText(kaml.encodeToString(serializer, default))
            return default
        }
        return kaml.decodeFromString(serializer, file.readText())
    }
}