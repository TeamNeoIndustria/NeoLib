package xyz.neonetwork.neolib;

import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import xyz.neonetwork.neolib.api.APIRequest;
import xyz.neonetwork.neolib.api.APIResponse;

import java.util.HashMap;

@Mod(NeoLib.MODID)
public class NeoLib {
    public static final String MODID = "neolib";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static MinecraftServer server;

    public NeoLib(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("NeoLib Server Loaded");
    }

    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        server = event.getServer();
    }
}
