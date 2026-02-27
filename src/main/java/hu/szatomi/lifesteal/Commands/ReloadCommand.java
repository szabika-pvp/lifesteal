package hu.szatomi.lifesteal.Commands;

import hu.szatomi.lifesteal.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final Lifesteal plugin;
    private final DimensionLockManager lockManager;
    private final MessageManager messageManager;

    public ReloadCommand(Lifesteal plugin, DimensionLockManager lockManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.lockManager = lockManager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("lifesteal.admin")) {
            messageManager.sendMessage(sender, "no_permission");
            return true;
        }

        plugin.reloadConfig();
        lockManager.loadConfig();
        messageManager.loadLanguages();
        messageManager.sendMessage(sender, "reload_success");
        return true;
    }
}
