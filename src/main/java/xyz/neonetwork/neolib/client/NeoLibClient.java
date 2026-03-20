package xyz.neonetwork.neolib.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import xyz.neonetwork.neolib.NeoLib;

@Mod(value = NeoLib.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = NeoLib.MODID, value = Dist.CLIENT)
public class NeoLibClient {
	public NeoLibClient(IEventBus eventBus, ModContainer container) {
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		eventBus.register(ClientModEvents.class);
	}

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {

	}
}
