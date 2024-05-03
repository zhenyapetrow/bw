package ua.larr4k.bedwars.listener;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ua.larr4k.bedwars.game.shop.Shop;

public class ShopListener implements Listener {

    private final Shop shop;

    public ShopListener(Shop shop){
        this.shop = shop;
    }

    @EventHandler
    private void onInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity.getType() != EntityType.VILLAGER) {
            return;
        }
        if (!entity.getCustomName().equals("Магазин")) {
            return;
        }
        event.setCancelled(true);
        shop.open(event.getPlayer());
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!shop.isShop(event.getClickedInventory())) {
            return;
        }
        event.setCancelled(true);
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) {
            return;
        }
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound = nmsCopy.getTag();
        if (nbtTagCompound == null) {
            return;
        }
        Material material = Material.matchMaterial(nbtTagCompound.getString("price_material"));
        int price = nbtTagCompound.getInt("price");
        if (!canBuy(player, material, price)) {
            player.sendMessage("Недостаточно материалов!");
            return;
        }
        player.getInventory().addItem(shop.getItem(event.getSlot()));
        removeAllItems(player, material, price);
    }

    private boolean canBuy(Player player, Material material, int price) {
        int found = 0;
        for (ItemStack items : player.getInventory().getContents()) {
            if (items.getType() == material) {
                found += items.getAmount();
            }
        }
        return found >= price;
    }

    private void removeAllItems(Player player, Material material, int amount) {
        int removed = 0;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack != null && itemStack.getType() == material) {
                if (removed + itemStack.getAmount() - amount > 0) {
                    itemStack.setAmount(itemStack.getAmount() - (amount - removed));
                    break;
                } else {
                    removed += itemStack.getAmount();
                    inventory.clear(slot);
                }
            }
        }
    }

}
