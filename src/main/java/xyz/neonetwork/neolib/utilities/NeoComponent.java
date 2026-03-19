package xyz.neonetwork.neolib.utilities;

import net.minecraft.network.chat.Component;

public class NeoComponent {
	public static Component Chat(String message, Object... args) {
		return Component.literal(String.format(message.replace("&", "§"), args));
	}

	public static Component Chat(Component message, Object... args) {
		return Chat(message.getString(), args);
	}
}
