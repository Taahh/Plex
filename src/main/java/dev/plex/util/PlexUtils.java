package dev.plex.util;

import dev.plex.Plex;
import dev.plex.PlexBase;
import dev.plex.cache.PlayerCache;
import dev.plex.storage.StorageType;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlexUtils implements PlexBase
{
    public static List<String> DEVELOPERS =
            Arrays.asList("78408086-1991-4c33-a571-d8fa325465b2", // Telesphoreo
                    "f5cd54c4-3a24-4213-9a56-c06c49594dff", // Taahh
                    "ca83b658-c03b-4106-9edc-72f70a80656d", // ayunami2000
                    "2e06e049-24c8-42e4-8bcf-d35372af31e6" //Fleek
            );

    public static <T> T addToArrayList(List<T> list, T object)
    {
        list.add(object);
        return object;
    }

    public static void disabledEffect(Player player, Location location)
    {
        Particle.CLOUD.builder().location(location).receivers(player).extra(0).offset(0.5, 0.5, 0.5).count(5).spawn();
        Particle.FLAME.builder().location(location).receivers(player).extra(0).offset(0.5, 0.5, 0.5).count(3).spawn();
        Particle.SOUL_FIRE_FLAME.builder().location(location).receivers(player).offset(0.5, 0.5, 0.5).extra(0).count(2).spawn();
        player.playSound(location, org.bukkit.Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 0.5f);
    }

    public static void disabledEffectMultiple(Player[] players, Location location)
    {
        Particle.CLOUD.builder().location(location).receivers(players).extra(0).offset(0.5, 0.5, 0.5).count(5).spawn();
        Particle.FLAME.builder().location(location).receivers(players).extra(0).offset(0.5, 0.5, 0.5).count(3).spawn();
        Particle.SOUL_FIRE_FLAME.builder().location(location).receivers(players).offset(0.5, 0.5, 0.5).extra(0).count(2).spawn();
        // note that the sound is played to everyone who is close enough to hear it
        players[0].getWorld().playSound(location, org.bukkit.Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 0.5f);
    }

    public static void testConnections()
    {
        if (Plex.get().getSqlConnection().getDataSource() != null)
        {
            try (Connection con = Plex.get().getSqlConnection().getCon())
            {
                if (Plex.get().getStorageType() == StorageType.MARIADB)
                {
                    PlexLog.log("Successfully enabled MySQL!");
                }
                else if (Plex.get().getStorageType() == StorageType.SQLITE)
                {
                    PlexLog.log("Successfully enabled SQLite!");
                }
            }
            catch (SQLException e)
            {
                if (Plex.get().getMongoConnection().getDatastore() != null)
                {
                    PlexLog.log("Successfully enabled MongoDB!");
                }
            }
        }
        else
        {
            if (Plex.get().getMongoConnection().getDatastore() != null)
            {
                PlexLog.log("Successfully enabled MongoDB!");
            }
        }
    }

    public static boolean isPluginCMD(String cmd, String pluginName)
    {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        if (plugin == null)
        {
            PlexLog.error(pluginName + " can not be found on the server! Make sure it is spelt correctly!");
            return false;
        }
        List<Command> cmds = PluginCommandYamlParser.parse(plugin);
        for (Command pluginCmd : cmds)
        {
            List<String> cmdAliases = pluginCmd.getAliases().size() > 0 ? pluginCmd.getAliases().stream().map(String::toLowerCase).collect(Collectors.toList()) : null;
            if (pluginCmd.getName().equalsIgnoreCase(cmd) || (cmdAliases != null && cmdAliases.contains(cmd.toLowerCase())))
            {
                return true;
            }
        }
        return false;
    }

    private static final MiniMessage safeMessage = MiniMessage.builder().tags(TagResolver.builder().resolvers(
            StandardTags.color(),
            StandardTags.decorations(),
            StandardTags.gradient(),
            StandardTags.rainbow(),
            StandardTags.reset()
    ).build()).build();

    public static String mmStripColor(String input)
    {
        return PlainTextComponentSerializer.plainText().serialize(mmDeserialize(input));
    }

    public static Component mmDeserialize(String input)
    {
        boolean aprilFools = true; // true by default
        if (plugin.config.contains("april_fools"))
        {
            aprilFools = plugin.config.getBoolean("april_fools");
        }
        ZonedDateTime date = ZonedDateTime.now(ZoneId.of(TimeUtils.TIMEZONE));
        if (aprilFools && date.getMonth() == Month.APRIL && date.getDayOfMonth() == 1)
        {
            Component component = MiniMessage.miniMessage().deserialize(input); // removes existing tags
            return MiniMessage.miniMessage().deserialize("<rainbow>" + PlainTextComponentSerializer.plainText().serialize(component));
        }
        return MiniMessage.miniMessage().deserialize(input);
    }

    public static Component mmCustomDeserialize(String input, TagResolver... resolvers)
    {
        return MiniMessage.builder().tags(TagResolver.builder().resolvers(resolvers).build()).build().deserialize(input);
    }

    public static Component messageComponent(String entry, Object... objects)
    {
        return MiniMessage.miniMessage().deserialize(messageString(entry, objects));
    }

    public static String messageString(String entry, Object... objects)
    {
        String f = plugin.messages.getString(entry);
        if (f == null)
        {
            throw new NullPointerException();
        }
        for (int i = 0; i < objects.length; i++)
        {
            f = f.replace("{" + i + "}", String.valueOf(objects[i]));
        }
        return f;
    }

    public static List<String> getPlayerNameList()
    {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    public static void broadcast(String s)
    {
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(s));
    }

    public static void broadcast(Component component)
    {
        Bukkit.broadcast(component);
    }

    public static void broadcastToAdmins(Component component)
    {
        Bukkit.getOnlinePlayers().stream().filter(pl -> PlayerCache.getPlexPlayer(pl.getUniqueId()).isAdminActive()).forEach(pl ->
        {
            pl.sendMessage(component);
        });
    }
}
