package dev.plex.world.paper

import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.WorldInfo
import org.bukkit.util.noise.PerlinNoiseGenerator
import java.util.*

/**
 * @author Taah
 * @since 8:51 PM [21-08-2023]
 *
 */
abstract class NoiseChunkGenerator(height: Int, private val options: NoiseOptions, vararg populator: BlockPopulator) :
    CustomChunkGenerator(height, *populator)
{
    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData)
    {
        val generator = PerlinNoiseGenerator(Random(worldInfo.seed))
        for (xx in 0..<16)
        {
            for (zz in 0..<16)
            {
                height = generator.noise(
                    options.x.toDouble(),
                    options.y,
                    options.frequency,
                    options.amplitude,
                    options.normalized
                ).toInt()
                createLoopChunkData(xx, height, zz, chunkData)
            }
        }
    }
}