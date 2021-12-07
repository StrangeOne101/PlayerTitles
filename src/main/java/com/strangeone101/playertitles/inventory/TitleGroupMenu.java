package com.strangeone101.playertitles.inventory;

import com.strangeone101.playertitles.Config;
import com.strangeone101.playertitles.PlayerTitles;
import com.strangeone101.playertitles.PlayerTitlesPlugin;
import com.strangeone101.playertitles.Title;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TitleGroupMenu implements InventoryProvider {

    @Getter
    private String group;

    @Getter
    private SmartInventory inventory;

    @Getter
    private InventoryContents parent;

    public TitleGroupMenu(String group, InventoryContents parent) {
        this.group = group;
        this.parent = parent;

        inventory = SmartInventory.builder()
                .title(InventoryConfig.getLanguageConfig().getString("menu.title-group")
                        .replace("%group%", group))
                .closeable(true)
                .id("titleGroupMenu_" + group)
                .provider(this)
                .manager(InventoryManager.MANAGER)
                .parent(parent.inventory())
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

        addFilter(contents, player);

        contents.set(5, 3, ClickableItem.of(order(), e -> {
            contents.setProperty("reverse", !contents.property("reverse", false));
            setTitles(contents.property("rarity", 0), contents, player); //Update the items
        }));

        contents.set(5, 4, ClickableItem.of(back(), e -> {
            if (parent != null) parent.inventory().open(player);
            else new TitleMenu(player);
        }));
    }

    public void addFilter(InventoryContents contents, Player player) {
        int rarity = contents.property("rarity", 0);
        contents.set(5, 5, ClickableItem.of(filter(rarity), e -> {
            int rarity2 = contents.property("rarity", 0); //Read existing
            int temp = (rarity2 + (e.getClick().isRightClick() ? -1 : 1));
            if (temp < 0) temp += Config.RARITIES.size();
            temp = temp % (Config.RARITIES.size() + 1); //+1 so rarity 0 still works

            contents.setProperty("rarity", temp);
            setTitles(temp, contents, player); //Update the displayed titles
            addFilter(contents, player);
        }));
    }

    public void addArrows(InventoryContents contents, Player player) {
        int page = contents.pagination().getPage();
        int max = (contents.pagination().getPageItems().length / (4 * 7)) + 2;

        ItemStack redEmpty = InventoryConfig.getBorder().clone();
        redEmpty.setType(Material.RED_STAINED_GLASS_PANE);

        if (page != 0) {
            contents.set(5, 1, ClickableItem.of(arrow(true, page, max), e -> {
                inventory.open(player, contents.pagination().getPage() - 1);
                //addArrows(contents, player);
            }));
        } else contents.set(5, 1, ClickableItem.empty(redEmpty));

        if (page != max) {
            contents.set(5, 7, ClickableItem.of(arrow(false, page, max), e -> {
                inventory.open(player, contents.pagination().getPage() + 1);
                //addArrows(contents, player);
            }));
        } else contents.set(5, 7, ClickableItem.empty(redEmpty));
    }

    public ItemStack arrow(boolean left, int page, int max) {
        ItemStack stack = InventoryConfig.getArrow();
        ItemMeta meta = stack.getItemMeta();
        String path = "menu.icons.page-" + (left ? "left" : "right");
        meta.setDisplayName(langString(path + ".title")
                .replace("%page%", (page + 1) + "")
                .replace("%max%", (max + 1) + ""));
        List<String> lore = new ArrayList<>();
        lore.add("");
        for (String s : langString(path + ".instructions").split("\n")) {
            lore.add(PlayerTitlesPlugin.color(s));
        }
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack back() {
        ItemStack stack = InventoryConfig.getBack();
        ItemMeta meta = stack.getItemMeta();
        String path = "menu.icons.back";
        meta.setDisplayName(langString(path + ".title"));
        List<String> lore = new ArrayList<>();
        lore.add("");
        for (String s : langString(path + ".instructions").split("\n")) {
            lore.add(PlayerTitlesPlugin.color(s));
        }
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack order() {
        ItemStack stack = InventoryConfig.getOrder();
        ItemMeta meta = stack.getItemMeta();
        String path = "menu.icons.order";
        meta.setDisplayName(langString(path + ".title"));
        List<String> lore = new ArrayList<>();
        lore.add("");
        for (String s : langString(path + ".instructions").split("\n")) {
            lore.add(PlayerTitlesPlugin.color(s));
        }
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack filter(int currentRarity) {
        ItemStack stack = InventoryConfig.getFilter();
        ItemMeta meta = stack.getItemMeta();
        String path = "menu.icons.filter";
        meta.setDisplayName(langString(path + ".title"));
        String all = langString(path + ".all");
        String selected = langString(path + ".selected-rarity");
        String not = langString(path + ".non-selected-rarity");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(PlayerTitlesPlugin.color(
                (currentRarity == 0 ? selected.replace("%rarity%", all) : not.replace("%rarity%", all))));
        for (int i = 1; i <= Config.RARITIES.size(); i++) {
            String rarity = PlayerTitles.getFancyRarity(i);
            String temp = not;
            if (currentRarity == i) temp = selected;
            temp = temp.replace("%rarity%", rarity);
            lore.add(temp);
        }
        lore.add("");
        lore.addAll(Arrays.asList(langString(path + ".instructions").split("\n")));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private String langString(String path) {
        return PlayerTitlesPlugin.color(InventoryConfig.getLanguageConfig().getString(path, "path-not-found")).replaceAll("\\\\n", "\n");
    }

    public void setTitles(int rarity, InventoryContents contents, Player player) {
        Pagination pagination = contents.pagination();
        boolean reverse = contents.property("reverse", false);
        List<Title> titles = PlayerTitles.getGroup(group);
        Comparator<Title> comparator = Comparator.comparingInt(Title::getRarity).thenComparing(Title::getSortingName);
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
                        lore.add(PlayerTitlesPlugin.color(s.replace("%rarity%", PlayerTitles.getFancyRarity(title.getRarity()))
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

        if (reverse) Collections.reverse(items);

        pagination.setItems(items.toArray(new ClickableItem[items.size()]));
        SlotIterator slotIterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1);
        for (int i = 0; i < 8; i++) {
            slotIterator.blacklist(0, i);
            slotIterator.blacklist(5, i);

            if (i < 5) {
                slotIterator.blacklist(i + 1, 0);
                slotIterator.blacklist(i + 1, 8);
            }
        }
        pagination.addToIterator(slotIterator);

        addArrows(contents, player); //Add arrows if we need them
    }



    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
