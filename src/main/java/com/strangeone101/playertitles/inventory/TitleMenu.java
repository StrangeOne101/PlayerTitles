package com.strangeone101.playertitles.inventory;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;

public class TitleMenu implements InventoryProvider {


    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(InventoryConfig.getBorder()));
        for (String groups : InventoryConfig.getTitleGroups().keySet()) {

        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
