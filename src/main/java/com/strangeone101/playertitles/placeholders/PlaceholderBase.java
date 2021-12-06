package com.strangeone101.playertitles.placeholders;

import com.strangeone101.playertitles.PlayerTitles;
import com.strangeone101.playertitles.PlayerTitlesPlugin;
import com.strangeone101.playertitles.Title;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public abstract class PlaceholderBase extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "StrangeOne101";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        UserManager userManager = PlayerTitlesPlugin.getLuckPerms().getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(p.getUniqueId());
        User user = userFuture.join();

        Title title = PlayerTitles.getTitle(PlayerTitles.getTitleFromUser(user));
        if (title == null) return "";

        return get(title);
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        User user = PlayerTitlesPlugin.getLuckPerms().getUserManager().getUser(p.getUniqueId());

        Title title = PlayerTitles.getTitle(PlayerTitles.getTitleFromUser(user));
        if (title == null) return "";

        return get(title);
    }

    public abstract String get(Title title);
}
