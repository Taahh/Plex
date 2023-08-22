package dev.plex.service

import io.papermc.paper.threadedregions.scheduler.ScheduledTask

/**
 * @author Taah
 * @since 10:31 PM [21-08-2023]
 *
 */
abstract class AbstractService(
    val asynchronous: Boolean,
    var repeating: Boolean = false,
    var periodInSeconds: Long = 1L,
    var delayInSeconds: Long = 0L
)
{
    abstract fun onStart()

    abstract fun run(task: ScheduledTask)

    abstract fun onEnd()
}