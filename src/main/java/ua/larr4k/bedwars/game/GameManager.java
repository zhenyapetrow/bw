package ua.larr4k.bedwars.game;

import ua.larr4k.bedwars.App;
import ua.larr4k.bedwars.game.waiting.Waiting;
import ua.larr4k.bedwars.utility.ScoreboardManager;

public class GameManager {

    private final Waiting waiting;
    private final ScoreboardManager scoreboardManager;
    private GameState gameState;
    private Game game;

    public GameManager(App plugin) {
        waiting = new Waiting(plugin, this, plugin.getArenaManager().getArena());
        waiting.startCount();
        scoreboardManager = new ScoreboardManager(plugin, this);
        gameState = GameState.WAITING;
    }

    public Waiting getWaiting() {
        return waiting;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}