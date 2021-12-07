package com.strangeone101.playertitles.inventory;

import com.strangeone101.playertitles.Config;
import com.strangeone101.playertitles.PlayerTitles;
import com.strangeone101.playertitles.Title;
import com.strangeone101.playertitles.placeholders.TitleRarityPlaceholder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TitleGroupMenu implements InventoryProvider {

    @Getter
    private String group;

    @Getter
    private SmartInventory inventory;

    @Getter
    private SmartInventory parent;

    public TitleGroupMenu(String group, SmartInventory parent) {
        this.group = group;
        this.parent = parent;

        inventory = SmartInventory.builder()
                .title(InventoryConfig.getLanguageConfig().getString("menu.title-group")
                        .replace("%group%", group))
                .closeable(true)
                .id("titleGroupMenu_" + group)
                .provider(this)
                .parent(parent)
                .size(6, 9)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(InventoryConfig.getBorder()));
        Pagination pagination = contents.pagination();
        pagination.setItemsPerPage(4 * 7);

        int rarity = contents.property("rarity", 0);

        setTitles(rarity, contents, player);

        int page = pagination.getPage();
        int max = pagination.last().getPage();

        if (!pagination.isFirst()) {
            contents.set(5, 1, ClickableItem.of(arrow(true, page, max), e -> {
                inventory.open(player, pagination.previous().getPage());
            }));
        }

        if (!pagination.isLast()) {
            contents.set(5, 7, ClickableItem.of(arrow(false, page, max), e -> {
                inventory.open(player, pagination.next().getPage());
            }));
        }

        contents.set(5, 5, ClickableItem.of(filter(rarity), e -> {
            int temp = rarity + (e.getClick().isRightClick() ? -1 : 1) % Config.RARITIES.size() + 1; //+1 so rarity 0 still works
            contents.setProperty("rarity", temp);
            setTitles(temp, contents, player); //Update the displayed titles
        }));
    }

    public ItemStack arrow(boolean left, int page, int max) {
        ItemStack stack = InventoryConfig.getArrow();
        ItemMeta meta = stack.getItemMeta();
        String path = "menu.icon.page-" + (left ? "left" : "right");
        meta.setDisplayName(langString(path + ".title")
                .replace("%page%", page + "")
                .replace("max%", max + ""));
        List<String> lore = new ArrayList<>();
        lore.add("");
        for (String s : langString(path + ".instructions").split("\n")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack filter(int currentRarity) {
        ItemStack stack = InventoryConfig.getFilter();
        ItemMeta meta = stack.getItemMeta();
        String path = "menu.icon.filter";
        meta.setDisplayName(langString(path + ".title"));
        String all = langString(path + ".all");
        String selected = langString(path + ".selected-rarity");
        String not = langString(path + ".non-selected-rarity");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&',
                (currentRarity == 0 ? selected.replace("%rarity%", all) : not.replace("%rarity%", all))));
        for (int i = 1; i <= Config.RARITIES.size(); i++) {
            String rarity = TitleRarityPlaceholder.getRarity(i);
            String temp = not;
            if (currentRarity == i) temp = selected;
            temp.replace("%rarity%", rarity);
            lore.add(temp);
        }
        lore.add("");
        for (String s : langString(path + ".instructions").split("\n")) {
            lore.add(s);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    private String langString(String path) {
        return ChatColor.translateAlternateColorCodes('&', InventoryConfig.getLanguageConfig().getString(path)).replaceAll("\\\\n", "\n");
    }

    public void setTitles(int rarity, InventoryContents contents, Player player) {
        Pagination pagination = contents.pagination();
        List<Title> titles = PlayerTitles.getGroup(group);
        Comparator<Title> comparator = Comparator.comparingInt(Title::getRarity).thenComparing(o -> o.getSortingName());
        titles.sort(comparator);

        String currentTitle = PlayerTitles.getPlayerTitle(player);

        List<ClickableItem> items = new ArrayList<>();

        for (Title title : titles) {
            if (rarity == 0 || rarity == title.getRarity()) { //If the ALL rarity is selected or the rarities match
                if (title.canUse(player)) {
                    ItemStack stack = new ItemStack(Material.NAME_TAG);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(title.getName());
                    List<String> lore = new ArrayList<>();

                    for (String s : InventoryConfig.getLanguageConfig().getStringList("menu.player-title-lore")) {
                        lore.add(langString(s.replace("%rarity%", TitleRarityPlaceholder.getRarity(title.getRarity()))
                                .replace("%description%", title.getDescription())));
                    }
                    if (currentTitle.equals(title.getId())) {
                        lore.add("");
                        lore.add(langString("menu.player-title-bound"));
                    }
                    meta.setLore(lore);
                    stack.setItemMeta(meta);

                    items.add(ClickableItem.of(stack, e -> {
                        PlayerTitles.setPlayerTitle(player, title); //Update the title to this one
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2F, 2F);
                        setTitles(rarity, contents, player); //Update the items to show the latest equipped one
                    }));
                }
            }
        }

       pagination.setItems(items.toArray(new ClickableItem[items.size()]));
    }



    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
