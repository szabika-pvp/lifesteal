package hu.szatomi.lifesteal.Commands;

import hu.szatomi.lifesteal.Colors;
import hu.szatomi.lifesteal.DimensionLockManager;
import hu.szatomi.lifesteal.Lifesteal;
import hu.szatomi.lifesteal.MessageTemplate;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final Lifesteal plugin;
    private final DimensionLockManager lockManager;

    public ReloadCommand(Lifesteal plugin, DimensionLockManager lockManager) {
        this.plugin = plugin;
        this.lockManager = lockManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("lifesteal.admin")) {
            sender.sendMessage(MessageTemplate.NO_PERMISSION);
            return true;
        }

        plugin.reloadConfig();
        lockManager.loadConfig();
        sender.sendMessage(Component.text("Konfiguráció újratöltve!", Colors.GREEN));
        return true;
    }
}
