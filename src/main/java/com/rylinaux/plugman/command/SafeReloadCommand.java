package com.rylinaux.plugman.command;

import com.rylinaux.plugman.PlugMan;
import com.rylinaux.plugman.util.PluginUtil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command that reloads plugin(s).
 *
 * @author rylinaux
 */
public class SafeReloadCommand extends AbstractCommand {

    /**
     * The name of the command.
     */
    public static final String NAME = "SafeReload";

    /**
     * The description of the command.
     */
    public static final String DESCRIPTION = "Reload a plugin safely.";

    /**
     * The main permission of the command.
     */
    public static final String PERMISSION = "plugman.reload";

    /**
     * The proper usage of the command.
     */
    public static final String USAGE = "/plugman safereload <plugin>";

    /**
     * The sub permissions of the command.
     */
    public static final String[] SUB_PERMISSIONS = {};

    /**
     * Construct out object.
     *
     * @param sender the command sender
     */
    public SafeReloadCommand(CommandSender sender) {
        super(sender, NAME, DESCRIPTION, PERMISSION, SUB_PERMISSIONS, USAGE);
    }

    /**
     * Execute the command.
     *
     * @param sender  the sender of the command
     * @param command the command being done
     * @param label   the name of the command
     * @param args    the arguments supplied
     */
    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {

        if (!hasPermission()) {
            sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("error.no-permission"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("error.specify-plugin"));
            sendUsage();
            return;
        }

        Plugin target = PluginUtil.getPluginByName(args, 1);

        if (target == null) {
            sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("error.invalid-plugin"));
            sendUsage();
            return;
        }

        if (PluginUtil.isIgnored(target)) {
            sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("error.ignored"));
            return;
        }

        sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("safereload.start", target.getName()));

        List<String> depends = PluginUtil.getDependingPlugins(target);
        List<String> softDepends = PluginUtil.getSoftDependencies(target);
        for(String pl : depends) {
            if (PluginUtil.isIgnored(pl)) {
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("safereload.ignored", pl));
                return;
            }
        }
        for(String pl : softDepends) {
            if (PluginUtil.isIgnored(pl)) {
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("safereload.ignored", pl));
                return;
            }
        }

        if(depends.size() > 0) {
            for(String depend : depends) {
                Plugin depPl = PluginUtil.getPluginByName(depend);
                if(depPl != null) {
                    sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("safereload.unload-depend", depPl.getName()));
                    PluginUtil.unload(depPl);
                }
            }
        }
        if(softDepends.size() > 0) {
            for(String depend : softDepends) {
                Plugin depPl = PluginUtil.getPluginByName(depend);
                if(depPl != null) {
                    sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("safereload.unload-softdepend", depPl.getName()));
                    PluginUtil.unload(depPl);
                }
            }
        }
        PluginUtil.reload(target);
        sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("reload.reloaded", target.getName()));

        if(depends.size() > 0) {
            for(String depend : depends) {
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("safereload.load-depend", depend));
                PluginUtil.load(depend);
            }
        }
        if(softDepends.size() > 0) {
            for(String depend : softDepends) {
                sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("safereload.load-softdepend", target.getName()));
                PluginUtil.load(depend);
            }
        }
        sender.sendMessage(PlugMan.getInstance().getMessageFormatter().format("safereload.reloaded", target.getName()));

    }
}
