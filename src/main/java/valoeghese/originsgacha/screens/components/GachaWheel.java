package valoeghese.originsgacha.screens.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import valoeghese.originsgacha.util.VertexFormats;

import java.util.ArrayList;
import java.util.List;

/**
 * A gacha wheel that spins vertically and lands on a target element.
 */
public class GachaWheel implements Widget {
	public GachaWheel(ItemRenderer itemRenderer, int x, int y, int width, int height) {
		this.itemRenderer = itemRenderer;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.sectionHeight = width + SPACING;

		int numElementsNoOffset = (height - width) / this.sectionHeight;
		int usedHeightNoOffset = this.sectionHeight * numElementsNoOffset + width;
		this.offset = this.defaultOffset = (height - usedHeightNoOffset) / 2;
	}

	private final ItemRenderer itemRenderer;
	private final int x;
	private final int y;
	private final int width;
	private final int height;

	private final int sectionHeight;

	private final int defaultOffset;
	private double offset;

	private final List<ItemStack> elements = new ArrayList<>();

	public void addElement(ItemStack element) {
		this.elements.add(element);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		// Positioning of the boxes and which elements to draw.
		int startY = this.getOffset() % this.sectionHeight;
		// as offset gets higher, the starting element will get higher
		// this means earlier elements need to have the higher indices.
		int startElement = this.getOffset() / this.sectionHeight;

		if (startY > SPACING) {
			startY -= this.sectionHeight;
			startElement++;
		}

		// Draw the squares
		try (VertexFormats.PositionColour builder = VertexFormats.drawPositionColour(VertexFormat.Mode.QUADS)) {
			// The 'rail' upon which the wheel rolls. With left highlight and right shadow.
			this.drawRect(builder, this.x, this.y, 1, this.height, HIGHLIGHT_SHADE);
			this.drawRect(builder, this.x + 1, this.y, this.width - 2, this.height, OUTER_SHADE);
			this.drawRect(builder, this.x + this.width - 1, this.y, 1, this.height, INNER_SHADE);

			// white squares down the height.

			final float leftBoxEdge = this.x + 2;
			final float rightBoxEdge = this.x + this.width - 2;
			final float boxWidth = rightBoxEdge - leftBoxEdge;

			for(int sectionTopY = startY; sectionTopY < this.height; sectionTopY += this.sectionHeight) {
				final float topBoxEdge = sectionTopY + 2;
				final float bottomBoxEdge = sectionTopY + this.width - 2;

				// shading
				this.drawRect(builder, leftBoxEdge, topBoxEdge, boxWidth, 1, SHADOW_SHADE);
				this.drawRect(builder, leftBoxEdge, topBoxEdge + 1, 1, boxWidth - 1, SHADOW_SHADE);
				// highlighting
				this.drawRect(builder, leftBoxEdge + 1, bottomBoxEdge - 1, boxWidth - 1, 1, HIGHLIGHT_SHADE);
				this.drawRect(builder, rightBoxEdge - 1, topBoxEdge + 1, 1, boxWidth - 2, HIGHLIGHT_SHADE);

				// inner
				this.drawRect(builder, leftBoxEdge + 1, topBoxEdge + 1, boxWidth - 2, boxWidth - 2, INNER_SHADE);
			}
		}

		// Draw the items
		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		for(int i = startElement, y = startY; y < this.height; i--, y += this.sectionHeight) {
			ItemStack stack = this.elements.get(Mth.positiveModulo(i, this.elements.size()));
			this.itemRenderer.renderGuiItem(stack, this.x + this.width/2 - 8, this.y + y + this.width/2 - 8);
		}

		// temp scroll
		this.offset += 0.1;
	}

	private int getOffset() {
		return (int) this.offset;
	}

	private void drawRect(VertexFormats.PositionColour builder, float x, float y, float w, float h, float shade) {
		// draw anti-clockwise due to how minecraft culls faces.
		builder.position(x, y).colour(shade, shade, shade).endVertex()
				.position(x, y + h).colour(shade, shade, shade).endVertex()
				.position(x + w, y + h).colour(shade, shade, shade).endVertex()
				.position(x + w, y).colour(shade, shade, shade).endVertex();
	}

	/**
	 * Play the wheel roll animation and land on the given stack.
	 * @param stack the item stack to land on.
	 * @return whether the roll was successful. The roll is successful if the given {@link ItemStack} is present on the
	 * wheel.
	 */
	public boolean roll(ItemStack stack) {
		Minecraft.getInstance().gui.getChat().addMessage(Component.literal("rolling not implemented, giving fail response (but should be unlocked)"));
		return false;
	}

	/**
	 * Get whether the wheel is empty. The wheel is empty if there are no elements to be selected.
	 * @return true if the wheel is empty.
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}

	private static final float SHADOW_SHADE = 0.14f;
	private static final float HIGHLIGHT_SHADE = 1.0f;
	private static final float INNER_SHADE = 0.545f;
	private static final float OUTER_SHADE = 0.776f;

	private static final int SPACING = 2;
}
