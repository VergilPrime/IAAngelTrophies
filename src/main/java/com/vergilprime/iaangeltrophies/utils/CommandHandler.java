package com.vergilprime.iaangeltrophies.utils;

import com.vergilprime.iaangeltrophies.IAAngelTrophies;
import com.vergilprime.iaangeltrophies.utils.MessageFormatter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor, TabCompleter {
	private final IAAngelTrophies plugin;

	public CommandHandler(IAAngelTrophies plugin) {
		this.plugin = plugin;
		PluginCommand cmd = plugin.getCommand("skin");
		cmd.setExecutor(this);
		cmd.setTabCompleter(this);
	}

	@Override
	public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		if (args.length == 0) {
			MessageFormatter.format(sender, "Commands:\n" +
					" {0} - Apply a skin to an item\n" +
					" {1} - Remove a skin from an item\n" +
					" {2} - Retrieve your skins if you die while\ncarrying them", "merge", "split", "lost");
			if (sender.hasPermission("angeltrophies.reload")) {
				MessageFormatter.format(sender, " {0} - Reloads the config files", "reload");
			}
			return true;
		} else if (args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("angeltrophies.reload")) {
				IAAngelTrophies.getInstance().reload(sender);
			}
			return true;
		} else if (!(sender instanceof Player)) {
			MessageFormatter.errorFormat(sender, "You need to be a player to use that command.");
			return true;
		}
		Player player = (Player) sender;
		if (args[0].equalsIgnoreCase("merge")) {
			if (sender.hasPermission("angeltrophies.merge")) {
				plugin.getInstance().getStickerManager().merge(player);
			}
		} else if (args[0].equalsIgnoreCase("split")) {
			if (sender.hasPermission("angeltrophies.split")) {
				plugin.getInstance().getStickerManager().split(player);
			}
		} else if (args[0].equalsIgnoreCase("lost")) {
			if (sender.hasPermission("angeltrophies.lost")) {
				plugin.getInstance().getStickerManager().recoverLostStickers(player);
			}
		} else {
			MessageFormatter.errorFormat(player, "Unknown subcommand {0}", args[0]);
		}
		return true;
	}
}
