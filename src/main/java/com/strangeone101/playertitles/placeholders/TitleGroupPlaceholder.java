package com.strangeone101.playertitles.placeholders;


import com.strangeone101.playertitles.Title;

public class TitleGroupPlaceholder extends PlaceholderBase {

    @Override
    public String getIdentifier() {
        return "title_group";
    }

    @Override
    public String get(Title title) {
        return title.getGroups().length == 0 ? "" : title.getGroups()[0];
    }
}
