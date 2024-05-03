package ua.larr4k.bedwars.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import ua.larr4k.bedwars.game.GameManager;
import ua.larr4k.bedwars.game.GameState;
import ua.larr4k.bedwars.game.GameTeam;
import ua.larr4k.bedwars.game.arena.TeamArena;
import ua.larr4k.bedwars.player.PlayerData;
import ua.larr4k.bedwars.player.PlayerDataHandler;
import ua.larr4k.bedwars.utility.cache.BlockCache;

public final class BlockListeners implements Listener {

    private final GameManager gameManager;
    private final PlayerDataHandler userManager;

    public BlockListeners(GameManager gameManager, PlayerDataHandler userManager) {
        this.gameManager = gameManager;
        this.userManager = userManager;
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        if (gameManager.getGameState() != GameState.GAME) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        BlockCache blockCache = gameManager.getGame().getBlockCache();
        blockCache.addBlock(block);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (gameManager.getGameState() != GameState.GAME) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        if (block.getType() == Material.BED_BLOCK) {
            Player player = event.getPlayer();
            PlayerData userData = userManager.getPlayerData(player);
            userData.addBrokenBed();
            GameTeam gameTeam = gameManager.getGame().getTeamByBedLocation(block.getLocation());
            event.setCancelled(true);
            gameTeam.setHasBed(false);
            TeamArena arenaTeam = gameTeam.getTeam();
            setAir(arenaTeam.getPrimaryBedLocation());
            setAir(arenaTeam.getSecondaryBedLocation());
            Bukkit.broadcastMessage("§fКровать команды " + arenaTeam.getTeamDisplayName() + " §fбыла сломана");
            return;
        }
        BlockCache blockCache = gameManager.getGame().getBlockCache();
        if (!blockCache.containsBlock(block)) {
            event.setCancelled(true);
            return;
        }
        blockCache.removeBlock(block);
    }

    private void setAir(Location location) {
        location.getBlock().setType(Material.AIR);
    }
}