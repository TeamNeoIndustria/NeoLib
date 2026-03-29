package xyz.neonetwork.neolib.toast;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.NeoLib;
import xyz.neonetwork.neolib.textures.NeoTexture;
import xyz.neonetwork.neolib.utilities.NeoComponent;

import java.io.*;
import java.util.Objects;

public class NeoToastData implements Serializable {
	String title;
	String message;
	String textureName;
	long duration;

	public static final StreamCodec<ByteBuf, NeoToastData> STREAM_CODEC =
		ByteBufCodecs.BYTE_ARRAY.map(NeoToastData::fromByteArray, NeoToastData::toByteArray);

	public NeoToastData(@NotNull Component title, @NotNull Component message, @NotNull NeoTexture texture, long duration) {
		this.title = NeoComponent.toJson(title);
		this.message = NeoComponent.toJson(message);
//		this.title = Component.Serializer.toJson(title, NeoLib.server.registryAccess());
//		this.message = Component.Serializer.toJson(message, NeoLib.server.registryAccess());
		this.textureName = texture.textureName;
		this.duration = duration;
	}

	public Component getTitle() {
		return NeoComponent.fromJson(title);
//		if (NeoLib.server != null) {
//			return Component.Serializer.fromJson(title, NeoLib.server.registryAccess());
//		}
//		return Component.Serializer.fromJson(title, Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess());
	}

	public Component getMessage() {
		return NeoComponent.fromJson(message);
//		if (NeoLib.server != null) {
//			return Component.Serializer.fromJson(message, NeoLib.server.registryAccess());
//		}
//		return Component.Serializer.fromJson(message, Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess());
	}

	public NeoTexture getTexture() {
		return NeoTexture.getTexture(textureName);
	}

	public long getDuration() {
		return duration;
	}

	public byte[] toByteArray() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
		return baos.toByteArray();
	}
	public static NeoToastData fromByteArray(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			return (NeoToastData) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
