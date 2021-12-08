package com.strangeone101.playertitles;

import com.strangeone101.playertitles.inventory.InventoryConfig;
import com.strangeone101.playertitles.inventory.InventoryManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.LuckPerms;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerTitlesPlugin extends JavaPlugin {

    private static PlayerTitlesPlugin plugin;

    private LuckPerms luckPermsAPI;

    @Override
    public void onEnable() {
        plugin = this;

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsAPI = provider.getProvider();
        }

        loadConfig();

        InventoryManager.MANAGER.init(); //Sets up the inventory API

        new TitlePlaceholders().register();

        TitlesCommand command = new TitlesCommand();
        getCommand("playertitles").setExecutor(command);
        getCommand("playertitles").setTabCompleter(command);

    }

    @Override
    public void onDisable() {
        PlayerTitles.removeAll(); //Remove perms and cached data

        PlaceholderAPI.unregisterExpansion(TitlePlaceholders.TITLE_PLACEHOLDERS);
    }

    /**
     * Copies a resource located in the jar to a file.
     *
     * @param resourceName
     *            The filename of the resource to copy
     * @param output
     *            The file location to copy it to. Should not exist.
     * @return True if the operation succeeded.
     */
    public static boolean saveResource(String resourceName, File output) {
        if (plugin.getResource(resourceName) == null)
            return false;

        try {
            InputStream in = plugin.getResource(resourceName);
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

            if (!output.exists()) {
                output.createNewFile();
            }

            OutputStream out = new FileOutputStream(output);
            byte[] buf = new byte[256];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadConfig() {
        Config config = new Config();
        config.readNormalConfig(new File(getDataFolder(), "config.yml"));
        config.readTitleConfig(new File(getDataFolder(), "titles.yml"));

        InventoryConfig invconfig = new InventoryConfig();
        invconfig.readMenuConfig(new File(getDataFolder(), "menu.yml"));
        invconfig.readLanguageConfig(new File(getDataFolder(), "language.yml"));
    }

    public static PlayerTitlesPlugin getPlugin() {
        return plugin;
    }

    public static LuckPerms getLuckPerms() {
        return plugin.luckPermsAPI;
    }

    /**
     * Splits the string every x characters. Allows the same
     * string to wrap to the next line after so many characters
     * @param line The full string
     * @param length The length to cut off to
     * */
    public static String lengthSplit(String line, int length)
    {
        Pattern p = Pattern.compile("\\G\\s*(.{1,"+length+"})(?=\\s|$)", Pattern.DOTALL);
        Matcher m = p.matcher(line);
        String newString = "";
        char lastColor = 'f';
        while (m.find())
        {
            String string = m.group(1);
            if (string.contains("\u00A7")) {
                lastColor = string.charAt(string.lastIndexOf('\u00A7') + 1);
            }
            newString = newString + "\n\u00A7" + lastColor + string;
        }
        return newString.substring(1);
    }

    public static String color(String s) {
        return StringEscapeUtils.unescapeJava(
                ChatColor.translateAlternateColorCodes('&',
                        s.replaceAll("&#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])", "&x&$1&$2&$3&$4&$5&$6")
                ));
    }
}
