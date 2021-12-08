package com.strangeone101.playertitles;

import lombok.Getter;
import org.bukkit.permissions.Permissible;

public class Title {

    public Title(String id, String name, String description, float rarity, String... groups) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.groups = groups;
        this.sortingName = name.toLowerCase().replaceAll("[&\u00A7]([0-9a-fA-F]|#[0-9a-fA-F]{6})", "");
    }

    @Getter
    public String id;

    @Getter
    private String name;

    @Getter
    private String description;

    @Getter
    private float rarity;

    @Getter
    private String[] groups;

    @Getter
    private String sortingName;

    public boolean canUse(Permissible permissible) {
        for (String group : groups) {
            if (permissible.hasPermission("playertitles.group." + group)) return true;
        }
        return permissible.hasPermission("playertitles.title." + id);
    }
}
