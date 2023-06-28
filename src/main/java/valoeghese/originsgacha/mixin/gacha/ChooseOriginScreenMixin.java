package valoeghese.originsgacha.mixin.gacha;

import io.github.apace100.origins.screen.ChooseOriginScreen;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import valoeghese.originsgacha.OriginsGacha;
import valoeghese.originsgacha.capabilities.IUnlockedOrigins;
import valoeghese.originsgacha.impl.ModifiableScreen;
import valoeghese.originsgacha.impl.OriginChoiceModificationCalls;

import java.util.List;
import java.util.Objects;

/**
 * Edit the choose origin screen to only have the randomly selected initial unlocked origins.
 */
@Mixin(value = ChooseOriginScreen.class, remap = false)
public abstract class ChooseOriginScreenMixin extends Screen implements ModifiableScreen {
	protected ChooseOriginScreenMixin(Component pTitle) {
		super(pTitle);
	}

	@Shadow @Final private List<Holder<Origin>> originSelection;

	@Shadow @Final private List<Holder<OriginLayer>> layerList;
	@Shadow @Final private int currentLayerIndex;

	@Shadow private int maxSelection;

	/**
	 * Variable which stores whether this screen has been modified by a third party (i.e. us).
	 * Modifications from mods that don't interact with our mod to set this will not have an effect, so don't guarantee
	 * this will catch all third party modifications.
	 */
	private boolean origins_gacha_modifiedChooseOriginScreen;

	@Inject(at = @At("HEAD"), method = "getCurrentOriginInternal")
	private void onGetCurrentOrigin(CallbackInfoReturnable<Holder<Origin>> cir) {
		if (OriginsGacha.FeatureFlags.ORIGIN_GACHA.isEnabled())
		{
			int originalOriginCount = this.originSelection.size();
			OriginChoiceModificationCalls.onGetCurrentOrigin(this, this.originSelection, this.layerList, this.currentLayerIndex);

			// if origin selection modified, reflect that in the variable storing the number of selectable origins.
			// no random origin.
			if (this.originSelection.size() != originalOriginCount) {
				this.maxSelection = this.originSelection.size();
			}
		}
	}

	@Override
	public boolean isModified() {
		return this.origins_gacha_modifiedChooseOriginScreen;
	}

	@Override
	public void setModified(boolean modified) {
		this.origins_gacha_modifiedChooseOriginScreen = modified;
	}
}
