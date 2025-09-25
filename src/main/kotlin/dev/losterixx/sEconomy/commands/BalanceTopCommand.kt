package dev.losterixx.sEconomy.commands

import dev.losterixx.sEconomy.Main
import dev.losterixx.sEconomy.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class BalanceTopCommand : CommandExecutor {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX
    private val eco = main.economyManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sEconomy.command.balancetop")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.isNotEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.balancetop.usage")))
            return false
        }

        val topAccounts = eco.getTopAccounts(10, filterExemptPermission = true)
        if (topAccounts.isEmpty()) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.balancetop.noPlayers")))
            return false
        }

        sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.balancetop.header")))
        for ((index, entry) in topAccounts.withIndex()) {
            val uuid = entry.first
            val balance = entry.second
            val name = Bukkit.getOfflinePlayer(uuid).name ?: "???"
            val formattedBalance = eco.format(balance)
            sender.sendMessage(mm.deserialize(
                getMessages().getString("commands.balancetop.entry")
                    .replace("%index%", (index + 1).toString())
                    .replace("%name%", name)
                    .replace("%balance%", formattedBalance)
            ))
        }

        return true
    }
}