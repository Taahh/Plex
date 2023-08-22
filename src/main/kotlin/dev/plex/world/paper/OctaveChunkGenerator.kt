package dev.plex.world.paper

import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.WorldInfo
import org.bukkit.util.noise.PerlinOctaveGenerator
import java.util.*

/**
 * @author Taah
 * @since 8:51 PM [21-08-2023]
 *
 */
abstract class OctaveChunkGenerator(height: Int, private val options: OctaveOptions, vararg populator: BlockPopulator) :
    CustomChunkGenerator(height, *populator)
{
    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData)
    {
        val generator = PerlinOctaveGenerator(Random(worldInfo.seed), options.octaves)
        for (xx in 0..<16)
        {
            for (zz in 0..<16)
            {
                height = generator.noise(
                    options.x.toDouble(),
                    options.y.toDouble(),
                    options.frequency,
                    options.amplitude,
                    options.normalized
                ).toInt()
                createLoopChunkData(xx, height, zz, chunkData)
            }
        }
    }
}