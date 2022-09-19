package com.vergilprime.iaangeltrophies.utils;

import com.google.gson.*;
import com.vergilprime.iaangeltrophies.IAAngelTrophies;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

public class PersistenceManager {
	private final IAAngelTrophies plugin;
	private File lostStickersFile;
	public PersistenceManager(IAAngelTrophies _plugin) {
		plugin = _plugin;
		plugin.getDataFolder().mkdirs();
		lostStickersFile = new File(plugin.getDataFolder(), "lostStickers.json");

	}

	public void loadLostStickers() {
		if(lostStickersFile.exists()){
			try {
				Scanner scanner = new Scanner(lostStickersFile);
				while (scanner.hasNextLine()){
					String line = scanner.nextLine();
					if (line.matches("[0-9a-fA-F-]{36}: [^#]+ #.*")) {
						String[] args = line.split(" ");
						UUID uuid = UUID.fromString(args[0].substring(0, 36));
						List<String> lostStickers = new ArrayList<>(Arrays.asList(args[1].split(",")));
						lostStickers.forEach(sticker -> plugin.getStickerManager().addLostSticker(uuid, sticker));
					}
				}
			}
		}
	}
}