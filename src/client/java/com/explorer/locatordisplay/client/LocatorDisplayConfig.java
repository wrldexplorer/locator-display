package com.explorer.locatordisplay.client;

import net.fabricmc.loader.api.FabricLoader;
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
    public static final String[] SYMBOLS = {"⬤", "▌", "⬛", "★", "◆", "▶", "✚", "✖", "Custom"};
    public static int symbolIndex = 0;
    public static String customSymbol = "";
    public static boolean isCustomSelected() {
        return symbolIndex >= 0 && symbolIndex < SYMBOLS.length && SYMBOLS[symbolIndex].equals("Custom");
    }
    public static String getCurrentSymbol() {
        if (symbolIndex < 0 || symbolIndex >= SYMBOLS.length) {
            symbolIndex = 0;
        }

        if (isCustomSelected()) {
            return customSymbol;
        }

        return SYMBOLS[symbolIndex];
    }

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