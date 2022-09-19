package com.vergilprime.iaangeltrophies.utils;

import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Pair;
import com.vergilprime.iaangeltrophies.IAAngelTrophies;
import com.vergilprime.iaangeltrophies.itemClasses.Sticker;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StickerManager implements Listener {
	private final HashMap<Material, ArrayList<Sticker>> stickers = new HashMap<>();
	private final IAAngelTrophies plugin;
	private Map<UUID, List<String>> lostStickers;
	private NamespacedKey namespacedKey;

	public StickerManager(IAAngelTrophies _plugin) {
		plugin = _plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		namespacedKey = new NamespacedKey(plugin, "iaat_sticker");
		plugin.getPersistenceManager().loadLostStickers();
	}

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

	@EventHandler(priority = org.bukkit.event.EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		PlayerInventory inventory = player.getInventory();
		inventory.forEach(itemStack -> {
			if (itemStack != null && itemStack.getType() != Material.AIR) {
				if (stickers.containsKey(itemStack.getType())) {
					for (Sticker sticker : stickers.get(itemStack.getType())) {
						if (sticker.canSticker(itemStack)) {
							try {
								CustomStack customStack = sticker.getStickeredItem(itemStack);
								lostSkins.add(new Pair<>(player.getUniqueId(), customStack));
							} catch (Sticker.NoConversionException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
	}

	public HashMap<Material, ArrayList<Sticker>> getStickers() {
		return stickers;
	}

	public ArrayList<Sticker> getStickersByMaterial(Material material) {
		return stickers.get(material);
	}

	public void registerSticker(Sticker sticker) {
		for(Material material : sticker.getConversions().keySet()) {
			if (stickers.containsKey(material)) {
				stickers.get(material).add(sticker);
			} else {
				stickers.put(material, new ArrayList<Sticker>());
				stickers.get(material).add(sticker);
			}
		}
	}

	public void addLostSticker(UUID uuid, String sticker) {
		if (lostStickers.containsKey(uuid)) {
			lostStickers.get(uuid).append(sticker);
		} else {
			lostStickers.put(uuid, List.of(sticker));
		}
	}

	public void recoverLostStickers(Player player){
		UUID uuid = player.getUniqueId();
		if (lostStickers.containsKey(uuid)) {
			List<String> playersLostStickers = lostStickers.get(uuid);
			for (String stickerId : playersLostStickers) {
				CustomStack customStack = CustomStack.getInstance(stickerId);
				if (customStack != null) {
					// add sticker items to player's inventory
					HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(customStack.getItemStack());
					// drop items that player couldn't hold
					leftOver.forEach((integer, itemStack) -> {
						player.getWorld().dropItem(player.getLocation(), itemStack);
					});
				}
			}
			lostStickers.remove(uuid);
		}
	}
}
