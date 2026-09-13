package com.explorer.locatordisplay.client.mixin;

import com.explorer.locatordisplay.client.LocatorColorUtil;
import com.explorer.locatordisplay.client.LocatorDisplayConfig;
import com.explorer.locatordisplay.client.UuidResolver;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {

	private static final Logger LOGGER = LoggerFactory.getLogger("LocatorDisplay");

	// to track which player names have already been logged (to avoid console spam | what oop does to a man ^=^)
	private static final Set<String> loggedPlayerNames = new HashSet<>();

	@Shadow
	public abstract GameProfile getProfile();

	@Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
	private void addLocatorColorDot(CallbackInfoReturnable<Text> cir) {
		if (!LocatorDisplayConfig.enabled) {
			return;
		}

		GameProfile profile = getProfile();
		String playerName = profile.getName();
		UUID playerUUID = profile.getId();

		// skip unnamed entries - npc? etc? whatever server have
		if (playerName == null || playerName.isEmpty()) {
			return;
		}

		/**
		 * Optional method, by default the mod will get the player UUID from the seesion/server
		 * Otherwise, if specified, will connect to the mojang api
		 * If that fails, will generate a temporary generated UUID(cracked players)
		 */
		if(LocatorDisplayConfig.onlineUUID){
			LOGGER.info("Fetching UUID using the Mojang API.");
			// Yay, we get the Mojang UUID (async). If the future is not done yet, we wait. TODO:add a limit
			CompletableFuture<UUID> uuidFuture = UuidResolver.getUuid(playerName);

			if (uuidFuture.isDone()) {
				playerUUID = uuidFuture.getNow(null);
				if (playerUUID == null) {
					// API returned null ~a.k.a~ player is not in Mojang database (cracked/offline)
					// or the server has not responded
					// deterministic offline UUID so their color is stable
					playerUUID = UUID.nameUUIDFromBytes(
							("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8)
					);
				}
			}
		}

		int rgbColor = LocatorColorUtil.getColorFromUuid(playerUUID);

		if (loggedPlayerNames.add(playerName.toLowerCase())) {
			LOGGER.info("Added locator dot for '{}' with UUID '{}'", playerName, playerUUID);
			LOGGER.info("Calculated hex code is: #{}", String.format("%06X", rgbColor));
		}

		/**
		 * Modifies the player name, by inserting the 'colored dot' in the beginning
		 * Might not be the best/safest implementation :\
		 */
		Text originalName = cir.getReturnValue();
		if (originalName == null) {
			originalName = Text.literal(playerName);
		}

		MutableText modifiedName = originalName.copy();
		if(LocatorDisplayConfig.colorName) {
			modifiedName.setStyle(modifiedName.getStyle().withColor(rgbColor));
		}

		if (!LocatorDisplayConfig.imageIcon) {
			String pickedSymbol = LocatorDisplayConfig.getCurrentSelection() + " ";
			Text prefixComponent = Text.literal(pickedSymbol)
					.setStyle(Style.EMPTY.withColor(rgbColor));

			modifiedName = Text.empty()
					.append(prefixComponent)
					.append(modifiedName);
		}

		cir.setReturnValue(modifiedName);
	}
}