package xyz.neonetwork.neolib.server;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import xyz.neonetwork.neolib.toast.NeoToastPacket;

public class ServerModEvents {
	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");
		registrar.executesOn(HandlerThread.NETWORK);
		registrar.playToClient(
			NeoToastPacket.TYPE,
			NeoToastPacket.STREAM_CODEC,
			ServerPayloadHandler::handleDataOnNetwork
		);
	}
}
