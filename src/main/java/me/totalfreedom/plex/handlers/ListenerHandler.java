package me.totalfreedom.plex.handlers;

import com.google.common.collect.Lists;
import me.totalfreedom.plex.listener.PlexListener;
import me.totalfreedom.plex.listener.impl.ChatListener;
import me.totalfreedom.plex.listener.impl.PlayerListener;
import me.totalfreedom.plex.util.PlexLog;

import java.util.List;

public class ListenerHandler
{

    private List<PlexListener> listeners = Lists.newArrayList();

    public ListenerHandler()
    {
        listeners.add(new ChatListener());
        listeners.add(new PlayerListener());

        PlexLog.log(String.format("Registered %s listeners!", listeners.size()));
    }

}
