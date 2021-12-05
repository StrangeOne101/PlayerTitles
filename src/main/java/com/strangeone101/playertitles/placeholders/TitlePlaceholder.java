package com.strangeone101.playertitles.placeholders;


import com.strangeone101.playertitles.Title;

public class TitlePlaceholder extends PlaceholderBase {

    @Override
    public String getIdentifier() {
        return "title_name";
    }

    @Override
    public String get(Title title) {
        return title.getName();
    }
}
