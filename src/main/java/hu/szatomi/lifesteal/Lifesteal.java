package hu.szatomi.lifesteal;

import hu.szatomi.lifesteal.Commands.HeartsCommand;
import hu.szatomi.lifesteal.Commands.HealCommand;
import hu.szatomi.lifesteal.Commands.ReloadCommand;
import hu.szatomi.lifesteal.Commands.WithdrawCommand;
import hu.szatomi.lifesteal.Items.HeartItem;
import hu.szatomi.lifesteal.Items.HeartListener;
import hu.szatomi.lifesteal.Listeners.BannedItemsListener;
import hu.szatomi.lifesteal.Listeners.CombatLogListener;
import hu.szatomi.lifesteal.Listeners.DimensionListener;
import hu.szatomi.lifesteal.Listeners.PlayerDeathListener;
import hu.szatomi.lifesteal.Listeners.SmithingListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Lifesteal extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();

        MessageManager messageManager = new MessageManager(this);

        getServer().getPluginManager().registerEvents(new BannedItemsListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this, messageManager), this);
        getServer().getPluginManager().registerEvents(new CombatLogListener(this, messageManager), this);
        getServer().getPluginManager().registerEvents(new HeartListener(this, messageManager), this);
        getServer().getPluginManager().registerEvents(new SmithingListener(this), this);

        DimensionLockManager lockManager = new DimensionLockManager(this);
        getServer().getPluginManager().registerEvents(new DimensionListener(lockManager, messageManager), this);

        HeartItem heartItem = new HeartItem(this);
        heartItem.registerRecipe();
        
        if (getCommand("lifestealreload") != null) {
            Objects.requireNonNull(getCommand("lifestealreload")).setExecutor(new ReloadCommand(this, lockManager, messageManager));
        }
        
        if (getCommand("hearts") != null) {
            Objects.requireNonNull(getCommand("hearts")).setExecutor(new HeartsCommand(this, messageManager));
        }

        if (getCommand("heal") != null) {
            HealCommand healCommand = new HealCommand(messageManager);
            Objects.requireNonNull(getCommand("heal")).setExecutor(healCommand);
            Objects.requireNonNull(getCommand("heal")).setTabCompleter(healCommand);
        }

        if (getCommand("withdraw") != null) {
            WithdrawCommand withdrawCommand = new WithdrawCommand(this, messageManager);
            Objects.requireNonNull(getCommand("withdraw")).setExecutor(withdrawCommand);
            Objects.requireNonNull(getCommand("withdraw")).setTabCompleter(withdrawCommand);
        }
        
        getLogger().info("LIFESTEAL PLUGIN BEKAPCSOLVA.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
