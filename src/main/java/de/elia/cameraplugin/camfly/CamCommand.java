package de.elia.cameraplugin.camfly;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CamCommand implements CommandExecutor {

    private final CameraPlugin plugin;

    public CamCommand(CameraPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessage("no-player"));
            return true;
        }

        if (!player.hasPermission("camplugin.use")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!player.isOp()) {
                player.sendMessage(plugin.getMessage("no-permission"));
                return true;
            }
            player.sendMessage(plugin.getMessage("reload-start"));
            plugin.reloadPlugin(player);
            player.sendMessage(plugin.getMessage("reload-success"));
            return true;
        }

        if (plugin.isInCameraMode(player)) {
            plugin.exitCameraMode(player);
            player.sendMessage(plugin.getMessage("camera-off"));
        } else {
            plugin.enterCameraMode(player);
            player.sendMessage(plugin.getMessage("camera-on"));
        }
        return true;
    }
}