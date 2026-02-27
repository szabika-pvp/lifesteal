package hu.szatomi.lifesteal.Commands;

import hu.szatomi.lifesteal.Items.HeartItem;
import hu.szatomi.lifesteal.Lifesteal;
import hu.szatomi.lifesteal.MessageManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WithdrawCommand implements CommandExecutor, TabCompleter {

    private final Lifesteal plugin;
    private final MessageManager messageManager;
    private final HeartItem heartItem;

    public WithdrawCommand(Lifesteal plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.heartItem = new HeartItem(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            messageManager.sendMessage(sender, "withdraw_player_only");
            return true;
        }
        Player player = (Player) sender;

        int amount = 1;
        if (args.length > 0) {
            try {
                amount = Integer.parseInt(args[0]);
                if (amount <= 0) {
                    messageManager.sendMessage(player, "withdraw_amount_positive");
                    return true;
                }
            } catch (NumberFormatException e) {
                messageManager.sendMessage(player, "invalid_number");
                return true;
            }
        }

        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttr == null) return true;

        double currentMaxHealth = maxHealthAttr.getBaseValue();
        int currentHearts = (int) (currentMaxHealth / 2);

        if (amount >= currentHearts) {
            messageManager.sendMessage(player, "withdraw_limit_error");
            return true;
        }

        // Remove hearts
        maxHealthAttr.setBaseValue(currentMaxHealth - (amount * 2.0));

        // Give items
        ItemStack items = heartItem.getItem();
        items.setAmount(amount);
        
        // Handle full inventory by dropping items at player location
        player.getInventory().addItem(items).values().forEach(remaining -> 
            player.getWorld().dropItemNaturally(player.getLocation(), remaining)
        );

        messageManager.sendMessage(player, "withdraw_success", Map.of("amount", String.valueOf(amount)));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && args[0].isEmpty()) {
            return List.of("[<amount>]");
        }
        return new ArrayList<>();
    }
}
