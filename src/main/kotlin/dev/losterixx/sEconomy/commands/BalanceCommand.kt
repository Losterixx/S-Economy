package dev.losterixx.sEconomy.commands

import dev.losterixx.sEconomy.Main
import dev.losterixx.sEconomy.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

class BalanceCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX
    private val eco = main.economyManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sEconomy.command.balance.self") && !sender.hasPermission("sEconomy.command.balance.other")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.size > 1) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.balance.usage")))
            return false
        }

        if (args.isEmpty()) {
            if (sender !is Player) {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPlayer")))
                return false
            }

            if (!sender.hasPermission("sEconomy.command.balance.self")) {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                return false
            }

            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.balance.self")
                .replace("%balance%", eco.getFormattedBalance(sender.uniqueId))))
        } else if (args.size == 1) {
            if (!sender.hasPermission("sEconomy.command.balance.other")) {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                return false
            }

            val target = Bukkit.getOfflinePlayer(args[0])

            if (!target.hasPlayedBefore() || !eco.hasAccount(target.uniqueId)) {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
                return false
            }

            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.balance.other")
                .replace("%player%", target.name ?: "?")
                .replace("%balance%", eco.getFormattedBalance(target.uniqueId))))
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sEconomy.command.money.other")) return completions

        if (args.isEmpty()) {
            for (players in Bukkit.getOnlinePlayers()) {
                completions.add(players.name)
            }
        } else if (args.size == 1) {
            for (players in Bukkit.getOnlinePlayers()) {
                if (players.name.lowercase().startsWith(args[0].lowercase())) {
                    completions.add(players.name)
                }
            }
        }

        return completions
    }

}