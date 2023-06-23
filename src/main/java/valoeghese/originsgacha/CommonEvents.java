package valoeghese.originsgacha;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import valoeghese.originsgacha.capabilities.UnlockedOrigins;
import valoeghese.originsgacha.event.PlayerTryEquipEvent;

@Mod.EventBusSubscriber
public class CommonEvents {
	@SubscribeEvent
	public static void onTick(final TickEvent.ServerTickEvent event) {
		for (Player player : event.getServer().getPlayerList().getPlayers()) {
			player.getCapability(UnlockedOrigins.CAPABILITY).ifPresent(x -> {
				System.out.println("haha");
			});
		}
	}

	@SubscribeEvent
	public static void onEquip(final PlayerTryEquipEvent event) {
		if (event.getStack().getItem() == Items.ELYTRA) {
			event.setCanceled(true);
		}
	}
}
