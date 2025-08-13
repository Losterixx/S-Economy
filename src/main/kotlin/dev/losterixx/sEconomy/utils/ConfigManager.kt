package dev.losterixx.sEconomy.utils

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import dev.losterixx.sEconomy.Main
import java.io.IOException
import java.nio.file.Files
import kotlin.collections.forEach
import kotlin.collections.set
import kotlin.io.use
import kotlin.let
import kotlin.text.isNotEmpty

object ConfigManager {

    private val plugin = Main.instance
    private val dataDirectory = plugin.dataFolder.toPath()

    private val configs: MutableMap<String, YamlDocument> = HashMap()

    init {
        try {
            if (!Files.exists(dataDirectory)) Files.createDirectories(dataDirectory)
        } catch (e: IOException) {
            throw RuntimeException("Could not create config directory: $dataDirectory", e)
        }
    }

    fun createConfig(name: String, resourcePath: String, subDir: String = ""): YamlDocument {
        val configPath = if (subDir.isNotEmpty()) {
            dataDirectory.resolve(subDir).resolve("$name.yml")
        } else {
            dataDirectory.resolve("$name.yml")
        }

        val configFile = configPath.toFile()
        try {
            if (!configFile.exists()) {
                plugin.getResource(resourcePath)?.use { inputStream ->
                    Files.createDirectories(configPath.parent)
                    Files.copy(inputStream, configPath)
                } ?: throw IOException("Resource not found: $resourcePath")
            }
            val document = YamlDocument.create(
                configFile,
                GeneralSettings.builder().build(),
                LoaderSettings.builder().build()
            )
            configs[name] = document
            return document
        } catch (e: IOException) {
            throw RuntimeException("Failed to create config: $name", e)
        }
    }

    fun existsConfig(name: String): Boolean = configs.containsKey(name)

    fun getConfig(name: String): YamlDocument =
        configs[name] ?: throw NullPointerException("Config '$name.yml' not found!")

    fun saveConfig(name: String) {
        configs[name]?.let {
            try {
                it.save()
            } catch (e: IOException) {
                throw RuntimeException("Failed to save config: $name", e)
            }
        }
    }

    fun reloadConfig(name: String) {
        configs[name]?.let {
            try {
                it.reload()
            } catch (e: IOException) {
                throw RuntimeException("Failed to reload config: $name", e)
            }
        }
    }

    fun saveAllConfigs() {
        configs.values.forEach {
            try {
                it.save()
            } catch (e: IOException) {
                throw RuntimeException("Failed to save all configs", e)
            }
        }
    }

    fun reloadAllConfigs() {
        configs.values.forEach {
            try {
                it.reload()
            } catch (e: IOException) {
                throw RuntimeException("Failed to reload all configs", e)
            }
        }
    }

    fun getAllConfigs(): Map<String, YamlDocument> = configs
}
