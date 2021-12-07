package com.strangeone101.playertitles;

import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlayerTitles {

    private static Map<String, Title> titles = new HashMap<>();
    private static Map<String, List<Title>> groups = new HashMap<>();

    private static List<Permission> addedPerms = new ArrayList<>();

    public static void registerTitle(Title title) {
        titles.put(title.getId(), title);

        for (String group : title.getGroups()) {
            if (!groups.containsKey(group)) {
                groups.put(group, new ArrayList<>());

                Permission perm = new Permission("playertitles.group." + group);
                Bukkit.getPluginManager().addPermission(perm);
                addedPerms.add(perm);
            }
            groups.get(group).add(title);
        }

        Permission perm = new Permission("playertitles.title." + title.getId());
        Bukkit.getPluginManager().addPermission(perm);
    }

    public static List<Title> getGroup(String group) {
        if (group.equalsIgnoreCase("all")) { //If "all", we provide everything
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

    public static CompletableFuture<Boolean> giveTitle(OfflinePlayer player, Title title) {
        UserManager userManager = PlayerTitlesPlugin.getLuckPerms().getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(player.getUniqueId());
        PermissionNode node = PermissionNode.builder().permission("playertitles.title." + title.getId()).value(true).build();

        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        userFuture.thenAccept(user -> { //Once the user is loaded,
            if (!user.data().contains(node, NodeEqualityPredicate.EXACT).asBoolean()) {
                user.data().add(node); //Add the permission node to them
                PlayerTitlesPlugin.getLuckPerms().getUserManager().saveUser(user); //Save the user
                completableFuture.complete(true);
            } else
                completableFuture.complete(false);
        });

        return completableFuture;
    }

    public static CompletableFuture<Boolean> giveGroup(OfflinePlayer player, String group) {
        UserManager userManager = PlayerTitlesPlugin.getLuckPerms().getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(player.getUniqueId());
        PermissionNode node = PermissionNode.builder().permission("playertitles.group." + group).value(true).build();

        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        userFuture.thenAccept(user -> { //Once the user is loaded,
            if (!user.data().contains(node, NodeEqualityPredicate.EXACT).asBoolean()) {
                user.data().add(node); //Add the permission node to them
                PlayerTitlesPlugin.getLuckPerms().getUserManager().saveUser(user); //Save the user
                completableFuture.complete(true);
            } else
                completableFuture.complete(false);
        });

        return completableFuture;
    }

    static void removeAll() {
        titles.clear();
        groups.clear();

        for (Permission perm : addedPerms) {
            Bukkit.getPluginManager().removePermission(perm);
        }
    }
}
