# SafeDrop

SafeDrop protects valuable items from accidental drops on modern Paper servers.
This VeridianMC-maintained fork is based on the original
[CodedRed SafeDrop](https://github.com/CodedRed-Spigot/SafeDrop) project.

## Requirements

- Paper 1.21.11 or newer
- Java 21 or newer

## Features

- Confirms the exact item being dropped, rather than granting a temporary
  player-wide bypass
- Protects all items or selected valuable-item categories
- Configurable tools, weapons, armour, enchanted items, named items, shulker
  boxes, spawn eggs and individual materials
- Optional sneak-and-drop bypass for deliberate drops
- Chat and action-bar feedback using MiniMessage
- Per-player preferences stored in YAML, SQLite or MySQL
- Player-friendly toggle, status and help commands
- Command tab completion

## Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/safedrop` | Toggle protection | `safedrop.use` |
| `/safedrop on` | Enable protection | `safedrop.use` |
| `/safedrop off` | Disable protection | `safedrop.use` |
| `/safedrop status` | Show the current status | `safedrop.use` |
| `/safedrop help` | Show player help | `safedrop.use` |
| `/safedrop reload` | Reload the configuration | `safedrop.admin` |

`/sd` remains available as an alias. The legacy `sd.use` and `sd.admin`
permission nodes are retained as compatibility aliases.

## Building

```bash
./gradlew clean build
```

The deployable JAR is produced in `build/libs/`.

## Licence and credits

SafeDrop remains available under the MIT Licence. The original project was
created by CodedRed Spigot and is maintained in this fork by VeridianMC.
