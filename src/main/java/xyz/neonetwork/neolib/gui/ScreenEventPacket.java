package xyz.neonetwork.neolib.gui;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import xyz.neonetwork.neolib.NeoLib;

public record ScreenEventPacket(ScreenEventData screenEventData) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ScreenEventPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "screenevent"));

	public static final StreamCodec<ByteBuf, ScreenEventPacket> STREAM_CODEC = StreamCodec.composite(
		ScreenEventData.STREAM_CODEC,
		ScreenEventPacket::screenEventData,
		ScreenEventPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
