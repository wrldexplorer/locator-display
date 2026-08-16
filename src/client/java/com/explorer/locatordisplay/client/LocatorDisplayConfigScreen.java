package com.explorer.locatordisplay.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class LocatorDisplayConfigScreen extends Screen {
    private final Screen parent;

    public LocatorDisplayConfigScreen(Screen parent) {
        super(Component.literal("Locator Display Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // toggle on/off button
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Enabled: " + (LocatorDisplayConfig.enabled ? "ON" : "OFF")),
                        button -> {
                            LocatorDisplayConfig.enabled = !LocatorDisplayConfig.enabled;
                            button.setMessage(
                                    Component.literal("Enabled: " + (LocatorDisplayConfig.enabled ? "ON" : "OFF"))
                            );
                            LocatorDisplayConfig.save();
                        }
                ).bounds(this.width / 2 - 75, this.height / 2 - 20, 150, 20).build()
        );

        // done button
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        button -> {
                            if (this.minecraft != null) {
                                this.minecraft.setScreen(this.parent);
                            }
                        }
                ).bounds(this.width / 2 - 75, this.height / 2 + 10, 150, 20).build()
        );
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}