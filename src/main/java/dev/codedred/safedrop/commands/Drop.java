package dev.codedred.safedrop.commands;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.managers.DropManager;
import dev.codedred.safedrop.utils.chat.ChatUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class Drop implements TabExecutor {

  private final SafeDrop plugin;
  private final DropManager dropManager;

  public Drop(SafeDrop plugin, DropManager dropManager) {
    this.plugin = plugin;
    this.dropManager = dropManager;
  }

  @Override
  public boolean onCommand(
    @NotNull CommandSender sender,
    @NotNull Command command,
    @NotNull String label,
    @NotNull String[] args
  ) {
    String subcommand = args.length == 0
      ? "toggle"
      : args[0].toLowerCase(Locale.ROOT);

    if (subcommand.equals("reload")) {
      if (!sender.hasPermission("safedrop.admin")) {
        send(sender, "messages.no-permission");
        return true;
      }
      plugin.reloadSafeDrop();
      send(sender, "messages.reloaded");
      return true;
    }

    if (!(sender instanceof Player player)) {
      send(sender, "messages.player-only");
      return true;
    }
    if (!player.hasPermission("safedrop.use")) {
      send(player, "messages.no-permission");
      return true;
    }

    switch (subcommand) {
      case "toggle" -> setStatus(player, !dropManager.isEnabled(player.getUniqueId()));
      case "on" -> setStatus(player, true);
      case "off" -> setStatus(player, false);
      case "status" -> send(
        player,
        dropManager.isEnabled(player.getUniqueId())
          ? "messages.status-enabled"
          : "messages.status-disabled"
      );
      case "help" -> plugin
        .getConfig()
        .getStringList("messages.usage")
        .forEach(line -> player.sendMessage(ChatUtils.message(plugin, line)));
      default -> plugin
        .getConfig()
        .getStringList("messages.usage")
        .forEach(line -> player.sendMessage(ChatUtils.message(plugin, line)));
    }
    return true;
  }

  private void setStatus(Player player, boolean enabled) {
    dropManager.setEnabled(player.getUniqueId(), enabled);
    plugin.savePreference(player.getUniqueId(), enabled);
    send(player, enabled ? "messages.enabled" : "messages.disabled");
  }

  private void send(CommandSender sender, String path) {
    sender.sendMessage(
      ChatUtils.message(plugin, plugin.getConfig().getString(path, ""))
    );
  }

  @Override
  public List<String> onTabComplete(
    @NotNull CommandSender sender,
    @NotNull Command command,
    @NotNull String alias,
    @NotNull String[] args
  ) {
    if (args.length != 1) return List.of();
    List<String> options = new ArrayList<>(
      List.of("on", "off", "status", "help")
    );
    if (sender.hasPermission("safedrop.admin")) options.add("reload");
    String input = args[0].toLowerCase(Locale.ROOT);
    return options.stream().filter(value -> value.startsWith(input)).toList();
  }
}
