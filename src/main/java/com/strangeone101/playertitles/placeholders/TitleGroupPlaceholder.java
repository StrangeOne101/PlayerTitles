package com.strangeone101.playertitles.placeholders;


import com.strangeone101.playertitles.Title;

public class TitleGroupPlaceholder extends PlaceholderBase {

    @Override
    public String getIdentifier() {
        return "title_group";
    }

    @Override
    public String get(Title title) {
        return title.getGroup() == null ? "" : title.getGroup();
    }
}
