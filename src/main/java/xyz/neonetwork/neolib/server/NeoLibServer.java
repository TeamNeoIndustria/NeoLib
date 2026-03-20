package xyz.neonetwork.neolib.server;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import xyz.neonetwork.neolib.NeoLib;

@Mod(value = NeoLib.MODID, dist = Dist.DEDICATED_SERVER)
@EventBusSubscriber(modid = NeoLib.MODID, value = Dist.DEDICATED_SERVER)
public class NeoLibServer {
	public NeoLibServer(IEventBus eventBus, ModContainer container) {
		eventBus.register(ServerModEvents.class);
	}

	@SubscribeEvent
	static void onServerSetup(FMLDedicatedServerSetupEvent event) {

	}
}
