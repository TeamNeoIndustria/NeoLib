package xyz.neonetwork.neolib.servergui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.gui.ScreenEventData;
import xyz.neonetwork.neolib.gui.ScreenEventPacket;
import xyz.neonetwork.neolib.gui.ScreenEventType;
import xyz.neonetwork.neolib.textures.NeoTexture;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class NeoServerScreen {

	private static final HashMap<UUID, NeoServerScreen> screenMap = new HashMap<>();

	private final UUID uuid;
	private final ServerPlayer player;
	private final Component title;
	private final NeoTexture texture;
	private final NeoServerScreenGrid grid;
	private final OnClose onClose;

	public NeoServerScreen(ServerPlayer player, Component title, NeoTexture texture, NeoServerScreenGrid grid, OnClose onClose) {
		UUID screenUUID;
		do {
			screenUUID = UUID.randomUUID();
		} while (screenMap.containsKey(screenUUID));
		this.uuid = screenUUID;
		screenMap.put(screenUUID, this);
		this.player = player;
		this.title = title;
		this.texture = texture;
		this.grid = grid;
		this.onClose = onClose;
	}

	public UUID getScreenUUID() {
		return this.uuid;
	}

	public void show(boolean preventScreenClose) {
		if (player == null) return;
		PacketDistributor.sendToPlayer(player, new ServerScreenPacket(new ServerScreenData(this.uuid, this.title, this.texture, this.grid, true)));
	}

	public void close() {
		if (player == null) return;
		PacketDistributor.sendToPlayer(player, new ScreenEventPacket(new ScreenEventData(this.uuid, ScreenEventType.CLOSE, null, null)));
		screenMap.remove(this.uuid);
	}

	public static NeoServerScreen getServerScreen(UUID screenUUID) {
		return screenMap.get(screenUUID);
	}

	public static void processIncomingPacket(String playerUUID, @NotNull ScreenEventData screenEventData) {
		NeoServerScreen serverScreen = getServerScreen(screenEventData.getUUID());
		if (!Objects.equals(playerUUID, serverScreen.player.getStringUUID())) return;
		switch (screenEventData.getType()) {
			case BUTTON:
				NeoServerScreenGrid.OnPress callback = serverScreen.grid.getCallback(screenEventData.getName());
				if (callback == null) return;
				callback.onPress(serverScreen, screenEventData.getValues());
				break;
			case CLOSE:
				serverScreen.onClose.onClose(serverScreen.player, screenEventData.getValues());
				break;
			default:
				break;
		}
	}

	// Need to listen for player leave event

	public interface OnClose {
		void onClose(ServerPlayer player, Map<String, String> values);
	}
}
