package dev.losterixx.sEconomy.utils

import dev.losterixx.sEconomy.Main
import dev.losterixx.sEconomy.commands.*
import dev.losterixx.sEconomy.listeners.BalanceListener
import dev.losterixx.sEconomy.other.updatechecker.UpdateListener
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

object RegisterManager {

    private val main = Main.instance

    private var commands = 0
    private var listeners = 0

    private fun registerCommands() {
        registerCommand("sEconomy", SEconomyCommand(), SEconomyCommand(), "seco", "s-eco")
        registerCommand("balance", BalanceCommand(), BalanceCommand(), "bal", "money", "coins")
        registerCommand("economy", EconomyCommand(), EconomyCommand(), "econ", "eco")

        main.logger.info("Registered $commands commands!")
    }

    private fun registerListeners() {
        HandlerList.unregisterAll(main)

        registerListener(UpdateListener())
        registerListener(BalanceListener())

        main.logger.info("Registered $listeners listeners!")
    }

    fun registerAll() {
        registerCommands()
        registerListeners()
    }


    private fun registerCommand(
        name: String,
        executor: CommandExecutor,
        completer: TabCompleter?,
        vararg aliases: String?
    ) {
        try {
            val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            val commandMap = commandMapField[Bukkit.getServer()] as CommandMap

            val command: Command = object : Command(name) {
                override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
                    return executor.onCommand(sender, this, commandLabel, args)
                }

                override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
                    if (completer == null) return super.tabComplete(sender, alias, args)
                    return completer.onTabComplete(sender, this, alias, args)!!
                }
            }

            if (aliases != null && aliases.size > 0 && aliases[0] != null && !aliases[0]!!.isEmpty()) {
                command.setAliases(listOf(*aliases))
            }

            commandMap.register(name, command)
            commands++
        } catch (e: Exception) {
            main.logger.warning("Failed to register command $name!")
            e.printStackTrace()
        }
    }

    private fun registerListener(listener: Listener) {
        try {
            main.server.pluginManager.registerEvents(listener, main)
            listeners++
        } catch (e: Exception) {
            main.logger.warning("Failed to register event ${listener.javaClass.simpleName}!")
            e.printStackTrace()
        }
    }

}