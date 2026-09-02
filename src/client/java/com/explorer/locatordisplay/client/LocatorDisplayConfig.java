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

    //sets settings to defaults if player messed them up
    public static void resetToDefaults() {
        enabled = true;
        onlineUUID = false;
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
    public static Identifier getSelectedIconIdentifier() {

        if (isCustomSelected()) {
            if (customDir == null || customDir.isBlank()) {
                return DEFAULT_ICON;
            }
            return resolveCustomIdentifier(customDir);
        }

        String selection = getCurrentSelection();
        return switch (selection) {
            case "Nearby" -> Identifier.withDefaultNamespace("hud/locator_bar_dot/default_1");
            case "Far" -> Identifier.withDefaultNamespace("hud/locator_bar_dot/default_2");
            case "Distant" -> Identifier.withDefaultNamespace("hud/locator_bar_dot/default_3");
            case "Bowtie" -> Identifier.withDefaultNamespace("hud/locator_bar_dot/bowtie");
            default -> DEFAULT_ICON;
        };
    }

    //mc 26.2 dirs @assets/textures/
    private static boolean isStandardTextureDir(String path) {
        String[] dirs = {
                "block", "colormap", "effect", "entity", "environment",
                "font", "gui", "item", "map", "misc", "mob_effect",
                "painting", "particle", "trims"
        };
        for (String dir : dirs) {
            if (path.startsWith(dir + "/")) {
                return true;
            }
        }
        return false;
    }

    private static Identifier resolveCustomIdentifier(String pathInput) {
        if (pathInput == null || pathInput.isBlank()) {
            LOGGER.info("[LocatorDisplay] Invalid/Empty input detected, defaulting");
            return DEFAULT_ICON;
        }

        pathInput = pathInput.trim();
        String cleaned = pathInput.replace('\\', '/');
        //default fallbacks
        String namespace = "minecraft";
        String path = cleaned;

        // Extract namespace "modid:block/custom_block" or "minecraft:block/dirt"
        if (cleaned.contains(":")) {
            LOGGER.info("[LocatorDisplay] {} contains ':'", cleaned);
            String[] split = cleaned.split(":", 2);
            namespace = split[0].trim();
            path = split[1].trim();
        }

        // Strip leading slash
        if (path.startsWith("/")) {
            LOGGER.info("[LocatorDisplay] {} starts with '/', stripping", path);
            path = path.substring(1);
        }

        if (isStandardTextureDir(path) && !path.startsWith("textures/")) {
            LOGGER.info("[LocatorDisplay] standard path doesnt start with 'textures/'");
            path = "textures/" + path;
        }

        if (path.startsWith("textures/") && !path.endsWith(".png")) {
            LOGGER.info("[LocatorDisplay] path doesnt end with 'png'");
            path = path + ".png";
        }
        path = path.toLowerCase();

        // Final safety check to completely prevent IdentifierException crashes
        if (!Identifier.isValidNamespace(namespace) || !Identifier.isValidPath(path)) {
            LOGGER.error("[LocatorDisplay] Invalid custom icon identifier characters: {}:{}", namespace, path);
            return DEFAULT_ICON;
        }

        LOGGER.info("[LocatorDisplay] Returning identifier with namespace:'{}', and path:'{}'", namespace, path);
        return Identifier.fromNamespaceAndPath(namespace, path);
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