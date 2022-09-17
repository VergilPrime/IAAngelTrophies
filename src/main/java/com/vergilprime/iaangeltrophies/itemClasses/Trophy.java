package com.vergilprime.iaangeltrophies.itemClasses;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.configuration.ConfigurationSection;

public class Trophy {
	private Integer ceiling;
	private Integer wall;
	private Integer floor;
	private CustomStack customStack;

	public Trophy(CustomStack stack) {
		customStack = stack;
		String itemName = customStack.getId();
		ConfigurationSection config = customStack.getConfig().getConfigurationSection("items." + itemName);
		ceiling = config.getInt("behaviours.furniture.trophy.ceiling") > 0 ? config.getInt("behaviours.furniture.trophy.ceiling") : null;
		wall = config.getInt("behaviours.furniture.trophy.wall") > 0 ? config.getInt("behaviours.furniture.trophy.wall") : null;
		floor = config.getInt("behaviours.furniture.trophy.floor") > 0 ? config.getInt("behaviours.furniture.trophy.floor") : null;
	}

	public Integer getCeiling() {
		return ceiling;
	}

	public Integer getWall() {
		return wall;
	}

	public Integer getFloor() {
		return floor;
	}

	public CustomStack getCustomStack() {
		return customStack;
	}
}
