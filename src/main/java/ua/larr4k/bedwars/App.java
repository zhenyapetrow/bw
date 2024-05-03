package ua.larr4k.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import ua.larr4k.bedwars.database.Database;
import ua.larr4k.bedwars.database.DatabaseConnection;
import ua.larr4k.bedwars.game.GameManager;
import ua.larr4k.bedwars.game.arena.ArenaManager;
import ua.larr4k.bedwars.listener.BlockListeners;
import ua.larr4k.bedwars.player.PlayerDataHandler;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends JavaPlugin {

    public static App instance;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private PlayerDataHandler userManager;

    @Override
    public void onEnable() {
        Logger logger = getLogger();
        instance = this;
        ConfigurationSection databaseSection = getConfig().getConfigurationSection("database");
        Database databaseData = new Database(
                databaseSection.getString("login", "root"),
                databaseSection.getString("password", ""),
                databaseSection.getString("host", "localhost"),
                databaseSection.getString("port", "3306"),
                databaseSection.getString("database", "bedwars")
        );
        DatabaseConnection databaseConnection;
        try {
            databaseConnection = new DatabaseConnection(databaseData, 8);
        } catch (Exception exception) {
            logger.log(Level.WARNING, "Database isn`t connected\n" + databaseData, exception);
            Bukkit.getServer().shutdown();
            return;
        }

        arenaManager = new ArenaManager(this);
        if (arenaManager.getArena() == null) {
            logger.log(Level.WARNING, "None of the arenas isn`t loaded");
            Bukkit.getServer().shutdown();
            return;
        }
        gameManager = new GameManager(this);
        userManager = new PlayerDataHandler(logger, databaseConnection);
        Bukkit.getPluginManager().registerEvents(new BlockListeners(gameManager,userManager),this);

    }

    @Override
    public void onDisable() {

    }

    public static App getInstance() {
        return instance;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PlayerDataHandler getUserManager() {
        return userManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
