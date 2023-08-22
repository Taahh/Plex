package dev.plex
import dev.plex.service.ServiceManager
import dev.plex.util.isFolia
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Taah
 * @since 8:20 PM [21-08-2023]
 *
 */
class Plex : JavaPlugin()
{
    companion object {
        private var plugin: Plex? = null
        fun get(): Plex = plugin!!
    }

    val serviceManager: ServiceManager = ServiceManager()
    override fun onLoad()
    {
        plugin = this
    }

    override fun onEnable()
    {
        serviceManager.startAll()
    }

    override fun onDisable()
    {
        serviceManager.stopAll()
    }
}