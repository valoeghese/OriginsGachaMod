package valoeghese.originsgacha;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Registry for sounds.
 */
public final class OriginsGachaSounds {
	/**
	 * The deferred registry for sounds.
	 */
	private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registry.SOUND_EVENT_REGISTRY, "origins_gacha");

	/**
	 * The sound for switching origin.
	 */
	public static final RegistryObject<SoundEvent> SWITCH_ORIGIN = register(() ->
			new SoundEvent(new ResourceLocation("origins_gacha", "actions.switch_origin"))
	);

	public static final RegistryObject<SoundEvent> ROLL_ORIGIN = register(() ->
			new SoundEvent(new ResourceLocation("origins_gacha", "actions.rolling_origin"))
	);

	public static final RegistryObject<SoundEvent> UNLOCK_ORIGIN = register(() ->
			new SoundEvent(new ResourceLocation("origins_gacha", "events.receive_rolled_origin"))
	);

	/**
	 * Registers the given sound event to the deferred registry.
	 * @param eventSupplier the supplier for the sound event to register.
	 */
	private static RegistryObject<SoundEvent> register(Supplier<SoundEvent> eventSupplier) {
		return SOUND_EVENTS.register(eventSupplier.get().getLocation().getPath(), eventSupplier);
	}

	/**
	 * Register the given sound events.
	 * @param bus the bus to register the sound events on.
	 */
	public static void register(IEventBus bus) {
		SOUND_EVENTS.register(bus);
	}
}
