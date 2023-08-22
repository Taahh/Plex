package dev.plex.world.paper

import dev.plex.Plex
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.generator.ChunkGenerator
import java.io.File
import java.util.function.Consumer


/**
 * @author Taah
 * @since 10:07 PM [21-08-2023]
 *
 */
class CustomWorld(
    name: String,
    private val chunks: CustomChunkGenerator,
    private val postGenerate: Consumer<World>? = null
) : WorldCreator(name)
{
    companion object
    {
        private val plugin: Plex = Plex.get()
        fun generateFlatWorld(name: String): World?
        {
            if (!plugin.config.contains("worlds.${name}"))
                return null
            val world = CustomWorld(name, ConfigurationChunkGenerator(name)) { postGeneratedWorld ->
                val existed = File(name).exists()
                if (!existed)
                {
                    val block = postGeneratedWorld.getBlockAt(0, postGeneratedWorld.getHighestBlockYAt(0, 0) + 1, 0)
                    block.type = Material.OAK_SIGN

                    val state = block.state
                    if (state is Sign)
                    {
                        state.getSide(Side.FRONT).line(
                            1,
                            Component.text(plugin.config.getString("worlds.${name}.name") ?: "world not found")
                                .color(NamedTextColor.GREEN)
                        )
                        state.getSide(Side.FRONT).line(2, Component.text("- 0, 0 -"))
                        state.update()
                    }
                }
            }

            return world.generate()
        }
    }

    init
    {
        this.generator(chunks)
    }

    fun generate(): World?
    {
        val world = this.createWorld()
        world?.let { postGenerate?.accept(it) }
        return world
    }

    override fun generator(): ChunkGenerator
    {
        return this.chunks
    }
}