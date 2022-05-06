package dev.plex.api.plugin;

import dev.plex.api.chat.IChatHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public abstract class PlexPlugin extends JavaPlugin
{
    @Setter(AccessLevel.NONE)
    private static PlexPlugin plugin;

    private IChatHandler chatHandler;

    @Override
    public void onLoad()
    {
        plugin = this;
    }

    public static PlexPlugin get()
    {
        return plugin;
    }
}
