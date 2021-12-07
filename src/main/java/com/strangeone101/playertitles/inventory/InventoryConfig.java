package com.strangeone101.playertitles.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.strangeone101.playertitles.PlayerTitlesPlugin;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.bukkit.DyeColor.*;

public class InventoryConfig {

    @Getter
    private static ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    @Getter
    private static ItemStack filter = new ItemStack(Material.HOPPER);
    @Getter
    private static ItemStack arrow = new ItemStack(Material.ARROW);
    @Getter
    private static ItemStack back = new ItemStack(Material.BARRIER);
    @Getter
    private static ItemStack order = new ItemStack(Material.OAK_SIGN);

    @Getter
    private static int inventoryHeight = 3;
    @Getter
    private static int inventoryWidth = 7;

    @Getter
    private static YamlConfiguration languageConfig;


    @Getter
    private static Map<String, ItemStack> titleGroups = new HashMap<>();
    @Getter
    private static Map<String, Byte[]> titleGroupPositions = new HashMap<>();
    private static Map<String, Boolean> titleGroupsHideIfEmpty = new HashMap<>();

    public void readMenuConfig(File file) {
        if (!file.exists()) {
            PlayerTitlesPlugin.saveResource("menu.yml", file);
        }

        YamlConfiguration menuConfig = YamlConfiguration.loadConfiguration(file);
        String size = menuConfig.getString("menu-config.size", "3x7");

        try {
            inventoryWidth = Integer.parseInt(size.split("x")[0]);
            inventoryHeight = Integer.parseInt(size.split("x")[1]);
        } catch (Exception e) {
            PlayerTitlesPlugin.getPlugin().getLogger().warning("Failed to parse size \"" + size + "\". Default size will be used");
        }


        loadItem(menuConfig.getString("icons.filter"), filter);
        loadItem(menuConfig.getString("icons.border"), border);
        loadItem(menuConfig.getString("icons.page"), arrow);
        loadItem(menuConfig.getString("icons.back"), back);
        loadItem(menuConfig.getString("icons.order"), order);

        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(ChatColor.RESET + "");
        border.setItemMeta(borderMeta);

        titleGroups.clear();
        titleGroupPositions.clear();
        titleGroupsHideIfEmpty.clear();

        for (String key : menuConfig.getConfigurationSection("group-icons").getKeys(false)) {
            String icon = menuConfig.getString("group-icons." + key + ".icon");
            String title = PlayerTitlesPlugin.color(menuConfig.getString("group-icons." + key + ".title"));
            List<String> lore = menuConfig.getStringList("group-icons." + key + ".lore");
            lore = lore.stream().map(s -> PlayerTitlesPlugin.color(s)).collect(Collectors.toList());
            boolean hideIfEmpty = menuConfig.getBoolean("group-icons." + key + ".hide-if-empty");
            String pos = menuConfig.getString("group-icons." + key + ".position");
            byte x = 0, y = 0;
            try {
                x = Byte.parseByte(pos.split(",")[0]);
                y = Byte.parseByte(pos.split(",")[1]);
            } catch (Exception e) {
                PlayerTitlesPlugin.getPlugin().getLogger().warning("Cannot parse position \"" + pos + "\" for group item \"" + key + "\"");
            }



            ItemStack stack = fromString(icon);
            if (stack == null) stack = new ItemStack(Material.GLASS);

            ItemMeta meta = stack.getItemMeta();
            if (title != null && !title.equals("")) meta.setDisplayName(title);
            if (lore != null && lore.size() > 0) meta.setLore(lore);
            stack.setItemMeta(meta);


            titleGroups.put(key, stack);
            titleGroupsHideIfEmpty.put(key, hideIfEmpty);
            titleGroupPositions.put(key, new Byte[] {x, y});
        }


    }

    public void readLanguageConfig(File file) {
        if (!file.exists()) {
            PlayerTitlesPlugin.saveResource("language.yml", file);
        }

        languageConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static boolean hideIfEmpty(String group) {
        return titleGroupsHideIfEmpty.containsKey(group) && titleGroupsHideIfEmpty.get(group);
    }

    private void loadItem(String line, ItemStack variable) {
        ItemStack stack = fromString(line);
        if (stack != null) variable = stack;
    }


    public ItemStack fromString(String line) {
        if (line == null) return null;

        String[] split = line.split(":");
        String material = split[0];
        int model = -1;
        String texture = "";

        try {
            if (line.contains(":")) model = Integer.parseInt(split[1]);
        } catch (NumberFormatException e) {
            PlayerTitlesPlugin.getPlugin().getLogger().severe("Model number '" + split[1] + "' is not an integer!");
            e.printStackTrace();
        }

        if (split.length > 2) {
            texture = split[2];
        }

        Material mat = Material.matchMaterial(material);
        if (mat == null) {
            PlayerTitlesPlugin.getPlugin().getLogger().severe("Material '" + split[0] + "' not found!");
            return null;
        }

        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();
        if (model != -1) {
            meta.setCustomModelData(model);
        }
        if (texture != null) {
            if (meta instanceof BannerMeta) {
                List<Pattern> patterns = new ArrayList<>();
                for (int i = 0; i < texture.length() / 2; i++) {
                    char color = texture.charAt(i);
                    char design = texture.charAt(i + 1);

                    DyeColor dyeColor;
                    if (color == 'a') dyeColor = BLACK;
                    else if (color == 'b') dyeColor = RED;
                    else if (color == 'c') dyeColor = GREEN;
                    else if (color == 'd') dyeColor = BROWN;
                    else if (color == 'e') dyeColor = BLUE;
                    else if (color == 'f') dyeColor = PURPLE;
                    else if (color == 'g') dyeColor = CYAN;
                    else if (color == 'h') dyeColor = LIGHT_GRAY;
                    else if (color == 'i') dyeColor = GRAY;
                    else if (color == 'j') dyeColor = PINK;
                    else if (color == 'k') dyeColor = LIME;
                    else if (color == 'l') dyeColor = YELLOW;
                    else if (color == 'm') dyeColor = LIGHT_BLUE;
                    else if (color == 'n') dyeColor = MAGENTA;
                    else if (color == 'o') dyeColor = ORANGE;
                    else dyeColor = WHITE;

                    Pattern pattern = new Pattern(dyeColor, PatternType.getByIdentifier(design + ""));
                    patterns.add(pattern);
                }
                ((BannerMeta) meta).setPatterns(patterns);
            } else if (meta instanceof SkullMeta) {
                if (texture.startsWith("https://")) {
                    meta = setSkinFromURL((SkullMeta) meta, texture);
                } else if (texture.matches("[a-f\\d]{64}")) {
                    meta = setSkinFromURL((SkullMeta) meta, "http://textures.minecraft.net/texture/" + texture);
                }
            }
        }
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Sets the skin of a Skull to the skin from the provided URL
     * @param meta The skull meta
     * @param skin The skin URL
     * @return The corrected ItemMeta
     */
    public static SkullMeta setSkinFromURL(SkullMeta meta, String skin) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta im = (SkullMeta) stack.getItemMeta();
        UUID uuid;
        Random random = new Random(skin.hashCode());

        GameProfile profile = new GameProfile(new UUID(random.nextLong(), random.nextLong()), null);
        byte[] encodedData = Base64.getEncoder()
                .encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", skin).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        try {
            Field profileField = im.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(im, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        return im;
    }
}
