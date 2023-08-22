package dev.plex.world.paper

import org.bukkit.Material
import org.bukkit.generator.BlockPopulator

/**
 * @author Taah
 * @since 8:27 PM [21-08-2023]
 *
 */
open class BlockMapChunkGenerator(private val blockMap: LinkedHashMap<Material, Int>, vararg populator: BlockPopulator) : FlatChunkGenerator(0, *populator)
{
    override fun createLoopChunkData(x: Int, y: Int, z: Int, chunk: ChunkData)
    {
        var height = -1;
        for (i: Int in blockMap.values) {
            height += i
        }
        for (entry: Map.Entry<Material, Int> in blockMap.entries) {
            for (i in 0..<entry.value) {
                chunk.setBlock(x, height, z, entry.key)
                height--
            }
        }
    }
}