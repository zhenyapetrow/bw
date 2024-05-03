package ua.larr4k.bedwars.utility;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.stream.Collectors;

public class ItemUtil {

    public static ItemStack buildItemFromConfigSection(ConfigurationSection section) {
        ItemStack itemStack = new ItemStack(Material.matchMaterial(section.getString("material", "AIR")),
                section.getInt("amount", 1), (byte) section.getInt("data", 0));
        ItemMeta itemMeta = itemStack.getItemMeta();

        section.getStringList("enchantments")
                .forEach(enchantment -> {
            String[] split = enchantment.split("-");
            itemStack.addUnsafeEnchantment(Enchantment.getByName(split[0]), Integer.parseInt(split[1]));
        });

        if (section.contains("flags")) {
            section.getStringList("flags")
                    .forEach(flag -> itemMeta.addItemFlags(ItemFlag.valueOf(flag)));
        }

        if (section.contains("name")) {
            itemMeta.setDisplayName(section.getString("name")
                    .replace("&", "§"));
        }

        if (section.contains("lore")) {
            itemMeta.setLore(section.getStringList("lore").stream()
                    .map(rawString -> ChatColor.translateAlternateColorCodes('&', rawString))
                    .collect(Collectors.toList()));
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}