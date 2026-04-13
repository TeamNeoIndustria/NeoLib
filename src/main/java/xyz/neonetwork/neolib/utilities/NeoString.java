package xyz.neonetwork.neolib.utilities;

import java.util.UUID;

public class NeoString {
	public static UUID UUIDFromString(String uuid) {
		String strippedUUID = uuid.toLowerCase().replace("-", "");
		if (strippedUUID.length() != 32) return null;
		for (char c : strippedUUID.toCharArray()) {
			if (!(c >= '0' && c <= '9') && !(c >= 'a' && c <= 'f') && !(c >= 'A' && c <= 'F')) return null;
		}
		try {
			return UUID.fromString(strippedUUID.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static String formatCurrency(int amount) {
		return formatNumber(amount) + "¢";
	}

	public static String formatNumber(int number) {
		return String.format("%,d", number);
	}

	public static String formatNumber(long number) {
		return String.format("%,d", number);
	}

	public static String formatNumber(double number) {
		return String.format("%1$,f", number);
	}

	public static String formatNumber(double number, int decimalPlaces) {
		if (decimalPlaces < 1) return formatNumber(number);
		String format = "%1$,." + decimalPlaces + "f";
		return String.format(format, number);
	}

	public static String formatNumber(float number) {
		return String.format("%1$,f", number);
	}

	public static String formatNumber(float number, int decimalPlaces) {
		if (decimalPlaces < 1) return formatNumber(number);
		String format = "%1$,." + decimalPlaces + "f";
		return String.format(format, number);
	}
}
