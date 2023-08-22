package dev.plex

import dev.plex.config.Config
import dev.plex.service.ServiceManager
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Taah
 * @since 8:20 PM [21-08-2023]
 *
 */
class Plex : JavaPlugin()
{
    val serviceManager: ServiceManager = ServiceManager()

    lateinit var config: Config
    override fun onLoad()
    {
        plugin = this
        config = Config(this, "config.yml")
    }

    override fun onEnable()
    {
        config.load()

        serviceManager.startAll()
    }

    override fun onDisable()
    {
        serviceManager.stopAll()
    }

    // Moved to the bottom to clean up a bit
    companion object
    {
        private lateinit var plugin: Plex
        fun get(): Plex = plugin
    }
}