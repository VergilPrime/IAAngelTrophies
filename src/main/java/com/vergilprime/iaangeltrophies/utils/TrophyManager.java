package com.vergilprime.iaangeltrophies.utils;

import com.vergilprime.iaangeltrophies.IAAngelTrophies;
import com.vergilprime.iaangeltrophies.itemClasses.Trophy;
import com.vergilprime.iaangeltrophies.itemClasses.utils.BlockFaceHelper;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurniturePlaceEvent;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TrophyManager implements Listener {
	private final IAAngelTrophies plugin;

	List<Utils.Pair<FurniturePlaceEvent,Integer>> eventModelIdPairs = new ArrayList<>();

	public TrophyManager(IAAngelTrophies plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * This event is called when a furniture is placed.
	 * If the furniture is a trophy, the event and model id are stored for the FurniturePlaceSuccessEvent
	 * @param event The event including a reference to the
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onFurniturePlaceEvent(FurniturePlaceEvent event){
		// Get item in hand
		CustomStack item = CustomStack.byItemStack(event.getPlayer().getInventory().getItemInMainHand());
		// Get id for configuration search
		String itemName = item.getId();
		// Get configuration section for this item
		ConfigurationSection config = item.getConfig().getConfigurationSection("items." + itemName);
		// Check if this item is a trophy
		if (config.isConfigurationSection("behaviours.furniture.trophy")) {
			Player player = event.getPlayer();
			// Trophies can only be placed while sneaking as they are typically skinned tools and have a right click function.
			if(!player.isSneaking()){
				event.setCancelled(true);
			}else{
				// Get the trophy which will find configured model IDs for ceiling, wall and floor placement.
				Trophy trophy =  new Trophy(item);
				// Get the block face the player is facing
				BlockFace face = BlockFaceHelper.getTargetBlockFace(player);
				// If the face is down and the ceiling model is configured, store the event and model ID for the success event.
				if(face == BlockFace.DOWN && trophy.getCeiling() != null){
					eventModelIdPairs.add(new Utils.Pair<FurniturePlaceEvent,Integer>(event,trophy.getCeiling()));
				}else if (face == BlockFace.UP && trophy.getFloor() != null){
					eventModelIdPairs.add(new Utils.Pair<FurniturePlaceEvent,Integer>(event,trophy.getFloor()));
				}else if(trophy.getWall() != null){
					eventModelIdPairs.add(new Utils.Pair<FurniturePlaceEvent,Integer>(event,trophy.getWall()));
				}
			}
		}
	}


	/**
	 * This event is called after the furniture has been placed and the entity has been spawned.
	 * If the FurniturePlaceEvent has been stored in the eventModelIdPairs list, the model ID will be set to the configured value.
	 * @param event The event including a reference to the placed CustomFurniture.
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onFurniturePlaceSuccessEvent(FurniturePlaceSuccessEvent successEvent){
		eventModelIdPairs.forEach(eventModelIdPair -> {
			if(
				// Check if the success event is for the same player and ItemsAdder item as the place event
				eventModelIdPair.first.equals(successEvent.getPlayer()) &&
				eventModelIdPair.first.getNamespacedID().equals(successEvent.getNamespacedID())
			){
				// getArmorStand can return an item frame or an armor stand, so we need to handle either.
				Entity entity = successEvent.getFurniture().getArmorstand();
				if(entity instanceof ItemFrame){
					// If the entity is an item frame, get the item inside and set the model ID.
					ItemFrame frame = (ItemFrame) entity;
					ItemStack itemStack = frame.getItem();
					ItemMeta meta = itemStack.getItemMeta();
					meta.setCustomModelData(eventModelIdPair.second);
					itemStack.setItemMeta(meta);
					frame.setItem(itemStack);
				}else{
					// If the entity is an armor stand, get the item in the helmet slot and set the model ID.
					ArmorStand stand = (ArmorStand) entity;
					ItemStack itemStack = stand.getEquipment().getHelmet();
					ItemMeta meta = itemStack.getItemMeta();
					meta.setCustomModelData(eventModelIdPair.second);
					stand.getEquipment().setHelmet(itemStack);
				}
				// Remove the event from the list
				eventModelIdPairs.remove(eventModelIdPair);
			}
		});
	}
}
