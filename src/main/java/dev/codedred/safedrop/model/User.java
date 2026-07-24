package dev.codedred.safedrop.model;

import java.util.UUID;

public record User(UUID uniqueId, boolean enabled) {

  public UUID getUniqueId() {
    return uniqueId;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
