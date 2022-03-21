package dev.plex.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import dev.plex.Plex;
import dev.plex.PlexBase;
import dev.plex.config.Config;
import dev.plex.storage.StorageType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PlexUtils extends PlexBase
{
    private static final Random RANDOM;
    private static final List<String> regxList = new ArrayList<>()
    {{
        add("y");
        add("mo");
        add("w");
        add("d");
        add("h");
        add("m");
        add("s");
    }};
    public static Map<String, ChatColor> CHAT_COLOR_NAMES;
    public static List<ChatColor> CHAT_COLOR_POOL;
    public static List<String> DEVELOPERS =
            Arrays.asList("78408086-1991-4c33-a571-d8fa325465b2", // Telesphoreo
                    "f5cd54c4-3a24-4213-9a56-c06c49594dff" // Taahh
            );

    static
    {
        RANDOM = new Random();
        CHAT_COLOR_NAMES = new HashMap<>();
        CHAT_COLOR_POOL = Arrays.asList(ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE);
        for (final ChatColor chatColor : CHAT_COLOR_POOL)
        {
            CHAT_COLOR_NAMES.put(chatColor.name().toLowerCase().replace("_", ""), chatColor);
        }
    }

    public static ChatColor randomChatColor()
    {
        return CHAT_COLOR_POOL.get(RANDOM.nextInt(CHAT_COLOR_POOL.size()));
    }

    public static void testConnections()
    {
        if (Plex.get().getSqlConnection().getCon() != null)
        {
            if (Plex.get().getStorageType() == StorageType.MARIADB)
            {
                PlexLog.log("Successfully enabled MySQL!");
            }
            else if (Plex.get().getStorageType() == StorageType.SQLITE)
            {
                PlexLog.log("Successfully enabled SQLite!");
            }
            try
            {
                Plex.get().getSqlConnection().getCon().close();
            }
            catch (SQLException ignored)
            {
            }
        }
        else if (Plex.get().getMongoConnection().getDatastore() != null)
        {
            PlexLog.log("Successfully enabled MongoDB!");
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

    public static String colorize(final String string)
    {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static Component messageComponent(String entry, Object... objects)
    {
        return MiniMessage.miniMessage().deserialize(LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(messageString(entry, objects))));
    }

    public static String messageString(String entry, Object... objects)
    {
        String f = plugin.messages.getString(entry);
        if (f == null)
        {
            throw new NullPointerException();
        }
        for (Object object : objects)
        {
            f = f.replaceFirst("<v>", String.valueOf(object));
        }
        return f;
    }

    private static long a(String parse)
    {
        StringBuilder sb = new StringBuilder();

        regxList.forEach(obj ->
        {
            if (parse.endsWith(obj))
            {
                sb.append(parse.split(obj)[0]);
            }
        });

        return Long.parseLong(sb.toString());
    }

    private static TimeUnit verify(String arg)
    {
        TimeUnit unit = null;
        for (String c : regxList)
        {
            if (arg.endsWith(c))
            {
                switch (c)
                {
                    case "y" -> unit = TimeUnit.YEAR;
                    case "mo" -> unit = TimeUnit.MONTH;
                    case "w" -> unit = TimeUnit.WEEK;
                    case "d" -> unit = TimeUnit.DAY;
                    case "h" -> unit = TimeUnit.HOUR;
                    case "m" -> unit = TimeUnit.MINUTE;
                    case "s" -> unit = TimeUnit.SECOND;
                }
                break;
            }
        }
        return (unit != null) ? unit : TimeUnit.DAY;
    }

    public static LocalDateTime parseDateOffset(String... time)
    {
        Instant instant = Instant.now();
        for (String arg : time)
        {
            instant = instant.plusSeconds(verify(arg).get() * a(arg));
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault().getRules().getOffset(instant));
    }

    public static ChatColor getChatColorFromConfig(Config config, ChatColor def, String path)
    {
        ChatColor color;
        if (config.getString(path) == null)
        {
            color = def;
        }
        else if (ChatColor.getByChar(config.getString(path)) == null)
        {
            color = def;
        }
        else
        {
            color = ChatColor.getByChar(config.getString(path));
        }
        return color;
    }

    public static void setBlocks(Location c1, Location c2, Material material)
    {
        if (!c1.getWorld().getName().equals(c1.getWorld().getName()))
        {
            return;
        }
        int sy = Math.min(c1.getBlockY(), c2.getBlockY()), ey = Math.max(c1.getBlockY(), c2.getBlockY()), sx = Math.min(c1.getBlockX(), c2.getBlockX()), ex = Math.max(c1.getBlockX(), c2.getBlockX()), sz = Math.min(c1.getBlockZ(), c2.getBlockZ()), ez = Math.max(c1.getBlockZ(), c2.getBlockZ());
        World world = c1.getWorld();
        for (int y = sy; y <= ey; y++)
        {
            for (int x = sx; x <= ex; x++)
            {
                for (int z = sz; z <= ez; z++)
                {
                    world.getBlockAt(x, y, z).setType(material);
                }
            }
        }
    }

    public static <T> void commitGlobalGameRules(World world)
    {
        for (String s : Plex.get().config.getStringList("global_gamerules"))
        {
            readGameRules(world, s);
        }
    }

    public static <T> void commitSpecificGameRules(World world)
    {
        for (String s : Plex.get().config.getStringList("worlds." + world.getName().toLowerCase(Locale.ROOT) + ".gameRules"))
        {
            readGameRules(world, s);
        }
    }

    private static <T> void readGameRules(World world, String s)
    {
        String gameRule = s.split(";")[0];
        T value = (T)s.split(";")[1];
        GameRule<T> rule = (GameRule<T>)GameRule.getByName(gameRule);
        if (rule != null && check(value).getClass().equals(rule.getType()))
        {
            world.setGameRule(rule, value);
            PlexLog.debug("Setting game rule " + gameRule + " for world " + world.getName() + " with value " + value);
        }
        else
        {
            PlexLog.error(String.format("Failed to set game rule %s for world %s with value %s!", gameRule, world.getName().toLowerCase(Locale.ROOT), value));
        }
    }

    public static <T> Object check(T value)
    {
        if (value.toString().equalsIgnoreCase("true") || value.toString().equalsIgnoreCase("false"))
        {
            return Boolean.parseBoolean(value.toString());
        }

        if (NumberUtils.isNumber(value.toString()))
        {
            return Integer.parseInt(value.toString());
        }
        return value;
    }

    public static List<String> getPlayerNameList()
    {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    public static void broadcast(String s)
    {
        Bukkit.broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(s));
    }

    public static void broadcast(Component component)
    {
        Bukkit.broadcast(component);
    }

    public static Object simpleGET(String url)
    {
        try
        {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)u.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = in.readLine()) != null)
            {
                content.append(line);
            }
            in.close();
            connection.disconnect();
            return new JSONParser().parse(content.toString());
        }
        catch (IOException | ParseException ex)
        {
            return null;
        }
    }

    public static UUID getFromName(String name)
    {
        JSONObject profile;
        profile = (JSONObject)simpleGET("https://api.ashcon.app/mojang/v2/user/" + name);
        if (profile == null)
        {
            PlexLog.error("Profile from Ashcon API returned null!");
            return null;
        }
        String uuidString = (String)profile.get("uuid");
        return UUID.fromString(uuidString);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Set<Class<?>> getClassesFrom(String packageName)
    {
        Set<Class<?>> classes = new HashSet<>();
        try
        {
            ClassPath path = ClassPath.from(Plex.class.getClassLoader());
            ImmutableSet<ClassPath.ClassInfo> infoSet = path.getTopLevelClasses(packageName);
            infoSet.forEach(info ->
            {
                try
                {
                    Class<?> clazz = Class.forName(info.getName());
                    classes.add(clazz);
                }
                catch (ClassNotFoundException ex)
                {
                    PlexLog.error("Unable to find class " + info.getName() + " in " + packageName);
                }
            });
        }
        catch (IOException ex)
        {
            PlexLog.error("Something went wrong while fetching classes from " + packageName);
            throw new RuntimeException(ex);
        }
        return Collections.unmodifiableSet(classes);
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<Class<? extends T>> getClassesBySubType(String packageName, Class<T> subType)
    {
        Set<Class<?>> loadedClasses = getClassesFrom(packageName);
        Set<Class<? extends T>> classes = new HashSet<>();
        loadedClasses.forEach(clazz ->
        {
            if (clazz.getSuperclass() == subType || Arrays.asList(clazz.getInterfaces()).contains(subType))
            {
                classes.add((Class<? extends T>)clazz);
            }
        });
        return Collections.unmodifiableSet(classes);
    }

    public static int randomNum()
    {
        return ThreadLocalRandom.current().nextInt();
    }

    public static int randomNum(int limit)
    {
        return ThreadLocalRandom.current().nextInt(limit);
    }

    public static int randomNum(int start, int limit)
    {
        return ThreadLocalRandom.current().nextInt(start, limit);
    }

    public static long getDateNow()
    {
        return new Date().getTime();
    }

    public static Date getDateFromLong(long epoch)
    {
        return new Date(epoch);
    }

    public static long hoursToSeconds(long hours)
    {
        return hours * 3600;
    }

    public static long minutesToSeconds(long minutes)
    {
        return minutes * 60;
    }
}
