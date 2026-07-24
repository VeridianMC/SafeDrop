package dev.codedred.safedrop.listeners;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.DataManager;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.model.User;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerJoinQuit implements Listener {

  private final SafeDrop plugin;
  private final DropManager dropManager;

  public PlayerJoinQuit(SafeDrop plugin, DropManager dropManager) {
    this.plugin = plugin;
    this.dropManager = dropManager;
  }

  public static void initialisePlayer(
    Player player,
    SafeDrop plugin,
    DropManager dropManager
  ) {
    UUID uuid = player.getUniqueId();
    boolean defaultStatus = plugin
      .getConfig()
      .getBoolean("safe-drop.enabled-by-default", true);

    if (plugin.hasDatabase()) {
      plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(plugin, () -> {
          User user = plugin.getDatabaseManager().getUsersTable().getByUuid(uuid);
          boolean enabled = user == null ? defaultStatus : user.isEnabled();
          if (user == null) {
            plugin
              .getDatabaseManager()
              .getUsersTable()
              .insert(new User(uuid, enabled));
          }
          plugin
            .getServer()
            .getScheduler()
            .runTask(plugin, () -> {
              if (player.isOnline()) dropManager.setEnabled(uuid, enabled);
            });
        });
      return;
    }

    DataManager dataManager = DataManager.getInstance();
    String path = "saves." + uuid;
    dropManager.setEnabled(
      uuid,
      dataManager.getSaves().contains(path)
        ? dataManager.getSaves().getBoolean(path)
        : defaultStatus
    );
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    initialisePlayer(event.getPlayer(), plugin, dropManager);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    plugin.savePreference(uuid, dropManager.isEnabled(uuid));
    dropManager.removePlayer(uuid);
  }
}
