package dev.plex.service

import dev.plex.Plex
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.jetbrains.annotations.Async.Schedule
import java.util.concurrent.TimeUnit

/**
 * @author Taah
 * @since 10:30 PM [21-08-2023]
 *
 */
class ServiceManager
{
    private val services: MutableList<AbstractService> = mutableListOf()
    private val runningServices: MutableMap<AbstractService, ScheduledTask> = mutableMapOf()
    fun startAll()
    {
        services.forEach(this::startService)
    }

    fun stopAll()
    {
        runningServices.values.filter { !it.isCancelled }.forEach { it.cancel() }
        runningServices.clear()
    }

    fun addService(service: AbstractService, start: Boolean = false)
    {
        services.add(service)
        if (start)
        {
            startService(service)
        }
    }

    private fun startService(service: AbstractService): ScheduledTask
    {
        service.onStart()
        val task: ScheduledTask
        if (service.asynchronous)
        {
            if (service.repeating)
            {
                task = Bukkit.getAsyncScheduler().runAtFixedRate(Plex.get(), service::run, service.delayInSeconds, service.periodInSeconds, TimeUnit.SECONDS)
            } else
            {
                task = Bukkit.getAsyncScheduler().runDelayed(Plex.get(), service::run, service.delayInSeconds, TimeUnit.SECONDS)
            }
        } else
        {
            if (service.repeating)
            {
                task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(Plex.get(), service::run, service.delayInSeconds * 20L, service.periodInSeconds * 20L)
            } else
            {
                task = Bukkit.getGlobalRegionScheduler().runDelayed(Plex.get(), service::run, service.delayInSeconds * 20L)
            }
        }

        runningServices[service] = task
        return task
    }

    fun services(): List<AbstractService>
    {
        return services
    }

    fun runningServices(): Map<AbstractService, ScheduledTask>
    {
        return runningServices
    }
}