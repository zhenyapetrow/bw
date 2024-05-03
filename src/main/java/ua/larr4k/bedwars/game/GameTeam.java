package ua.larr4k.bedwars.game;

import org.bukkit.entity.Player;
import ua.larr4k.bedwars.game.arena.TeamArena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameTeam {

    private final TeamArena team;
    private final List<Player> teamPlayers;
    private boolean hasBed;

    public GameTeam(TeamArena team) {
        this.team = team;
        teamPlayers = new ArrayList<>();
        hasBed = true;
    }

    public TeamArena getTeam() {
        return team;
    }

    public List<Player> getTeamPlayers() {
        return Collections.unmodifiableList(teamPlayers);
    }

    public boolean hasBed() {
        return hasBed;
    }

    public void setHasBed(boolean hasBed) {
        this.hasBed = hasBed;
    }

    public void addPlayer(Player player) {
        teamPlayers.add(player);
    }

    public void removePlayer(Player player) {
        teamPlayers.remove(player);
    }
}
