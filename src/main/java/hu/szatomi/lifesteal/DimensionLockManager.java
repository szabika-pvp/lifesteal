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

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DimensionLockManager(Lifesteal plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();

        this.netherUnlockTime = parseDate(config.getString("dimensions.nether.unlock-date"));
        this.endUnlockTime = parseDate(config.getString("dimensions.the_end.unlock-date"));
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
        return isLocked(world.getEnvironment());
    }

    public boolean isLocked(World.Environment env) {
        LocalDateTime now = LocalDateTime.now();
        if (env == World.Environment.NETHER) {
            return now.isBefore(netherUnlockTime);
        }
        if (env == World.Environment.THE_END) {
            return now.isBefore(endUnlockTime);
        }
        return false;
    }

    public LocalDateTime getUnlockTime(World.Environment env) {
        if (env == World.Environment.NETHER) return netherUnlockTime;
        if (env == World.Environment.THE_END) return endUnlockTime;
        return null;
    }
}
