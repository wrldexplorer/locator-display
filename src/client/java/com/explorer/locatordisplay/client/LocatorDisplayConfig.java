package com.explorer.locatordisplay.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class LocatorDisplayConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("LocatorDisplay");
    private static final Path CONFIG_FILE =
            FabricLoader.getInstance().getConfigDir().resolve("locator-display.properties");

    // should only show the xp bar in survival and adventure
    // and not in creative and spectator
    public static boolean showXP() {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (minecraft == null || minecraft.player == null) {
            return false;
        }

        return !minecraft.player.isSpectator() && !minecraft.player.isCreative();
    }

    public static boolean enabled = true;
    public static boolean onlineUUID = false;
    public static boolean proximity = false;
    public static boolean colorName = false;
    public static boolean disableLocatorBar = false;
    public static boolean imageIcon = true;

    // icon
    public static final String[] ICONS = {"Near", "Nearby", "Far", "Distant", "Bowtie", "Custom"};
    public static String customDir = "";
    //symbol
    public static final String[] SYMBOLS = {"⬤", "▌", "⬛", "★", "◆", "▶", "✚", "✖", "Custom"};
    public static String customSymbol = "";
    //select index for either
    public static int selectIndex = 0;

    //sets settings to defaults if player messed them up
    public static void resetToDefaults() {
        enabled = true;
        onlineUUID = false;
        proximity = false;
        colorName = false;
        disableLocatorBar = false;
        imageIcon = true;
        selectIndex = 0;
        customDir = "";
        customSymbol = "";
        save();
    }

    //check if alr defaults
    public static boolean isDefault() {
        return enabled
                && !onlineUUID
                && !proximity
                && !colorName
                && !disableLocatorBar
                && imageIcon
                && selectIndex == 0
                && (customDir == null || customDir.isEmpty())
                && (customSymbol == null || customSymbol.isEmpty());
    }

    public static boolean isCustomSelected() {
        String[] activeArray = imageIcon ? ICONS : SYMBOLS;
        return selectIndex >= 0 && selectIndex < activeArray.length && activeArray[selectIndex].equals("Custom");
    }
    public static String getCurrentSelection() {
        String[] activeArray = imageIcon ? ICONS : SYMBOLS;
        if (selectIndex < 0 || selectIndex >= activeArray.length) {
            selectIndex = 0;
        }

        if (isCustomSelected()) {
            return (imageIcon ? customDir : customSymbol);
        }

        return activeArray[selectIndex];
    }

    public static final Identifier DEFAULT_ICON = Identifier.withDefaultNamespace("hud/locator_bar_dot/default_0");


    public static void load() {
        if (Files.exists(CONFIG_FILE)) {
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
                props.load(in);
                enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
            } catch (IOException e) {
                LOGGER.error("Failed to load config", e);
            }
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("enabled", Boolean.toString(enabled));

        try {
            if (Files.notExists(CONFIG_FILE.getParent())) {
                Files.createDirectories(CONFIG_FILE.getParent());
            }
            try (OutputStream out = Files.newOutputStream(CONFIG_FILE)) {
                props.store(out, "Locator Display config");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }
}