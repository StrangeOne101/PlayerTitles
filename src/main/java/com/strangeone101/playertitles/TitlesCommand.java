package com.strangeone101.playertitles;

import com.strangeone101.playertitles.inventory.TitleGroupMenu;
import com.strangeone101.playertitles.inventory.TitleMenu;
import com.strangeone101.playertitles.placeholders.TitleRarityPlaceholder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class TitlesCommand implements CommandExecutor, TabExecutor {

    private Map<Integer, Consumer<Player>> callbacks = new HashMap<>();
    private List<Integer> expired = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("menu")) {
            openMenu(sender, args);
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            giveTitle(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }

        if (args[0].equalsIgnoreCase("callback")) {
            doCallback(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    public void openMenu(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sendHelp(sender);
                return;
            }
            sender.sendMessage(ChatColor.RED + "Only players can open the menu!");
            return;
        }

        if (args.length > 1) {
            if (PlayerTitles.getGroup(args[1].toLowerCase()) == null) {
                sender.sendMessage(ChatColor.RED + "Group not found!");
                return;
            }
            new TitleGroupMenu(args[1].toLowerCase(), null).getInventory().open((Player) sender);
            return;
        }
        TitleMenu menu = new TitleMenu((Player) sender);
    }

    public void sendHelp(CommandSender sender) {
        //TODO
    }

    public void giveTitle(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage is /titles give <title/group> <player> [...]");
            return;
        }

        List<OfflinePlayer> players = new ArrayList<>();
        if (args[1].equals("*")) {
            players.addAll(Bukkit.getOnlinePlayers());
        } else {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (!player.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            players.add(player);
        }

        if (args[0].equalsIgnoreCase("title")) {
            Title title = PlayerTitles.getTitle(args[2]);
            if (title == null) {
                sender.sendMessage(ChatColor.RED + "Title not found!");
                return;
            }
            String titleString = title.getName() + "\n" + TitleRarityPlaceholder.getRarity(title.getRarity())
                    + "\n\n" + PlayerTitlesPlugin.lengthSplit(title.getDescription(), Config.MAX_DESC_LENGTH);
            players.forEach(p -> {
                PlayerTitles.giveTitle(p, title).thenAccept(bool -> {
                    if (bool) {
                        if (p instanceof Player) {
                            //The [Title] text that has hover text for the full title
                            TextComponent titleComp = new TextComponent(TextComponent.fromLegacyText(title.getName()));
                            titleComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(titleString)));

                            //Create the callback that applies that title. Assign the callback to an id
                            String id = assignCallback((player) -> {
                                //Only do it if they have the permission
                                if (title.canUse(player)) {
                                    PlayerTitles.setPlayerTitle(player, title);
                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 2);
                                    player.sendMessage(ChatColor.GREEN + "Player title updated!");
                                }
                            }, 60); //Timeout for 60s

                            //The component that applies it now
                            TextComponent applyNow = new TextComponent(new ComponentBuilder()
                                    .color(ChatColor.DARK_GREEN).bold(true).append("APPLY IT NOW")
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "Click here to apply!")))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/titles callback " + id))
                                    .create());

                            //The standard text
                            TextComponent comp = new TextComponent(new ComponentBuilder()
                                    .color(ChatColor.GREEN).append("You have been given the ")
                                    .color(ChatColor.DARK_GRAY).append("[")
                                    .append(titleComp)
                                    .color(ChatColor.DARK_GRAY).append("]")
                                    .color(ChatColor.GREEN).append("title. Do you wish to apply it now? ")
                                    .append(applyNow).create());
                            ((Player) p).spigot().sendMessage(comp);
                        }
                    }
                });
            });
        } else if (args[0].equalsIgnoreCase("group")) {
            List<Title> titles = PlayerTitles.getGroup(args[2]);
            if (titles == null || titles.size() == 0) {
                sender.sendMessage(ChatColor.RED + "Group not found!");
                return;
            }

            players.forEach(p -> {
                PlayerTitles.giveGroup(p, args[2].toLowerCase()).thenAccept(bool -> {
                    if (bool) {
                        if (p instanceof Player) {
                            //The component that opens the menu
                            TextComponent openMenuComponent = new TextComponent(new ComponentBuilder()
                                    .color(ChatColor.DARK_GREEN).bold(true).append("OPEN TITLE MENU")
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "Click here to browse new titles!")))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/titles menu " + args[2].toLowerCase()))
                                    .create());

                            //The standard text
                            TextComponent comp = new TextComponent(new ComponentBuilder()
                                    .color(ChatColor.GREEN).append("You have been given ")
                                    .color(ChatColor.RED).append(titles.size() + "")
                                    .color(ChatColor.GREEN).append("titles!. Do you wish to browse them now? ")
                                    .append(openMenuComponent).create());
                            ((Player) p).spigot().sendMessage(comp);
                        }
                    }
                });
            });
        } else {
            sender.sendMessage(ChatColor.RED + "Usage is /titles give <title/group> <player> [...]");
            return;
        }
    }

    public void doCallback(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Error: Something went wrong! Let StrangeOne101 know!");
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "What are you doing...? *insert emoji looking at you weirdly face*");
            return;
        }

        //Convert the hex back to integer
        int num = Integer.parseInt(args[0], 16);

        if (callbacks.containsKey(num)) {
            callbacks.get(num).accept((Player) sender); //Call the function
            callbacks.remove(num);
        } else if (expired.contains(num)) { //If the callback has already be uncached
            sender.sendMessage(ChatColor.RED + "This action no longer exists! Did you possibly wait too long?");
            expired.remove(num);
        }
    }

    /**
     * Assign a function to run when a callback command is run
     * @param consumer The consumer
     * @return The ID of the callback. Use this in /titles callback <id>
     */
    public String assignCallback(Consumer<Player> consumer, int timeoutInSeconds) {
        int randNum;
        Random rand = new Random();

        do {
            randNum = rand.nextInt();
        } while (callbacks.containsKey(randNum)); //While the number already exists in it, keep getting a new random num

        String numAsString = Integer.toString(randNum, 16);
        callbacks.put(randNum, consumer);

        int finalRandNum = randNum;
        Bukkit.getScheduler().runTaskLater(PlayerTitlesPlugin.getPlugin(), () -> {
            if (callbacks.containsKey(finalRandNum)) { //If it hasn't been completed yet
                callbacks.remove(finalRandNum);
                expired.add(finalRandNum); //Add it to the expired list to tell players they took too long
            }
        }, 20 * timeoutInSeconds);
        return numAsString;
    }
}
