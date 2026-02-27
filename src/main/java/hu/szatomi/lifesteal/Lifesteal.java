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
import org.bukkit.plugin.java.JavaPlugin;

public final class Lifesteal extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();

        MessageManager messageManager = new MessageManager(this);

        getServer().getPluginManager().registerEvents(new BannedItemsListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this, messageManager), this);
        getServer().getPluginManager().registerEvents(new CombatLogListener(this, messageManager), this);
        getServer().getPluginManager().registerEvents(new HeartListener(this, messageManager), this);

        DimensionLockManager lockManager = new DimensionLockManager(this);
        getServer().getPluginManager().registerEvents(new DimensionListener(lockManager, messageManager), this);

        HeartItem heartItem = new HeartItem(this);
        heartItem.registerRecipe();
        
        if (getCommand("lifestealreload") != null) {
            getCommand("lifestealreload").setExecutor(new ReloadCommand(this, lockManager, messageManager));
        }
        
        if (getCommand("hearts") != null) {
            getCommand("hearts").setExecutor(new HeartsCommand(this, messageManager));
        }

        if (getCommand("heal") != null) {
            HealCommand healCommand = new HealCommand(messageManager);
            getCommand("heal").setExecutor(healCommand);
            getCommand("heal").setTabCompleter(healCommand);
        }

        if (getCommand("withdraw") != null) {
            WithdrawCommand withdrawCommand = new WithdrawCommand(this, messageManager);
            getCommand("withdraw").setExecutor(withdrawCommand);
            getCommand("withdraw").setTabCompleter(withdrawCommand);
        }
        
        getLogger().info("LIFESTEAL PLUGIN BEKAPCSOLVA.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
