package dev.plex.world.paper

import dev.plex.Plex
import org.bukkit.World
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator

/**
 * @author Taah
 * @since 8:27 PM [21-08-2023]
 *
 */
abstract class CustomChunkGenerator(protected var height: Int, vararg populator: BlockPopulator) : ChunkGenerator()
{
    companion object {
        @JvmStatic
        protected val plugin: Plex = Plex.get()
    }
    private val populators: List<BlockPopulator> = populator.asList()
    abstract fun createLoopChunkData(x: Int, y: Int, z: Int, chunk: ChunkData)

    override fun getDefaultPopulators(world: World): List<BlockPopulator>
    {
        return populators
    }
}