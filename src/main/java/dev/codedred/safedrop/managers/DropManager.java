package dev.codedred.safedrop.managers;

import dev.codedred.safedrop.SafeDrop;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class DropManager {

  private final SafeDrop plugin;
  private final Map<UUID, Boolean> enabledPlayers = new ConcurrentHashMap<>();
  private final Map<UUID, PendingDrop> pendingDrops = new ConcurrentHashMap<>();
  private Set<Material> protectedMaterials = Set.of();

  public DropManager(SafeDrop plugin) {
    this.plugin = plugin;
    reload();
  }

  public void reload() {
    Set<Material> configured = ConcurrentHashMap.newKeySet();
    for (String value : plugin.getConfig().getStringList("safe-drop.protected.materials")) {
      Material material = Material.matchMaterial(value);
      if (material == null) {
        plugin.getLogger().warning("Ignoring unknown protected material: " + value);
      } else {
        configured.add(material);
      }
    }
    protectedMaterials = Set.copyOf(configured);
    pendingDrops.clear();
  }

  public boolean isEnabled(UUID uuid) {
    return enabledPlayers.getOrDefault(
      uuid,
      plugin.getConfig().getBoolean("safe-drop.enabled-by-default", true)
    );
  }

  public void setEnabled(UUID uuid, boolean enabled) {
    enabledPlayers.put(uuid, enabled);
    pendingDrops.remove(uuid);
  }

  public void removePlayer(UUID uuid) {
    enabledPlayers.remove(uuid);
    pendingDrops.remove(uuid);
  }

  public boolean confirmOrRequest(UUID uuid, ItemStack item) {
    long now = System.currentTimeMillis();
    PendingDrop pending = pendingDrops.get(uuid);

    if (pending != null && pending.expiresAt() >= now && pending.item().isSimilar(item)) {
      pendingDrops.remove(uuid);
      return true;
    }

    long seconds = Math.max(
      1L,
      plugin.getConfig().getLong("safe-drop.confirmation-seconds", 3L)
    );
    pendingDrops.put(
      uuid,
      new PendingDrop(item.clone(), now + (seconds * 1_000L))
    );
    return false;
  }

  public boolean shouldProtect(ItemStack item) {
    String mode = plugin
      .getConfig()
      .getString("safe-drop.protection-mode", "SELECTED")
      .toUpperCase(Locale.ROOT);
    if (mode.equals("ALL")) return true;

    Material material = item.getType();
    String name = material.name();
    ItemMeta meta = item.getItemMeta();

    if (protectedMaterials.contains(material)) return true;
    if (category("enchanted-items") && meta != null && meta.hasEnchants()) return true;
    if (category("named-items") && meta != null && meta.hasDisplayName()) return true;
    if (category("shulker-boxes") && name.endsWith("SHULKER_BOX")) return true;
    if (category("spawn-eggs") && name.endsWith("_SPAWN_EGG")) return true;
    if (category("armour") && isArmour(name)) return true;
    if (category("tools") && isTool(name)) return true;
    return category("weapons") && isWeapon(name);
  }

  private boolean category(String name) {
    return plugin
      .getConfig()
      .getBoolean("safe-drop.protected.categories." + name, false);
  }

  private boolean isTool(String name) {
    return name.endsWith("_PICKAXE") ||
      name.endsWith("_SHOVEL") ||
      name.endsWith("_HOE") ||
      name.endsWith("_AXE") ||
      name.equals("SHEARS") ||
      name.equals("FISHING_ROD") ||
      name.equals("BRUSH") ||
      name.equals("FLINT_AND_STEEL");
  }

  private boolean isWeapon(String name) {
    return name.endsWith("_SWORD") ||
      name.endsWith("_AXE") ||
      name.equals("BOW") ||
      name.equals("CROSSBOW") ||
      name.equals("TRIDENT") ||
      name.equals("MACE");
  }

  private boolean isArmour(String name) {
    return name.endsWith("_HELMET") ||
      name.endsWith("_CHESTPLATE") ||
      name.endsWith("_LEGGINGS") ||
      name.endsWith("_BOOTS") ||
      name.equals("ELYTRA") ||
      name.equals("SHIELD") ||
      name.equals("TURTLE_HELMET");
  }

  private record PendingDrop(ItemStack item, long expiresAt) {}
}
