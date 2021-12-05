package com.strangeone101.playertitles;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerTitles {

    private static Map<String, Title> titles = new HashMap<>();
    private static Map<String, List<Title>> groups = new HashMap<>();

    public static void registerTitle(Title title) {
        titles.put(title.getId(), title);

        if (title.getGroup() != null) {
            if (!groups.containsKey(title.getGroup())) {
                groups.put(title.getGroup(), new ArrayList<>());

                Permission perm = new Permission("playertitles.group." + title.getGroup());
                Bukkit.getPluginManager().addPermission(perm);
            }
            groups.get(title.getGroup()).add(title);
        }

        Permission perm = new Permission("playertitles.title." + title.getId());
        Bukkit.getPluginManager().addPermission(perm);
    }

    public static List<Title> getGroup(String group) {
        return groups.get(group.toLowerCase());
    }

    public static Title getTitle(String id) {
        return titles.get(id.toLowerCase());
    }

    static void removeAll() {
        titles.clear();
        groups.clear();
    }
}
