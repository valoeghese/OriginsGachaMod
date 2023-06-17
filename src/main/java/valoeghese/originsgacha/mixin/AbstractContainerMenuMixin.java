package valoeghese.originsgacha.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import valoeghese.originsgacha.event.PlayerTryEquipEvent;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
	@Shadow public abstract ItemStack getCarried();

	@Shadow public abstract Slot getSlot(int pSlotId);

	@Inject(at = @At("HEAD"), cancellable = true, method = "doClick")
	private void onDoClick(int pSlotId, int pButton, ClickType pClickType, Player pPlayer, CallbackInfo ci) {
		// only inventory menu
		AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;

		if (!(menu instanceof InventoryMenu)) {
			return;
		}

		// Check whether this is legal.

		Slot slot = this.getSlot(pSlotId);

		if (slot != null) {
			// Equipment slot is an anonymous class in InventoryMenu
			// This slot is characterised by its max stack size of 1.
			if (slot.getMaxStackSize() == 1) {
				if (MinecraftForge.EVENT_BUS.post(
						new PlayerTryEquipEvent(
								pPlayer,
								EquipmentSlot.CHEST, // TODO associate correctly.
								this.getCarried(),
								PlayerTryEquipEvent.Reason.PLACE_IN_SLOT
						))) {
					ci.cancel();
				}
			}
		}
	}
}
