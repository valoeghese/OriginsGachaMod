package valoeghese.originsgacha.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import valoeghese.originsgacha.event.PlayerTryEquipEvent;

@Mixin(Player.class)
public class PlayerMixin {
	@Inject(at = @At("HEAD"), cancellable = true, method = "setItemSlot")
	private void onSetItemSlot(EquipmentSlot pSlot, ItemStack pStack, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new PlayerTryEquipEvent((Player) (Object) this, pSlot, pStack,
				PlayerTryEquipEvent.Reason.GENERAL))) {
			info.cancel();
		}
	}
}
