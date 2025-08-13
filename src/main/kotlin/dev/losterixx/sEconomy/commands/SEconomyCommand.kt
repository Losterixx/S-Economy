package dev.losterixx.sEconomy.commands

import dev.losterixx.sEconomy.Main
import dev.losterixx.sEconomy.utils.ConfigManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import kotlin.collections.isEmpty
import kotlin.collections.joinToString
import kotlin.getOrElse
import kotlin.runCatching
import kotlin.system.measureTimeMillis
import kotlin.text.contains
import kotlin.text.lowercase
import kotlin.text.replace
import kotlin.text.startsWith

class SEconomyCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sEconomy.admin")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.s-economy.usage")))
            return false
        }

        when (args[0].lowercase()) {

            "about" -> {
                var aboutMessage = getMessages().getString("commands.s-economy.about", null)

                if (aboutMessage == null || !aboutMessage.contains("%version%") || !aboutMessage.contains("%author%")) {
                    aboutMessage = "<gray>S-Economy v%version% <dark_gray>- <gray>%author%"
                }

                sender.sendMessage(mm.deserialize(getPrefix() + aboutMessage
                    .replace("%version%", main.description.version)
                    .replace("%author%", main.description.authors.joinToString(", "))))
            }

            "reload", "rl" -> {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.s-economy.reload.reloading")))

                val elapsedTime = runCatching {
                    measureTimeMillis {
                        ConfigManager.reloadConfig("config")
                        main.loadConfigFiles()
                        ConfigManager.reloadAllConfigs()
                    }
                }.getOrElse {
                    sender.sendMessage(mm.deserialize(getPrefix() + "<red>Error while reloading configs! Check console."))
                    it.printStackTrace()
                    -1
                }

                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.s-economy.reload.reloaded")
                    .replace("%time%", elapsedTime.toString())))
            }

            else -> {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.s-economy.usage")))
            }
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sEconomy.admin")) return completions

        if (args.isEmpty()) {
            completions.add("about")
            completions.add("reload")
        } else if (args.size == 1) {
            if ("about".startsWith(args[0].lowercase())) completions.add("about")
            if ("reload".startsWith(args[0].lowercase())) completions.add("reload")
        }

        return completions
    }

}