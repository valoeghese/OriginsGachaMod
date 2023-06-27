package valoeghese.originsgacha;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import valoeghese.originsgacha.capabilities.IUnlockedOriginData;
import valoeghese.originsgacha.capabilities.IUnlockedOrigins;
import valoeghese.originsgacha.capabilities.UnlockedOrigins;
import valoeghese.originsgacha.event.PlayerTryEquipEvent;

@Mod.EventBusSubscriber
public class CommonEvents {
	@SubscribeEvent
	public static void onTick(final TickEvent.ServerTickEvent event) {
		if (OriginsGacha.FeatureFlags.ORIGIN_GACHA.isEnabled())
		{
			for (Player player : event.getServer().getPlayerList().getPlayers()) {
				player.getCapability(UnlockedOrigins.CAPABILITY).ifPresent(IUnlockedOrigins::tick);
			}
		}
	}

	@SubscribeEvent
	public static void onEquip(final PlayerTryEquipEvent event) {
		if (OriginsGacha.FeatureFlags.DISABLED_ELYTRA.isEnabled())
		{
			if (event.getStack().getItem() == Items.ELYTRA) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerDeath(final LivingDeathEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			if (OriginsGacha.FeatureFlags.ORIGIN_GACHA.isEnabled())
			{
				// reset cooldowns
				IUnlockedOrigins unlockedOrigins = IUnlockedOrigins.getUnlockedOrigins(player);

				unlockedOrigins.getUnlockedOrigins().forEach(data -> data.setCooldown(0));
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawnDataClone(final PlayerEvent.Clone event) {
		// only run on server and if ORIGIN_GACHA feature flag is enabled.
		if (OriginsGacha.FeatureFlags.ORIGIN_GACHA.isEnabled() && event.getEntity() instanceof ServerPlayer)
		{
			IUnlockedOrigins unlockedOrigins = IUnlockedOrigins.getUnlockedOrigins(event.getOriginal());
			IUnlockedOrigins newUnlockedOrigins = IUnlockedOrigins.getUnlockedOrigins(event.getEntity());

			// copy old data into new data
			newUnlockedOrigins.deserializeNBT(unlockedOrigins.serializeNBT());
		}
	}

	@SubscribeEvent
	public static void onPlayerChangeWorld(final PlayerEvent.PlayerChangedDimensionEvent event) {
		if (OriginsGacha.FeatureFlags.ORIGIN_GACHA.isEnabled())
		{
			if (event.getEntity() instanceof ServerPlayer player) {
				IUnlockedOrigins.getUnlockedOrigins(player).sync();
			}
		}
	}
}
