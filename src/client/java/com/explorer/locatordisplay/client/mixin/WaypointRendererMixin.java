package com.explorer.locatordisplay.client.mixin;

import com.explorer.locatordisplay.client.LocatorDisplayConfig;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.LocatorBar;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// for creative and spectator
@Mixin(LocatorBar.class)
public class WaypointRendererMixin {

    @Inject(
            method = "renderBar",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideLocatorBarBackground(DrawContext graphics, RenderTickCounter deltaTracker, CallbackInfo ci) {
        if (LocatorDisplayConfig.disableLocatorBar && !LocatorDisplayConfig.showXP()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "renderAddons",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideLocatorBarWaypoints(DrawContext graphics, RenderTickCounter deltaTracker, CallbackInfo ci) {
        if (LocatorDisplayConfig.disableLocatorBar && !LocatorDisplayConfig.showXP()) {
            ci.cancel();
        }
    }
}