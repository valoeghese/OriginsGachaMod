package valoeghese.originsgacha.network.packet;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;
import valoeghese.originsgacha.capabilities.UnlockedOrigins;

import java.util.LinkedList;
import java.util.List;

/**
 * Packet for synchronising unlocked origins from the server to the client.
 */
public class S2CUnlockedOriginsSyncPacket implements Packet<S2CUnlockedOriginsSyncPacket> {
	/**
	 * Refers to a type of update that could occur.
	 */
	public enum UpdateType {
		/**
		 * Replace origins. This updates everything on the client.
		 */
		REPLACE_ORIGINS,
		/**
		 * Add origins. This is used when spinning the wheel to unlock new origins. The wheel screen will open
		 * and select the first origin in the list.
		 */
		ADD_ORIGINS,
		/**
		 * Sync unlocked origin data. This only updates existing origins (and adds any missing origins).
		 */
		SYNC_DATA
	}

	/**
	 * Create a packet for the given update type, with the given unlocked origins.
	 * @param updateType the type of update to perform.
	 * @param origins the list of unlocked origins, in serialised NBT form.
	 */
	public S2CUnlockedOriginsSyncPacket(UpdateType updateType, List<CompoundTag> origins) {
		this.updateType = updateType;
		this.origins = origins;
	}

	private final UpdateType updateType;
	private final List<CompoundTag> origins;

	/**
	 * Get the update type. This determines how the packet should modify the client origin list.
	 * @return the update type.
	 */
	public UpdateType getUpdateType() {
		return this.updateType;
	}

	/**
	 * Get the list of unlocked origins in NBT form.
	 * @return the list of unlocked origins.
	 */
	public List<CompoundTag> getOrigins() {
		return this.origins;
	}

	@Override
	public S2CUnlockedOriginsSyncPacket decode(FriendlyByteBuf buf) {
		// Read Update Type
		UpdateType type = UpdateType.values()[buf.readByte()];

		// Read Origins
		List<CompoundTag> origins = new LinkedList<>();

		int nElements = buf.readShort();

		for (int i = 0; i < nElements; i++) {
			origins.add(buf.readNbt());
		}

		return new S2CUnlockedOriginsSyncPacket(type, origins);
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		// Write Update Type
		buf.writeByte(this.updateType.ordinal());

		// Write Origins
		buf.writeShort(this.origins.size());

		for (CompoundTag unlockedOrigin : this.origins) {
			buf.writeNbt(unlockedOrigin);
		}
	}

	@Override
	public @Nullable NetworkDirection getDirection() {
		return NetworkDirection.PLAY_TO_CLIENT;
	}

	@Override
	public String toString() {
		return "S2CUnlockedOriginsSyncPacket{" +
				"updateType=" + updateType +
				", origins=" + origins +
				'}';
	}
}
