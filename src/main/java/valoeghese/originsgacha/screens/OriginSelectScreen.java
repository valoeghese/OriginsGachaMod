package valoeghese.originsgacha.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import io.github.apace100.origins.registry.ModItems;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import valoeghese.originsgacha.ClientEvents;
import valoeghese.originsgacha.OriginsGacha;
import valoeghese.originsgacha.capabilities.IUnlockedOriginData;
import valoeghese.originsgacha.capabilities.IUnlockedOrigins;
import valoeghese.originsgacha.network.NetworkManager;
import valoeghese.originsgacha.network.packet.C2SSwitchOriginPacket;
import valoeghese.originsgacha.util.VertexFormats;
import valoeghese.originsgacha.util.Division;
import valoeghese.originsgacha.util.Utils;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The selection wheel for switching origins.
 */
public class OriginSelectScreen extends Screen {
	public OriginSelectScreen() {
		super(Component.translatable("screens.origins_gacha.select"));

		Player player = Minecraft.getInstance().player;
		assert player != null; // appease the static code analysis

		// Get the player's current origin.
		this.playerOriginContainer = player.getCapability(OriginsAPI.ORIGIN_CONTAINER).resolve().orElseThrow(
				() -> new IllegalStateException("Player does not have origin container?!")
		);

		ResourceKey<Origin> currentOrigin = this.playerOriginContainer.getOrigin(OriginsGacha.ORIGIN_LAYER);
		this.currentOrigin = OriginsAPI.getOriginsRegistry().get(currentOrigin);

		if (this.currentOrigin == null) {
			throw new IllegalStateException("Unknown Origin: " + currentOrigin.location());
		}

		// Get all unlocked origins.
		IUnlockedOrigins unlockedOrigins = IUnlockedOrigins.getUnlockedOrigins(player);
		Registry<Origin> originRegistry = OriginsAPI.getOriginsRegistry();

		this.availableOrigins = unlockedOrigins.getUnlockedOrigins().stream()
				.map(k -> {
					Origin origin = originRegistry.get(k.getOrigin());

					// if any origin is null this screen cannot be displayed.
					if (origin == null) {
						LOGGER.warn("Tried to open OriginSelectScreen but origin " + k.getOrigin().location() + " is null!");
						this.canDisplay.set(false);
					}

					return new AbstractMap.SimpleEntry<>(k, origin);
				})
				.toList();

		this.requiredOrbsForNext = unlockedOrigins.getRequiredOrbsForNextRoll();
		this.page = unlockedOrigins.getPage();
	}

	private final AtomicBoolean canDisplay = new AtomicBoolean(true);

	/**
	 * Get whether this screen is allowed to open.
	 * @return whether this screen is allowed to open.
	 */
	public boolean canDisplay() {
		return canDisplay.get();
	}

	// origin data to display
	private final IOriginContainer playerOriginContainer;
	private final Origin currentOrigin;
	private final List<? extends Map.Entry<IUnlockedOriginData, Origin>> availableOrigins;
	private final int requiredOrbsForNext;

	private double page = 0;

	// scaling
	private double scaleFactor = 0.05;
	private long lastScaleTime = System.currentTimeMillis();

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		super.render(stack, mouseX, mouseY, partialTick);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		double centreX = this.width / 2.0;
		double centreY = this.height / 2.0;
		double outerEdgeSize = this.scaleFactor * this.height / 2.5;
		double innerButtonSize = outerEdgeSize * 0.2;
		double innerEdgeSize = outerEdgeSize * 0.25;

		int selectedButton = getSelectedButton(mouseX, mouseY, innerButtonSize, innerEdgeSize, outerEdgeSize);

		this.drawCircles(centreX, centreY, outerEdgeSize, innerButtonSize, innerEdgeSize, selectedButton);

		RenderSystem.disableBlend();

		// Draw Icons
		this.drawIcons(stack, centreX, centreY, 0.5 * (outerEdgeSize + innerEdgeSize));

