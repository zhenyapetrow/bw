package ua.larr4k.bedwars.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ua.larr4k.bedwars.App;
import ua.larr4k.bedwars.game.arena.GameArena;
import ua.larr4k.bedwars.game.arena.TeamArena;
import ua.larr4k.bedwars.game.shop.Shop;
import ua.larr4k.bedwars.player.PlayerData;
import ua.larr4k.bedwars.player.PlayerDataHandler;
import ua.larr4k.bedwars.utility.MaterialSpawner;
import ua.larr4k.bedwars.utility.ScoreboardManager;
import ua.larr4k.bedwars.utility.cache.BlockCache;

import java.util.*;

public class Game {

    private final App plugin;
    private final GameManager gameManager;
    private final PlayerDataHandler userManager;
    private final GameArena arena;
    private final List<GameTeam> teams;
    private final Map<Location, GameTeam> bedTeams;
    private final Map<Player, GameTeam> playerTeams;
    private final Shop shop;
    private final BlockCache blockCache;

    public Game(App plugin, GameManager gameManager, GameArena arena) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.userManager = plugin.getUserManager();
        this.arena = arena;
        teams = new ArrayList<>();
        bedTeams = new HashMap<>();
        playerTeams = new HashMap<>();
        shop = new Shop(plugin.getConfig().getConfigurationSection("shop"));
        blockCache = new BlockCache();

        initializeTeams();
    }

    private void initializeTeams() {
        for (TeamArena arenaTeam : arena.getGameTeams()) {
            GameTeam gameTeam = new GameTeam(arenaTeam);
            bedTeams.put(arenaTeam.getPrimaryBedLocation(), gameTeam);
            bedTeams.put(arenaTeam.getSecondaryBedLocation(), gameTeam);
            teams.add(gameTeam);
        }
    }

    public GameArena getArena() {
        return arena;
    }

    public List<GameTeam> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public GameTeam getTeamByBedLocation(Location bedLocation) {
        return bedTeams.get(bedLocation);
    }

    public GameTeam getPlayerTeam(Player player) {
        return playerTeams.get(player);
    }

    public Shop getShop() {
        return shop;
    }

    public BlockCache getBlockCache() {
        return blockCache;
    }

    public void removePlayer(Player player) {
        GameTeam team = playerTeams.remove(player);
        if (team != null) {
            team.removePlayer(player);
            if (team.getTeamPlayers().isEmpty()) {
                teams.remove(team);
                Bukkit.broadcastMessage("§fКоманда " + team.getTeam().getTeamDisplayName() + " §fбыла уничтожена");
                if (teams.size() == 1) {
                    end(teams.get(0).getTeamPlayers());
                }
            }
        }
    }

    public boolean playerDeath(Player player) {
        PlayerData playerUser = userManager.getPlayerData(player);
        playerUser.addDeath();

        Player killer = player.getKiller();
        if (killer != null) {
            PlayerData killerUser = userManager.getPlayerData(killer);
            killerUser.addKill();
            Bukkit.broadcastMessage(String.format("Игрок %s был убит игроком %s", player.getName(), killer.getName()));
        } else {
            Bukkit.broadcastMessage(String.format("Игрок %s умер", player.getName()));
        }

        GameTeam team = getPlayerTeam(player);
        if (!team.hasBed()) {
            playerUser.addLoss();
            removePlayer(player);
            return true;
        }

        return false;
    }

    private void balancePlayers(List<Player> players) {
        Collections.shuffle(players);
        Queue<Player> balance = new LinkedList<>(players);
        while (!balance.isEmpty()) {
            for (GameTeam gameTeam : teams) {
                Player player = balance.poll();
                gameTeam.addPlayer(player);
                playerTeams.put(player, gameTeam);
            }
        }
    }

    private void spawnPlayers() {
        for (Map.Entry<Player, GameTeam> playerEntry : playerTeams.entrySet()) {
            playerEntry.getKey().teleport(playerEntry.getValue().getTeam().getSpawnPoint());
        }
    }

    public void start(List<Player> players) {
        for (Location shopLocation : arena.getShopPositions()) {
            shop.spawn(shopLocation);
        }
        balancePlayers(players);
        spawnPlayers();

        arena.getMaterialSpawners().forEach(arenaMaterialSpawner -> arenaMaterialSpawner.startSpawnTask(plugin));
        gameManager.getScoreboardManager().start();
    }

    public void end(List<Player> winners) {
        if (!winners.isEmpty()) {
            for (Player player : winners) {
                PlayerData userData = userManager.getPlayerData(player);
                userData.addWin();
            }
        }
        arena.getMaterialSpawners().forEach(MaterialSpawner::stopSpawnTask);

        ScoreboardManager scoreboardManager = gameManager.getScoreboardManager();
        scoreboardManager.stop();
        scoreboardManager.clear();
        gameManager.setGameState(GameState.END);

        new BukkitRunnable() {
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("Сервер перезапускается"));
                arena.getGameWorld().getEntities().forEach(Entity::remove);
                blockCache.clear();
                Bukkit.getServer().shutdown();
            }
        }.runTaskLater(plugin, 20 * 20);
    }
}
