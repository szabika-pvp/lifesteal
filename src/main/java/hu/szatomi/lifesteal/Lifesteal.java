package hu.szatomi.lifesteal;

import hu.szatomi.lifesteal.Commands.HeartsCommand;
import hu.szatomi.lifesteal.Commands.HealCommand;
import hu.szatomi.lifesteal.Commands.ReloadCommand;
import hu.szatomi.lifesteal.Listeners.BannedItemsListener;
import hu.szatomi.lifesteal.Listeners.CombatLogListener;
import hu.szatomi.lifesteal.Listeners.DimensionListener;
import hu.szatomi.lifesteal.Listeners.PlayerDeathListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lifesteal extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new BannedItemsListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatLogListener(this), this);

        DimensionLockManager lockManager = new DimensionLockManager(this);
        getServer().getPluginManager().registerEvents(new DimensionListener(lockManager), this);
        
        if (getCommand("lifestealreload") != null) {
            getCommand("lifestealreload").setExecutor(new ReloadCommand(this, lockManager));
        }
        
        if (getCommand("hearts") != null) {
            getCommand("hearts").setExecutor(new HeartsCommand(this));
        }

        if (getCommand("heal") != null) {
            HealCommand healCommand = new HealCommand();
            getCommand("heal").setExecutor(healCommand);
            getCommand("heal").setTabCompleter(healCommand);
        }
        
        getLogger().info("LIFESTEAL PLUGIN BEKAPCSOLVA.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
