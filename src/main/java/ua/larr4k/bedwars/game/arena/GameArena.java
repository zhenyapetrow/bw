package ua.larr4k.bedwars.game.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import ua.larr4k.bedwars.utility.LocationUtil;
import ua.larr4k.bedwars.utility.MaterialSpawner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameArena {

    private final World gameWorld;
    private final List<MaterialSpawner> materialSpawners;
    private final List<TeamArena> gameTeams;
    private final List<Location> shopPositions;
    private final int minRequiredPlayers;
    private final int maxAllowedPlayers;

    public GameArena(World world, List<MaterialSpawner> spawners, List<TeamArena> teams, List<Location> shops, int minPlayers, int maxPlayers) {
        this.gameWorld = world;
        this.materialSpawners = spawners;
        this.gameTeams = teams;
        this.shopPositions = shops;
        this.minRequiredPlayers = minPlayers;
        this.maxAllowedPlayers = maxPlayers;
    }

    public GameArena(ConfigurationSection configSection) {
        this(
                Bukkit.getWorld(configSection.getString("world", "world")),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                configSection.getInt("min_players"),
                configSection.getInt("max_players")
        );

        ConfigurationSection spawnersSection = configSection.getConfigurationSection("spawners");
        for (String spawnerKey : spawnersSection.getKeys(false)) {
            materialSpawners.add(new MaterialSpawner(spawnersSection.getConfigurationSection(spawnerKey)));
        }

        ConfigurationSection teamsSection = configSection.getConfigurationSection("teams");
        for (String teamKey : teamsSection.getKeys(false)) {
            gameTeams.add(new TeamArena(teamsSection.getConfigurationSection(teamKey)));
        }

        for (String shopLocation : configSection.getStringList("shops")) {
            shopPositions.add(LocationUtil.getLocation(shopLocation));
        }
    }

    public World getGameWorld() {
        return gameWorld;
    }

    public List<MaterialSpawner> getMaterialSpawners() {
        return Collections.unmodifiableList(materialSpawners);
    }

    public List<TeamArena> getGameTeams() {
        return Collections.unmodifiableList(gameTeams);
    }

    public List<Location> getShopPositions() {
        return Collections.unmodifiableList(shopPositions);
    }

    public int getMinRequiredPlayers() {
        return minRequiredPlayers;
    }

    public int getMaxAllowedPlayers() {
        return maxAllowedPlayers;
    }
}
