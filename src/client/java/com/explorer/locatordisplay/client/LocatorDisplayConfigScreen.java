package com.explorer.locatordisplay.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LocatorDisplayConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget customSymbolBox;

    public LocatorDisplayConfigScreen(Screen parent) {
        super(Text.literal("Locator Display Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2 - 75;
        int startY = this.height / 2 - 50;
        int spacing = 24;

        // toggle on/off button
        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Enabled: " + (LocatorDisplayConfig.enabled ? "ON" : "OFF")),
                        button -> {
                            LocatorDisplayConfig.enabled = !LocatorDisplayConfig.enabled;
                            button.setMessage(
                                    Text.literal("Enabled: " + (LocatorDisplayConfig.enabled ? "ON" : "OFF"))
                            );
                            LocatorDisplayConfig.save();
                        }
                ).dimensions(centerX, startY, 150, 20).build()
        );

        // symbol customizer button
        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Symbol: " + LocatorDisplayConfig.SYMBOLS[LocatorDisplayConfig.symbolIndex]),
                        button -> {
                            LocatorDisplayConfig.symbolIndex = (LocatorDisplayConfig.symbolIndex + 1) % LocatorDisplayConfig.SYMBOLS.length;

                            boolean customActive = LocatorDisplayConfig.isCustomSelected();

                            button.setMessage(
                                    Text.literal("Symbol: " + LocatorDisplayConfig.SYMBOLS[LocatorDisplayConfig.symbolIndex])
                            );

                            // Enable/disable text box interaction depending on selection
                            customSymbolBox.setEditable(customActive);
                            if (!customActive) {
                                customSymbolBox.setFocused(false);
                            }

                            LocatorDisplayConfig.save();
                        }
                ).dimensions(centerX, startY + spacing, 150, 20).build()
        );

        // custom symbol textbox
        this.customSymbolBox = new TextFieldWidget(
                this.textRenderer,
                centerX,
                startY + (spacing * 2),
                150,
                20,
                Text.literal("Custom Symbol")
        );
        this.customSymbolBox.setPlaceholder(Text.literal("> Custom (max length 3)"));
        this.customSymbolBox.setText(LocatorDisplayConfig.customSymbol);
        this.customSymbolBox.setEditable(LocatorDisplayConfig.isCustomSelected());
        this.customSymbolBox.setChangedListener(text -> {
            if (!text.isEmpty() && text.length() <= 3) LocatorDisplayConfig.customSymbol = text; //max char 3
            else                                       LocatorDisplayConfig.customSymbol = "⬤";
            LocatorDisplayConfig.save();
        });
        this.addDrawableChild(this.customSymbolBox);

        // done button
        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Done"),
                        button -> {
                            if (this.client != null) {
                                this.client.setScreen(this.parent);
                            }
                        }
                ).dimensions(centerX, startY + (spacing * 3) + 6, 150, 20).build()
        );
    }

    @Override
    public void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
        // REMOVED: super.renderBackground(...) - The 1.21.11 pipeline handles this automatically now.

        guiGraphics.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick); // This draws the widgets
    }
}