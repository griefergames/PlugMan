package com.rylinaux.plugman.command;

import com.rylinaux.plugman.PlugMan;
import com.rylinaux.plugman.util.*;
import com.rylinaux.plugman.pojo.UpdateResult;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Command that checks if a plugin is up-to-date.
 *
 * @author rylinaux
 */
public class DependsCommand extends AbstractCommand {

    /**
     * The name of the command.
     */
    public static final String NAME = "Depends";

    /**
     * The description of the command.
     */
    public static final String DESCRIPTION = "Check a plugin for its dependencies.";

    /**
     * The main permission of the command.
     */
    public static final String PERMISSION = "plugman.depends";

    /**
     * The proper usage of the command.
     */
    public static final String USAGE = "/plugman depends <plugin>";

    /**
     * The sub permissions of the command.
     */
    public static final String[] SUB_PERMISSIONS = {};

    /**
     * Construct out object.
     *
     * @param sender the command sender
     */
    public DependsCommand(CommandSender sender) {
        super(sender, NAME, DESCRIPTION, PERMISSION, SUB_PERMISSIONS, USAGE);
    }

    /**
     * Execute the command
     *
     * @param sender  the sender of the command
     * @param command the command being done
     * @param label   the name of the command
     * @param args    the arguments supplied
     */
    @Override
    public void execute(final CommandSender sender, final Command command, final String label, final String[] args) {

        if (!hasPermission()) {
            sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("error.no-permission"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("error.specify-plugin"));
            sendUsage();
            return;
        }

        final String pluginName = StringUtil.consolidateStrings(args, 1).replaceAll(" ", "+").replace("-[a-zA-Z]", "");
        final Plugin plugin = PluginUtil.getPluginByName(pluginName);
        if(plugin == null) {
            sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("error.invalid-plugin"));
            return;
        }

        sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("depends.header"));

        ThreadUtil.async(new Runnable() {
            @Override
            public void run() {
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("depends.header.start", plugin.getName()));

                List<String> depends = PluginUtil.getDependencies(plugin);
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format(false, "depends.depend", getDisplayList(depends, true)));

                List<String> softDepends = PluginUtil.getSoftDependencies(plugin);
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format(false, "depends.soft-depend", getDisplayList(softDepends, true)));

                List<String> loadBefore = PluginUtil.getLoadBefore(plugin);
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format(false, "depends.load-before", getDisplayList(loadBefore, false)));

                List<String> dependingPlugins = PluginUtil.getDependingPlugins(plugin);
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format(false, "depends.plugin-depends", getDisplayList(dependingPlugins, false)));

                List<String> softDependingPlugins = PluginUtil.getSoftDependingPlugins(plugin);
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format(false, "depends.plugin-softdepends", getDisplayList(softDependingPlugins, false)));

                List<String> loadBeforePlugins = PluginUtil.getLoadBeforePlugins(plugin);
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format(false, "depends.plugin-loadbefore", getDisplayList(loadBeforePlugins, false)));
            }

        });

    }

    private String getDisplayList(List<String> names, boolean enabledCheck) {
        if(names.size() == 0) {
            return "none";
        }
        StringBuilder builder = new StringBuilder();
        for(String plName : names) {
            if(!builder.toString().isEmpty()) {
                builder.append("§7, ");
            }
            if (!enabledCheck || Bukkit.getPluginManager().isPluginEnabled(plName)) {
                builder.append("§a");
            }else{
                builder.append("§c");
            }
            builder.append(plName);
        }
        return builder.toString();
    }

}