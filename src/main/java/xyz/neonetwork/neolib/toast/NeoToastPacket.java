package xyz.neonetwork.neolib.toast;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import xyz.neonetwork.neolib.NeoLib;

public record NeoToastPacket(NeoToastData toastData) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<NeoToastPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "toast"));

	public static final StreamCodec<ByteBuf, NeoToastPacket> STREAM_CODEC = StreamCodec.composite(
		NeoToastData.STREAM_CODEC,
		NeoToastPacket::toastData,
		NeoToastPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
