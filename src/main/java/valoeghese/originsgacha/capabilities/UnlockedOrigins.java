package valoeghese.originsgacha.capabilities;

import com.mojang.logging.LogUtils;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import valoeghese.originsgacha.network.NetworkManager;
import valoeghese.originsgacha.network.packet.S2CUnlockedOriginsSyncPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UnlockedOrigins implements IUnlockedOrigins, ICapabilitySerializable<CompoundTag> {
	public UnlockedOrigins(Player player) {
		this.player = player;
		this.unlockedOrigins = new ArrayList<>();
	}

	private final transient Player player;
	private final transient LazyOptional<IUnlockedOrigins> lazyOptionalOfThis = LazyOptional.of(() -> this);
	private transient boolean shouldSync;

	private final List<UnlockedOrigin> unlockedOrigins;

	// IUnlockedOrigins

	@Override
	public void tick() {
		// Note: from prior testing, the first deserialisation happens before this is run, given there is data to
		// deserialise.

		// if no unlocked origins, select two random origins.
		if (this.unlockedOrigins.size() == 0 && this.player instanceof ServerPlayer player) {
			Registry<Origin> originRegistry = OriginsAPI.getOriginsRegistry(player.getServer());

			var origins = new ArrayList<>(originRegistry.entrySet());
			origins.removeIf(entry -> entry.getValue().isUnchoosable());
			Collections.shuffle(origins);

			// Make sure there are actually enough origins for the mod to function
			// Who would even want to use this mod with only one selectable origin anyway

			if (origins.size() < 2) {
				throw new IllegalStateException("Less than 2 selectable origins! OriginsGacha requires at least 2 selectable origins be present.");
			}

			// Add the two origins at the top of the shuffled list
			this.unlockedOrigins.add(new UnlockedOrigin(origins.get(0).getKey(), this.getGameTime()));
			this.unlockedOrigins.add(new UnlockedOrigin(origins.get(1).getKey(), this.getGameTime()));

			// We should synchronise the unlocked origins to the client.
			this.shouldSync = true;
		}

		// synchronise data if the client needs an update sent to it.
		if (this.shouldSync && this.player instanceof ServerPlayer player) {
			this.shouldSync = false;

			NetworkManager.sendToPlayer(player, new S2CUnlockedOriginsSyncPacket(
					S2CUnlockedOriginsSyncPacket.UpdateType.REPLACE_ORIGINS,
					this.unlockedOrigins.stream().map(UnlockedOrigin::serializeSyncNBT).toList()
			));
		}
	}

	@Override
	@Nullable
	public IUnlockedOriginData getUnlockedOrigin(ResourceKey<Origin> data) {
		return this.unlockedOrigins.stream().filter(uo -> uo.origin.equals(data)).findFirst().orElse(null);
	}

	@Override
	public void sync() {
		this.shouldSync = true;
	}

	public void onSync(S2CUnlockedOriginsSyncPacket packet) {
		if (packet.getUpdateType() == S2CUnlockedOriginsSyncPacket.UpdateType.REPLACE_ORIGINS) {
			this.unlockedOrigins.clear();
		}

		if (packet.getUpdateType() == S2CUnlockedOriginsSyncPacket.UpdateType.SYNC_DATA) {
			// O(n * m)
			// I would make a lookup table first but I only realistically ever sync one at a time, making it O(n).

			// Alternatively a linked hash map between the resource key and unlocked origin instance could
			// allow for SYNC_DATA and ADD_ORIGINS to be merged with good performance. I might do this in the future.

			for (CompoundTag tag : packet.getOrigins()) {
				UnlockedOrigin origin = this.readUnlockedOrigin(tag);
				// find index of previous origin with same key
				int index = this.unlockedOrigins.indexOf(origin);

				if (index == -1) {
					this.unlockedOrigins.add(origin);
				} else {
					this.unlockedOrigins.set(index, origin);
				}
			}
		} else {
			packet.getOrigins().stream().map(this::readUnlockedOrigin).forEach(this.unlockedOrigins::add);
		}
	}

	@Override
	public List<IUnlockedOriginData> getUnlockedOrigins() {
		return List.copyOf(this.unlockedOrigins);
	}

	@Override
	public boolean hasOrigin(ResourceKey<Origin> origin) {
		return this.unlockedOrigins.stream().anyMatch(uo -> uo.origin.equals(origin));
	}

	@Override
	public Player getOwner() {
		return this.player;
	}

	// Capability and Serialisation

	/**
	 * Get the current game time.
	 * @return the current game time, in ticks.
	 */
	long getGameTime() {
		return this.getOwner().getLevel().getGameTime();
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return CAPABILITY.orEmpty(cap, this.lazyOptionalOfThis);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();

		// Write unlocked origins
		ListTag unlockedOriginsList = new ListTag();

		for (UnlockedOrigin origin : this.unlockedOrigins) {
			unlockedOriginsList.add(origin.serializeNBT());
		}

		tag.put("unlockedOrigins", unlockedOriginsList);

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		// Read Unlocked Origins
		if (nbt.contains("unlockedOrigins", Tag.TAG_LIST)) {
			this.unlockedOrigins.clear(); // just in case

			ListTag unlockedOriginsList = nbt.getList("unlockedOrigins", Tag.TAG_COMPOUND);
			final int size = unlockedOriginsList.size();

			for (int i = 0; i < size; i++) {
				this.unlockedOrigins.add(this.readUnlockedOrigin(unlockedOriginsList.getCompound(i)));
			}
		}

		this.shouldSync = true;
	}

	/**
	 * Create an {@link UnlockedOrigin} from the given serialised NBT data.
	 * @param tag the nbt tag of the unlocked origin.
	 * @return the deserialised nbt data.
	 */
	private UnlockedOrigin readUnlockedOrigin(CompoundTag tag) {
		UnlockedOrigin unlocked = new UnlockedOrigin(null, 0);
		unlocked.deserializeNBT(tag);
		return unlocked;
	}

	private static final Logger LOGGER = LogUtils.getLogger();

	public static final ResourceLocation ID = new ResourceLocation("origins_gacha", "unlocked_origins");
	public static final Capability<IUnlockedOrigins> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

	/**
	 * Represents the data of an unlocked origin.
	 */
	public final class UnlockedOrigin implements IUnlockedOriginData, INBTSerializable<CompoundTag> {
		public UnlockedOrigin(ResourceKey<Origin> origin, long unlockTimeTicks) {
			this.origin = origin;
			this.unlockTimeTicks = unlockTimeTicks;
		}

		private ResourceKey<Origin> origin;
		private long unlockTimeTicks;

		@Override
		public ResourceKey<Origin> getOrigin() {
			return this.origin;
		}

		@Override
		public long getUnlockTimeTicks() {
			return this.unlockTimeTicks;
		}

		@Override
		public void setCooldown(double cooldownTime) {
			this.setUnlockTimeTicks((long) (cooldownTime * 20) + UnlockedOrigins.this.getGameTime());
		}

		public void setUnlockTimeTicks(long unlockTimeTicks) {
			this.unlockTimeTicks = unlockTimeTicks;

			// server side: sync
			if (UnlockedOrigins.this.getOwner() instanceof ServerPlayer player) {
				NetworkManager.sendToPlayer(player, new S2CUnlockedOriginsSyncPacket(
						S2CUnlockedOriginsSyncPacket.UpdateType.SYNC_DATA,
						List.of(this.serializeSyncNBT())
				));
			}
		}

		@Override
		public String toString() {
			return "UnlockedOrigin[" +
					"origin=" + origin + ", " +
					"unlockTimeTicks=" + unlockTimeTicks + ']';
		}


		@Override
		public CompoundTag serializeNBT() {
			CompoundTag data = new CompoundTag();
			data.putString("origin", this.origin.location().toString());

			long ticksRemaining = this.unlockTimeTicks - UnlockedOrigins.this.getGameTime();
			data.putLong("ticksRemaining", ticksRemaining);

			return data;
		}

		public CompoundTag serializeSyncNBT() {
			CompoundTag data = new CompoundTag();
			data.putString("origin", this.origin.location().toString());

			data.putLong("unlockTimeTicks", this.unlockTimeTicks);

			return data;
		}

		@Override
		public void deserializeNBT(CompoundTag data) {
			ResourceLocation origin = new ResourceLocation(data.getString("origin"));
			this.origin = ResourceKey.create(OriginsDynamicRegistries.ORIGINS_REGISTRY, origin);

			if (data.contains("unlockTimeTicks")) {
				this.unlockTimeTicks = data.getLong("unlockTimeTicks");
			} else {
				this.unlockTimeTicks = UnlockedOrigins.this.getGameTime() + data.getLong("ticksRemaining");
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof UnlockedOrigin that)) return false;
			return origin.equals(that.origin);
		}

		@Override
		public int hashCode() {
			return Objects.hash(origin);
		}
	}
}
