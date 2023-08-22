package dev.plex.util

import dev.plex.Plex
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.logger.slf4j.ComponentLogger


/**
 * @author Taah
 * @project plex-kotlin
 * @since 5:43 AM [22-08-2023]
 */
class PlexLog
{
    companion object
    {
        private val LOGGER = ComponentLogger.logger("Plex")
        fun log(message: String, vararg strings: Any)
        {
            var message = message
            for (i in strings.indices)
            {
                if (message.contains("{$i}"))
                {
                    message = message.replace("{$i}", strings[i].toString())
                }
            }
            LOGGER.info(PlexUtil.mmDeserialize("<yellow>[Plex] <gray>$message"))
        }

        fun log(component: Component)
        {
            LOGGER.info(
                Component.text("[Plex] ").color(NamedTextColor.YELLOW).append(component)
                    .colorIfAbsent(NamedTextColor.GRAY)
            )
        }

        fun error(message: String, vararg strings: Any)
        {
            var message = message
            for (i in strings.indices)
            {
                if (message.contains("{$i}"))
                {
                    message = message.replace("{$i}", strings[i].toString())
                }
            }
            LOGGER.error(PlexUtil.mmDeserialize("<red>[Plex Error] <gold>$message"))
        }

        fun warn(message: String, vararg strings: Any)
        {
            var message = message
            for (i in strings.indices)
            {
                if (message.contains("{$i}"))
                {
                    message = message.replace("{$i}", strings[i].toString())
                }
            }
            LOGGER.warn(PlexUtil.mmDeserialize("<#eb7c0e>[Plex Warning] <gold>$message"))
        }

        fun debug(message: String, vararg strings: Any)
        {
            var message = message
            if (Plex.get().config.getBoolean("debug"))
            {
                for (i in strings.indices)
                {
                    if (message.contains("{$i}"))
                    {
                        message = message.replace("{$i}", strings[i].toString())
                    }
                }
                LOGGER.info(PlexUtil.mmDeserialize("<dark_purple>[Plex Debug] <gold>$message"))
            }
        }
    }
}