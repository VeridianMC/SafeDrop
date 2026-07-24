package dev.codedred.safedrop.data.database.datasource.impl;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.database.datasource.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite implements DataSource {

  private Connection connection;

  public SQLite(SafeDrop plugin) {
    String database = plugin
      .getConfig()
      .getString("database-settings.database", "safedrop");

    try {
      Class.forName("org.sqlite.JDBC");
      if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
      String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + database + ".db";
      this.connection = DriverManager.getConnection(url);
      plugin.getLogger().info("Successfully connected to database.");
    } catch (SQLException | ClassNotFoundException exception) {
      plugin
        .getLogger()
        .severe(
          "ERROR! Database failed to connect. Please check your config.yml and try again."
        );
    }
  }

  @Override
  public Connection getConnection() {
    return connection;
  }

  @Override
  public void closeConnection() throws SQLException {
    connection.close();
  }
}
