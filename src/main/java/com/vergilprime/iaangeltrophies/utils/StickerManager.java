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

	/**
	 * This event is called when ItemsAdder loads.
	 * This populates the sticker map with all stickers registered in ItemsAdder.
	 * @param event The event object.
	 */
	@EventHandler(priority = org.bukkit.event.EventPriority.HIGH, ignoreCancelled = true)
	public void onCustomItemsLoadedEvent(ItemsAdderLoadDataEvent event){
		// get a list of all custom items from ItemsAdder
		ArrayList<CustomStack> customItems = (ArrayList)ItemsAdder.getAllItems();
		// loop through all custom items
		for (CustomStack customItem : customItems) {
			// if the item contains the configuration section for a sticker, add it to the sticker map
			if (customItem.getConfig().getConfigurationSection("items." + customItem.getId() + ".behaviours.furniture.sticker") != null) {
				// Create a new sticker object using the customItem
				Sticker sticker = new Sticker(this, customItem);
				// Loop through all the conversions for this sticker
				sticker.getConversions().forEach((material,customStack) -> {
					// If the sticker map already contains a list of stickers for this material, add the sticker to the list
					if (stickers.containsKey(material)) {
						stickers.get(material).add(sticker);
					} else {
						// If the sticker map does not contain a list of stickers for this material, create a new list and add the sticker to it
						ArrayList<Sticker> list = new ArrayList<>();
						list.add(sticker);
						stickers.put(material, list);
					}
				});
			}
		}
	}

	/**
	 * This event is called when a player dies.
	 * This checks if the player has any stickers or skinned items in their inventory and adds them to the lostStickers map.
	 * @param event The event object.
	 */
	@EventHandler(priority = org.bukkit.event.EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		PlayerInventory inventory = player.getInventory();
		// For each item in the player's inventory
		inventory.forEach(itemStack -> {

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
