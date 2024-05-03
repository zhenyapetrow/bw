package ua.larr4k.bedwars.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ua.larr4k.bedwars.game.Game;
import ua.larr4k.bedwars.game.GameManager;
import ua.larr4k.bedwars.game.GameState;
import ua.larr4k.bedwars.game.arena.ArenaManager;
import ua.larr4k.bedwars.game.waiting.Waiting;
import ua.larr4k.bedwars.player.PlayerDataHandler;

import java.util.HashSet;
import java.util.Set;

public class PlayerListener implements Listener {

    private final PlayerDataHandler userManager;
    private final ArenaManager arenaManager;
    private final GameManager gameManager;
    private final Set<Player> respawnAsSpectator;

    public PlayerListener(PlayerDataHandler dataHandler,ArenaManager arenaManager,GameManager gameManager){
        this.userManager = dataHandler;
        this.arenaManager = arenaManager;
        this.gameManager = gameManager;
        respawnAsSpectator = new HashSet<>();
    }


    @EventHandler
    private void onPreLogin(AsyncPlayerPreLoginEvent event) {
        GameState gameState = gameManager.getGameState();
        if (gameState == GameState.END) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "Сервер перезапускается");
            return;
        }
        Waiting waiting = gameManager.getWaiting();
        if (waiting.getPlayers().size() >= arenaManager.getArena().getMaxAllowedPlayers()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "Сервер заполнен");
            return;
        }
        userManager.fetchPlayerData(event.getUniqueId(), event.getName());
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (gameManager.getGameState() != GameState.WAITING) {
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }
        Waiting waiting = gameManager.getWaiting();
        waiting.addPlayer(player);
        player.teleport(waiting.getLobbyLocation());
        player.setGameMode(GameMode.SURVIVAL);
        Bukkit.broadcastMessage(String.format("Игрок §e%s §fприсоединился к игре", player.getName()));
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameState gameState = gameManager.getGameState();
        if (gameState == GameState.WAITING) {
            Waiting waiting = gameManager.getWaiting();
            waiting.removePlayer(player);
            Bukkit.broadcastMessage(String.format("Игрок §e%s §fпокинул игру", player.getName()));
        } else if (gameState == GameState.END) {
            Game game = gameManager.getGame();
            game.removePlayer(player);
        }
        gameManager.getScoreboardManager().clear(player);
        userManager.unloadPlayerData(player);
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
        event.setKeepInventory(true);
        if (gameManager.getGameState() != GameState.GAME) {
            return;
        }
        Player player = event.getEntity();
        if (gameManager.getGame().playerDeath(player)) {
            respawnAsSpectator.add(player);
        }
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();
        if (respawnAsSpectator.contains(player)) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }



}
