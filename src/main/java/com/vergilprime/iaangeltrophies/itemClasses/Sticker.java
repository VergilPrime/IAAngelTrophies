package com.vergilprime.iaangeltrophies.itemClasses;

import com.vergilprime.iaangeltrophies.utils.StickerManager;
import com.vergilprime.iaangeltrophies.exceptions.NoConversionException;
import dev.lone.itemsadder.api.CustomStack;
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
		stickerManager.registerSticker(this);
	}

	public HashMap<Material, CustomStack> getConversions() {
		return conversions;
	}

	public CustomStack getCustomStack() {
		return customStack;
	}

	/**
	 * Can this sticker be applied to this item?
	 * @param stack
	 * @return
	 */
	public boolean canSticker (ItemStack stack) {
		return conversions.containsKey(stack.getType());
	}

	/**
	 * Returns an ItemsAdder CustomStack which results from stickering the given itemstack with properties copied from the itemstack.
	 * @param stack
	 * @return
	 * @throws NoConversionException
	 */
	public CustomStack getStickeredItem (ItemStack stack) throws NoConversionException {
		if (canSticker(stack)) {
			CustomStack stickered = getConversion(stack);
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
		}else{
			throw new NoConversionException("This sticker does not have a conversion for " + stack.getType().name());
		}
	}

	/**
	 * Returns a brand new ItemsAdder CustomStack which results from stickering the given itemstack.
	 * @param itemStack
	 * @return
	 * @throws NoConversionException
	 */
	public CustomStack getConversion(ItemStack itemStack) throws NoConversionException {
		return getConversion(itemStack.getType());
	}

	/**
	 * Returns a brand new ItemsAdder CustomStack which results from stickering the given material.
	 * @param type
	 * @return
	 * @throws NoConversionException
	 */
	public CustomStack getConversion(Material type) throws NoConversionException {
		if(conversions.containsKey(type)) {
			return(conversions.get(type));
		}else{
			throw new NoConversionException("This sticker does not have a conversion for " + type.name());
		}
	}
}
