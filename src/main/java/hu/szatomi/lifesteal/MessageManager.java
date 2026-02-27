package hu.szatomi.lifesteal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final JavaPlugin plugin;
    private final Map<String, YamlConfiguration> languages = new HashMap<>();

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadLanguages();
    }

    public void loadLanguages() {
        languages.clear();
        loadLanguage("en");
        loadLanguage("hu");
    }

    private void loadLanguage(String lang) {
        File file = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        if (!file.exists()) {
            plugin.saveResource("messages_" + lang + ".yml", false);
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        // Also load defaults from jar to ensure we have all keys if file is outdated
        InputStream defStream = plugin.getResource("messages_" + lang + ".yml");
        if (defStream != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, StandardCharsets.UTF_8)));
        }
        
        languages.put(lang, config);
    }

    public Component getMessage(CommandSender sender, String key, Map<String, String> placeholders) {
        String lang = "en";
        if (sender instanceof Player) {
            String locale = ((Player) sender).locale().getLanguage();
            if ("hu".equalsIgnoreCase(locale)) {
                lang = "hu";
            }
        }

        YamlConfiguration config = languages.getOrDefault(lang, languages.get("en"));
        String message = config.getString(key);
        
        if (message == null) {
            // Fallback to English if key missing in HU
            if (!lang.equals("en")) {
                config = languages.get("en");
                message = config.getString(key);
            }
        }
        
        if (message == null) {
            return Component.text("Missing message key: " + key);
        }

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public void sendMessage(CommandSender sender, String key) {
        sendMessage(sender, key, null);
    }

    public void sendMessage(CommandSender sender, String key, Map<String, String> placeholders) {
        sender.sendMessage(getMessage(sender, key, placeholders));
    }
}
