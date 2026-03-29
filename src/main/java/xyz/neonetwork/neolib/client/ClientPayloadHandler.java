package xyz.neonetwork.neolib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xyz.neonetwork.neolib.gui.*;
import xyz.neonetwork.neolib.servergui.ServerScreenData;
import xyz.neonetwork.neolib.servergui.ServerScreenPacket;
import xyz.neonetwork.neolib.toast.NeoToast;
import xyz.neonetwork.neolib.toast.NeoToastData;
import xyz.neonetwork.neolib.toast.NeoToastPacket;

public class ClientPayloadHandler {
	public static void handleToastPacket(final NeoToastPacket toastPacket, final IPayloadContext context) {
		NeoToastData toastData = toastPacket.toastData();
		if (toastData == null) return;
		Toast toast = new NeoToast(toastData.getTitle(), toastData.getMessage(), toastData.getTexture(), toastData.getDuration());
		Minecraft.getInstance().getToasts().addToast(toast);
	}

	public static void handleServerScreenPacket(final ServerScreenPacket screenPacket, final IPayloadContext context) {
		ServerScreenData screenData = screenPacket.serverScreenData();
		NeoScreenGrid grid = new NeoScreenGrid(screenData);
		NeoScreen screen = new NeoScreen(screenData.getUUID(), screenData.getTitle(), screenData.getTexture(), grid, (finalGrid) -> {
			PacketDistributor.sendToServer(new ScreenEventPacket(new ScreenEventData(screenData.getUUID(), ScreenEventType.CLOSE, null, finalGrid.getAllEditBoxValues())));
		});
		screen.show();
	}

	public static void handleScreenEventPacket(final ScreenEventPacket screenEventPacket, final IPayloadContext context) {
		if (screenEventPacket == null || screenEventPacket.screenEventData() == null) return;
		ScreenEventData screenEventData = screenEventPacket.screenEventData();
		NeoScreen.processIncomingPacket(screenEventData);
	}
}
