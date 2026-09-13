package com.explorer.locatordisplay.client.mixin;

import com.explorer.locatordisplay.client.LocatorDisplayConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// for survival and adventure
@Mixin(InGameHud.class)
public class ExperienceRendererMixin {

    @Final
    private MinecraftClient minecraft;

    @Inject(
            method = "getCurrentBarType",
            at = @At("RETURN"),
            cancellable = true
    )
    private void forceXP(CallbackInfoReturnable<InGameHud.BarType> cir) {

        if (LocatorDisplayConfig.disableLocatorBar && LocatorDisplayConfig.showXP()) {
            if (cir.getReturnValue() == InGameHud.BarType.LOCATOR) {
                cir.setReturnValue(InGameHud.BarType.EXPERIENCE);
            }
        }
    }
}