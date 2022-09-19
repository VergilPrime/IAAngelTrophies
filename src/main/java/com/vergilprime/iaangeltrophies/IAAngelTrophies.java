package com.vergilprime.iaangeltrophies;

import com.sun.tools.javac.util.List;
import com.vergilprime.iaangeltrophies.itemClasses.Sticker;
import com.vergilprime.iaangeltrophies.itemClasses.Trophy;
import com.vergilprime.iaangeltrophies.utils.*;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;
import sun.security.jca.GetInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public final class IAAngelTrophies extends JavaPlugin {
	private TrophyManager trophyManager;
	private StickerManager stickerManager;
	private PersistenceManager persistenceManager;
	private CommandHandler cmdHandler;

	public TrophyManager getTrophyManager() {
		return trophyManager;
	}
	public StickerManager getStickerManager() {
		return stickerManager;
	}
	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	@Override
	public void onEnable() {
		loadManagers();
		getLogger().info("IAAngelTrophies has been enabled!");
	}

	@Override
	public void onDisable() {
		trophyManager = null;
		stickerManager = null;
		persistenceManager.save().quit();
		getLogger().info("IAAngelTrophies has been disabled!");
	}


	public static IAAngelTrophies getInstance() {
		return getPlugin(IAAngelTrophies.class);
	}

	public void reload(CommandSender sender) {

		try {
			HandlerList.unregisterAll(stickerManager);
			HandlerList.unregisterAll(trophyManager);
			getLogger().info("Reloading...");
			loadManagers();
			getLogger().info("Done Reloading.");
			MessageFormatter.format(sender, "Reloaded {0}.", this.getDescription().getName());
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Error while reloading:", e);
			getLogger().severe("An error occurred while enabling AngelTrophies, disabling...");
			MessageFormatter.errorFormat(sender, "An error occurred while reload {0}.", this.getDescription().getName());
			MessageFormatter.errorFormat(sender, "Disabling the plugin...");
			setEnabled(false);
		}
	}

	private void loadManagers() {
		trophyManager = new TrophyManager(this);
		MessageFormatter.format((CommandSender) getLogger(), "TrophyManager loaded.");
		stickerManager = new StickerManager(this);
		MessageFormatter.format((CommandSender) getLogger(), "StickerManager loaded.");
		persistenceManager = new PersistenceManager(this);
		MessageFormatter.format((CommandSender) getLogger(), "PersistenceManager loaded.");
		cmdHandler = new CommandHandler(this);
		MessageFormatter.format((CommandSender) getLogger(), "CommandHandler loaded.");
	}
}
