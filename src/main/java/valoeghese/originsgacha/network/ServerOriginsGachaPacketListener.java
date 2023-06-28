package valoeghese.originsgacha.network;

import com.mojang.logging.LogUtils;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import valoeghese.originsgacha.OriginsGacha;
import valoeghese.originsgacha.OriginsGachaConfig;
import valoeghese.originsgacha.capabilities.IUnlockedOriginData;
import valoeghese.originsgacha.capabilities.IUnlockedOrigins;
import valoeghese.originsgacha.network.packet.C2SRollOriginPacket;
import valoeghese.originsgacha.network.packet.C2SSwitchOriginPacket;

import java.util.Objects;

import static valoeghese.originsgacha.OriginsGacha.SOUND_SWITCH_ORIGIN;

/**
 * Like {@link net.minecraft.network.protocol.game.ServerGamePacketListener} but for this mod.
 */
public final class ServerOriginsGachaPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void onRoll(C2SRollOriginPacket packet, NetworkEvent.Context context) {
		LOGGER.info("Received " + packet);
	}

	public static void onSwitchOrigin(C2SSwitchOriginPacket packet, NetworkEvent.Context context) {
		LOGGER.info("Received " + packet);

		// Ensure the player has the origin unlocked
		ServerPlayer player = context.getSender();
		IUnlockedOrigins unlockedOrigins = IUnlockedOrigins.getUnlockedOrigins(Objects.requireNonNull(player));

		if (unlockedOrigins.hasOrigin(packet.getOrigin())) {
			IUnlockedOriginData toData = unlockedOrigins.getUnlockedOrigin(packet.getOrigin());
			assert toData != null; // if hasOrigin passed this must be null

			// check cooldown
			if ((player.getLevel().getGameTime() - toData.getUnlockTimeTicks()) >= 0) {
				IOriginContainer originContainer = context.getSender().getCapability(OriginsAPI.ORIGIN_CONTAINER).orElseThrow(
						() -> new IllegalStateException("Player " + context.getSender().getName().getString() + " does not have an origin container!")
				);

				// only switch if origins are different
				ResourceKey<Origin> currentOrigin = originContainer.getOrigin(OriginsGacha.ORIGIN_LAYER);

				if (!currentOrigin.equals(packet.getOrigin())) {
					// play cool sound
					player.getLevel().playSound(
							null,
							player.getX(), player.getY(), player.getZ(),
							SOUND_SWITCH_ORIGIN, SoundSource.PLAYERS,
							1.0f, 1.0f);

					// update cooldown time (will automatically sync to client)
					IUnlockedOriginData fromData = unlockedOrigins.getUnlockedOrigin(currentOrigin);

					if (fromData != null) {
						fromData.setCooldown(OriginsGachaConfig.CONFIG.getCoolDownSeconds());
					}

					// this will automatically sync to the client too
					originContainer.setOrigin(OriginsGacha.ORIGIN_LAYER, packet.getOrigin());
					originContainer.synchronize();
				}
			}
		}
	}
}
