package com.explorer.locatordisplay.client;

import net.minecraft.util.ARGB;
import java.util.UUID;

public class LocatorColorUtil {
    /**
     * Packs 255 Alpha onto the UUID hashCode integer and adjusts brightness
     * @param uuid
     * @return color with stripped alpha
     */
    public static int getColorFromUuid(UUID uuid) {
        int argbColor = ARGB.color(255, uuid.hashCode());
        int colorWithBrightness = ARGB.setBrightness(argbColor, 0.9F);
        return colorWithBrightness & 0xFFFFFF;
    }
}