package valoeghese.originsgacha.impl;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import valoeghese.originsgacha.event.PlayerTryEquipEvent;

/**
 * Implementations of complex mixin logic.
 */
public class PlayerTryEquipCalls {
	/**
	 * Called when the player clicks in an inventory.
	 * @param menu the menu in which the player clicked.
	 * @param player the player.
	 * @param slotId the id of the slot clicked.
	 * @param info the info allowing this to be cancelled.
	 */
	public static void onInventoryClick(InventoryMenu menu, Player player, int slotId, CallbackInfo info) {
		// Check whether this is legal.
		if (slotId < 0) {
			return;
		}

		Slot slot = menu.getSlot(slotId);

		// Equipment slot is an anonymous class in InventoryMenu
		// This slot is characterised by its max stack size of 1.
		if (slot.getMaxStackSize() == 1) {
			if (MinecraftForge.EVENT_BUS.post(
					new PlayerTryEquipEvent(
							player,
							menu.getCarried(),
							PlayerTryEquipEvent.Reason.PLACE_IN_SLOT
					))) {
				info.cancel();
			}
		}
	}
}
