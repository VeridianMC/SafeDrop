package dev.codedred.safedrop;

import dev.codedred.safedrop.commands.Drop;
import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.data.database.manager.DatabaseManager;
import dev.codedred.safedrop.listeners.PlayerDropItem;
import dev.codedred.safedrop.listeners.PlayerJoinQuit;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.model.User;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SafeDrop extends JavaPlugin {

  private DatabaseManager databaseManager;
  private DropManager dropManager;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    DataManager.getInstance();
    dropManager = new DropManager(this);

    PluginCommand command = Objects.requireNonNull(getCommand("safedrop"));
    Drop executor = new Drop(this, dropManager);
    command.setExecutor(executor);
    command.setTabCompleter(executor);

    getServer()
      .getPluginManager()
      .registerEvents(new PlayerDropItem(this, dropManager), this);
    getServer()
      .getPluginManager()
      .registerEvents(new PlayerJoinQuit(this, dropManager), this);

    loadDatabase();
    getServer()
      .getOnlinePlayers()
      .forEach(player -> PlayerJoinQuit.initialisePlayer(player, this, dropManager));

    getLogger().info("SafeDrop " + getDescription().getVersion() + " enabled.");
  }

  @Override
  public void onDisable() {
    if (
      databaseManager != null &&
      databaseManager.getDataSource() != null &&
      databaseManager.getDataSource().getConnection() != null
    ) {
      try {
        databaseManager.getDataSource().closeConnection();
      } catch (Exception exception) {
        getLogger().warning("Could not close the database connection cleanly.");
      }
    }
  }

  public void reloadSafeDrop() {
    reloadConfig();
    DataManager.getInstance().reload();
    dropManager.reload();
  }

  public void loadDatabase() {
    if (!getConfig().getBoolean("database-settings.enabled", false)) return;
    try {
      databaseManager = new DatabaseManager(this);
      databaseManager.load();
    } catch (Exception exception) {
      databaseManager = null;
      getLogger().severe(
        "Database initialisation failed. SafeDrop will use saves.yml instead."
      );
    }
  }

  public boolean hasDatabase() {
    return databaseManager != null &&
      databaseManager.getDataSource() != null &&
      databaseManager.getDataSource().getConnection() != null;
  }

  public DatabaseManager getDatabaseManager() {
    return databaseManager;
  }

  public void savePreference(UUID uuid, boolean enabled) {
    if (hasDatabase()) {
      databaseManager.getUsersTable().update(new User(uuid, enabled));
      return;
    }
    DataManager dataManager = DataManager.getInstance();
    dataManager.getSaves().set("saves." + uuid, enabled);
    dataManager.saveSaves();
  }
}
