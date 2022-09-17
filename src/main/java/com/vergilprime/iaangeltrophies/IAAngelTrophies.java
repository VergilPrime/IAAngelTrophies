package com.vergilprime.iaangeltrophies;

import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Pair;
import com.vergilprime.iaangeltrophies.itemClasses.Sticker;
import com.vergilprime.iaangeltrophies.itemClasses.Trophy;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public final class IAAngelTrophies extends JavaPlugin {
	private TrophyManager trophyManager;
	private StickerManager stickerManager;
	private List<Pair<UUID, CustomStack>> lostSkins = new List<Pair<UUID, CustomStack>>();

	@Override
	public void onEnable() {
		listener = new TrophyManager(this);
		getServer().getPluginManager().registerEvents(listener, this);
		getLogger().info("IAAngelTrophies has been enabled!");
	}

	@Override
	public void onDisable() {
		listener = null;
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
			C.main(sender, "Reloaded {0}.", Main.getInstance().getDescription().getName());
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Error while reloading:", e);
			getLogger().severe("An error occurred while enabling AngelTrophies, disabling...");
			C.error(sender, "An error occurred while reload {0}.", Main.getInstance().getDescription().getName());
			C.error(sender, "Disabling the plugin...");
			setEnabled(false);
		}
	}

	private void loadManagers() {
		List<Sticker> stickers = new ArrayList<>();
		List<Trophy> trophies = new ArrayList<>();

		for (File file : new File(getDataFolder().getParent(), "Oraxen/items").listFiles(f -> f.getName().endsWith(".yml"))) {
			try {
				YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
				for (String key : yaml.getKeys(false)) {
					if (!yaml.isConfigurationSection(key)) {
						continue;
					}
					if (yaml.isConfigurationSection(key + ".skin")) {
						ConfigurationSection skin = yaml.getConfigurationSection(key + ".skin");
						for (String source : skin.getKeys(false)) {
							try {
								if (skin.isString(source)) {
									String target = skin.getString(source);
									if (source.toLowerCase().startsWith("minecraft_")) {
										String material = source;
										String[] split = material.split("_");
										int id = 0;
										try {
											id = Integer.parseInt(split[split.length - 1]);
											material = String.join("_", Arrays.asList(split).subList(0, split.length - 1));
										} catch (NumberFormatException ignored) {
										}
										material = material.replaceFirst("_", ":");
										stickers.add(new Sticker(Material.matchMaterial(material), id, target, key));
									} else {
										stickers.add(new Sticker(source, target, key));
									}
								}
							} catch (Exception e) {
								throw new RuntimeException("Error while parsing skin " + key + ".skin." + source, e);
							}
						}
					} else if (yaml.isConfigurationSection(key + ".trophies")) {
						try {
							ConfigurationSection t = yaml.getConfigurationSection(key + ".trophies");
							if (floor || wall) {
								trophies.add(new Trophy(key, floor, floorSmall, floorOffset, wall, wallSmall, wallOffset, floorPlaceSlab, floorRotationResolution, cGroup, cRole));
							}
						} catch (Exception e) {
							throw new RuntimeException("Error while parsing trophy " + key + ".trophies", e);
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Error while parsing config file " + file.getPath(), e);
			}
		}
		CouchUtil.buildCache(trophies);
		getLogger().info("Loaded " + skins.size() + " skins.");
		getLogger().info("Loaded " + trophies.size() + " trophies.");

		skinManager = new SkinManager(skins);
		trophyManager = new TrophyManager(trophies);
	}
}
