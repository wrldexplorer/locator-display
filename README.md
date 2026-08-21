<img src="assets/frames/animated.gif" alt="Locator Display Logo" width="100%">

<img src="assets/locator-displayx128.png" alt="Locator Display Logo" width="128" align="right">

# Locator Display
### Just a Client-Sided Locator Bar Color Display Mod
Locator Display is a client-side Minecraft mod that adds a customizable symbol 
next to player names in the tab list, colored exactly like their locator bar color.
<hr>

## Features
- Displays a customizable colored symbol before each player's name in the tab list.
- Customize the symbol by choosing one of the characters in the preset or by specifying a custom one. 
- Characters in the Preset: `⬤, ▌, ⬛, ★, ◆, ▶, ✚, ✖`
- Uses the official Mojang UUID for accurate colors.
- Falls back to a deterministic offline UUID for players not registered with Mojang.
- Fully client-side; works on any SMP.
- Turns off in servers with thousand of players in the tab list(minigames, pvp servers)

<img src="assets/screenshots/mod-game.png" alt="In-Game" width="720">
<img src="assets/screenshots/tab-menu.png" alt="TAB" width="720">
<img src="assets/screenshots/inventory.png" alt="Locator Bar" width="720">

<hr>

## Requirements
- Minecraft 1.21.6+
- Fabric Loader 0.19.3+
- Java 21+
- ModMenu (recommended)

<hr>

## Installation
1. Download the latest appropriate `.jar` from the [Releases](../../releases) page.
2. Place the file into your `.minecraft/mods` folder.
3. Launch Minecraft with Fabric Loader.

<hr>

## How It Works
1. The mod fetches the player's Mojang UUID from the public Mojang API (asynchronously).
2. It computes a color from the UUID using Java's hash code algorithm (same as the locator bar).
3. The specified symbol is prepended to the player's name in the tab list.

> NOTE: If the player name does not exist in Mojang's database (e.g., offline/cracked server), 
> the mod uses the standard offline UUID (`UUID.nameUUIDFromBytes("OfflinePlayer:<name>")`) 
> so the color remains stable.

<hr>

## Configuration
Mod is configurable with ModMenu.

<img src="assets/screenshots/config.png" alt="ModMenu Configuration" width="720">

### Options:
- `Enabled` → to turn the mod on and off
- `Symbol` → Select desired character to be displayed
- `> Custom Character(max length 3)` → If `Custom` was specified in the previous selection, put your custom symbol here
- `Done` → Saves the configuration and exits

<hr>

## Disclaimer
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.

## License
Code: GPL-3.0-only  
Assets (logo/icon): CC BY-NC-ND 4.0 (see [LICENSE-ASSETS](LICENSE-ASSETS))

## Contributing
Open an issue if you find a bug or have a feature request.
If you want to contribute code-wise, make sure to check the [TODO](TODO) file and opened issues.
By contributing to this repository you agree that all additions/modifications will be licensed
under GPLv3-only and you agree to allow distribution in compiled form.

## Credits
Based on the Fabric example mod template (CC0).
