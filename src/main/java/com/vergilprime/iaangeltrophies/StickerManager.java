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

	@EventHandler(ignoreCancelled = true, priority = org.bukkit.event.EventPriority.HIGH){
		public void onCustomItemsLoadedEvent(ItemsAdderLoadDataEvent event){
			ArrayList<CustomStack> customItems =  ItemsAdder.getAllItems();

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
