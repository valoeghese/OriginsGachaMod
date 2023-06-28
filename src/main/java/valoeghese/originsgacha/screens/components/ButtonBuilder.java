package valoeghese.originsgacha.screens.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

/**
 * Builder for {@linkplain net.minecraft.client.gui.components.Button buttons}.
 * Related properties are grouped (dimensions, position...).
 */
public class ButtonBuilder {
	private int x = 0;
	private int y = 0;
	private int width = 200;
	private int height = 20;
	private boolean centered;
	private boolean disabled;
	private Component message = Component.empty();
	private Button.OnPress action = bn -> {};
	private Button.OnTooltip onTooltip = Button.NO_TOOLTIP;

	/**
	 * Set the position of the button.
	 * @param x the x position of the button.
	 * @param y the y position of the button.
	 * @return this builder.
	 */
	public ButtonBuilder position(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Set the dimensions of the button.
	 * @param width the width of the button.
	 * @param height the height of the button.
	 * @return this builder.
	 */
	public ButtonBuilder dimensions(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	/**
	 * Set the message of the button.
	 * @param message the message of the button.
	 * @return this builder.
	 */
	public ButtonBuilder message(Component message) {
		this.message = message;
		return this;
	}

	/**
	 * Set the action of the button.
	 * @param action the action of the button.
	 * @return this builder.
	 */
	public ButtonBuilder action(Runnable action) {
		this.action = bn -> action.run();
		return this;
	}

	/**
	 * Set the action of the button.
	 * @param action the action of the button.
	 * @return this builder.
	 */
	public ButtonBuilder action(Button.OnPress action) {
		this.action = action;
		return this;
	}

	/**
	 * Set the button to be centered horizontally.
	 * @return this builder.
	 */
	public ButtonBuilder centered() {
		return this.centered(true);
	}

	/**
	 * Set whether the button should be centered horizontally.
	 * @param centered whether the button should be centered horizontally.
	 * @return this builder.
	 */
	public ButtonBuilder centered(boolean centered) {
		this.centered = centered;
		return this;
	}

	/**
	 * Disable the button this builder outputs.
	 * @return this builder.
	 */
	public ButtonBuilder disable() {
		return this.disabled(true);
	}

	/**
	 * Set whether this button should be disabled.
	 * @param disabled whether the button should be disabled.
	 * @return this builder.
	 */
	public ButtonBuilder disabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	/**
	 * Set the tooltip of the button.
	 * @param tooltip the tooltip for the button. If null, the button will be given no tooltip.
	 * @return this builder.
	 */
	public ButtonBuilder tooltip(@Nullable Component tooltip) {
		if (tooltip == null) {
			this.onTooltip = Button.NO_TOOLTIP;
		} else {
			final Font font = Minecraft.getInstance().font;

			// create onTooltip for the given text.
			this.onTooltip = (bn, stack, pMouseX, pMouseY) -> {
				Screen screen = Minecraft.getInstance().screen;

				if (screen != null) {
					screen.renderTooltip(stack, font.split(tooltip, Math.max(screen.width / 2 - 43, 170)), pMouseX, pMouseY);
				}
			};
		}

		return this;
	}

	/**
	 * Apply the given setup for this button builder if the condition is met.
	 * @param condition the condition to test.
	 * @param setup the setup to run if the condition is true.
	 * @return this builder. More precisely, the result of {@code setup.apply(this)} if the condition is met, and
	 * {@code this} if false.
	 */
	public ButtonBuilder setupIf(boolean condition, UnaryOperator<ButtonBuilder> setup) {
		if (condition) {
			return setup.apply(this);
		} else {
			return this;
		}
	}

	/**
	 * Build a button with the properties in this builder.
	 * @return the created button. Unspecified properties will have their default values.<br>
	 * Default properties are as follows:<br>
	 * <ul>
	 *     <li><strong>x, y</strong>: 0</li>
	 *     <li><strong>Width, Height</strong>: 200, 20</li>
	 *     <li><strong>Centered</strong>: false</li>
	 *     <li><strong>Disabled</strong>: false</li>
	 *     <li><strong>Message</strong>: (empty)</li>
	 *     <li><strong>Action</strong>: (no action)</li>
	 *     <li><strong>Tooltip</strong>: (no tooltip)</li>
	 * </ul>
	 */
	public Button build() {
		Button result = new Button(this.x - (this.centered ? this.width/2 : 0), this.y, this.width, this.height,
				this.message, this.action, this.onTooltip);
		result.active = !this.disabled;
		return result;
	}
}
