package com.strangeone101.playertitles.placeholders;


import com.strangeone101.playertitles.Config;
import com.strangeone101.playertitles.Title;
import org.bukkit.ChatColor;

public class TitleRarityPlaceholder extends PlaceholderBase {

    @Override
    public String getIdentifier() {
        return "title_rarity";
    }

    @Override
    public String get(Title title) {
        return getRarity(title.getRarity());
    }

    public static String getRarity(int rarity) {
        int intRarity = Math.max(Math.min(rarity, Config.RARITIES.size()), 1);
        return ChatColor.translateAlternateColorCodes('&', Config.RARITIES.get(intRarity - 1));
    }
}
