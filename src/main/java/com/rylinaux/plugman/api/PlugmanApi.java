package com.rylinaux.plugman.api;

import com.rylinaux.plugman.PlugMan;
import org.bukkit.plugin.Plugin;

public class PlugmanApi {

    /**
     * Registers a Plugin as ignored Plugin
     * The Plugin could not be reloaded
     * @param plugin Plugin
     */
    public static void registerAsIgnoredPlugin(Plugin plugin) {
        if(!PlugMan.getInstance().getIgnoredPlugins().contains(plugin.getName())) {
            PlugMan.getInstance().getIgnoredPlugins().add(plugin.getName());
        }
    }

    /**
     * Removes a Plugin from ignored Plugins
     * The Plugin could now be reloaded
     * @param plugin Plugin
     */
    public static void unregisterAsIgnoredPlugin(Plugin plugin) {
        PlugMan.getInstance().getIgnoredPlugins().remove(plugin.getName());
    }

}
