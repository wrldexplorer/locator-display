package com.explorer.locatordisplay.client.mixin;

import com.explorer.locatordisplay.client.LocatorColorUtil;
import com.explorer.locatordisplay.client.LocatorDisplayConfig;
import com.explorer.locatordisplay.client.LocatorDisplayTabLayout;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
public class TabMenuOverlayMixin {

    @ModifyConstant(method = "extractRenderState", constant = @Constant(intValue = 13))
    private int widenRowForIcon(int vanillaPingWidth) {
        if (!LocatorDisplayConfig.enabled || !LocatorDisplayConfig.imageIcon) {
            return vanillaPingWidth;
        }
        return vanillaPingWidth + LocatorDisplayTabLayout.ICON_SLOT;
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/PlayerFaceExtractor;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;IIIZZI)V"
            )
    )
    private void shiftHeadPastIcon(
            GuiGraphicsExtractor graphics,
            Identifier texture,
            int faceX,
            int faceY,
            int size,
            boolean hat,
            boolean flip,
            int color,
            Operation<Void> original
    ) {
        if (LocatorDisplayConfig.enabled && LocatorDisplayConfig.imageIcon) {
            faceX += LocatorDisplayTabLayout.ICON_SLOT;
        }
        original.call(graphics, texture, faceX, faceY, size, hat, flip, color);
    }
    /**
     * The injection method
     * modifies the tab menu to include the texture
     */
    @Inject(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
            )
    )
    private void drawCustomIcon(
            GuiGraphicsExtractor graphics,
            int screenWidth,
            Scoreboard scoreboard,
            Objective displayObjective,
            CallbackInfo ci,
            @Local PlayerInfo playerEntry,
            @Local(name = "xo") int contentXOffset,
            @Local(name = "yo") int contentYOffset,
            @Local(name = "showHead") boolean isRenderingHead
    ) { //if the mod is enabled and the texture option is selected
        if (!LocatorDisplayConfig.enabled || !LocatorDisplayConfig.imageIcon) {
            return;
        }

        Identifier iconIdentifier = LocatorDisplayConfig.getSelectedIconIdentifier();
        if (iconIdentifier == null || iconIdentifier.getPath().isEmpty()) {
            return;
        }

        int slotLeftX = isRenderingHead ? contentXOffset - 9 : contentXOffset;
        int rgbColor = LocatorColorUtil.getColorFromUuid(playerEntry.getProfile().id());
        int iconColorTint = 0xFF000000 | (rgbColor & 0x00FFFFFF);

        // drawing as a registered GUI sprite
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                iconIdentifier,
                slotLeftX,
                contentYOffset,
                LocatorDisplayTabLayout.ICON_SIZE,
                LocatorDisplayTabLayout.ICON_SIZE,
                iconColorTint
        );
    }

    @ModifyArg(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
            ),
            index = 2
    )
    private int shiftNamePastIcon(int textXPosition) {
        if (LocatorDisplayConfig.enabled && LocatorDisplayConfig.imageIcon) {
            return textXPosition + LocatorDisplayTabLayout.ICON_SLOT;
        }
        return textXPosition;
    }
}