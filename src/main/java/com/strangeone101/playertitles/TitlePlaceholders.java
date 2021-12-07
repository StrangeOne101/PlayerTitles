package com.strangeone101.playertitles;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class TitlePlaceholders extends PlaceholderExpansion {

    @Getter
    public static TitlePlaceholders TITLE_PLACEHOLDERS;

    public TitlePlaceholders() {
        TITLE_PLACEHOLDERS = this;
    }

    @Override
    public String getAuthor() {
        return "StrangeOne101";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getIdentifier() {
        return "playertitles";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        if (params.equalsIgnoreCase("title_name") || params.equalsIgnoreCase("title_description") ||
                params.equalsIgnoreCase("title_rarity")) {
            UserManager userManager = PlayerTitlesPlugin.getLuckPerms().getUserManager();
            CompletableFuture<User> userFuture = userManager.loadUser(p.getUniqueId());
            User user = userFuture.join();

            Title title = PlayerTitles.getTitle(PlayerTitles.getTitleFromUser(user));
            if (title == null) return "";

            if (params.equalsIgnoreCase("title_name")) {
                return title.getName();
            } else if (params.equalsIgnoreCase("title_description")) {
                return Config.MAX_DESC_LENGTH < 1 ? title.getDescription() : //if its -1, dont split into multiple lines
                        PlayerTitlesPlugin.lengthSplit(title.getDescription(), Config.MAX_DESC_LENGTH);
            } else if (params.equalsIgnoreCase("title_rarity")) {
                return PlayerTitles.getFancyRarity(title.getRarity());
            }
        }

        return null;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (params.equalsIgnoreCase("title_name") || params.equalsIgnoreCase("title_description") ||
                params.equalsIgnoreCase("title_rarity")) {
            User user = PlayerTitlesPlugin.getLuckPerms().getUserManager().getUser(p.getUniqueId());

            Title title = PlayerTitles.getTitle(PlayerTitles.getTitleFromUser(user));
            if (title == null) return "";

            if (params.equalsIgnoreCase("title_name")) {
                return title.getName();
            } else if (params.equalsIgnoreCase("title_description")) {
                return Config.MAX_DESC_LENGTH < 1 ? title.getDescription() : //if its -1, dont split into multiple lines
                        PlayerTitlesPlugin.lengthSplit(title.getDescription(), Config.MAX_DESC_LENGTH);
            } else if (params.equalsIgnoreCase("title_rarity")) {
                int intRarity = Math.max(Math.min(title.getRarity(), Config.RARITIES.size()), 1);
                return PlayerTitlesPlugin.color(Config.RARITIES.get(intRarity - 1));
            }
        }

        return null;
    }
}
