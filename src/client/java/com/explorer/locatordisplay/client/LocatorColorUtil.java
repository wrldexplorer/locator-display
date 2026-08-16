package com.explorer.locatordisplay.client;

import net.minecraft.util.ARGB;
import java.util.UUID;

public class LocatorColorUtil {

    public static int getColorFromUuid(UUID uuid) {
        // pack 255 Alpha onto the UUID hashCode integer and adjusts brightness
        int argbColor = ARGB.color(255, uuid.hashCode());
        int colorWithBrightness = ARGB.setBrightness(argbColor, 0.9F);

        // return the color with stripped alpha
        return colorWithBrightness & 0xFFFFFF;
    }
}