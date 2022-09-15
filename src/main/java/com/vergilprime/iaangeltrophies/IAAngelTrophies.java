package com.vergilprime.iaangeltrophies;

import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Pair;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

public final class IAAngelTrophies extends JavaPlugin {
	private TrophyFurnitureListener listener;
	private List<Pair<UUID, CustomStack>> lostSkins = new List<Pair<UUID, CustomStack>>();
	@Override
	public void onEnable() {
		listener = new TrophyFurnitureListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
		getLogger().info("IAAngelTrophies has been enabled!");
	}

	@Override
	public void onDisable() {
		listener = null;
		getLogger().info("IAAngelTrophies has been disabled!");
	}


}
