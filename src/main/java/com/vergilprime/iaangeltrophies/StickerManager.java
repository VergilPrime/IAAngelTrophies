package com.vergilprime.iaangeltrophies;

import com.vergilprime.iaangeltrophies.itemClasses.Sticker;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class StickerManager implements Listener {
	private HashMap<Material, ArrayList<Sticker>> stickers = new HashMap<>();

	@EventHandler(priority = org.bukkit.event.EventPriority.HIGH, ignoreCancelled = true)
	public void onCustomItemsLoadedEvent(ItemsAdderLoadDataEvent event){
		ArrayList<CustomStack> customItems = (ArrayList)ItemsAdder.getAllItems();
		for (CustomStack customItem : customItems) {
			if (customItem.getConfig().getConfigurationSection("items." + customItem.getId() + ".behaviours.furniture.sticker") != null) {
				Sticker sticker = new Sticker(this, customItem);
				sticker.getConversions().forEach((material,customStack) -> {
					if (stickers.containsKey(material)) {
						stickers.get(material).add(sticker);
					} else {
						ArrayList<Sticker> list = new ArrayList<>();
						list.add(sticker);
						stickers.put(material, list);
					}
				});
				if (stickers.containsKey(sticker.getMaterial())) {
					stickers.get(sticker.getMaterial()).add(sticker);
				} else {
					ArrayList<Sticker> stickerList = new ArrayList<>();
					stickerList.add(sticker);
					stickers.put(sticker.getMaterial(), stickerList);
				}
			}
		}
	}

	public HashMap<Material, ArrayList<Sticker>> getStickers() {
		return stickers;
	}

	public ArrayList<Sticker> getStickersByMaterial(Material material) {
		return stickers.get(material);
	}

	public void addSticker(Sticker sticker) {
		for(Material material : sticker.getConversions().keySet()) {
			if (stickers.containsKey(material)) {
				stickers.get(material).add(sticker);
			} else {
				stickers.put(material, new ArrayList<Sticker>());
				stickers.get(material).add(sticker);
			}
		}
	}
}
