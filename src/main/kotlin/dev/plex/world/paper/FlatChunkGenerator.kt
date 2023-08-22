package dev.plex.world.paper

import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.WorldInfo
import java.util.*

/**
 * @author Taah
 * @since 8:35 PM [21-08-2023]
 *
 */
abstract class FlatChunkGenerator(height: Int, vararg populator: BlockPopulator) :
    CustomChunkGenerator(height, *populator)
{

    override fun generateSurface(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData)
    {
        for (xx in 0..<16)
        {
            for (zz in 0..<16)
            {
                createLoopChunkData(xx, height, zz, chunkData)
            }
        }
    }
}