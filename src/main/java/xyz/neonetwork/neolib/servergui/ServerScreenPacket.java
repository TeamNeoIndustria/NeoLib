package xyz.neonetwork.neolib.servergui;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import xyz.neonetwork.neolib.NeoLib;

public record ServerScreenPacket(ServerScreenData serverScreenData) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerScreenPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "serverscreen"));

	public static final StreamCodec<ByteBuf, ServerScreenPacket> STREAM_CODEC = StreamCodec.composite(
		ServerScreenData.STREAM_CODEC,
		ServerScreenPacket::serverScreenData,
		ServerScreenPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}