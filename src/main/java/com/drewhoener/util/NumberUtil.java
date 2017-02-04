package com.drewhoener.util;

public class NumberUtil {

	public static int toInt(Object object) {
		if (object instanceof Number) {
			return ((Number) object).intValue();
		}

		try {
			return Integer.valueOf(object.toString());
		} catch (NumberFormatException e) {
		} catch (NullPointerException e) {
		}
		return 0;
	}

	public static float toFloat(Object object) {
		if (object instanceof Number) {
			return ((Number) object).floatValue();
		}

		try {
			return Float.valueOf(object.toString());
		} catch (NumberFormatException e) {
		} catch (NullPointerException e) {
		}
		return 0;
	}

	public static double toDouble(Object object) {
		if (object instanceof Number) {
			return ((Number) object).doubleValue();
		}

		try {
			return Double.valueOf(object.toString());
		} catch (NumberFormatException e) {
		} catch (NullPointerException e) {
		}
		return 0;
	}

	public static long toLong(Object object) {
		if (object instanceof Number) {
			return ((Number) object).longValue();
		}

		try {
			return Long.valueOf(object.toString());
		} catch (NumberFormatException e) {
		} catch (NullPointerException e) {
		}
		return 0;
	}
}
