package ua.larr4k.bedwars.player;

import org.bukkit.entity.Player;
import ua.larr4k.bedwars.database.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlayerDataHandler {

    private final Logger log;
    private final DatabaseConnection dbConnector;
    private final Map<UUID, PlayerData> playerDataMap;

    public PlayerDataHandler(Logger logger, DatabaseConnection connector) {
        this.log = logger;
        this.dbConnector = connector;
        this.playerDataMap = new HashMap<>();
    }

    public PlayerData getPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerDataMap.containsKey(uuid)) {
            return fetchPlayerData(uuid, player.getName());
        }
        return playerDataMap.get(uuid);
    }

    public PlayerData fetchPlayerData(UUID uuid, String name) {
        PlayerData playerData = new PlayerData(name);
        try {
            ResultSet resultSet = dbConnector.prepareStatement("SELECT * FROM `player_stats` WHERE uuid=?", uuid.toString())
                    .get(10, TimeUnit.SECONDS);
            if (!resultSet.next()) {
                createNewPlayer(uuid, name);
            } else {
                playerData = new PlayerData(
                        name,
                        resultSet.getInt("wins"),
                        resultSet.getInt("losses"),
                        resultSet.getInt("kills"),
                        resultSet.getInt("deaths"),
                        resultSet.getInt("brokenBeds")
                );
            }
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            log.log(Level.WARNING, String.format("Error loading player data for %s with uuid %s", name, uuid), exception);
        } catch (SQLException exception) {
            log.log(Level.WARNING, "Error executing SQL query", exception);
        }
        playerDataMap.put(uuid, playerData);
        return playerData;
    }

    private void createNewPlayer(UUID uuid, String name) throws ExecutionException, InterruptedException {
        dbConnector.prepareStatementUpdate("INSERT INTO `player_stats` VALUES(?,?,?,?,?,?,?)",
                uuid.toString(), name, 0, 0, 0, 0, 0);
    }

    public void savePlayerData(Player player) {
        PlayerData playerData = getPlayerData(player);
        dbConnector.prepareStatementUpdate("UPDATE `player_stats` SET wins=?, losses=?, kills=?, deaths=?, brokenBeds=? WHERE uuid=?",
                playerData.getWins(), playerData.getLosses(),
                playerData.getKills(), playerData.getDeaths(),
                playerData.getBrokenBeds(),
                player.getUniqueId().toString()
        );
    }

    public void unloadPlayerData(Player player) {
        savePlayerData(player);
        playerDataMap.remove(player.getUniqueId());
    }
}
