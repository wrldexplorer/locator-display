package com.explorer.locatordisplay.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class LocatorDisplayConfigScreen extends Screen {
    private final Screen parent;
    private EditBox customSymbolBox;

    public LocatorDisplayConfigScreen(Screen parent) {
        super(Component.literal("Locator Display Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2 - 75;
        int startY = this.height / 2 - 50;
        int spacing = 24;

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
                ).bounds(centerX, startY, 150, 20).build()
        );

        // UUID fetching button
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Fetch: " + (LocatorDisplayConfig.onlineUUID ? "Mojang API UUID" : "Local UUID")),
                        button -> {
                            LocatorDisplayConfig.onlineUUID = !LocatorDisplayConfig.onlineUUID;
                            button.setMessage(
                                    Component.literal("Fetch: " + (LocatorDisplayConfig.onlineUUID ? "Mojang API UUID" : "Local UUID"))
                            );
                            LocatorDisplayConfig.save();
                        }
                ).bounds(centerX, startY + spacing, 150, 20).build()
        );

        // symbol customizer button
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Symbol: " + LocatorDisplayConfig.SYMBOLS[LocatorDisplayConfig.symbolIndex]),
                        button -> {
                            LocatorDisplayConfig.symbolIndex = (LocatorDisplayConfig.symbolIndex + 1) % LocatorDisplayConfig.SYMBOLS.length;

                            boolean customActive = LocatorDisplayConfig.isCustomSelected();

                            button.setMessage(
                                    Component.literal("Symbol: " + LocatorDisplayConfig.SYMBOLS[LocatorDisplayConfig.symbolIndex])
                            );

                            // Enable/disable text box interaction depending on selection
                            customSymbolBox.setEditable(customActive);
                            if (!customActive) {
                                customSymbolBox.setFocused(false);
                            }

                            LocatorDisplayConfig.save();
                        }
                ).bounds(centerX, startY + (spacing * 2), 150, 20).build()
        );

        // custom symbol textbox
        this.customSymbolBox = new EditBox(
                this.font,
                centerX,
                startY + (spacing * 3),
                150,
                20,
                Component.literal("Custom Symbol")
        );
        this.customSymbolBox.setHint(Component.literal("> Custom (max length 3)"));
        this.customSymbolBox.setValue(LocatorDisplayConfig.customSymbol);
        this.customSymbolBox.setEditable(LocatorDisplayConfig.isCustomSelected());
        this.customSymbolBox.setResponder(text -> {
            if (!text.isEmpty() && text.length() <= 3) LocatorDisplayConfig.customSymbol = text; //max char 3
            else                   LocatorDisplayConfig.customSymbol = "⬤";
            LocatorDisplayConfig.save();
        });
        this.addRenderableWidget(this.customSymbolBox);

        // done button
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        button -> {
                            if (this.minecraft != null) {
                                this.minecraft.gui.setScreen(this.parent);
                            }
                        }
                ).bounds(centerX, startY + (spacing * 4) + 15, 150, 20).build()
        );
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}