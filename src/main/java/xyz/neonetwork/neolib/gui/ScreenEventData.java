package xyz.neonetwork.neolib.gui;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import xyz.neonetwork.neolib.servergui.ServerScreenData;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScreenEventData implements Serializable {
	public static final StreamCodec<ByteBuf, ScreenEventData> STREAM_CODEC =
		ByteBufCodecs.BYTE_ARRAY.map(ScreenEventData::fromByteArray, ScreenEventData::toByteArray);

	private final UUID uuid;
	private final ScreenEventType type;
	private final String name;
	private final Map<String, String> values;

	public ScreenEventData(UUID uuid, ScreenEventType type, String name, Map<String, String> values) {
		this.uuid = uuid;
		this.type = type;
		this.name = name;
		this.values = values;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public ScreenEventType getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public Map<String, String> getValues() {
		return this.values;
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
	public static ScreenEventData fromByteArray(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			return (ScreenEventData) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
