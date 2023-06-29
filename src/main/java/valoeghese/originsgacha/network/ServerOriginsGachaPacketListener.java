package valoeghese.originsgacha.network;

import com.mojang.logging.LogUtils;
import io.github.apace100.origins.registry.ModItems;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import valoeghese.originsgacha.OriginsGacha;
import valoeghese.originsgacha.OriginsGachaConfig;
import valoeghese.originsgacha.OriginsGachaSounds;
import valoeghese.originsgacha.capabilities.IUnlockedOriginData;
import valoeghese.originsgacha.capabilities.IUnlockedOrigins;
import valoeghese.originsgacha.network.packet.C2SRollOriginPacket;
import valoeghese.originsgacha.network.packet.C2SSwitchOriginPacket;
import valoeghese.originsgacha.util.Utils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * Like {@link net.minecraft.network.protocol.game.ServerGamePacketListener} but for this mod.
 */
public final class ServerOriginsGachaPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Random RANDOM = new Random();

	public static void onRoll(C2SRollOriginPacket packet, NetworkEvent.Context context) {
		LOGGER.info("Received " + packet);

		context.enqueueWork(() -> {
			// ensure the player has enough orbs of origin
			ServerPlayer player = context.getSender();
			IUnlockedOrigins unlockedOrigins = IUnlockedOrigins.getUnlockedOrigins(Objects.requireNonNull(player));

			if (player.getInventory().countItem(ModItems.ORB_OF_ORIGIN.get()) >= unlockedOrigins.getRequiredOrbsForNextRoll()) {
				// remove orbs of origin
				int requiredOrbs = unlockedOrigins.getRequiredOrbsForNextRoll();
				int spentOrbs = Utils.removeItems(player.getInventory(), ModItems.ORB_OF_ORIGIN.get(), requiredOrbs);

				if (spentOrbs < requiredOrbs) {
					LOGGER.warn("Spent less than the required number of items to pay for roll: {} < {}.",
							spentOrbs, requiredOrbs);
				}

				// pick new origin to unlock
				OriginLayer originContainerOpt = OriginsAPI.getLayersRegistry(context.getSender().server)
						.get(OriginsGacha.ORIGIN_LAYER);

				if (originContainerOpt == null) {
					throw new IllegalStateException("Must have origins:origins layer present to use this mod, but it was missing!.");
				}

				List<ResourceKey<Origin>> unlockCandidates = originContainerOpt.origins().stream()
						.filter(origin -> origin.get().isChoosable() && origin.unwrapKey().isPresent() && !unlockedOrigins.hasOrigin(origin.unwrapKey().get()))
						.map(Holder::unwrapKey)
						.filter(Optional::isPresent)
						.map(Optional::get)
						.toList();

				if (!unlockCandidates.isEmpty()) {
					ResourceKey<Origin> origin = unlockCandidates.get(RANDOM.nextInt(unlockCandidates.size()));
					unlockedOrigins.unlockOrigin(origin);
				}
			}
		});
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
					context.enqueueWork(() -> {
						// play cool sound
						player.getLevel().playSound(
								null,
								player.getX(), player.getY(), player.getZ(),
								OriginsGachaSounds.SWITCH_ORIGIN.get(), SoundSource.PLAYERS,
								1.0f, 1.0f);

						// update cooldown time (will automatically sync to client)
						IUnlockedOriginData fromData = unlockedOrigins.getUnlockedOrigin(currentOrigin);

						if (fromData != null) {
							fromData.setCooldown(OriginsGachaConfig.CONFIG.getCoolDownSeconds());
						}

						// this will automatically sync to the client too
						originContainer.setOrigin(OriginsGacha.ORIGIN_LAYER, packet.getOrigin());
						originContainer.synchronize();
					});
				}
			}
		}
	}
}
