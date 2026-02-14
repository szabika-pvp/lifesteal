package hu.szatomi.lifesteal.Commands;

import hu.szatomi.lifesteal.Colors;
import hu.szatomi.lifesteal.Lifesteal;
import hu.szatomi.lifesteal.MessageTemplate;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HeartsCommand implements TabExecutor {

    private final Lifesteal plugin;

    public HeartsCommand(Lifesteal plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(MessageTemplate.NO_PERMISSION);
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Component.text("Használat: /hearts <add|set|remove> <player> <amount>").color(Colors.RED));
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
            sender.sendMessage(Component.text("Érvénytelen érték, kérlek számot adj meg").color(Colors.RED));
            return true;
        }
        
        // Convert hearts to HP (1 heart = 2 HP)
        double healthAmount = amount * 2.0;

        AttributeInstance maxHealthAttribute = target.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttribute == null) {
            sender.sendMessage(Component.text("A player 'MAX_HEALTH'-je nem elérhető").color(Colors.RED));
            return true;
        }

        double currentMaxHealth = maxHealthAttribute.getBaseValue();
        double newMaxHealth;
        
        // Get max hearts from config (default 20), convert to HP
        int configMaxHearts = plugin.getConfig().getInt("max_hearts", 20);
        double maxHealthCap = configMaxHearts * 2.0;

        switch (action) {
            case "add":
                newMaxHealth = currentMaxHealth + healthAmount;
                sender.sendMessage(Component.text(amount + " szív adva " + target.getName() + " játékosnak").color(Colors.GREEN));
                break;
            case "set":
                newMaxHealth = healthAmount;
                sender.sendMessage(Component.text(target.getName() + " játékos szívei beállítva " + amount + " szívre").color(Colors.GREEN));
                break;
            case "remove":
                newMaxHealth = currentMaxHealth - healthAmount;
                sender.sendMessage(Component.text(amount + " szív elvéve " + target.getName() + " játékostól").color(Colors.GREEN));
                break;
            default:
                sender.sendMessage(Component.text("Ismeretlen parancs: " + action).color(Colors.RED));
                return true;
        }

        // Apply min cap (1 heart / 2 HP)
        if (newMaxHealth < 2.0) {
            newMaxHealth = 2.0; 
            sender.sendMessage(Component.text("Az szívek száma elérte a minimumot (1), ezért arra lett állítva.").color(Colors.YELLOW));
        }
        
        // Apply max cap from config
        if (newMaxHealth > maxHealthCap) {
            newMaxHealth = maxHealthCap;
            sender.sendMessage(Component.text("Az szívek száma elérte a maximumot (" + configMaxHearts + "), ezért arra lett állítva.").color(Colors.YELLOW));
        }

        maxHealthAttribute.setBaseValue(newMaxHealth);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
