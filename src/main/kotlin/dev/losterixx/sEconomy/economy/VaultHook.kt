package dev.losterixx.sEconomy.economy

import dev.losterixx.sEconomy.Main
import dev.losterixx.sEconomy.utils.ConfigManager
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer


class VaultHook : Economy {

    private val main = Main.instance
    private fun getConfig() = ConfigManager.getConfig("config")
    private val eco = main.economyManager

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getName(): String? {
        return "sEconomy"
    }

    override fun hasBankSupport(): Boolean {
        return false
    }

    override fun fractionalDigits(): Int {
        return eco.factorialDigits
    }

    override fun format(amount: Double): String {
        return eco.format(amount)
    }

    override fun currencyNamePlural(): String? {
        return getConfig().getString("economy.economyName.plural", "Coins")
    }

    override fun currencyNameSingular(): String? {
        return getConfig().getString("economy.economyName.singular", "Coin")
    }

    override fun hasAccount(playerName: String): Boolean {
        return eco.hasAccount(Bukkit.getOfflinePlayer(playerName).uniqueId)
    }

    override fun getBalance(player: OfflinePlayer): Double {
        return eco.getBalance(player.uniqueId)
    }

    override fun getBalance(playerName: String, worldName: String?): Double {
        val player = Bukkit.getOfflinePlayer(playerName)
        return getBalance(player, worldName)
    }

    override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        val uuid = player.uniqueId
        eco.setBalance(uuid, eco.getBalance(uuid) + amount)
        return EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "")
    }

    override fun depositPlayer(playerName: String, worldName: String?, amount: Double): EconomyResponse {
        val player = Bukkit.getOfflinePlayer(playerName)
        return depositPlayer(player, amount)
    }

    override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        val uuid = player.uniqueId
        val balance: Double = eco.getBalance(uuid)
        if (balance < amount) return EconomyResponse(
            0.0,
            balance,
            EconomyResponse.ResponseType.FAILURE,
            "Not enough money"
        )
        eco.setBalance(uuid, balance - amount)
        return EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "")
    }

    override fun withdrawPlayer(playerName: String, worldName: String?, amount: Double): EconomyResponse {
        val player = Bukkit.getOfflinePlayer(playerName)
        return withdrawPlayer(player, amount)
    }

    override fun hasAccount(player: OfflinePlayer): Boolean {
        return eco.hasAccount(player.uniqueId)
    }

    override fun hasAccount(playerName: String, worldName: String?): Boolean {
        val player = Bukkit.getOfflinePlayer(playerName)
        return hasAccount(player)
    }

    override fun createPlayerAccount(player: OfflinePlayer?): Boolean {
        return true
    }

    override fun createPlayerAccount(playerName: String, worldName: String?): Boolean {
        val player = Bukkit.getOfflinePlayer(playerName)
        return createPlayerAccount(player)
    }

    override fun hasAccount(player: OfflinePlayer, worldName: String?): Boolean {
        return hasAccount(player)
    }

    override fun getBalance(playerName: String): Double {
        return getBalance(Bukkit.getOfflinePlayer(playerName))
    }

    override fun createPlayerAccount(player: OfflinePlayer?, worldName: String?): Boolean {
        return createPlayerAccount(player)
    }

    override fun getBalance(player: OfflinePlayer, worldName: String?): Double {
        return getBalance(player)
    }

    override fun has(playerName: String, amount: Double): Boolean {
        return eco.hasEnough(Bukkit.getOfflinePlayer(playerName).uniqueId, amount)
    }

    override fun depositPlayer(player: OfflinePlayer, worldName: String?, amount: Double): EconomyResponse {
        return depositPlayer(player, amount)
    }

    override fun createBank(bankName: String?, playerName: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun withdrawPlayer(player: OfflinePlayer, worldName: String?, amount: Double): EconomyResponse {
        return withdrawPlayer(player, amount)
    }

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        val player = Bukkit.getOfflinePlayer(playerName)
        return depositPlayer(player, amount)
    }

    override fun has(player: OfflinePlayer, amount: Double): Boolean {
        return eco.hasEnough(player.uniqueId, amount)
    }

    override fun has(playerName: String, worldName: String?, amount: Double): Boolean {
        val player = Bukkit.getOfflinePlayer(playerName)
        return has(player, amount)
    }

    override fun has(player: OfflinePlayer, worldName: String?, amount: Double): Boolean {
        return has(player, amount)
    }

    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        val player = Bukkit.getOfflinePlayer(playerName)
        return withdrawPlayer(player, amount)
    }

    override fun createBank(name: String?, owner: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun deleteBank(name: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun bankBalance(name: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun bankHas(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun bankDeposit(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun isBankOwner(s: String?, s1: String?): EconomyResponse? {
        return null
    }

    override fun bankWithdraw(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun isBankOwner(name: String?, player: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun isBankMember(name: String?, playerName: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun isBankMember(name: String?, player: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun getBanks(): MutableList<String?> {
        return ArrayList<String?>()
    }

    override fun createPlayerAccount(playerName: String?): Boolean {
        return true
    }
}
