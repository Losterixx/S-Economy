package dev.losterixx.sEconomy.utils

import dev.losterixx.sEconomy.Main

object ConfigUpdater {

    private val main = Main.instance
    private val config get() = ConfigManager.getConfig("config")
    private val messages get() = ConfigManager.getConfig(config.getString("langFile", "english"))
    private val commands get() = ConfigManager.getConfig("commands")

    fun updateConfigs() {
        val currentVersion = config.getInt("file-version", 1)

        when (currentVersion) {
            1 -> {
                main.logger.info("Detected config version 1, updating to version 2...")
                messages.apply {
                    set("commands.seconomy.check.allFailed", "<gray>You <red>cannot check the balance of all players at once<gray>! Please specify a player.")
                    set("commands.seconomy.give.all", "<gray>You have given <white>all players <yellow>%balance%<gray>!")
                    set("commands.seconomy.take.all", "<gray>You have taken <white>from all players <yellow>%balance%<gray>!")
                    set("commands.seconomy.set.all", "<gray>You have set the balance of <white>all players <gray>to <yellow>%balance%<gray>!")
                    set("commands.seconomy.reset.all", "<gray>You have reset the balance of <white>all players<gray>!")
                    set("commands.balance.balancetop.usage", "<gray>Please use <red>/balancetop<gray>!")
                    set("commands.balance.balancetop.header", "<gray>Top <yellow>10 Players <gray>by Balance:")
                    set("commands.balance.balancetop.noPlayers", "<gray>There are <red>no players<gray> with a balance!")
                    set("commands.balance.balancetop.entry", "<dark_gray> 🢒 <gold>%index%<gray>. <white>%name% <dark_gray>- <yellow>%balance%<gray>")
                    save()
                }
                commands.apply {
                    set("balancetop.enabled", true)
                    set("balancetop.aliases", listOf("baltop", "moneytop", "coinstop"))
                    save()
                }
            }
        }
    }

}