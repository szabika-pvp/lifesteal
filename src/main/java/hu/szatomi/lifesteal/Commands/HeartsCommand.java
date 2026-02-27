package hu.szatomi.lifesteal.Commands;

import hu.szatomi.lifesteal.Lifesteal;
import hu.szatomi.lifesteal.MessageManager;
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
import java.util.Map;

public class HeartsCommand implements TabExecutor {

    private final Lifesteal plugin;
    private final MessageManager messageManager;

    public HeartsCommand(Lifesteal plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.isOp()) {
            messageManager.sendMessage(sender, "no_permission");
            return true;
        }

        if (args.length < 3) {
            messageManager.sendMessage(sender, "usage_hearts");
            return true;
        }

        String action = args[0].toLowerCase();
        String playerName = args[1];
        String amountStr = args[2];

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            messageManager.sendMessage(sender, "player_not_found");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            messageManager.sendMessage(sender, "invalid_number");
            return true;
        }
        
        // Convert hearts to HP (1 heart = 2 HP)
        double healthAmount = amount * 2.0;

        AttributeInstance maxHealthAttribute = target.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttribute == null) {
            messageManager.sendMessage(sender, "hearts_max_health_unavailable");
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
                messageManager.sendMessage(sender, "hearts_added", Map.of("amount", String.valueOf(amount), "player", target.getName()));
                break;
            case "set":
                newMaxHealth = healthAmount;
                messageManager.sendMessage(sender, "hearts_set", Map.of("amount", String.valueOf(amount), "player", target.getName()));
                break;
            case "remove":
                newMaxHealth = currentMaxHealth - healthAmount;
                messageManager.sendMessage(sender, "hearts_removed", Map.of("amount", String.valueOf(amount), "player", target.getName()));
                break;
            default:
                messageManager.sendMessage(sender, "hearts_unknown_subcommand", Map.of("subcommand", action));
                return true;
        }

        // Apply min cap (1 heart / 2 HP)
        if (newMaxHealth < 2.0) {
            newMaxHealth = 2.0; 
            messageManager.sendMessage(sender, "hearts_min_limit");
        }
        
        // Apply max cap from config
        if (newMaxHealth > maxHealthCap) {
            newMaxHealth = maxHealthCap;
            messageManager.sendMessage(sender, "hearts_max_limit", Map.of("max", String.valueOf(configMaxHearts)));
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
