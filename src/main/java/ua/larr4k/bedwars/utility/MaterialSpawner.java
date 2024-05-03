package ua.larr4k.bedwars.utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ua.larr4k.bedwars.App;

public class MaterialSpawner {

    private final int spawnDelay;
    private final Location spawnLocation;
    private final ItemStack spawnItem;
    private int spawnTaskId;

    public MaterialSpawner(int delay, Location location, ItemStack item) {
        this.spawnDelay = delay;
        this.spawnLocation = location;
        this.spawnItem = item;
        spawnTaskId = -1;
    }

    public MaterialSpawner(ConfigurationSection configSection) {
        this(
                configSection.getInt("delay", 20),
                LocationUtil.getLocation(configSection.getString("location", "world 0 0 0")),
                ItemUtil.buildItemFromConfigSection(configSection)
        );
    }

    public void startSpawnTask(App plugin) {
        spawnTaskId = new BukkitRunnable() {
            public void run() {
                spawnLocation.getWorld().dropItemNaturally(spawnLocation, spawnItem);
            }
        }.runTaskTimer(plugin, 0, spawnDelay).getTaskId();
    }

    public void stopSpawnTask() {
        if (spawnTaskId == -1)
            return;

        Bukkit.getScheduler().cancelTask(spawnTaskId);
        spawnTaskId = -1;
    }
}
