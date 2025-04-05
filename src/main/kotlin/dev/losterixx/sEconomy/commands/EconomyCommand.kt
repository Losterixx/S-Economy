package dev.losterixx.sEconomy.commands

import dev.losterixx.sEconomy.Main
import dev.losterixx.sEconomy.utils.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class EconomyCommand : CommandExecutor, TabCompleter {

    private val mm = Main.miniMessage
    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getMessages() = ConfigManager.getConfig(getConfig().getString("langFile", "english"))
    private fun getPrefix() = getConfig().getString("prefix") ?: Main.DEFAULT_PREFIX
    private val eco = main.economyManager

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("sEconomy.command.eco.give") && !sender.hasPermission("sEconomy.command.eco.take")
            && !sender.hasPermission("sEconomy.command.eco.set") && !sender.hasPermission("sEconomy.command.eco.reset")
            && !sender.hasPermission("sEconomy.command.eco.check")) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
            return false
        }

        if (args.size < 2) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.usage")))
            return false
        }

        val target = Bukkit.getOfflinePlayer(args[1])

        if (!target.hasPlayedBefore() || !eco.hasAccount(target.uniqueId)) {
            sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.playerNotFound")))
            return false
        }

        when (args[0].lowercase()) {

            "give", "add" -> {
                if (!sender.hasPermission("sEconomy.command.eco.give")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                if (args.size != 3) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.give.usage")))
                    return false
                }

                val amount = args[2].toDoubleOrNull()

                if (amount == null || amount <= 0.0) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.noAmount")))
                    return false
                }

                eco.addBalance(target.uniqueId, amount)
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.give.success")
                    .replace("%player%", target.name ?: "?")
                    .replace("%balance%", eco.format(amount))))
            }

            "take", "remove" -> {
                if (!sender.hasPermission("sEconomy.command.eco.take")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                if (args.size != 3) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.take.usage")))
                    return false
                }

                val amount = args[2].toDoubleOrNull()

                if (amount == null || amount <= 0.0) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.noAmount")))
                    return false
                }

                eco.removeBalance(target.uniqueId, amount)
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.take.success")
                    .replace("%player%", target.name ?: "?")
                    .replace("%balance%", eco.format(amount))))
            }

            "set" -> {
                if (!sender.hasPermission("sEconomy.command.eco.set")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                if (args.size != 3) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.set.usage")))
                    return false
                }

                val amount = args[2].toDoubleOrNull()

                if (amount == null || amount < 0.0) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.noAmount")))
                    return false
                }

                eco.setBalance(target.uniqueId, amount)
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.set.success")
                    .replace("%player%", target.name ?: "?")
                    .replace("%balance%", eco.format(amount))))
            }

            "reset" -> {
                if (!sender.hasPermission("sEconomy.command.eco.reset")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                if (args.size != 2) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.reset.usage")))
                    return false
                }

                eco.resetBalance(target.uniqueId)
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.reset.success")
                    .replace("%player%", target.name ?: "?")))
            }

            "check" -> {
                if (!sender.hasPermission("sEconomy.command.eco.check")) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("general.noPerms")))
                    return false
                }

                if (args.size != 2) {
                    sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.check.usage")))
                    return false
                }

                val balance = eco.getFormattedBalance(target.uniqueId)
                sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.check.success")
                    .replace("%player%", target.name ?: "?")
                    .replace("%balance%", balance)))
            }

            else -> sender.sendMessage(mm.deserialize(getPrefix() + getMessages().getString("commands.economy.usage")))
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (args.isEmpty()) {
            listOf("give", "take", "set", "reset", "check").forEach {
                if (sender.hasPermission("sEconomy.command.eco.$it")) completions.add(it)
            }
        } else if (args.size == 1) {
            listOf("give", "take", "set", "reset", "check").forEach {
                if (sender.hasPermission("sEconomy.command.eco.$it") && it.startsWith(args[0].lowercase())) completions.add(it)
            }
        } else if (args.size == 2) {
            completions.addAll(Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], true) })
        } else if (args.size == 3 && listOf("give", "take", "set").contains(args[0].lowercase())) {
            val amounts = getConfig().getIntList("commands.economy.amountCompletions", listOf(10, 50, 100))
            completions.addAll(amounts.filter { it.toString().startsWith(args[2]) }.map { it.toString() })
        }

        return completions
    }
}
