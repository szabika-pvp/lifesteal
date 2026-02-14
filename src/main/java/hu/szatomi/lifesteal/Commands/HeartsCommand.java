package hu.szatomi.lifesteal.Commands;

import hu.szatomi.lifesteal.Lifesteal;
import hu.szatomi.lifesteal.MessageTemplate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HeartsCommand implements TabExecutor {

    private final Lifesteal plugin;

    public HeartsCommand(Lifesteal plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(MessageTemplate.NO_PERMISSION);
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Használat: /hearts <add|set|remove> <player> <amount>");
            return true;
        }

        String action = args[0].toLowerCase();
        String playerName = args[1];
        String amountStr = args[2];

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(MessageTemplate.PLAYER_NOT_FOUND);
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Érvénytelen érték, kérlek számot adj meg");
            return true;
        }
        
        // Convert hearts to HP (1 heart = 2 HP)
        double healthAmount = amount * 2.0;

        AttributeInstance maxHealthAttribute = target.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttribute == null) {
            sender.sendMessage(ChatColor.RED + "A player 'MAX_HEALTH'-je nem elérhető");
            return true;
        }

        double currentMaxHealth = maxHealthAttribute.getBaseValue();
        double newMaxHealth = currentMaxHealth;
        
        // Get max hearts from config (default 20), convert to HP
        int configMaxHearts = plugin.getConfig().getInt("max_hearts", 20);
        double maxHealthCap = configMaxHearts * 2.0;

        switch (action) {
            case "add":
                newMaxHealth = currentMaxHealth + healthAmount;
                sender.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " szív adva " + target.getName() + " játékosnak");
                break;
            case "set":
                newMaxHealth = healthAmount;
                sender.sendMessage(ChatColor.GREEN + target.getName() + " játékos szívei beállítva " + amount + " szívre");
                break;
            case "remove":
                newMaxHealth = currentMaxHealth - healthAmount;
                sender.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " szív elvéve " + target.getName() + " játékostól");
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Ismeretlen parancs: " + action);
                return true;
        }

        // Apply min cap (1 heart / 2 HP)
        if (newMaxHealth < 2.0) {
            newMaxHealth = 2.0; 
            sender.sendMessage(ChatColor.YELLOW + "Az szívek száma túl alacsony, ezért a minimumra (1) lett állítva.");
        }
        
        // Apply max cap from config
        if (newMaxHealth > maxHealthCap) {
            newMaxHealth = maxHealthCap;
            sender.sendMessage(ChatColor.YELLOW + "Az szívek száma elérte a maximumot (" + configMaxHearts + "), ezért arra lett állítva.");
        }

        maxHealthAttribute.setBaseValue(newMaxHealth);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String[] actions = {"add", "set", "remove"};
            for (String action : actions) {
                if (action.startsWith(args[0].toLowerCase())) {
                    completions.add(action);
                }
            }
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 3) {
            completions.add("[<amount>]");
        }

        return completions;
    }
}
