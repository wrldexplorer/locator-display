package com.explorer.locatordisplay.client.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.waypoints.ClientWaypointManager;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(ClientWaypointManager.class)
public interface ClientWaypointManagerAccessor {
    @Accessor("waypoints")
    Map<Either<UUID, String>, TrackedWaypoint> getWaypoints();
}