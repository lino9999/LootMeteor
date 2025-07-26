# LootMeteor Plugin for Spigot 1.21.4

A dynamic meteor spawning plugin that creates exciting loot events for your Minecraft server.

## Features

- **Automatic Meteor Spawns**: Meteors spawn at configurable intervals
- **Custom Loot System**: Configure loot with a user-friendly GUI
- **Visual Effects**: Beautiful meteor fall animation with particles and sounds
- **Crater Generation**: Creates realistic impact craters with obsidian spheres
- **Automatic Regeneration**: Terrain regenerates after a configurable time
- **Player Notifications**: Nearest player receives coordinates of meteor impact
- **Update Checker**: Automatic checking for plugin updates via SpigotMC API
- **Full Customization**: All messages and settings are configurable

## Installation

1. Download the LootMeteor.jar file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/LootMeteor/config.yml`

## Commands

- `/lootmeteor edit` or `/lm edit` - Open the loot editor GUI
- `/lootmeteor reload` or `/lm reload` - Reload configuration files
- `/lootmeteor spawn` or `/lm spawn` - Force spawn a meteor

## Permissions

- `lootmeteor.admin` - Access to all LootMeteor commands (default: op)
- `lootmeteor.bypass` - Bypass meteor chest protection (default: op)

## Configuration Files

### config.yml
```yaml
meteor:
  spawn-interval: 10        # Minutes between spawns
  world: world             # World name
  min-radius: 100          # Min distance from spawn
  max-radius: 500          # Max distance from spawn
  height: 150              # Spawn height
  speed: 0.5               # Fall speed
  explosion-power: 4.0     # Visual explosion size
  crater-radius: 5         # Crater size
  regeneration-time: 10    # Minutes before regeneration
  fire-spread: true        # Enable fire in crater
  obsidian-sphere-radius: 3 # Obsidian sphere size
```

### loot.yml
Automatically generated with default items. Can be edited via GUI or manually.

### messages.yml
All plugin messages can be customized here.
