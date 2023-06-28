package valoeghese.originsgacha.mixin.gacha;

import io.github.apace100.origins.screen.ChooseOriginScreen;
import io.github.apace100.origins.screen.OriginDisplayScreen;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import valoeghese.originsgacha.OriginsGacha;
import valoeghese.originsgacha.impl.ChooseOriginScreenAccess;
import valoeghese.originsgacha.impl.OriginChoiceModificationCalls;

import java.util.List;

/**
 * Edit the choose origin screen to only have the randomly selected initial unlocked origins.
 */
@Mixin(value = ChooseOriginScreen.class, remap = false)
public abstract class ChooseOriginScreenMixin extends OriginDisplayScreen implements ChooseOriginScreenAccess {
	protected ChooseOriginScreenMixin(Component pTitle) {
		super(pTitle, true);
	}

	@Shadow @Final private List<Holder<Origin>> originSelection;

	@Shadow @Final private List<Holder<OriginLayer>> layerList;
	@Shadow @Final private int currentLayerIndex;

	@Shadow private int maxSelection;

	@Shadow private int currentOrigin;

	@Shadow protected abstract Holder<Origin> getCurrentOriginInternal();

	/**
	 * Variable which stores whether this screen has been modified by a third party (i.e. us).
	 * Modifications from mods that don't interact with our mod to set this will not have an effect, so don't guarantee
	 * this will catch all third party modifications.
	 */
	private boolean origins_gacha_modifiedChooseOriginScreen;

	/**
	 * Edit the choose origin screen to only have the randomly selected initial unlocked origins.
	 * @param info the callback info returnable.
	 */
	@Inject(at = @At("HEAD"), method = "getCurrentOriginInternal")
	private void onGetCurrentOrigin(CallbackInfoReturnable<Holder<Origin>> info) {
		if (OriginsGacha.FeatureFlags.ORIGIN_GACHA.isEnabled())
		{
			this.correctDisplayedOrigins(false);
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

	@Override
	public boolean isDefaultOriginLayer() {
		return OriginChoiceModificationCalls.isDefaultOriginLayer(this.layerList, this.currentLayerIndex);
	}

	@Override
	public List<Holder<Origin>> getSelectableOrigins() {
		return this.originSelection;
	}

	@Override
	public void correctDisplayedOrigins(boolean refreshScreen) {
		if (this.isModified()) {
			return;
		}

		int originalOriginCount = this.originSelection.size();
		OriginChoiceModificationCalls.updateSelectableOrigins(this, this.originSelection);

		// if origin selection modified, reflect that in the variable storing the number of selectable origins.
		// no random origin.
		if (this.originSelection.size() != originalOriginCount) {
			this.maxSelection = this.originSelection.size();

			if (this.currentOrigin >= this.maxSelection) {
				this.currentOrigin = this.maxSelection - 1;
			}
		}

		if (refreshScreen && this.isModified()) {
			Holder<Origin> newOrigin = this.getCurrentOriginInternal();
			this.showOrigin(newOrigin, this.layerList.get(this.currentLayerIndex), false);
		}
	}
}
