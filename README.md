# DragonFights Addon
[![Discord](https://img.shields.io/discord/272499714048524288.svg?logo=discord)](https://discord.bentobox.world)
[![Build Status](https://ci.codemc.io/buildStatus/icon?job=BONNePlayground/DragonFights)](https://ci.codemc.io/job/BONNePlayground/job/DragonFights/)

This is DragonFights Addon for BentoBox plugin.  

## How to install

1. Place the addon jar in the addons folder of the BentoBox plugin.
2. Restart the server.
3. Change DragonFights/config.yml to suit your needs.
4. Restart the server again.

## Configuration

The latest configuration file with comments can be found [here](https://github.com/BONNePlayground/DragonFights/blob/develop/addon/src/main/resources/config.yml).

## How to use

This addon works only in GameModes with enabled end islands.
It is not required to have an exit portal in the end blueprint, as users can generate it by placing End Crystal on the bedrock block. It will automatically generate a new exit portal.

To summon a new dragon players must place 4 end crystals on the middle block per each end trophy side. It will start the end dragon summoning sequence.

## FAQ

1. **I have created end trophy but nothing happens.**

    Hmm, it could be a bug in my system. Maybe you could fill the bug report?

2. **My dragon was removed when I teleported to the end.**

    This can happen if dragon was close to spawn position, and it is not added to "remove-mobs-whitelist" in gamemode config. It should be enough if you add ENDER_DRAGON to the whitelist.

3. **There are no obsidian towers around the trophy.**

    Obsidian towers will be generated after player initialize dragon fight. 
   
4. **Players can stop the dragon fight if they destroy end crystals.**

   Yes, that is how it should work. If they destroy end crystals before dragon is summoned, battle is stopped.

5. **How can I get this addon?**

   The official releases can be found here: [Releases](https://github.com/BONNePlayground/DragonFights/releases)
   
   Development versions can be found here: [Releases](https://ci.codemc.io/job/BONNePlayground/job/DragonFights/)

## Placeholders

Addon currently have 2 placeholders:
- `[gamemode]_dragonsfights_killed_dragon_count` - returns number of killed dragons on user.
- `[gamemode]_dragonsfights_visited_killed_dragon_count` - returns number of killed dragons on visited island.

## Compatibility

- [x] BentoBox 1.17
- [x] Spigot 1.17

Addon is not compatible with Older BentoBox and Spigot version. It requires Java 16+.