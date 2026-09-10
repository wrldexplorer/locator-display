// no longer needed, replaced by ExperienceRendererMixin.java

package com.explorer.locatordisplay.client.mixin;

import com.explorer.locatordisplay.client.LocatorDisplayConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.LocatorBar;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocatorBar.class)
public class WaypointRenderer {

    @Inject(
            method = "extractBackground",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideLocatorBarBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (LocatorDisplayConfig.disableLocatorBar) {
            ci.cancel();
        }
    }

    @Inject(
            method = "extractRenderState",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideLocatorBarWaypoints(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (LocatorDisplayConfig.disableLocatorBar) {
            ci.cancel();
        }
    }
}