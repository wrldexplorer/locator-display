package com.explorer.locatordisplay.client.mixin;

import com.explorer.locatordisplay.client.LocatorColorUtil;
import com.explorer.locatordisplay.client.LocatorDisplayConfig;
import com.explorer.locatordisplay.client.UuidResolver;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;

import net.minecraft.network.chat.contents.ObjectContents;
import net.minecraft.network.chat.contents.objects.ObjectInfo;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(PlayerInfo.class)
public abstract class PlayerListEntryMixin {

	@Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("LocatorDisplay");

	// to track which player names have already been logged
	@Unique
    private static final Set<String> loggedPlayerNames = new HashSet<>();

	@Shadow
	public abstract GameProfile getProfile();

	@Inject(method = "getTabListDisplayName", at = @At("RETURN"), cancellable = true)
	private void addLocatorColorDot(CallbackInfoReturnable<Component> cir) {
		if (!LocatorDisplayConfig.enabled) {
			return;
		}

		GameProfile profile = getProfile();
		String playerName = profile.name();
		UUID playerUUID = profile.id();

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
		Component originalName = cir.getReturnValue();
		if (originalName == null) {
			originalName = Component.literal(playerName);
		}

		if (!LocatorDisplayConfig.imageIcon) {
			String pickedSymbol = LocatorDisplayConfig.getCurrentSelection() + " ";
			Component prefixComponent = Component.literal(pickedSymbol)
					.setStyle(Style.EMPTY.withColor(rgbColor));

			MutableComponent modifiedName = Component.empty()
					.append(prefixComponent)
					.append(originalName);

			cir.setReturnValue(modifiedName);
		}
	}
}