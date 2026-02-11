package hu.szatomi.lifesteal;

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
            sender.sendMessage("§cYou don't have permission to do this!");
            return true;
        }

        plugin.reloadConfig();
        lockManager.loadConfig();
        sender.sendMessage("§aLifesteal configuration reloaded!");
        return true;
    }
}
