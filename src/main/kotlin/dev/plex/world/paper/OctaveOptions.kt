package dev.plex.world.paper

/**
 * @author Taah
 * @since 8:51 PM [21-08-2023]
 *
 */
class OctaveOptions(x: Int, y: Int, frequency: Double, amplitude: Double, normalized: Boolean, val octaves: Int) :
    NoiseOptions(x, y, frequency, amplitude, normalized)
