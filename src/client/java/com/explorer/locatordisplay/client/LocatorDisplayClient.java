package com.explorer.locatordisplay.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorDisplayClient implements ClientModInitializer, ModMenuApi {

	public static final String MOD_ID = "locator-display";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final String MOD_ART = """
                          ████████████
                    ██████░░░░░░░░░░░░██████
                ████░░░░░░░░░░░░░░░░░░░░░░░░████
              ██░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░██
            ██░░░░        ░░░░░░░░░░░░░░░░░░░░░░░░██
          ██░░░░            ░░░░░░░░░░░░░░░░░░░░░░░░██
        ██░░░░░░            ░░░░░░░░░░░░░░░░░░░░░░░░▒▒██
      ██░░░░░░░░            ░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒██
      ██░░░░░░░░░░        ░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒██
    ██░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒██
    ██░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒██
    ██░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒▒▒██
    ██▒▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒██
    ██▒▒▒▒▒▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒██
      ██▒▒▒▒▒▒▒▒▒▒░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒██
      ██▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒██
        ██▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒██
          ██▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒██
            ██▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒██
              ██▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒██
                ████▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒████
                    ██████▒▒▒▒▒▒▒▒▒▒▒▒██████
                          ████████████

LOCATOR DISPLAY - a client-sided locator bar color display mod
""";

	@Override
	public void onInitializeClient() {
		LocatorDisplayConfig.load(); //for modmenu

		String username = Minecraft.getInstance().getUser().getName();
		LOGGER.info("hihihi {}", username);
		LOGGER.info("Locator Display mod initialized!");
		LOGGER.info("Config enabled: {}", LocatorDisplayConfig.enabled);
		LOGGER.info("\n\n{}", MOD_ART);
	}

	@SuppressWarnings("unused")
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return LocatorDisplayConfigScreen::new;
	}
}