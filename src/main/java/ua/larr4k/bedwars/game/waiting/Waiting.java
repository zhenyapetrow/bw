package ua.larr4k.bedwars.game.waiting;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ua.larr4k.bedwars.App;
import ua.larr4k.bedwars.game.Game;
import ua.larr4k.bedwars.game.GameManager;
import ua.larr4k.bedwars.game.GameState;
import ua.larr4k.bedwars.game.arena.GameArena;
import ua.larr4k.bedwars.utility.LocationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Waiting {

    private final App plugin;
    private final GameManager gameManager;
    private final Game game;
    private final GameArena arena;
    private final List<Player> players;
    private final Location lobbyLocation;
    private int toStart;

    public Waiting(App plugin, GameManager gameManager, GameArena arena) {
        this.plugin = plugin;
        game = new Game(plugin, gameManager, arena);
        this.gameManager = gameManager;
        gameManager.setGame(game);
        this.arena = arena;
        players = new ArrayList<>();
        FileConfiguration config = plugin.getConfig();
        toStart = config.getInt("waiting.toStart", 30);
        lobbyLocation = LocationUtil.getLocation(config.getString("waiting.lobby_location"));
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    private void startGame() {
        gameManager.setGameState(GameState.GAME);
        game.start(players);
    }

    public void startCount() {
        new BukkitRunnable() {
            public void run() {
                if (players.size() < arena.getMinRequiredPlayers())
                    return;

                toStart--;
                if (toStart == 0) {
                    startGame();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}