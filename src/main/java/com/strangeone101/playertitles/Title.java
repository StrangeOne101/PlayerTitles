package com.strangeone101.playertitles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.permissions.Permissible;

public class Title {

    public Title(String id, String name, String description, int rarity, String group) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.group = group;
        this.sortingName = name.toLowerCase().replaceAll("[&\u00A7][0-9a-fA-F]", "");
    }

    @Getter
    public String id;

    @Getter
    private String name;

    @Getter
    private String description;

    @Getter
    private int rarity;

    @Getter
    private String group;

    @Getter
    private String sortingName;

    public boolean canUse(Permissible permissible) {
        return permissible.hasPermission("playertitles.title." + id) ||
                (group != null && permissible.hasPermission("playertitles.group." + group));
    }
}
