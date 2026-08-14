package com.explorer.locatordisplay.client;

import java.util.UUID;

public class LocatorColorUtil {

    public static int getColorFromUuid(UUID uuid) {
        // get x64 halfs
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();

        // combine them w/ XOR
        long combined = mostSigBits ^ leastSigBits;
        int hash = (int)(combined ^ (combined >>> 32));

        // get rgb from the low 24 bits of the hash
        int red = (hash >> 16) & 0xFF;
        int green = (hash >> 8) & 0xFF;
        int blue = hash & 0xFF;

        // return packed 0xRRGGBB integer
        return (red << 16) | (green << 8) | blue;
    }
}