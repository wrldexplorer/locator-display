package com.explorer.locatordisplay.client;

import java.util.UUID;
import net.minecraft.util.math.ColorHelper;

public class LocatorColorUtil {
    /**
     * Packs 255 Alpha onto the UUID hashCode integer and adjusts brightness
     * @param uuid
     * @return color with stripped alpha
     */

    public static int getColorFromUuid(UUID uuid) {
        int argbColor = ColorHelper.fullAlpha(uuid.hashCode());
        int colorWithBrightness = ColorHelper.withBrightness(argbColor, 0.9F);
        return colorWithBrightness & 0xFFFFFF;
    }
}