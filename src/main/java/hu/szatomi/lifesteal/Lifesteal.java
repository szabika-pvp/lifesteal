package hu.szatomi.lifesteal;

import hu.szatomi.lifesteal.Listeners.BannedItemsListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lifesteal extends JavaPlugin {

    private DimensionLockManager lockManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new BannedItemsListener(), this);
        
        this.lockManager = new DimensionLockManager(this);
        getServer().getPluginManager().registerEvents(new DimensionListener(lockManager), this);
        
        if (getCommand("lifestealreload") != null) {
            getCommand("lifestealreload").setExecutor(new ReloadCommand(this, lockManager));
        }
        
        getLogger().info("LIFESTEAL PLUGIN ENABLED - Dimension locking active.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
