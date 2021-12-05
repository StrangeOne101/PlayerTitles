package com.strangeone101.playertitles.placeholders;


import com.strangeone101.playertitles.Config;
import com.strangeone101.playertitles.Title;

public class TitleRarityPlaceholder extends PlaceholderBase {

    @Override
    public String getIdentifier() {
        return "title_rarity";
    }

    @Override
    public String get(Title title) {
        int rarity = Math.max(Math.min(title.getRarity(), Config.RARITIES.size()), 1);
        return Config.RARITIES.get(rarity - 1);
    }
}
