package com.vergilprime.iaangeltrophies.itemClasses;

import com.sun.tools.javac.util.Pair;
import com.vergilprime.iaangeltrophies.StickerManager;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class Sticker {
	private CustomStack customStack;
	private HashMap<Material, CustomStack> conversions = new HashMap<>();

	public Sticker(StickerManager stickerManager, CustomStack customStack) {
		String itemName = customStack.getId();
		ConfigurationSection config = customStack.getConfig().getConfigurationSection("items." + itemName);
		if(config.isList("behaviours.furniture.sticker")) {
			this.customStack = customStack;
			for(String conversion : config.getStringList("behaviours.furniture.sticker")) {
				String[] split = conversion.split(": ",1);
				Material baseItem = Material.getMaterial(split[0]);
				CustomStack becomes = CustomStack.getInstance(split[1]);
				conversions.put(baseItem, becomes);
			}
		}
		stickerManager.addSticker(this);
	}

	public HashMap<Material, CustomStack> getConversions() {
		return conversions;
	}

	public CustomStack getCustomStack() {
		return customStack;
	}

	public boolean canSticker (ItemStack stack) {
		return conversions.containsKey(stack.getType());
	}

	public CustomStack getStickeredItem (ItemStack stack) {
		if (canSticker(stack)) {
			CustomStack stickered = conversions.get(stack.getType());
			ItemMeta stickeredMeta = stickered.getItemStack().getItemMeta();
			ItemMeta stackMeta = stack.getItemMeta();
			String displayName = stackMeta.hasDisplayName() ? stackMeta.getDisplayName() : stickeredMeta.getDisplayName();
			List<String> lore = stackMeta.hasLore() ? stackMeta.getLore() : stickeredMeta.getLore();
			if(stack instanceof Damageable) {
				((Damageable) stickeredMeta).setDamage(((Damageable) stackMeta).getDamage());
			}
			if(stackMeta.hasAttributeModifiers()) {
				stickeredMeta.setAttributeModifiers(stackMeta.getAttributeModifiers());
			}
			if(stackMeta.hasEnchants()) {
				stackMeta.getEnchants().forEach((enchant, level) -> {
					stickeredMeta.addEnchant(enchant, level, true);
				});
			}

			stickered.getItemStack().setItemMeta(stickeredMeta);
			return stickered;
		}
		return null;
	}
}
