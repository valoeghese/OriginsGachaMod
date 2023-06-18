package valoeghese.originsgacha.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import valoeghese.originsgacha.ClientEvents;
import valoeghese.originsgacha.screens.util.VertexFormats;

public class OriginSelectScreen extends Screen {
	public OriginSelectScreen() {
		super(Component.translatable("screens.origins_gacha.select"));
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		super.render(stack, mouseX, mouseY, partialTick);

		try (VertexFormats.PositionColour builder = VertexFormats.drawPositionColour(VertexFormat.Mode.QUADS)) {
			builder.position(0, 0, 0).colour(0.5f, 0.5f, 0.5f, 0.5f).endVertex();
			builder.position(0, 10, 0).colour(0.5f, 0.5f, 0.5f, 0.5f).endVertex();
			builder.position(10, 10, 0).colour(0.5f, 0.5f, 0.5f, 0.5f).endVertex();
			builder.position(10, 0, 0).colour(0.5f, 0.5f, 0.5f, 0.5f).endVertex();
		}
	}

	@Override
	public void tick() {
		if (!isDown(ClientEvents.SELECT_ORIGIN)) {
			System.out.println("closing");
			this.onClose();
		}
	}

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
}
