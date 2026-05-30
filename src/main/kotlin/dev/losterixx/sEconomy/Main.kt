package dev.losterixx.sEconomy

import dev.losterixx.sEconomy.economy.EconomyManager
import dev.losterixx.sEconomy.economy.VaultHook
import dev.losterixx.sEconomy.utils.ConfigManager
import dev.losterixx.sEconomy.utils.ConfigUpdater
import dev.losterixx.sEconomy.utils.CoroutineUtils
import dev.losterixx.sEconomy.utils.RegisterManager
import dev.losterixx.sEconomy.utils.UpdateChecker
import dev.losterixx.sEconomy.utils.bStats.Metrics
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Files

class Main : JavaPlugin() {

    companion object {
        lateinit var instance: Main
            private set

        const val DEFAULT_PREFIX = "<#ECC868><b>S-Economy</b> <dark_gray>⚡ <gray>"
        val miniMessage = MiniMessage.miniMessage()
        var luckperms: LuckPerms? = null
            private set
    }

    lateinit var economyManager: EconomyManager
        private set
    private lateinit var vaultHook: VaultHook

    override fun onEnable() {

        logger.info("Plugin is being enabled...")

        //-> Custom
        instance = this

        //-> Configs
        loadLangFiles()
        loadConfigFiles()
        ConfigUpdater.updateConfigs()
        logger.info("Loaded ${ConfigManager.getAllConfigs().size} configs!")

        //-> Economy
        economyManager = EconomyManager()
        vaultHook = VaultHook()

        //-> APIs
        if (server.pluginManager.getPlugin("Vault") != null) {
            logger.info("Vault found! Registering Vault economy hook...")
            server.servicesManager.register<Economy?>(Economy::class.java, vaultHook, this, ServicePriority.High)
            logger.info("Vault economy hook registered!")
        } else {
            logger.warning("Vault not found! This plugin is required for the economy system to work.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }
        if (server.pluginManager.getPlugin("LuckPerms") != null ) {
            luckperms = LuckPermsProvider.get()
            logger.info("Hooked into LuckPerms v${luckperms?.pluginMetadata?.version}!")
        } else {
            logger.warning("LuckPerms not found! The exempt permission for the /balancetop command will not work.")
        }

        //-> Register
        RegisterManager.registerAll()

        //-> Update Checker
        if (ConfigManager.getConfig("config").getBoolean("updateChecker.consoleMessage")) {
            CoroutineUtils.launchAsync {
                if (!isLatestVersion()) {
                    logger.warning("You are not using the latest version of S-Economy! Please update to the latest version.")
                    logger.warning("Latest version: ${UpdateChecker.getLatestGitHubRelease("Losterixx", "S-Economy")}")
                    logger.warning("Your version: ${description.version}")
                } else {
                    logger.info("You are using the latest version of S-Economy!")
                }
            }
        }

        //-> Metrics
        val metrics = Metrics(this, 25353)

        logger.info("Plugin has been enabled!")

    }

    override fun onDisable() {

        CoroutineUtils.cancelAll()

        //-> Economy
        economyManager.saveBalances()

        logger.info("Plugin has been disabled!")

    }

    fun loadConfigFiles() {
        ConfigManager.createConfig("config", "config.yml")
        ConfigManager.createConfig("commands", "commands.yml")
        ConfigManager.createConfig("data", "data.yml")

        loadLangFiles()
        val langFile = ConfigManager.getConfig("config").getString("langFile", null)
        if (langFile == null) {
            logger.warning("No language file specified in config.yml! Defaulting to english.yml.")
            config.set("langFile", "english")
            ConfigManager.saveConfig("config")
        }
        ConfigManager.createConfig(langFile, "lang/$langFile.yml", "lang")
        logger.info("Using language file: $langFile.yml")
    }

    fun loadLangFiles() {
        val langDirectory = dataFolder.toPath().resolve("lang")

        if (!Files.exists(langDirectory)) {
            Files.createDirectories(langDirectory)
        }

        val defaultLangFiles = listOf("english.yml", "german.yml")

        defaultLangFiles.forEach { fileName ->
            val langConfig = fileName.removeSuffix(".yml")
            ConfigManager.createConfig(langConfig, "lang/$fileName", "lang")
        }

        Files.list(langDirectory).filter { it.toString().endsWith(".yml") }.forEach { langFile ->
            val langConfig = langFile.fileName.toString().removeSuffix(".yml")
            if (!ConfigManager.existsConfig(langConfig)) {
                ConfigManager.createConfig(langConfig, "lang/${langFile.fileName}", "lang")
            }
        }
    }

    fun isLatestVersion(): Boolean {
        val currentVersion = description.version
        val latestVersion = UpdateChecker.getLatestGitHubRelease("Losterixx", "S-Economy")
        return latestVersion != null && latestVersion == currentVersion
    }

}
