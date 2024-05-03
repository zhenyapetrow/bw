package ua.larr4k.bedwars.player;

public final class PlayerData {

    private final String playerName;
    private int wins;
    private int losses;
    private int kills;
    private int deaths;
    private int brokenBeds;

    public PlayerData(String playerName) {
        this.playerName = playerName;
        wins = 0;
        losses = 0;
        kills = 0;
        deaths = 0;
        brokenBeds = 0;
    }

    public PlayerData(String playerName, int wins, int losses, int kills, int deaths, int brokenBeds) {
        this.playerName = playerName;
        this.wins = wins;
        this.losses = losses;
        this.kills = kills;
        this.deaths = deaths;
        this.brokenBeds = brokenBeds;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTotalGames() {
        return wins + losses;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getBrokenBeds() {
        return brokenBeds;
    }

    public void addWin() {
        wins++;
    }

    public void addLoss() {
        losses++;
    }

    public void addKill() {
        kills++;
    }

    public void addDeath() {
        deaths++;
    }

    public void addBrokenBed() {
        brokenBeds++;
    }
}
