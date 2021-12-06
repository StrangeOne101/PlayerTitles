package com.strangeone101.playertitles;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        if (group.equalsIgnoreCase("all")) {
            List<Title> all = new ArrayList<>();
            for (String g : groups.keySet()) all.addAll(groups.get(g));
            return all;
        }
        return groups.get(group.toLowerCase());
    }

    public static void setPlayerTitle(Player player, Title title) {
        User user = PlayerTitlesPlugin.getLuckPerms().getUserManager().getUser(player.getUniqueId());

        //Make new, updated title with the ID of the new title
        MetaNode node = MetaNode.builder("title", title.getId()).build();

        // Clear existing title
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals("title")));
        // Add the node to the user
        user.data().add(node);

        // Save!
        PlayerTitlesPlugin.getLuckPerms().getUserManager().saveUser(user);
    }

    public static String getTitleFromUser(User user) {
        String title = user.getCachedData().getMetaData().getMetaValue("title");
        return title == null ? "" : title;
    }

    public static String getPlayerTitle(Player player) {
        return getTitleFromUser(PlayerTitlesPlugin.getLuckPerms().getUserManager().getUser(player.getUniqueId()));
    }


    public static Title getTitle(String id) {
        return titles.get(id.toLowerCase());
    }

    static void removeAll() {
        titles.clear();
        groups.clear();
    }
}
