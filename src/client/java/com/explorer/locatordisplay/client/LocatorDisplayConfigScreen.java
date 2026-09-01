package com.explorer.locatordisplay.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class LocatorDisplayConfigScreen extends Screen {
    private final Screen parent;
    private EditBox customBox;

    public LocatorDisplayConfigScreen(Screen parent) {
        super(Component.literal("Locator Display Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2 - 75;
        int startY = this.height / 2 - 50;
        int width = 180;
        int height = 20;
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
                ).bounds(centerX, startY, width, height).build()
        );

        // UUID fetching button
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("UUID Source: " + (LocatorDisplayConfig.onlineUUID ? "Official Mojang" : "Server-Provided")),
                        button -> {
                            LocatorDisplayConfig.onlineUUID = !LocatorDisplayConfig.onlineUUID;
                            button.setMessage(
                                    Component.literal("UUID Source: " + (LocatorDisplayConfig.onlineUUID ? "Official Mojang" : "Server-Provided"))
                            );
                            LocatorDisplayConfig.save();
                        }
                ).bounds(centerX, startY + spacing, width, height).build()
        );

        // icon type button
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Icon Type: " + (LocatorDisplayConfig.imageIcon ? "Texture" : "Symbol")),
                        button -> {
                            LocatorDisplayConfig.imageIcon = !LocatorDisplayConfig.imageIcon;
                            LocatorDisplayConfig.selectIndex = 0;
                            button.setMessage(
                                    Component.literal("Icon Type: " + (LocatorDisplayConfig.imageIcon ? "Texture" : "Symbol"))
                            );
                            LocatorDisplayConfig.save();
                        }
                ).bounds(centerX, startY + (spacing * 2), width, height).build()
        );

        // symbol customizer button
        this.addRenderableWidget(
                Button.builder(
                        Component.literal(LocatorDisplayConfig.imageIcon
                                        ? ("Icon: "  + LocatorDisplayConfig.ICONS[LocatorDisplayConfig.selectIndex])
                                        : ("Symbol: " + LocatorDisplayConfig.SYMBOLS[LocatorDisplayConfig.selectIndex])
                        ),
                        button -> {
                            LocatorDisplayConfig.selectIndex = (LocatorDisplayConfig.selectIndex + 1)
                                                % (LocatorDisplayConfig.imageIcon
                                                        ? LocatorDisplayConfig.ICONS.length
                                                        : LocatorDisplayConfig.SYMBOLS.length
                                                )
                            ;

                            boolean customActive = LocatorDisplayConfig.isCustomSelected();

                            button.setMessage(
                                    Component.literal(LocatorDisplayConfig.imageIcon
                                            ? ("Icon: "  + LocatorDisplayConfig.ICONS[LocatorDisplayConfig.selectIndex])
                                            : ("Symbol: " + LocatorDisplayConfig.SYMBOLS[LocatorDisplayConfig.selectIndex])
                                    )
                            );

                            // Enables/disables text box interaction depending on selection
                            customBox.setEditable(customActive);
                            if (!customActive) {
                                customBox.setFocused(false);
                            }

                            LocatorDisplayConfig.save();
                        }
                ).bounds(centerX, startY + (spacing * 3), width, height).build()
        );

        // custom symbol/icon textbox
        this.customBox = new EditBox(
                this.font,
                centerX,
                startY + (spacing * 4),
                width,
                height,
                Component.literal("Custom")
        );
        if (LocatorDisplayConfig.imageIcon){
            this.customBox.setMaxLength(256);
        }
        this.customBox.setHint(
                Component.literal(
                    LocatorDisplayConfig.imageIcon
                            ? "> Custom texture PATH"
                            : "> Custom (max length 3)"
                )
        );
        this.customBox.setEditable(LocatorDisplayConfig.isCustomSelected());

        if (!LocatorDisplayConfig.imageIcon){
            this.customBox.setValue(LocatorDisplayConfig.customSymbol);
            this.customBox.setResponder(text -> {
                if (!text.isEmpty() && text.length() <= 3) LocatorDisplayConfig.customSymbol = text; //max char 3
                else LocatorDisplayConfig.customSymbol = "⬤";
                LocatorDisplayConfig.save();
            });
        } else {
            this.customBox.setValue(LocatorDisplayConfig.customDir);
            this.customBox.setResponder(text -> {
                if (!text.isEmpty()) LocatorDisplayConfig.customDir = text;
                else LocatorDisplayConfig.customDir = "";
                LocatorDisplayConfig.save();
            });
        }
        this.addRenderableWidget(this.customBox);

        // done button
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        button -> {
                            if (this.minecraft != null) {
                                this.minecraft.gui.setScreen(this.parent);
                            }
                        }
                ).bounds(centerX, startY + (spacing * 5) + 15, width, height).build()
        );
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}