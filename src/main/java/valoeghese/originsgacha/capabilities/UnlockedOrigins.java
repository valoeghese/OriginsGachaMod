package valoeghese.originsgacha.capabilities;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnlockedOrigins implements IUnlockedOrigins, ICapabilitySerializable<CompoundTag> {
	public UnlockedOrigins(Player player) {
		this.player = player;
	}

	private final transient Player player;
	private final transient LazyOptional<IUnlockedOrigins> lazyOptionalOfThis = LazyOptional.of(() -> this);

	// IUnlockedOrigins

	@Override
	public void tick() {

	}

	@Override
	public List<ResourceKey<Origin>> getUnlockedOrigins() {
		return List.of();
	}

	@Override
	public Player getOwner() {
		return this.player;
	}


	// Capability and Serialisation

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return CAPABILITY.orEmpty(cap, this.lazyOptionalOfThis);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
	}

	public static final ResourceLocation ID = new ResourceLocation("origins_gacha", "unlocked_origins");
	public static final Capability<IUnlockedOrigins> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
}
