package valoeghese.originsgacha.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.screen.OriginDisplayScreen;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import valoeghese.originsgacha.screens.components.ButtonBuilder;

/**
 * Screen for showing the newly unlocked origin.
 */
public class NewOriginScreen extends OriginDisplayScreen {
	public NewOriginScreen(Holder<Origin> origin, Holder<OriginLayer> layer) {
		super(Component.translatable("screens.origins_gacha.new_origin"), true);
		this.showOrigin(origin, layer, false);
	}

	@Override
	protected void init() {
		super.init();

		// same position as ViewOriginScreen
		this.addRenderableWidget(
				new ButtonBuilder()
						.position(this.guiLeft + windowWidth / 2 - 50, this.guiTop + windowHeight + 5)
						.dimensions(100, 20)
						.message(Component.translatable(Origins.MODID + ".gui.close"))
						.action(this::onClose)
						.build()
		);
	}

	@Override
	protected Component getTitleText() {
		return this.title;
	}
}
