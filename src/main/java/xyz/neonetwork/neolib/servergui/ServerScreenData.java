package xyz.neonetwork.neolib.servergui;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import xyz.neonetwork.neolib.gui.ScreenElementType;
import xyz.neonetwork.neolib.gui.ScreenGridCoordinate;
import xyz.neonetwork.neolib.textures.NeoTexture;
import xyz.neonetwork.neolib.utilities.NeoComponent;

import java.io.*;
import java.util.Map;
import java.util.UUID;

public class ServerScreenData implements Serializable {
	public static final StreamCodec<ByteBuf, ServerScreenData> STREAM_CODEC =
		ByteBufCodecs.BYTE_ARRAY.map(ServerScreenData::fromByteArray, ServerScreenData::toByteArray);

	private final UUID uuid;
	private final String title;
	private final String texture;
	private final boolean preventScreenClose;

	private final int columnWidth;
	private final int rowHeight;
	private final int padding;
	private final int columns;
	private final int rows;

	private final Map<String, ScreenElementType> elementTypes;
	private final Map<String, ScreenGridCoordinate> grid;
	private final Map<String, MetaEditBoxWidget> metaEditBoxes;
	private final Map<String, MetaMultiLineEditBoxWidget> metaMLEditBoxes;
	private final Map<String, MetaStringWidget> metaStrings;
	private final Map<String, MetaButtonWidget> metaButtons;
	private final Map<String, MetaItemWidget> metaItems;

	public ServerScreenData(UUID uuid, Component title, NeoTexture texture, NeoServerScreenGrid screenGrid, boolean preventScreenClose) {
		this.uuid = uuid;
		this.title = NeoComponent.toJson(title);
		this.texture = texture.textureName;
		this.preventScreenClose = preventScreenClose;
		this.columnWidth = screenGrid.getColumnWidth();
		this.rowHeight = screenGrid.getRowHeight();
		this.padding = screenGrid.getPadding();
		this.columns = screenGrid.getColumns();
		this.rows = screenGrid.getRows();
		this.elementTypes = screenGrid.getScreenElementTypes();
		this.grid = screenGrid.getScreenGridCoordinates();
		this.metaEditBoxes = screenGrid.getMetaEditBoxes();
		this.metaMLEditBoxes = screenGrid.getMetaMLEditBoxes();
		this.metaStrings = screenGrid.getMetaStrings();
		this.metaButtons = screenGrid.getMetaButtons();
		this.metaItems = screenGrid.getMetaItems();
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public Component getTitle() {
		return NeoComponent.fromJson(this.title);
	}

	public NeoTexture getTexture() {
		return NeoTexture.getTexture(this.texture);
	}

	public boolean getPreventScreenClose() {
		return this.preventScreenClose;
	}

	public int getColumnWidth() {
		return this.columnWidth;
	}

	public int getRowHeight() {
		return this.rowHeight;
	}

	public int getPadding() {
		return this.padding;
	}

	public int getColumns() {
		return this.columns;
	}

	public int getRows() {
		return this.rows;
	}

	public Map<String, ScreenElementType> getElementTypes() {
		return this.elementTypes;
	}

	public Map<String, ScreenGridCoordinate> getScreenGridCoordinates() {
		return this.grid;
	}

	public Map<String, MetaEditBoxWidget> getMetaEditBoxes() {
		return this.metaEditBoxes;
	}

	public Map<String, MetaMultiLineEditBoxWidget> getMetaMLEditBoxes() {
		return this.metaMLEditBoxes;
	}

	public Map<String, MetaStringWidget> getMetaStrings() {
		return this.metaStrings;
	}

	public Map<String, MetaButtonWidget> getMetaButtons() {
		return this.metaButtons;
	}

	public Map<String, MetaItemWidget> getMetaItems() {
		return this.metaItems;
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
	public static ServerScreenData fromByteArray(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			return (ServerScreenData) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
