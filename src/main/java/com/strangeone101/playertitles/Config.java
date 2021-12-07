package com.strangeone101.playertitles;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    public static List<String> RARITIES;

    public static int MAX_DESC_LENGTH;

    public void readTitleConfig(File file) {
        if (!file.exists()) {
            PlayerTitlesPlugin.saveResource("titles.yml", file);
        }

        YamlConfiguration titleConfig = YamlConfiguration.loadConfiguration(file);

        PlayerTitles.removeAll();

        for (String titleId : titleConfig.getKeys(false)) {
            String name = titleConfig.getString(titleId + ".name", "????").toLowerCase();
            String desc = titleConfig.getString(titleId + ".description", "");
            int rarity = titleConfig.getInt(titleId + ".rarity", 1);
            String group = titleConfig.getString(titleId + ".group");

            Title title;

            if (group != null) {
                group = group.toLowerCase();
                title = new Title(titleId, name, desc, rarity, group);
            } else {
                List<String> groups = titleConfig.getStringList(titleId + ".groups");
                String[] groupArray = {};
                if (groups != null) groupArray = groups.toArray(new String[groups.size()])
                title = new Title(titleId, name, desc, rarity, groupArray);
            }

            PlayerTitles.registerTitle(title);
        }
    }

    public void readNormalConfig(File file) {
        if (!file.exists()) {
            PlayerTitlesPlugin.saveResource("config.yml", file);
        }

        YamlConfiguration titleConfig = YamlConfiguration.loadConfiguration(file);
        RARITIES = titleConfig.getStringList("rarities");

        MAX_DESC_LENGTH = titleConfig.getInt("description-max-width", 50);

    }
}
