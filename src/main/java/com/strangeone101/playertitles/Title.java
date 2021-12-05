package com.strangeone101.playertitles;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Title {

    @Getter
    public String id;

    @Getter
    private String name;

    @Getter
    private String description;

    @Getter
    private int rarity;

    @Getter
    private String group;
}
