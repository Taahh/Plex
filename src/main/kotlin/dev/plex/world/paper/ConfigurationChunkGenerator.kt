package dev.plex.world.paper

import org.bukkit.Material
import org.bukkit.generator.BlockPopulator

/**
 * @author Taah
 * @since 8:44 PM [21-08-2023]
 *
 */
class ConfigurationChunkGenerator(worldName: String, vararg populator: BlockPopulator) :
    BlockMapChunkGenerator(createBlockMap(worldName), *populator)
{
    companion object
    {
        private fun createBlockMap(worldName: String): LinkedHashMap<Material, Int>
        {
            val blockMap = linkedMapOf<Material, Int>()
            for (key: String in plugin.config.getConfigurationSection("worlds.${worldName}.parameters")!!
                .getKeys(false))
            {
                val material = Material.getMaterial(key.uppercase()) ?: continue
                val count = plugin.config.getInt("worlds.${worldName}.parameters.${key}")
                blockMap[material] = count
            }
            return blockMap
        }
    }

}