package valoeghese.originsgacha.screens.components;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * The component for a gacha wheel that 'spins' vertically and lands on the element.
 */
public class GachaWheel extends AbstractWidget {
	public GachaWheel(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
		super(pX, pY, pWidth, pHeight, pMessage);
	}

	@Override
	public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

	}
}
