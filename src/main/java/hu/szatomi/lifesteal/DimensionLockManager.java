package hu.szatomi.lifesteal;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;

public class DimensionLockManager {

    private final Lifesteal plugin;
    private LocalDateTime netherUnlockTime;
    private LocalDateTime endUnlockTime;
    private String netherMessage;
    private String endMessage;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DimensionLockManager(Lifesteal plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();

        this.netherUnlockTime = parseDate(config.getString("dimensions.nether.unlock-date"));
        this.endUnlockTime = parseDate(config.getString("dimensions.the_end.unlock-date"));
        
        this.netherMessage = config.getString("dimensions.nether.locked-message", "§cThe Nether is locked!");
        this.endMessage = config.getString("dimensions.the_end.locked-message", "§cThe End is locked!");
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null) return LocalDateTime.MAX;
        try {
            return LocalDateTime.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not parse date: " + dateStr, e);
            return LocalDateTime.MAX;
        }
    }

    public boolean isLocked(World world) {
        if (world == null) return false;
        
        LocalDateTime now = LocalDateTime.now();
        
        if (world.getEnvironment() == World.Environment.NETHER) {
            return now.isBefore(netherUnlockTime);
        }
        
        if (world.getEnvironment() == World.Environment.THE_END) {
            return now.isBefore(endUnlockTime);
        }
        
        return false;
    }

    public String getLockedMessage(World world) {
        if (world.getEnvironment() == World.Environment.NETHER) {
            return netherMessage;
        }
        if (world.getEnvironment() == World.Environment.THE_END) {
            return endMessage;
        }
        return "§cEz a dimenzió le van zárva";
    }
}
