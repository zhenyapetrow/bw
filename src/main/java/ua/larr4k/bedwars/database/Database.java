package ua.larr4k.bedwars.database;

import java.util.Objects;

public final class Database {

    private final String user, password, host, port, database;

    public Database(String user, String password, String host, String port, String database) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        this.database = database;
    }

    public String getUser() { return user; }
    public String getPassword() { return password; }
    public String getHost() { return host; }
    public String getPort() { return port; }
    public String getDatabase() { return database; }

    public String getConnectionString() {
        return String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true&characterEncoding=utf8", host, port, database);
    }

    public String toString() {
        return String.format("DatabaseData{ user='%s', password='%s', host='%s', port='%s', database='%s' }",
                user, password, host, port, database);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Database that = (Database) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(password, that.password) &&
                Objects.equals(host, that.host) &&
                Objects.equals(port, that.port) &&
                Objects.equals(database, that.database);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, password, host, port, database);
    }
}
