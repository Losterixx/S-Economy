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

class PayCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX
    private val eco = main.economyManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sEconomy.command.pay") && !sender.hasPermission("sEconomy.command.pay.all")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.size != 2) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.pay.usage")))
            return false
        }

        val player = sender as Player
        val amount = args[1].toDoubleOrNull()

        if (amount == null || amount < getConfig().getDouble("command.pay.minAmount", 0.0)) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.pay.noAmount")))
            return false
        }

        if (args[0].equals("*", ignoreCase = true)) {
            if (!sender.hasPermission("sEconomy.command.pay.all")) {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                return false
            }

            if ((amount * Bukkit.getOnlinePlayers().size - 1) > eco.getBalance(player.uniqueId)) {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.pay.notEnoughMoney")))
                return false
            }

            for (target in Bukkit.getOnlinePlayers()) {
                if (target == sender) continue

                eco.pay(player.uniqueId, target.uniqueId, amount)
                target.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.pay.success.target")
                        .replace("%player%", sender.name)
                        .replace("%balance%", eco.format(amount))))
            }

            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.pay.success.all")
                    .replace("%balance%", eco.format(amount))))
        } else {
            if (!sender.hasPermission("sEconomy.command.pay")) {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                return false
            }

            if (amount > eco.getBalance(player.uniqueId)) {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.pay.notEnoughMoney")))
                return false
            }

            val target = Bukkit.getPlayer(args[0])

            if (target == null || !target.isOnline) {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
                return false
            }

            if (target == sender) {
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.pay.selfPay")))
                return false
            }

            eco.pay(player.uniqueId, target.uniqueId, amount)
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.pay.success.player")
                        .replace("%player%", target.name)
                        .replace("%balance%", eco.format(amount))))
            target.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.pay.success.target")
                        .replace("%player%", player.name)
                        .replace("%balance%", eco.format(amount))))
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (!sender.hasPermission("sEconomy.command.pay") && !sender.hasPermission("sEconomy.command.pay.all")) return completions

        if (args.isEmpty()) {
            if (sender.hasPermission("sEconomy.command.pay")) completions.addAll(Bukkit.getOnlinePlayers().map { it.name })
            if (sender.hasPermission("sEconomy.command.pay.all")) completions.add("*")
        } else if (args.size == 1) {
            if (sender.hasPermission("sEconomy.command.pay")) completions.addAll(Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], true) })
            if (sender.hasPermission("sEconomy.command.pay.all") && "*".startsWith(args[0].lowercase())) completions.add("*")
        } else if (args.size == 2) {
            val amounts = getConfig().getIntList("commands.pay.amountCompletions", listOf(10, 50, 100))
            completions.addAll(amounts.filter { it.toString().startsWith(args[1]) }.map { it.toString() })
        }

        return completions
    }

}