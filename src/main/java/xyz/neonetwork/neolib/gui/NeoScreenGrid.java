package xyz.neonetwork.neolib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import xyz.neonetwork.neolib.NeoLib;
import xyz.neonetwork.neolib.servergui.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NeoScreenGrid {
	private final int columnWidth;
	private final int rowHeight;
	private final int padding;
	private final int columns;
	private final int rows;

	private Boolean[] occupiedSpaces;
	private Map<String, ScreenElementType> elementTypes = new HashMap<>();
	private Map<String, ScreenGridCoordinate> grid = new HashMap<>();
	private Map<String, EditBox> editBoxWidgets = new HashMap<>();
	private Map<String, StringWidget> stringWidgets = new HashMap<>();
	private Map<String, Button.Builder> buttonWidgets = new HashMap<>();
	private Map<String, ItemWidget> itemWidgets =  new HashMap<>();

	public NeoScreenGrid(ServerScreenData screenData) {
		this.columnWidth = screenData.getColumnWidth();
		this.rowHeight = screenData.getRowHeight();
		this.padding = screenData.getPadding();
		this.columns = screenData.getColumns();
		this.rows = screenData.getRows();
		occupiedSpaces = new Boolean[this.columns * this.rows];
		Arrays.fill(occupiedSpaces, false);

		for (Map.Entry<String, ScreenElementType> screenElement : screenData.getElementTypes().entrySet()) {
			ScreenGridCoordinate coordinate = screenData.getScreenGridCoordinates().get(screenElement.getKey());
			if (coordinate == null) continue;
			switch (screenElement.getValue()) {
				case EDIT_BOX:
					MetaEditBoxWidget editBoxWidget = screenData.getMetaEditBoxes().get(screenElement.getKey());
					if (editBoxWidget == null) continue;
					this.addEditBoxWidget(coordinate.x, coordinate.y, coordinate.width, coordinate.height, editBoxWidget.getName(), editBoxWidget.getPlaceholder(), editBoxWidget.getMaxLength());
					break;
				case STRING:
					MetaStringWidget stringWidget = screenData.getMetaStrings().get(screenElement.getKey());
					if (stringWidget == null) continue;
					this.addStringWidget(coordinate.x, coordinate.y, coordinate.width, coordinate.height, stringWidget.getName(), stringWidget.getLabel());
					break;
				case BUTTON:
					MetaButtonWidget buttonWidget = screenData.getMetaButtons().get(screenElement.getKey());
					if (buttonWidget == null) continue;
					this.addButtonWidget(coordinate.x, coordinate.y, coordinate.width, coordinate.height, buttonWidget.getName(), buttonWidget.getLabel(), buttonWidget.getTooltip(), (grid, button) -> {
						NeoLib.LOGGER.info("SERVER BUTTON PRESSED");
						PacketDistributor.sendToServer(new ScreenEventPacket(new ScreenEventData(screenData.getUUID(), ScreenEventType.BUTTON, buttonWidget.getName(), this.getAllEditBoxValues())));
					});
					break;
				case ITEM:
					MetaItemWidget itemWidget = screenData.getMetaItems().get(screenElement.getKey());
					if (itemWidget == null) continue;
					this.addItemWidget(coordinate.x, coordinate.y, coordinate.width, coordinate.height, itemWidget.getName(), itemWidget.getItem());
					break;
			}
		}
	}
	public NeoScreenGrid(int columnWidth, int rowHeight, int padding, int columns, int rows) {
		this.columnWidth = Math.max(columnWidth, 10);
		this.rowHeight = Math.max(rowHeight, 10);
		this.padding = Math.max(padding, 0);
		this.columns = Math.max(columns, 1);
		this.rows = Math.max(rows, 1);
		occupiedSpaces = new Boolean[this.columns * this.rows];
		Arrays.fill(occupiedSpaces, false);
	}

	private boolean nameExists(String name) {
		if (grid.containsKey(name)) {
			NeoLib.LOGGER.warn("ScreenGrid#nameExists: Duplicate name: {}", name);
			return true;
		}
		return false;
	}

	private int coordinateToOffset(int x, int y) {
		if (x < 0 || y < 0 || x > this.columns || y > this.rows) {
			NeoLib.LOGGER.warn("ScreenGrid#coordinateToOffset: Invalid column/row out of bounds X:{}, Y:{}", x, y);
			return -1;
		}
		int offset = this.columns * y;
		offset += x;
		return offset;
	}

	private boolean spaceOccupied(int x, int y) {
		int offset = coordinateToOffset(x, y);
		if (offset == -1) return true;
		return occupiedSpaces[offset];
	}

	private boolean spaceOccupied(int x, int y, int colspan, int rowspan) {
		if (x + colspan > this.columns || y + rowspan > this.rows) {
			NeoLib.LOGGER.warn("ScreenGrid#spaceOccupied: Invalid column/row out of bounds X:{}, Y:{}", x, y);
		}

		for (int currentX = x; currentX < x + colspan; currentX++) {
			for (int currentY = y; currentY < y + rowspan; currentY++) {
				if (spaceOccupied(currentX, currentY)) return true;
			}
		}
		return false;
	}

	private boolean setOccupied(int x, int y, int colspan, int rowspan, String name, ScreenElementType elementType) {
		if (spaceOccupied(x, y, colspan, rowspan)) {
			NeoLib.LOGGER.warn("ScreenGrid#setOccupied: Grid space already occupied for '{}'", name);
			return false;
		}
		for (int currentX = x; currentX < x + colspan; currentX++) {
			for (int currentY = y; currentY < y + rowspan; currentY++) {
				occupiedSpaces[coordinateToOffset(currentX, currentY)] = true;
			}
		}
		grid.put(name, new ScreenGridCoordinate(x, y, colspan, rowspan));
		elementTypes.put(name, elementType);
		return true;
	}

	public NeoScreenGrid addEditBoxWidget(int column, int row, int colspan, int rowspan, String name, Component placeholder, int maxLength) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.EDIT_BOX)) return this;

		EditBox editBox = new EditBox(Minecraft.getInstance().font, 0, 0, placeholder);
		editBox.setMaxLength(maxLength);
		this.editBoxWidgets.put(name, editBox);
		return this;
	}

	public NeoScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, Component label) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.STRING)) return this;

		StringWidget stringWidget = new StringWidget(0, 0, 0, 0, label, Minecraft.getInstance().font);
		stringWidget.alignLeft();
		this.stringWidgets.put(name, stringWidget);
		return this;
	}

	public NeoScreenGrid addButtonWidget(int column, int row, int colspan, int rowspan, String name, Component label, Component tooltip, OnPress onPressCallback) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.BUTTON)) return this;

		Button.Builder button = Button.builder(label, (onPress) -> {
			onPressCallback.onPress(this, onPress);
		});
		if (tooltip != null) button.tooltip(Tooltip.create(tooltip));
		this.buttonWidgets.put(name, button);
		return this;
	}
	public NeoScreenGrid addItemWidget(int column, int row, int colspan, int rowspan, String name, Item item) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.ITEM)) return this;

		ItemWidget itemWidget = new ItemWidget(new ItemStack(item, 1), 0, 0, 0);
		this.itemWidgets.put(name, itemWidget);
		return this;
	}

	public EditBox getEditBoxWidget(String name, int offsetX, int offsetY) {
		ScreenGridCoordinate coordinate = this.calculateOffsets(name, offsetX, offsetY);
		EditBox editBox = this.editBoxWidgets.get(name);
		if (coordinate == null || editBox == null) return null;
		editBox.setX(coordinate.x);
		editBox.setY(coordinate.y);
		editBox.setWidth(coordinate.width);
		editBox.setHeight(coordinate.height);
		return editBox;
	}

	public StringWidget getStringWidget(String name, int offsetX, int offsetY) {
		ScreenGridCoordinate coordinate = this.calculateOffsets(name, offsetX, offsetY);
		StringWidget stringWidget = this.stringWidgets.get(name);
		if (coordinate == null || stringWidget == null) return null;
		stringWidget.setX(coordinate.x);
		stringWidget.setY(coordinate.y);
		stringWidget.setWidth(coordinate.width);
		stringWidget.setHeight(coordinate.height);
		return stringWidget;
	}

	public Button getButtonWidget(String name, int offsetX, int offsetY) {
		ScreenGridCoordinate coordinate = this.calculateOffsets(name, offsetX, offsetY);
		Button.Builder button = this.buttonWidgets.get(name);
		if (coordinate == null || button == null) return null;
		button.bounds(coordinate.x, coordinate.y, coordinate.width, coordinate.height);
		return button.build();
	}

	public ItemWidget getItemWidget(String name, int offsetX, int offsetY) {
		ScreenGridCoordinate coordinate = this.calculateOffsets(name, offsetX, offsetY);
		ItemWidget itemWidget = this.itemWidgets.get(name);
		if (coordinate == null || itemWidget == null) return null;
		itemWidget.setX(coordinate.x + (coordinate.width / 2f));
		itemWidget.setY(coordinate.y + (coordinate.height / 2f));
		int minDimension = Math.min(coordinate.width, coordinate.height);
		itemWidget.setScale(minDimension / 16f);
		return itemWidget;
	}

	public String getEditBoxValue(String name) {
		EditBox editBox = this.editBoxWidgets.get(name);
		if (editBox == null) return null;
		return editBox.getValue();
	}

	public Map<String, String> getAllEditBoxValues() {
		Map<String, String> values = new HashMap<>();
		for (Map.Entry<String, EditBox> entry : this.editBoxWidgets.entrySet()) {
			if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
			values.put(entry.getKey(), entry.getValue().getValue());
		}
		return values;
	}

	public ScreenElementType getElementType(String name) {
		return this.elementTypes.get(name);
	}

	public String[] elementNames() {
		return grid.keySet().toArray(new String[0]);
	}

	public ScreenGridCoordinate calculateOffsets(String name, int offsetX, int offsetY) {
		ScreenGridCoordinate coordinate = grid.get(name);
		if (coordinate == null) return null;

		return new ScreenGridCoordinate(
			offsetX + coordinate.x * (this.columnWidth + this.padding),
			offsetY + coordinate.y * (this.rowHeight + this.padding),
			this.columnWidth + (coordinate.width - 1) * (this.columnWidth + this.padding),
			this.rowHeight + (coordinate.height - 1) * (this.rowHeight + this.padding)
		);
	}

	public ScreenGridCoordinate getBoundingBox(int offsetX, int offsetY) {
		return new ScreenGridCoordinate(offsetX, offsetY, ((columnWidth + padding) * columns) - padding, ((rowHeight + padding) * rows) - padding);
	}

//	@OnlyIn(Dist.CLIENT)
	public interface OnPress {
		void onPress(NeoScreenGrid grid, Button button);
	}
}
