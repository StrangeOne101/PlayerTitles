package com.strangeone101.playertitles;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
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
    public boolean persist() {
        return true;
    }

    @Override
    public List<String> getPlaceholders() {
        return Arrays.asList("name", "description", "rarity");
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        if (params.equalsIgnoreCase("name") || params.equalsIgnoreCase("description") ||
                params.equalsIgnoreCase("rarity")) {
            UserManager userManager = PlayerTitlesPlugin.getLuckPerms().getUserManager();
            CompletableFuture<User> userFuture = userManager.loadUser(p.getUniqueId());
            User user = userFuture.join();

            Title title = PlayerTitles.getTitle(PlayerTitles.getTitleFromUser(user));
            if (title == null) return "";

            if (params.equalsIgnoreCase("name")) {
                return title.getName();
            } else if (params.equalsIgnoreCase("description")) {
                return Config.MAX_DESC_LENGTH < 1 ? title.getDescription() : //if its -1, dont split into multiple lines
                        PlayerTitlesPlugin.lengthSplit(title.getDescription(), Config.MAX_DESC_LENGTH);
            } else if (params.equalsIgnoreCase("rarity")) {
                return PlayerTitles.getFancyRarity(title.getRarity());
            }
        }

        return null;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (params.equalsIgnoreCase("name") || params.equalsIgnoreCase("description") ||
                params.equalsIgnoreCase("rarity")) {
            User user = PlayerTitlesPlugin.getLuckPerms().getUserManager().getUser(p.getUniqueId());

            Title title = PlayerTitles.getTitle(PlayerTitles.getTitleFromUser(user));
            if (title == null) return "";

            if (params.equalsIgnoreCase("name")) {
                return title.getName();
            } else if (params.equalsIgnoreCase("description")) {
                return Config.MAX_DESC_LENGTH < 1 ? title.getDescription() : //if its -1, dont split into multiple lines
                        PlayerTitlesPlugin.lengthSplit(title.getDescription(), Config.MAX_DESC_LENGTH);
            } else if (params.equalsIgnoreCase("rarity")) {
                int intRarity = Math.max(Math.min(title.getRarity(), Config.RARITIES.size()), 1);
                return PlayerTitlesPlugin.color(Config.RARITIES.get(intRarity - 1));
            }
        }

        return null;
    }
}
