package ua.larr4k.bedwars.game.arena;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ua.larr4k.bedwars.App;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {

    private final List<GameArena> arenas;
    private GameArena arena;

    public ArenaManager(App plugin) {
        List<GameArena> arenas = new ArrayList<>();
        File parent = new File(plugin.getDataFolder(), "arenas");

        if (!parent.exists()) {
            parent.mkdirs();
        } else {
            File[] arenaFiles = parent.listFiles();
            if (arenaFiles != null) {
                for (File arenaFile : arenaFiles) {
                    FileConfiguration arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
                    ConfigurationSection configurationSection = arenaConfig.getConfigurationSection("arena");
                    if (configurationSection != null) {
                        arenas.add(new GameArena(configurationSection));
                    }
                }
            }
        }
        this.arenas = Collections.unmodifiableList(arenas);
    }

    public GameArena getArena() {
        if (arena == null) {
            loadRandomArena();
        }
        return arena;
    }

    public void loadRandomArena() {
        if (arenas.size() == 1) {
            arena = arenas.get(0);
            return;
        }
        arena = arenas.get(ThreadLocalRandom.current().nextInt(arenas.size() - 1));
    }
}
