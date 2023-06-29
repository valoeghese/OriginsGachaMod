package valoeghese.originsgacha.screens.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import valoeghese.originsgacha.util.VertexFormats;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A gacha wheel that spins vertically and lands on a target element.
 */
// TODO clean up animation code
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

	// rolling target stuff
	private int speed = 1;
	private int targetIndex;
	private int nextTargetIndex;
	private float ticksTillSlowDown;
	private boolean endgame; // hack for making sure it ends when passing the actual element
	private Runnable afterRoll = NOT_ROLLING;

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

				this.drawShading(builder, leftBoxEdge, topBoxEdge, bottomBoxEdge, rightBoxEdge, boxWidth,
						SHADOW_SHADE, SHADOW_SHADE, SHADOW_SHADE,
						HIGHLIGHT_SHADE, HIGHLIGHT_SHADE, HIGHLIGHT_SHADE);

				// inner
				this.drawRect(builder, leftBoxEdge + 1, topBoxEdge + 1, boxWidth - 2, boxWidth - 2, INNER_SHADE);
			}
		}

		// Draw the items
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.disableDepthTest();

		for(int i = startElement, y = startY; y < this.height; i--, y += this.sectionHeight) {
			ItemStack stack = this.elements.get(this.getElementIndex(i, 0));
			this.itemRenderer.renderGuiItem(stack, this.x + this.width/2 - 8, this.y + y + this.width/2 - 8);
		}

		// Calculate the halfway index
		int halfwayIndex = startElement;
		int halfwayCount = 0;

		for(int i = startElement, y = this.defaultOffset; y < this.height; halfwayCount++, i--, y += this.sectionHeight) {
			halfwayIndex = i;
		}

		// average of start and last is the halfway index
		halfwayIndex = (startElement + halfwayIndex) / 2;
		final int halfwayIndexOffset = halfwayCount / 2;

		// Wrap halfway index.
		halfwayIndex = this.getElementIndex(halfwayIndex, MIN_SPACES_TIMING);

		// Draw centre box
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		try (VertexFormats.PositionColour builder = VertexFormats.drawPositionColour(VertexFormat.Mode.QUADS)) {
			final int outerWidth = 2;
			// -1 hack goes with the +1 hack for index
			final int centreYStart = this.defaultOffset + (halfwayIndexOffset - 1) * this.sectionHeight;

			this.drawRect(builder, this.x - outerWidth, centreYStart - outerWidth, this.width + outerWidth * 2, outerWidth, 1.0f, 0.7f, 0.0f);
			this.drawRect(builder, this.x - outerWidth, centreYStart + this.width, this.width + outerWidth * 2, outerWidth, 1.0f, 0.7f, 0.0f);
			this.drawRect(builder, this.x - outerWidth, centreYStart, outerWidth, this.width, 1.0f, 0.7f, 0.0f);
			this.drawRect(builder, this.x + this.width, centreYStart, outerWidth, this.width, 1.0f, 0.7f, 0.0f);

			this.drawShading(builder, this.x - 1, centreYStart - 1,
					centreYStart + this.width + 1, this.x + this.width + 1,
					this.width + 2,
					0.67f, 0.494f, 0.06f,
					0.97f, 0.808f, 0.453f);
		}

		// scroll (partialTick is time since last frame in ticks)
		this.offset += partialTick * this.speed;

		if (this.afterRoll != NOT_ROLLING && this.afterRoll != FINISHED_ROLLING) {
			System.out.println(speed + " i am speed || " + halfwayIndex + " : " + this.nextTargetIndex + " || " + this.ticksTillSlowDown);
			if (this.ticksTillSlowDown > 0) {
				if (this.speed < TOP_SPEED) {
					this.speed++;
				}
			} else {
				// Be precise for last few steps down.
				if (this.speed <= 5) {
					if (this.nextTargetIndex == halfwayIndex || this.endgame) {
						// for last lot of speed, land exactly as possible
						if (this.speed < 3) {
							this.endgame = true;

							// if on actual index and not yet at default offset
							if (this.getOffset() % this.sectionHeight < this.defaultOffset && this.nextTargetIndex == halfwayIndex) {
								return; // cooldown thing doesn't matter, so just skip
							}
						}

						// Positions to expect for next target for slowing down.
						this.nextTargetIndex = switch (this.speed) {
							case 5 -> this.getElementIndex(this.targetIndex - 4, MIN_SPACES_TIMING);
							case 4 -> this.getElementIndex(this.targetIndex - 2, MIN_SPACES_TIMING);
							case 3 -> this.getElementIndex(this.targetIndex - 1, MIN_SPACES_TIMING);
							default -> this.targetIndex;
						};

						this.speed--;

						if (this.speed == 0) {
							// wait 1 second then run the after-roll code
							final Runnable afterRoll = this.afterRoll;
							this.afterRoll = FINISHED_ROLLING;

							new Thread(() -> {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								Minecraft.getInstance().tell(afterRoll);
							}).start();
						}
					}
				} else {
					// min value of 5 in case of lag, ensuring the last stages are precisely controlled
					this.speed = Math.max(5, (int) (TOP_SPEED + this.ticksTillSlowDown/3.0f));
				}
			}

			this.ticksTillSlowDown -= partialTick;
		}
	}

	private void drawShading(VertexFormats.PositionColour builder, float leftBoxEdge, float topBoxEdge,
							 float bottomBoxEdge, float rightBoxEdge, float boxWidth,
							 float sr, float sg, float sb, float hr, float hg, float hb) {
		// shading
		this.drawRect(builder, leftBoxEdge, topBoxEdge, boxWidth, 1, sr, sg, sb);
		this.drawRect(builder, leftBoxEdge, topBoxEdge + 1, 1, boxWidth - 1, sr, sg, sb);
		// highlighting
		this.drawRect(builder, leftBoxEdge + 1, bottomBoxEdge - 1, boxWidth - 1, 1, hr, hg, hb);
		this.drawRect(builder, rightBoxEdge - 1, topBoxEdge + 1, 1, boxWidth - 2, hr, hg, hb);
	}

	/**
	 * Get whether this wheel is currently rolling.
	 * @return whether this wheel is currently rolling.
	 */
	public boolean isRolling() {
		return this.afterRoll != NOT_ROLLING;
	}

	/**
	 * Copy data from the other wheel.
	 * @param other the other wheel to copy data from.
	 */
	public void copyFrom(GachaWheel other) {
		this.afterRoll = other.afterRoll;
		this.speed = other.speed;
		this.offset = other.offset;
		this.targetIndex = other.targetIndex;
		this.nextTargetIndex = other.nextTargetIndex;
		this.ticksTillSlowDown = other.ticksTillSlowDown;

		this.elements.clear();
		this.elements.addAll(other.elements);
	}

	/**
	 * Get the element list index of the stack at wheel index i, which can be any number. Outside the range
	 * of {@linkplain GachaWheel#elements this wheel's elements}, the pattern will loop.
	 * @param i the index to get the element list index at.
	 * @param minSpaces the minimum number of spaces to occur in the output. If above 0, some indices returned
	 *                     may not exist if the list is too small.
	 * @return the element list index to get the stack from.
	 */
	private int getElementIndex(int i, int minSpaces) {
		return Mth.positiveModulo(i, Math.max(minSpaces, this.elements.size()));
	}

	private int getOffset() {
		return (int) this.offset;
	}

	private void drawRect(VertexFormats.PositionColour builder, float x, float y, float w, float h, float shade) {
		this.drawRect(builder, x, y, w, h, shade, shade, shade);
	}

	private void drawRect(VertexFormats.PositionColour builder, float x, float y, float w, float h, float r, float g, float b) {
		this.drawRect(builder, x, y, 0.0f, w, h, r, g, b);
	}

	private void drawRect(VertexFormats.PositionColour builder, float x, float y, float z, float w, float h, float r, float g, float b) {
		// draw anti-clockwise due to how minecraft culls faces.
		builder.position(x, y, z).colour(r, g, b).endVertex()
				.position(x, y + h, z).colour(r, g, b).endVertex()
				.position(x + w, y + h, z).colour(r, g, b).endVertex()
				.position(x + w, y, z).colour(r, g, b).endVertex();
	}

	/**
	 * Play the wheel roll animation and land on the given stack.
	 * @param stack the item stack to land on.
	 * @param afterRoll the code to run after the roll is finished.
	 * @return whether the roll was successful. The roll is successful if the given {@link ItemStack} is present on the
	 * wheel.
	 */
	public boolean roll(ItemStack stack, Runnable afterRoll) {
		int index = -1;

		// itemstack doesn't override equals. Manual search for stack.
		for (int i = 0; i < this.elements.size(); i++) {
			if (stack.sameItemStackIgnoreDurability(this.elements.get(i))) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			return false;
		} else {
			this.afterRoll = afterRoll;
			// stupid hack
			this.nextTargetIndex = this.targetIndex = this.getElementIndex(index + 1, 0);
			this.ticksTillSlowDown = RANDOM.nextInt(20 * 2 + 10, 20 * 4);
			return true;
		}
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

	private static final @Nullable Runnable NOT_ROLLING = null;
	private static final @Nullable Runnable FINISHED_ROLLING = new Runnable() {
		@Override
		public void run() {
		}
	};

	private static final float TOP_SPEED = 20.0f;
	private static final int MIN_SPACES_TIMING = 5;

	private static final Random RANDOM = new Random();
}
