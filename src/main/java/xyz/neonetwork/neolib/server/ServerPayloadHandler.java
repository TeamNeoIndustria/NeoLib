package xyz.neonetwork.neolib.server;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xyz.neonetwork.neolib.NeoLib;
import xyz.neonetwork.neolib.gui.ScreenEventData;
import xyz.neonetwork.neolib.gui.ScreenEventPacket;
import xyz.neonetwork.neolib.servergui.NeoServerScreen;
import xyz.neonetwork.neolib.servergui.ServerScreenPacket;
import xyz.neonetwork.neolib.toast.NeoToastPacket;

import java.util.Map;

public class ServerPayloadHandler {
	public static void handleToastPacket(final NeoToastPacket toastPacket, final IPayloadContext context) {
		
	}

	public static void handleServerScreenPacket(final ServerScreenPacket screenPacket, final IPayloadContext context) {

	}

	public static void handleScreenEventPacket(final ScreenEventPacket screenEventPacket, final IPayloadContext context) {
		if (screenEventPacket == null || screenEventPacket.screenEventData() == null) return;
		NeoServerScreen.processIncomingPacket(screenEventPacket.screenEventData());
	}
}
