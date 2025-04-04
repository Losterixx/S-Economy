package dev.losterixx.sEconomy.economy

import dev.losterixx.sEconomy.utils.ConfigManager
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class EconomyManager {

    private fun getConfig() = ConfigManager.getConfig("config")
    private fun getData() = ConfigManager.getConfig("data")

    fun getBalance(uuid: UUID?): Double {
        return getData().getDouble("$uuid.balance", -1.0)
    }

    fun getFormattedBalance(uuid: UUID?): String {
        return format(getData().getDouble("$uuid.balance", -1.0))
    }

    fun setBalance(uuid: UUID?, amount: Double) {
        getData().set("$uuid.balance", amount)
        ConfigManager.saveConfig("data")
    }

    fun addBalance(uuid: UUID?, amount: Double) {
        setBalance(uuid, getBalance(uuid) + amount)
    }

    fun removeBalance(uuid: UUID?, amount: Double) {
        setBalance(uuid, getBalance(uuid) - amount)
    }

    fun hasEnough(uuid: UUID?, amount: Double): Boolean {
        return getBalance(uuid) >= amount
    }

    @Throws(IllegalStateException::class)
    fun pay(from: UUID?, to: UUID?, amount: Double) {
        if (hasEnough(from, amount)) {
            removeBalance(from, amount)
            addBalance(to, amount)
        } else {
            throw IllegalStateException("Sender does not have enough balance.")
        }
    }

    fun hasAccount(uuid: UUID): Boolean {
        return getData().contains(uuid.toString())
    }

    fun resetBalance(uuid: UUID?) {
        setBalance(uuid, getConfig().getDouble("economy.startingBalance", 0.0))
    }

    val currencySymbol: String = getConfig().getString("economy.currencySymbol", "$")
    val factorialDigits: Int = getConfig().getInt("economy.fractionalDigits", 2)

    fun format(amount: Double): String {
        val decimalFormatPattern = getConfig().getString("economy.decimalFormat.format", "#,###.##")

        val decimalSymbols = DecimalFormatSymbols(Locale.of(getConfig().getString("economy.decimalFormat.locale", "ENGLISH")))
        val decimalFormat = DecimalFormat(decimalFormatPattern, decimalSymbols)
        val formattedAmount = decimalFormat.format(amount)

        var format = getConfig().getString("economy.format", "%currencySymbol%%amount%")
            .replace("%currencySymbol%", currencySymbol)
            .replace("%amount%", formattedAmount)

        return format
    }

    fun saveBalances() {
        ConfigManager.saveConfig("data")
    }
}
