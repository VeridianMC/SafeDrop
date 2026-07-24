package dev.codedred.safedrop.utils.chat;

import dev.codedred.safedrop.SafeDrop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class ChatUtils {

  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  private ChatUtils() {}

  public static Component message(SafeDrop plugin, String value) {
    return message(plugin, value, Component.empty(), 0L);
  }

  public static Component message(
    SafeDrop plugin,
    String value,
    Component item,
    long seconds
  ) {
    String prefix = plugin.getConfig().getString("messages.prefix", "");
    TagResolver resolver = TagResolver.resolver(
      Placeholder.parsed("prefix", prefix),
      Placeholder.component("item", item),
      Placeholder.unparsed("seconds", Long.toString(seconds))
    );
    return MINI_MESSAGE.deserialize(value, resolver);
  }
}
