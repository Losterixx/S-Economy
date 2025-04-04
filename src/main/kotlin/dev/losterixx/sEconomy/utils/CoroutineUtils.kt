package dev.losterixx.sEconomy.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

object CoroutineUtils {

    private lateinit var plugin: JavaPlugin
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    fun init(plugin: JavaPlugin) {
        this.plugin = plugin
    }

    fun launchSync(block: suspend CoroutineScope.() -> Unit): Job {
        return scope.launch {
            withContext(Dispatchers.IO) {
                Bukkit.getScheduler().runTask(plugin, kotlinx.coroutines.Runnable {
                    scope.launch { block() }
                })
            }
        }
    }

    fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job {
        return scope.launch(Dispatchers.IO) { block() }
    }

    fun cancelAll() {
        job.cancelChildren()
    }
}
