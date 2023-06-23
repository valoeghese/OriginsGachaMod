package valoeghese.originsgacha;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import valoeghese.originsgacha.capabilities.IUnlockedOrigins;
import valoeghese.originsgacha.capabilities.UnlockedOrigins;
import valoeghese.originsgacha.event.PlayerTryEquipEvent;
import valoeghese.originsgacha.network.NetworkManager;
import valoeghese.originsgacha.network.packet.S2CUnlockOriginsSyncPacket;

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
}
