package xyz.neonetwork.neolib.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import xyz.neonetwork.neolib.NeoLib;

public class NeoComponent {
	public static Component Chat(String message, Object... args) {
		return Component.literal(String.format(message.replace("&", "§"), args));
	}

	public static Component Chat(Component message, Object... args) {
		return Chat(message.getString(), args);
	}

	public static FormattedCharSequence autoTruncateText(Font font, Component message, int width) {
		if (font.width(message) > width) {
			FormattedText formattedtext = font.substrByWidth(message, width - font.width(CommonComponents.ELLIPSIS));
			return Language.getInstance().getVisualOrder(FormattedText.composite(formattedtext, CommonComponents.ELLIPSIS));
		}
		return message.getVisualOrderText();
	}

	public static String toJson(Component message) {
		if (message == null) return null;
		if (NeoLib.server != null) {
			return Component.Serializer.toJson(message, NeoLib.server.registryAccess());
		}
		ClientPacketListener cpl = Minecraft.getInstance().getConnection();
		if (cpl == null) return null;
		return Component.Serializer.toJson(message, cpl.registryAccess());
	}

	public static Component fromJson(String message) {
		if (message == null) return null;
		if (NeoLib.server != null) {
			return Component.Serializer.fromJson(message, NeoLib.server.registryAccess());
		}
		ClientPacketListener cpl = Minecraft.getInstance().getConnection();
		if (cpl == null) return null;
		return Component.Serializer.fromJson(message, cpl.registryAccess());
	}
}
