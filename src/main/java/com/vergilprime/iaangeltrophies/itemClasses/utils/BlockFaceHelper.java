package com.vergilprime.iaangeltrophies.itemClasses.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

public class BlockFaceHelper {
	// Thanks Ben26
	// https://www.spigotmc.org/threads/getting-the-blockface-of-a-targeted-block.319181/
	public static BlockFace getTargetBlockFace(Player player) {
		List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 5);
		if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
		Block targetBlock = lastTwoTargetBlocks.get(1);
		Block adjacentBlock = lastTwoTargetBlocks.get(0);
		return targetBlock.getFace(adjacentBlock);
	}
}
