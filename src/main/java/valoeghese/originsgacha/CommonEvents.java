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
				// TODO reset cooldowns
			}
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
