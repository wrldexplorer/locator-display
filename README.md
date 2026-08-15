<img src="assets/locator-displayx128.png" alt="Locator Display Logo" width="128">

# Locator Display
### Just a Client-Sided Locator Bar Color Display Mod
Locator Display is a client-side Minecraft mod that adds a colored dot (●) next to player names in the tab list. 
The color is determined by the player's Minecraft UUID, just like the locator bar color.<hr>

## Features
- Displays a colored dot before each player's name in the tab list.
- Uses the official Mojang UUID (via Mojang API) for accurate colors.
- Falls back to a deterministic offline UUID for players not registered with Mojang.
- Fully client-side; works on any server.

<img src="assets/screenshots/mod-game.png" alt="Locator Display Logo" width="720">
<img src="assets/screenshots/tab-menu.png" alt="Locator Display Logo" width="720">
<img src="assets/screenshots/inventory.png" alt="Locator Display Logo" width="720">

## Requirements
- Minecraft 1.21.6+
- Fabric Loader 0.19.3+
- Java 21+

## Installation
1. Download the latest `.jar` from the [Releases](../../releases) page.
2. Place the file into your `.minecraft/mods` folder.
3. Launch Minecraft with Fabric Loader.

## How It Works
1. The mod fetches the player's Mojang UUID from the public Mojang API (asynchronously).
2. It computes a color from the UUID using Java's hash code algorithm (same as the locator bar).
3. A dot is prepended to the player's name in the tab list.

> NOTE: If the player name does not exist in Mojang's database (e.g., offline/cracked server), 
> the mod uses the standard offline UUID (`UUID.nameUUIDFromBytes("OfflinePlayer:<name>")`) 
> so the color remains stable.

## Configuration
Currently, there are no configurable options. This may change in future versions.


## Disclaimer
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.

## License
Code: GPL-3.0-only  
Assets (logo/icon): CC BY-NC-ND 4.0 (see [LICENSE-ASSETS](LICENSE-ASSETS))

## Contributing
Open an issue if you find a bug or have a feature request.

## Credits
Based on the Fabric example mod template (CC0).