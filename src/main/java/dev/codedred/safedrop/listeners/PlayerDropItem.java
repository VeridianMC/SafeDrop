package dev.codedred.safedrop.listeners;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.utils.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class PlayerDropItem implements Listener {

  private final SafeDrop plugin;
  private final DropManager dropManager;

  public PlayerDropItem(SafeDrop plugin, DropManager dropManager) {
    this.plugin = plugin;
    this.dropManager = dropManager;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onItemDrop(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    if (!player.hasPermission("safedrop.use")) return;
    if (
      player.getGameMode() == GameMode.CREATIVE &&
      !plugin.getConfig().getBoolean("safe-drop.protect-in-creative", false)
    ) return;
    if (!dropManager.isEnabled(player.getUniqueId())) return;
    if (
      player.isSneaking() &&
      plugin.getConfig().getBoolean("safe-drop.sneak-to-bypass", true)
    ) return;

    ItemStack item = event.getItemDrop().getItemStack();
    if (!dropManager.shouldProtect(item)) return;
    if (dropManager.confirmOrRequest(player.getUniqueId(), item)) return;

    event.setCancelled(true);
    long seconds = Math.max(
      1L,
      plugin.getConfig().getLong("safe-drop.confirmation-seconds", 3L)
    );
    Component itemName = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
      ? item.getItemMeta().displayName()
      : Component.translatable(item.translationKey()).color(NamedTextColor.WHITE);

    String chat = plugin.getConfig().getString("messages.confirm-chat", "");
    if (!chat.isBlank()) {
      player.sendMessage(ChatUtils.message(plugin, chat, itemName, seconds));
    }

    String actionbar = plugin
      .getConfig()
      .getString("messages.confirm-actionbar", "");
    if (!actionbar.isBlank()) {
      player.sendActionBar(ChatUtils.message(plugin, actionbar, itemName, seconds));
    }
  }
}
