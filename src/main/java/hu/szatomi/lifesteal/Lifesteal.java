package hu.szatomi.lifesteal;

import hu.szatomi.lifesteal.Commands.HeartsCommand;
import hu.szatomi.lifesteal.Listeners.BannedItemsListener;
import hu.szatomi.lifesteal.Listeners.PlayerDeathListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lifesteal extends JavaPlugin {

    private DimensionLockManager lockManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new BannedItemsListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        
        this.lockManager = new DimensionLockManager(this);
        getServer().getPluginManager().registerEvents(new DimensionListener(lockManager), this);
        
        if (getCommand("lifestealreload") != null) {
            getCommand("lifestealreload").setExecutor(new ReloadCommand(this, lockManager));
        }
        
        if (getCommand("hearts") != null) {
            getCommand("hearts").setExecutor(new HeartsCommand(this));
        }
        
        getLogger().info("LIFESTEAL PLUGIN BEKAPCSOLVA.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
