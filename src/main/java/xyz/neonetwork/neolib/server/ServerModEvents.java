package xyz.neonetwork.neolib.server;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import xyz.neonetwork.neolib.client.ClientPayloadHandler;
import xyz.neonetwork.neolib.gui.ScreenEventPacket;
import xyz.neonetwork.neolib.servergui.ServerScreenPacket;
import xyz.neonetwork.neolib.toast.NeoToastPacket;

public class ServerModEvents {
	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");
		registrar.executesOn(HandlerThread.NETWORK);
		registrar.playToClient(
			NeoToastPacket.TYPE,
			NeoToastPacket.STREAM_CODEC,
			ServerPayloadHandler::handleToastPacket
		);
		registrar.playToClient(
			ServerScreenPacket.TYPE,
			ServerScreenPacket.STREAM_CODEC,
			ServerPayloadHandler::handleServerScreenPacket
		);
		registrar.playBidirectional(
			ScreenEventPacket.TYPE,
			ScreenEventPacket.STREAM_CODEC,
			ServerPayloadHandler::handleScreenEventPacket
		);
	}
}