		// scale up
		if (this.scaleFactor < 1) {
			long currentTime = System.currentTimeMillis();
			// each 1 = 1 tick (50ms)
			double diffTime = (currentTime - this.lastScaleTime) / 50.0;
			this.lastScaleTime = currentTime;

			final double baseChangeRate = 0.2;
			double scaleChange = baseChangeRate - 0.01 * baseChangeRate * Math.exp(2 * this.scaleFactor);
			this.scaleFactor += scaleChange * diffTime;

			if (this.scaleFactor > 1) {
				this.scaleFactor = 1;
			}
		}
	}

	/**
	 * Draw the item icons representing the selectable origins, and orb of origin and its count in the centre.
	 * @param guiStack the {@link PoseStack} for GUI rendering.
	 * @param centreX the x position of the centre of the origin ring.
	 * @param centreY the y position of the centre of the origin ring.
	 * @param distance the distance from the origin ring at which to render the icons.
	 */
	private void drawIcons(PoseStack guiStack, double centreX, double centreY, double distance) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
		float scale = (float) (this.scaleFactor) * (this.height <= 480 ? 1.0f : 2.0f);

		PoseStack stack = RenderSystem.getModelViewStack();
		stack.pushPose();
		stack.scale(scale, scale, 0.0f);

		final int nSectors = 8;
		final double theta = 2.0 * Math.PI / nSectors;
		final int currentOriginIndex = this.indexOf(this.currentOrigin);

		for (int i = 0; i < nSectors; i++) {
			int index = i + this.getPage() * nSectors;

			if (index < this.availableOrigins.size()) {
				double angle = theta * (i - 1.5);
				var unlockedOriginPair = this.availableOrigins.get(index);

				long timeRemainingTicks =
						unlockedOriginPair.getKey().getUnlockTimeTicks()
						- this.playerOriginContainer.getOwner().getLevel().getGameTime();

				boolean dark = currentOriginIndex == index || timeRemainingTicks > 0;

				if (dark) {
					RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 0.5f);
				}

				final double x = centreX + distance * Math.cos(angle);
				final double y = centreY + distance * Math.sin(angle);

				// divide by scale to counteract the scale multiplication for positioning.
				this.itemRenderer.renderGuiItem(
						unlockedOriginPair.getValue().getIcon(),
						Mth.floor(x/scale - 8),
						Mth.floor(y/scale - 8));

				if (timeRemainingTicks > 0) {
					GuiComponent.drawCenteredString(guiStack, this.font, Utils.timestampComponent(timeRemainingTicks),
							(int)(x/scale), (int)(y/scale + 8), 0xFFFFFF);
				}

				if (dark) {
					RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}
		}

		// Render Central Orb and Under-text
		this.itemRenderer.renderGuiItem(
				ORB_OF_ORIGIN_STACK,
				Mth.floor(centreX/scale - 8),
				Mth.floor(centreY/scale - 8));

		final int orbOfOriginCount = this.playerOriginContainer.getOwner().getInventory().countItem(ModItems.ORB_OF_ORIGIN.get());
		GuiComponent.drawCenteredString(guiStack, this.font, Component.literal(orbOfOriginCount + "/" + this.requiredOrbsForNext),
				(int)(centreX/scale), (int)(centreY/scale + 8), 0xFFFFFF);

		stack.popPose();
	}

	/**
	 * Draw the circles (inner button and outer ring) in the GUI.
	 * @param centreX the centre X of the circles.
	 * @param centreY the centre Y of the circles.
	 * @param outerEdgeSize the radius of the outer edge of the outer ring of origin buttons.
	 * @param innerButtonSize the radius of the inner button.
	 * @param innerEdgeSize the radius of the inner edge of the outer ring of origin buttons.
	 * @param highlightedSector the sector of the outer ring to highlight. Use 0-7 to highlight one of the outer-ring sectors,
	 *                          8 for the inner button, anything else highlights nothing.
	 */
	private void drawCircles(double centreX, double centreY, double outerEdgeSize, double innerButtonSize,
							 double innerEdgeSize, int highlightedSector) {
		final int nOriginSectors = 8;
		final int nRenderSectors = 64;
		final double theta = 2.0 * Math.PI / nRenderSectors;

		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		float shade = highlightedSector == 8 ? 1.0f : 0.2f;

		try (VertexFormats.PositionColour builder = VertexFormats.drawPositionColour(VertexFormat.Mode.TRIANGLES)) {
			// Inner Circle
			for (int i = 0; i < nRenderSectors; i++) {
				double angle = theta * i;

				this.drawRenderSector(builder, theta, angle, centreX, centreY, innerButtonSize, shade);

				// use this (and force shade to 0.2) if highlighting around centre button instead of
				// changing the colour itself.
//				if (highlightedSector == 8) {
//					this.drawRenderArc(builder, theta, angle, centreX, centreY,
//							innerButtonSize, (innerButtonSize + innerEdgeSize) * 0.5, 1.0f);
//				}
			}

			int currentOriginSector = this.indexOf(this.currentOrigin) - this.getPage() * nOriginSectors;

			// Outer Circle
			for (int i = 0; i < nRenderSectors; i++) {
				double angle = theta * i;
				final int sector = SECTORS.get(angle);

				shade = (sector == currentOriginSector) ? 0.0f : (
						sector == highlightedSector ? 1 : (
								(sector & 1) == 0 ? 0.2f : 0.4f
						)
				);

				this.drawRenderArc(builder, theta, angle, centreX, centreY, innerEdgeSize, outerEdgeSize, shade);
			}
		}
	}

	private void drawRenderSector(VertexFormats.PositionColour builder, double theta, double angle,
								  double centreX, double centreY, double radius, float shade) {
		builder.position(centreX, centreY)
				.colour(shade, shade, shade, 0.5f)
				.endVertex();

		builder.position(centreX + radius * Math.cos(angle + theta), centreY + radius * Math.sin(angle + theta))
				.colour(shade, shade, shade, 0.5f)
				.endVertex();

		builder.position(centreX + radius * Math.cos(angle), centreY + radius * Math.sin(angle))
				.colour(shade, shade, shade, 0.5f)
				.endVertex();
	}

	private void drawRenderArc(VertexFormats.PositionColour builder, double theta, double angle,
							   double centreX, double centreY, double innerRadius, double outerRadius,
							   float shade) {
		final double cos = Math.cos(angle);
		final double sin = Math.sin(angle);
		final double cosNext = Math.cos(angle + theta);
		final double sinNext = Math.sin(angle + theta);

		// 2, 1, 0

		builder.position(centreX + innerRadius * cos, centreY + innerRadius * sin)
				.colour(shade, shade, shade, 0.5f)
				.endVertex();

		builder.position(centreX + outerRadius * cosNext, centreY + outerRadius * sinNext)
				.colour(shade, shade, shade, 0.5f)
				.endVertex();

		builder.position(centreX + outerRadius * cos, centreY + outerRadius * sin)
				.colour(shade, shade, shade, 0.5f)
				.endVertex();

		// 3, 1, 2

		builder.position(centreX + innerRadius * cosNext, centreY + innerRadius * sinNext)
				.colour(shade, shade, shade, 0.5f)
				.endVertex();

		builder.position(centreX + outerRadius * cosNext, centreY + outerRadius * sinNext)
				.colour(shade, shade, shade, 0.5f)
				.endVertex();

		builder.position(centreX + innerRadius * cos, centreY + innerRadius * sin)
				.colour(shade, shade, shade, 0.5f)
				.endVertex();
	}

	/**
	 * Get the index of the given origin in the list of unlocked origins.
	 * @param origin the origin to find the index of.
	 * @return the index of the given origin in the list of unlocked origins. -1 if the origin is not unlocked.
	 */
	private int indexOf(Origin origin) {
		for (int i = 0; i < this.availableOrigins.size(); i++) {
			if (this.availableOrigins.get(i).getValue().equals(origin)) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public void tick() {
		if (!isDown(ClientEvents.SELECT_ORIGIN)) {
			this.onClose();
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		double outerEdgeSize = this.getOuterEdgeRadius();
		double innerButtonSize = outerEdgeSize * 0.2;
		double innerEdgeSize = outerEdgeSize * 0.25;

		int selectedButton = this.getSelectedButton((float) mouseX, (float) mouseY,
				innerButtonSize, innerEdgeSize, outerEdgeSize);

		if (button == 0 && selectedButton > -1) {
			if (selectedButton < 8) {
				int index = selectedButton + this.getPage() * 8;

				if (index < this.availableOrigins.size()) {
					var originPair = this.availableOrigins.get(index);
					IUnlockedOriginData originData = originPair.getKey();

					long timeRemainingTicks =
							originData.getUnlockTimeTicks()
							- this.playerOriginContainer.getOwner().getLevel().getGameTime();

					if (!originPair.getValue().equals(this.currentOrigin) && timeRemainingTicks <= 0) {
						// ask the server to switch to the selected origin
						NetworkManager.sendToServer(new C2SSwitchOriginPacket(originData.getOrigin()));
						// close the GUI
						this.onClose();
					}
				}
			} else if (selectedButton == 8) {
				assert this.minecraft != null;

				this.minecraft.getSoundManager().play(
						SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)
				);

				this.minecraft.setScreen(new RollOriginScreen());
			}

			return true;
		}

		return false;
	}

	// TODO a way to scroll without needing a scroll wheel. A/D? < > Buttons?
	// Likely a <  Page 1/1   > design
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (delta < 0) {
			this.page = Math.max(0, this.page + delta);
		} else {
			this.page = Math.min(this.getLastPage(), this.page + delta);
		}

		return true;
	}

	/**
	 * Get the last page that exists in this origin select screen.
	 * @return the last page that exists in this origin select screen.
	 */
	private int getLastPage() {
		return (this.availableOrigins.size() - 1) / 8 + 1;
	}

	private int getPage() {
		return (int) this.page;
	}

	/**
	 * Get the radius of the outer edge of the outer ring of origins.
	 * @return the radius of the outer edge of the outer ring of origins.
	 */
	private double getOuterEdgeRadius() {
		return this.scaleFactor * this.height / 2.5;
	}

	/**
	 * Get the selected button in the GUI.
	 * @param mouseX the x position of the mouse on the screen.
	 * @param mouseY the y position of the mouse on the screen.
	 * @param innerButtonSize the radius of the central (inner) button.
	 * @param innerEdgeSize the radius of the inner edge of the outer ring of buttons.
	 * @param outerEdgeSize the radius of the outer edge of the outer ring of buttons.
	 * @return the sector selected. -1 is returned if no button is selected, 8 is returned if the centre button is
	 * selected, and 0-7 are returned for the buttons on the outer ring of buttons.
	 */
	private int getSelectedButton(float mouseX, float mouseY,
								  double innerButtonSize, double innerEdgeSize, double outerEdgeSize) {
		float[] mousePosPolar = rect2polar(mouseX - (float)(this.width / 2.0), mouseY - (float)(this.height / 2.0));

		if (mousePosPolar[0] < innerButtonSize) {
			return 8;
		} else if (mousePosPolar[0] >= innerEdgeSize && mousePosPolar[0] < outerEdgeSize) {
			return SECTORS.get(mousePosPolar[1]);
		}

		// no button selected
		return -1;
	}

	// Non-Render Non-Input Screen Methods

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void onClose() {
		if (this.minecraft != null && this.minecraft.player != null) {
			IUnlockedOrigins.getUnlockedOrigins(this.minecraft.player).setPage(this.getPage());
		}

		super.onClose();
	}

	private static final ItemStack ORB_OF_ORIGIN_STACK = new ItemStack(ModItems.ORB_OF_ORIGIN.get());
	private static final Logger LOGGER = LogUtils.getLogger();

	/**
	 * The angles of the start of each sector of the outer ring.
	 */
	private static final Division<Integer> SECTORS = new Division<Integer>()
			.addSection(2 * Math.PI * (6.0/8.0), 0)
			.addSection(2 * Math.PI * (7.0/8.0), 1)
			.addSection(2 * Math.PI * (8.0/8.0), 2)
			.addSection(0, 2)
			.addSection(2 * Math.PI * (1.0/8.0), 3)
			.addSection(2 * Math.PI * (2.0/8.0), 4)
			.addSection(2 * Math.PI * (3.0/8.0), 5)
			.addSection(2 * Math.PI * (4.0/8.0), 6)
			.addSection(2 * Math.PI * (5.0/8.0), 7);

	/**
	 * Check if the given key for the key mapping is down. This is preferred over .isDown() in screens due to isDown
	 * only working is this.minecraft.screen == null.
	 * @param mapping the mapping to check.
	 * @return if the key mapping is down.
	 */
	private static boolean isDown(KeyMapping mapping) {
		if (mapping.isUnbound()) {
			return false;
		}

		InputConstants.Key key = mapping.getKey();
		long window = Minecraft.getInstance().getWindow().getWindow();
		int value = key.getValue();

		if (key.getType() == InputConstants.Type.KEYSYM) {
			return GLFW.glfwGetKey(window, value) != GLFW.GLFW_RELEASE;
		} else if (key.getType() == InputConstants.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(window, value) != GLFW.GLFW_RELEASE;
		}

		return false;
	}

	/**
	 * Converts the given coordinates from rectangular (cartesian) to polar space.
	 * @param x the x coordinate in cartesian coordinates.
	 * @param y the y coordinate in cartesian coordinates.
	 * @return a size-2 array containing [r, theta], where r is the distance from the origin, and theta is the angle
	 * clockwise from the horizontal, in radians. If the case that r is 0, theta is also 0.
	 */
	private static float[] rect2polar(float x, float y) {
		float r = (float) Math.sqrt(x * x + y * y);
		float theta = (float) Math.atan2(y, x);

		if (theta < 0) {
			theta += 2 * Math.PI; // Adjusted to ensure theta is between 0 and 2 pi
		}

		return new float[] {r, theta};
	}
}
