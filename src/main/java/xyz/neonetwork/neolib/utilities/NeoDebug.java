package xyz.neonetwork.neolib.utilities;

import xyz.neonetwork.neolib.NeoLib;

import java.lang.reflect.Field;

public class NeoDebug {
	public static void dumpFields(Object... obj) {
		for (Object object : obj) {
			for (Field field : object.getClass().getFields()) {
				try {
					NeoLib.LOGGER.info("{}: [{}]", field.getName(), field.get(field.getName()));
				} catch (IllegalAccessException exception) {
					NeoLib.LOGGER.error("Illegal Access: {}", (Object) exception.getStackTrace());
				}
			}
		}
	}
}
