package com.explorer.locatordisplay.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
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

    public static boolean enabled = true;
    public static boolean onlineUUID = false;
    public static boolean imageIcon = true;

    // icon
    public static final String[] ICONS = {"Near", "Nearby", "Far", "Distant", "Bowtie", "Custom"};
    public static String customDir = "";
    //symbol
    public static final String[] SYMBOLS = {"⬤", "▌", "⬛", "★", "◆", "▶", "✚", "✖", "Custom"};
    public static String customSymbol = "";
    //select index for either
    public static int selectIndex = 0;

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
            return (LocatorDisplayConfig.imageIcon ? customDir : customSymbol);
        }

        return activeArray[selectIndex];
    }

    public static Identifier getSelectedIconIdentifier() {

        if (isCustomSelected()) {
            if (customDir == null || customDir.isBlank()) {
                return null;
            }
            return Identifier.parse(customDir);
        }

        String selection = getCurrentSelection();
        return switch (selection) {
            case "Near" -> Identifier.withDefaultNamespace("hud/locator_bar_dot/default_0");
            case "Nearby" -> Identifier.withDefaultNamespace("hud/locator_bar_dot/default_1");
            case "Far" -> Identifier.withDefaultNamespace("hud/locator_bar_dot/default_2");
            case "Distant" -> Identifier.withDefaultNamespace("hud/locator_bar_dot/default_3");
            case "Bowtie" -> Identifier.withDefaultNamespace("hud/locator_bar_dot/bowtie");
            default -> Identifier.withDefaultNamespace("");
        };
    }

    public static void load() {
        if (Files.exists(CONFIG_FILE)) {
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
                props.load(in);
                enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
                onlineUUID = Boolean.parseBoolean(props.getProperty("onlineUUID", "false"));
                imageIcon = Boolean.parseBoolean(props.getProperty("imageIcon", "true"));
                selectIndex = Integer.parseInt(props.getProperty("selectIndex", "0"));
                customDir = props.getProperty("customDir", "");
                customSymbol = props.getProperty("customSymbol", "⬤");
            } catch (IOException e) {
                LOGGER.error("Failed to load config", e);
            }
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("enabled", Boolean.toString(enabled));
        props.setProperty("onlineUUID", Boolean.toString(onlineUUID));
        props.setProperty("imageIcon", Boolean.toString(imageIcon));
        props.setProperty("selectIndex", Integer.toString(selectIndex));
        props.setProperty("customDir", customDir != null ? customDir : "");
        props.setProperty("customSymbol", customSymbol != null ? customSymbol : "⬤");

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