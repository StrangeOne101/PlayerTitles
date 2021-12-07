package com.strangeone101.playertitles.inventory;

import com.strangeone101.playertitles.PlayerTitlesPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryManager extends fr.minuskube.inv.InventoryManager {

    public static final InventoryManager MANAGER = new InventoryManager();

    public InventoryManager() {
        super(PlayerTitlesPlugin.getPlugin());
    }
}
