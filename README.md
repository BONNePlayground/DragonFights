# DragonFights Addon
[![Discord](https://img.shields.io/discord/272499714048524288.svg?logo=discord)](https://discord.bentobox.world)
[![Build Status](https://ci.codemc.io/buildStatus/icon?job=BONNePlayground/DragonFights)](https://ci.codemc.io/job/BONNePlayground/job/DragonFights/)

This is DragonFights Addon for BentoBox plugin.  

## How to install

1. Place the addon jar in the addons folder of the BentoBox plugin
2. Restart the server
3. In game you can change flags that allows to use current addon.

## Configuration

The latest configuration file with comments can be found [here](https://github.com/BONNePlayground/DragonFights/blob/develop/addon/src/main/resources/config.yml).

## How to use

This addon works only in GameModes with enabled end islands.
It is not required to have an exit portal in the end blueprint, as users can generate it by placing End Crystal on the bedrock block. It will automatically generate a new exit portal.

To summon a new dragon players must place 4 end crystals on the middle block per each side.

## FAQ

1. **I have created exit portal but nothing happens.**

    Yes, sometimes there are some issues with detecting a correct portal or spawning an ender dragon. It should be enough if you place a new End Crystal on the middle bedrock block.

2. **My dragon was removed when I teleported to the end.**

    This can happen if dragon was close to spawn position, and it is not added to "remove-mobs-whitelist" in gamemode config. It should be enough if you add ENDER_DRAGON to the whitelist.

3. **There are no obsidian towers around the portal.**

    Yes, it is not an easy task to generate obsidian towers without ruining other blocks in that positions and avoiding to generating them from the bottom. 
    Maybe will implement in the future, if enough people will request it.

## Placeholders

Addon currently have 2 placeholders:
- `[gamemode]_dragonsfighs_killed_dragon_count` - returns number of killed dragons on user.
- `[gamemode]_dragonsfighs_visited_killed_dragon_count` - returns number of killed dragons on visited island.

## Compatibility

- [x] BentoBox 1.15.0