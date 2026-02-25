package hu.szatomi.lifesteal.Commands;

import hu.szatomi.lifesteal.Colors;
import hu.szatomi.lifesteal.Items.HeartItem;
import hu.szatomi.lifesteal.Lifesteal;
import hu.szatomi.lifesteal.MessageTemplate;
import net.kyori.adventure.text.Component;
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

public class WithdrawCommand implements CommandExecutor, TabCompleter {

    private final Lifesteal plugin;
    private final HeartItem heartItem;

    public WithdrawCommand(Lifesteal plugin) {
        this.plugin = plugin;
        this.heartItem = new HeartItem(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Ezt a parancsot csak játékosok használhatják!", Colors.RED));
            return true;
        }

        int amount = 1;
        if (args.length > 0) {
            try {
                amount = Integer.parseInt(args[0]);
                if (amount <= 0) {
                    player.sendMessage(Component.text("Az összegnek nagyobbnak kell lennie, mint 0!", Colors.RED));
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("Érvénytelen szám!", Colors.RED));
                return true;
            }
        }

        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttr == null) return true;

        double currentMaxHealth = maxHealthAttr.getBaseValue();
        int currentHearts = (int) (currentMaxHealth / 2);

        if (amount >= currentHearts) {
            player.sendMessage(Component.text("Nem vonhatsz ki ennyi szívet! Legalább 1 szívednek maradnia kell.", Colors.RED));
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

        player.sendMessage(Component.text("Sikeresen kivontál " + amount + " szívet!").color(Colors.GREEN));

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
