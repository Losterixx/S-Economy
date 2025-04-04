package dev.losterixx.sEconomy.other.updatechecker

import dev.losterixx.sEconomy.Main
import dev.losterixx.sEconomy.utils.ConfigManager
import dev.losterixx.sEconomy.utils.CoroutineUtils
import dev.losterixx.sEconomy.utils.UpdateChecker
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.text.replace

class UpdateListener : Listener {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (!getConfig().getBoolean("updateChecker.playerMessage")) return
        if (!player.hasPermission("sEconomy.admin")) return

        CoroutineUtils.launchAsync {
            val currentVersion = main.description.version
            val latestVersion = UpdateChecker.getLatestGitHubRelease("Losterixx", "S-Economy")

            if (latestVersion != null && latestVersion != currentVersion) {
                event.player.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("other.updateChecker")
                    .replace("%latest-version%", latestVersion)
                    .replace("%current-version%", currentVersion)))
            }
        }
    }

}