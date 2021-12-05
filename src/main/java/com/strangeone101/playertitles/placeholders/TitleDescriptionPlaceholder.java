package com.strangeone101.playertitles.placeholders;


import com.strangeone101.playertitles.Config;
import com.strangeone101.playertitles.PlayerTitlesPlugin;
import com.strangeone101.playertitles.Title;

public class TitleDescriptionPlaceholder extends PlaceholderBase {

    @Override
    public String getIdentifier() {
        return "title_description";
    }

    @Override
    public String get(Title title) {
        return Config.MAX_DESC_LENGTH < 0 ? title.getDescription() : //if its -1, dont split into multiple lines
                PlayerTitlesPlugin.lengthSplit(title.getDescription(), Config.MAX_DESC_LENGTH);
    }
}
