package ua.larr4k.bedwars.game.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import ua.larr4k.bedwars.utility.LocationUtil;

public class TeamArena {

    private final String teamName;
    private final String teamColor;
    private final Location spawnPoint;
    private final Location primaryBedLocation;
    private final Location secondaryBedLocation;

    public TeamArena(String name, String color, Location spawn, Location bedLocation) {
        this.teamName = name;
        this.teamColor = color;
        this.spawnPoint = spawn;
        this.primaryBedLocation = bedLocation;

        Block primaryBedBlock = bedLocation.getBlock();
        Location secondaryBed = null;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block secondaryBedBlock = primaryBedBlock.getRelative(x, 0, z);
                if (!secondaryBedBlock.getLocation().equals(bedLocation) && secondaryBedBlock.getType() == Material.BED_BLOCK) {
                    secondaryBed = secondaryBedBlock.getLocation();
                    break;
                }
            }
        }
        this.secondaryBedLocation = secondaryBed;
    }

    public TeamArena(ConfigurationSection config) {
        this(
                config.getString("name"),
                config.getString("color"),
                LocationUtil.getLocation(config.getString("spawn_point")),
                LocationUtil.getLocation(config.getString("bed_location"))
        );
    }

    public String getTeamDisplayName() {
        return getPrefix() + teamName;
    }

    private String getPrefix() {
        return "§" + teamColor;
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public Location getPrimaryBedLocation() {
        return primaryBedLocation;
    }

    public Location getSecondaryBedLocation() {
        return secondaryBedLocation;
    }
}
