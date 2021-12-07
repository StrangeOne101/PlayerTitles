package com.strangeone101.playertitles.inventory;

import com.strangeone101.playertitles.PlayerTitles;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TitleMenu implements InventoryProvider {

    public TitleMenu(Player player) {
        SmartInventory inv = SmartInventory.builder()
                .title(InventoryConfig.getLanguageConfig().getString("menu.title"))
                .size(InventoryConfig.getInventoryWidth() + 2, InventoryConfig.getInventoryHeight() + 2)
                .provider(this)
                .manager(InventoryManager.MANAGER)
                .closeable(true)
                .id("playertitles").build();
        inv.open(player);
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(InventoryConfig.getBorder()));
        for (String group : InventoryConfig.getTitleGroups().keySet()) {
            ItemStack stack = InventoryConfig.getTitleGroups().get(group);
            int x = InventoryConfig.getTitleGroupPositions().get(group)[0];
            int y = InventoryConfig.getTitleGroupPositions().get(group)[1];

            if (InventoryConfig.hideIfEmpty(group)) { //If it should hide when the user has no titles in it
                if (PlayerTitles.getGroup(group).size() == 0) continue; //Don't place the item
            }

            contents.set(y, x, ClickableItem.of(stack, e -> {
                TitleGroupMenu newMenu = new TitleGroupMenu(group, contents);
                newMenu.getInventory().open(player);
            }));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
