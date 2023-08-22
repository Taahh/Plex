package dev.plex.util

import dev.plex.Plex
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime


/**
 * @author Taah
 * @since 11:09 PM [21-08-2023]
 *
 */

class PlexUtil
{
    companion object
    {
        private val MINI_MESSAGE: MiniMessage = MiniMessage.miniMessage()
        private val plugin: Plex = Plex.get()

        val DEVELOPERS: List<String> = listOf(
            "78408086-1991-4c33-a571-d8fa325465b2",  // Telesphoreo
            "f5cd54c4-3a24-4213-9a56-c06c49594dff",  // Taahh
            "53b1512e-3481-4702-9f4f-63cb9c8be6a1",  // supernt
            "ca83b658-c03b-4106-9edc-72f70a80656d",  // ayunami2000
            "2e06e049-24c8-42e4-8bcf-d35372af31e6",  // Fleek
            "a52f1f08-a398-400a-bca4-2b74b81feae6" // Allink
        )

        fun isFolia(): Boolean
        {
            return try
            {
                Class.forName("io.papermc.paper.threadedregions.ThreadedRegionizer")
                true
            } catch (e: Exception)
            {
                false
            }
        }

        fun mmDeserialize(input: String): Component
        {
            var aprilFools = true // true by default
            if (plugin.config.contains("april_fools"))
            {
                aprilFools = plugin.config.getBoolean("april_fools")
            }
            val date: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
            if (aprilFools && date.month === Month.APRIL && date.dayOfMonth == 1)
            {
                val component: Component = MINI_MESSAGE!!.deserialize(input) // removes existing tags
                return MINI_MESSAGE.deserialize(
                    "<rainbow>" + PlainTextComponentSerializer.plainText().serialize(component)
                )
            }
            return MINI_MESSAGE!!.deserialize(input)
        }
    }
}