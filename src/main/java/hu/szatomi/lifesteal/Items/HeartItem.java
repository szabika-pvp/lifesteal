package hu.szatomi.lifesteal.Items;

import hu.szatomi.lifesteal.Colors;
import hu.szatomi.lifesteal.Lifesteal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class HeartItem {

    private final Lifesteal plugin;
    public static NamespacedKey HEART_KEY;

    public HeartItem(Lifesteal plugin) {
        this.plugin = plugin;
        HEART_KEY = new NamespacedKey(plugin, "heart_item");
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Heart")
                    .color(Colors.RED)
                    .decoration(TextDecoration.ITALIC, false));
            meta.getPersistentDataContainer().set(HEART_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void registerRecipe() {
        NamespacedKey recipeKey = new NamespacedKey(plugin, "heart_recipe");
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, getItem());

        recipe.shape("ABC", "DEF", "GHI");

        List<String> row1 = plugin.getConfig().getStringList("recipes.heart.row1");
        List<String> row2 = plugin.getConfig().getStringList("recipes.heart.row2");
        List<String> row3 = plugin.getConfig().getStringList("recipes.heart.row3");

        setIngredient(recipe, 'A', row1.size() > 0 ? row1.get(0) : "COMMAND_BLOCK");
        setIngredient(recipe, 'B', row1.size() > 1 ? row1.get(1) : "COMMAND_BLOCK");
        setIngredient(recipe, 'C', row1.size() > 2 ? row1.get(2) : "COMMAND_BLOCK");

        setIngredient(recipe, 'D', row2.size() > 0 ? row2.get(0) : "COMMAND_BLOCK");
        setIngredient(recipe, 'E', row2.size() > 1 ? row2.get(1) : "COMMAND_BLOCK");
        setIngredient(recipe, 'F', row2.size() > 2 ? row2.get(2) : "COMMAND_BLOCK");

        setIngredient(recipe, 'G', row3.size() > 0 ? row3.get(0) : "COMMAND_BLOCK");
        setIngredient(recipe, 'H', row3.size() > 1 ? row3.get(1) : "COMMAND_BLOCK");
        setIngredient(recipe, 'I', row3.size() > 2 ? row3.get(2) : "COMMAND_BLOCK");

        plugin.getServer().addRecipe(recipe);
    }

    private void setIngredient(ShapedRecipe recipe, char key, String materialName) {
        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            material = Material.AIR;
            plugin.getLogger().warning("Ismeretlen material a receptben: " + materialName);
        }
        recipe.setIngredient(key, material);
    }
}
