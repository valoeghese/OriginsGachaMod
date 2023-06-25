package valoeghese.originsgacha.util;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class Utils {
	public static <T, U> Optional<U> apply(FallableFunction<T, @Nullable U> function, T value) {
		try {
			return Optional.ofNullable(function.apply(value));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Create a string for the timestamp, in HH:MM:SS. If the HH component is 0, it will not be included.
	 * @param timestampTicks the timestamp in ticks. One tick is 1/20th of a second.
	 * @return the formatted timestamp string for the given timestamp.
	 */
	public static String timestampComponent(long timestampTicks) {
		long seconds = timestampTicks / 20L;
		long minutes = seconds / 60L;
		long hours = minutes / 60L;

		// wrap seconds and minutes between 0 and 60
		seconds %= 60L;
		minutes %= 60L;

		// only show hours if more than 0
		return hours > 0 ?
				String.format("%02d:%02d:%02d", hours, minutes, seconds) :
				String.format("%02d:%02d", minutes, seconds);
	}

	@FunctionalInterface
	public interface FallableFunction<I, O> {
		O apply(I input);
	}
}
