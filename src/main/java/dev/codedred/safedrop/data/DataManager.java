package dev.codedred.safedrop.data;

import dev.codedred.safedrop.SafeDrop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class DataManager {

  private static DataManager instance;
  private final CustomFile saves;

  private DataManager() {
    saves = new CustomFile(JavaPlugin.getPlugin(SafeDrop.class), "saves.yml");
  }

  public static DataManager getInstance() {
    if (instance == null) instance = new DataManager();
    return instance;
  }

  public FileConfiguration getSaves() {
    return saves.getConfig();
  }

  public void reload() {
    saves.reloadConfig();
  }

  public void saveSaves() {
    saves.saveConfig();
  }
}
