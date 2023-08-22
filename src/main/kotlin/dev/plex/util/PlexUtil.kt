package dev.plex.util

/**
 * @author Taah
 * @since 11:09 PM [21-08-2023]
 *
 */
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