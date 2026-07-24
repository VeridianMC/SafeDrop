package dev.codedred.safedrop.data.database.datasource.impl;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.database.datasource.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL implements DataSource {

  private Connection connection;

  public MySQL(SafeDrop plugin) {
    var config = plugin
      .getConfig()
      .getConfigurationSection("database-settings");

    String host = config.getString("host");
    String port = config.getString("port");
    String user = config.getString("user");
    String password = config.getString("password");
    String database = config.getString("database");

    String connectionUrl = String.format(
      "jdbc:mysql://%s:%s/%s?useSSL=true&serverTimezone=UTC",
      host,
      port,
      database
    );

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");

      this.connection =
        DriverManager.getConnection(connectionUrl, user, password);
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
