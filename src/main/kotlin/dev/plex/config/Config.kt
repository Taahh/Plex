package dev.plex.config

import dev.plex.Plex
import dev.plex.util.PlexLog
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader


/**
 * @author Taah
 * @project plex-kotlin
 * @since 5:46 AM [22-08-2023]
 */
class Config(private val plugin: Plex, private val name: String) : YamlConfiguration()
{
    /**
     * The File instance
     */
    private val file: File = File(plugin.dataFolder, name)

    /**
     * Whether new entries were added to the file automatically
     */
    private var added = false

    /**
     * Creates a config object
     *
     * @param plugin The plugin instance
     * @param name   The file name
     */
    init
    {
        if (!file.exists())
        {
            saveDefault()
        }
    }

    /**
     * Loads the configuration file
     */
    fun load(loadFromFile: Boolean = true)
    {
        try
        {
            if (loadFromFile)
            {
                val externalYamlConfig: YamlConfiguration = loadConfiguration(file)
                val internalConfigFileStream = plugin.getResource(name)?.let { InputStreamReader(it, Charsets.UTF_8) }
                val internalYamlConfig: YamlConfiguration? = internalConfigFileStream?.let { loadConfiguration(it) }

                if (internalYamlConfig != null)
                {
                    // Gets all the keys inside the internal file and iterates through all of it's key pairs
                    for (key in internalYamlConfig.getKeys(true))
                    {
                        // Checks if the external file contains the key already.
                        if (!externalYamlConfig.contains(key))
                        {
                            // If it doesn't contain the key, we set the key based off what was found inside the plugin jar
                            externalYamlConfig.setComments(key, internalYamlConfig.getComments(key))
                            externalYamlConfig.setInlineComments(key, internalYamlConfig.getInlineComments(key))
                            externalYamlConfig[key] = internalYamlConfig[key]
                            PlexLog.log("Setting key: $key in $name to the default value(s) since it does not exist!")
                            added = true
                        }
                    }
                }


                if (added)
                {
                    externalYamlConfig.save(file)
                    PlexLog.log("Saving new file...")
                    added = false
                }
            }
            super.load(file)
        } catch (ex: Exception)
        {
            ex.printStackTrace()
        }
    }

    /**
     * Saves the configuration file
     */
    fun save()
    {
        try
        {
            super.save(file)
        } catch (ex: Exception)
        {
            ex.printStackTrace()
        }
    }

    /**
     * Moves the configuration file from the plugin's resources folder to the data folder (plugins/Plex/)
     */
    private fun saveDefault()
    {
        plugin.saveResource(name, false)
    }
}