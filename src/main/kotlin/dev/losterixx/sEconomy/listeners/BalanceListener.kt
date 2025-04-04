package dev.losterixx.sEconomy.listeners

import dev.losterixx.sEconomy.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class BalanceListener : Listener {

    private val main = Main.instance
    private val eco = main.economyManager

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.getPlayer()

        if (!eco.hasAccount(player.uniqueId)) {
            eco.resetBalance(player.uniqueId)
        }
    }

}
