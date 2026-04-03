package xyz.neonetwork.neolib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.NeoLib;
import xyz.neonetwork.neolib.servergui.*;

import java.util.*;

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
	private Map<String, EditBoxType> editBoxTypes = new HashMap<>();
	private Map<String, MultiLineEditBox> mlEditBoxWidgets = new HashMap<>();
	private Map<String, NeoStringWidget> stringWidgets = new HashMap<>();
	private Map<String, Button.Builder> buttonWidgets = new HashMap<>();
	private List<String> disabledButtonWidgets = new ArrayList<>();
	private Map<String, NeoItemWidget> itemWidgets =  new HashMap<>();

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
					this.addEditBoxWidget(coordinate.x, coordinate.y, coordinate.width, coordinate.height, editBoxWidget.getName(), editBoxWidget.getPlaceholder(), editBoxWidget.getMaxLength(), editBoxWidget.getType());
					break;
				case ML_EDIT_BOX:
					MetaMultiLineEditBoxWidget mlEditBoxWidget = screenData.getMetaMLEditBoxes().get(screenElement.getKey());
					if (mlEditBoxWidget == null) continue;
					this.addMultiLineEditBox(coordinate.x, coordinate.y, coordinate.width, coordinate.height, mlEditBoxWidget.getName(), mlEditBoxWidget.getPlaceholder(), mlEditBoxWidget.getMaxLength(), mlEditBoxWidget.getType());
				case STRING:
					MetaStringWidget stringWidget = screenData.getMetaStrings().get(screenElement.getKey());
					if (stringWidget == null) continue;
					this.addStringWidget(coordinate.x, coordinate.y, coordinate.width, coordinate.height, stringWidget.getName(), stringWidget.getLabel(), stringWidget.getHorizontalAlign(), stringWidget.getVerticalAlign(), stringWidget.getLineHeight());
					break;
				case BUTTON:
					MetaButtonWidget buttonWidget = screenData.getMetaButtons().get(screenElement.getKey());
					if (buttonWidget == null) continue;
					this.addButtonWidget(coordinate.x, coordinate.y, coordinate.width, coordinate.height, buttonWidget.getName(), buttonWidget.getLabel(), buttonWidget.getTooltip(), buttonWidget.isDisabled(), (grid, button) -> {
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
			return true;
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

	public NeoScreenGrid addEditBoxWidget(int column, int row, int colspan, int rowspan, @NotNull String name, @NotNull Component placeholder, int maxLength) {
		return addEditBoxWidget(column, row, colspan, rowspan, name, placeholder, maxLength, EditBoxType.TEXT);
	}

	public NeoScreenGrid addEditBoxWidget(int column, int row, int colspan, int rowspan, @NotNull String name, @NotNull Component placeholder, int maxLength, EditBoxType type) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.EDIT_BOX)) return this;
		ScreenGridCoordinate coordinate = this.calculateOffsets(name, 0, 0);
		EditBox editBox = new EditBox(Minecraft.getInstance().font, coordinate.width, coordinate.height, placeholder);
		editBox.setMaxLength(maxLength);
		this.editBoxWidgets.put(name, editBox);
		this.editBoxTypes.put(name, type);
		return this;
	}

	public NeoScreenGrid addMultiLineEditBox(int column, int row, int colspan, int rowspan, @NotNull String name, @NotNull Component placeholder, int maxLength) {
		return this.addMultiLineEditBox(column, row, colspan, rowspan, name, placeholder, maxLength, EditBoxType.TEXT);
	}

	public NeoScreenGrid addMultiLineEditBox(int column, int row, int colspan, int rowspan, @NotNull String name, @NotNull Component placeholder, int maxLength, EditBoxType type) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.ML_EDIT_BOX)) return this;
		ScreenGridCoordinate coordinate = this.calculateOffsets(name, 0, 0);
		MultiLineEditBox editBox = new MultiLineEditBox(Minecraft.getInstance().font, 0, 0, coordinate.width, coordinate.height, placeholder, placeholder);
		editBox.setCharacterLimit(maxLength);
		this.mlEditBoxWidgets.put(name, editBox);
		this.editBoxTypes.put(name, type);
		return this;
	}

	public NeoScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, Component label) {
		return this.addStringWidget(column, row, colspan, rowspan, name, List.of(label), NeoStringAlign.Horizontal.CENTER, NeoStringAlign.Vertical.MIDDLE, 10);
	}

	public NeoScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, List<Component> label) {
		return this.addStringWidget(column, row, colspan, rowspan, name, label, NeoStringAlign.Horizontal.CENTER, NeoStringAlign.Vertical.MIDDLE, 10);
	}

	public NeoScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, Component label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign) {
		return this.addStringWidget(column, row, colspan, rowspan, name, List.of(label), horizontalAlign, verticalAlign, 10);
	}

	public NeoScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, List<Component> label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign) {
		return this.addStringWidget(column, row, colspan, rowspan, name, label, horizontalAlign, verticalAlign, 10);
	}

	public NeoScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, Component label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign, int lineHeight) {
		return this.addStringWidget(column, row, colspan, rowspan, name, List.of(label));
	}

	public NeoScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, List<Component> label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign, int lineHeight) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.STRING)) return this;
		NeoStringWidget stringWidget = new NeoStringWidget(0, 0, 0, 0, label, horizontalAlign, verticalAlign, lineHeight, Minecraft.getInstance().font);
		this.stringWidgets.put(name, stringWidget);
		return this;
	}

	public NeoScreenGrid addButtonWidget(int column, int row, int colspan, int rowspan, String name, Component label, Component tooltip, boolean disabled, OnPress onPressCallback) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.BUTTON)) return this;

		Button.Builder button = Button.builder(label, (onPress) -> {
			onPressCallback.onPress(this, onPress);
		});
		if (tooltip != null) button.tooltip(Tooltip.create(tooltip));
		this.buttonWidgets.put(name, button);
		if (disabled) this.disabledButtonWidgets.add(name);
		return this;
	}
	public NeoScreenGrid addItemWidget(int column, int row, int colspan, int rowspan, String name, Item item) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.ITEM)) return this;

		NeoItemWidget itemWidget = new NeoItemWidget(new ItemStack(item, 1), 0, 0, 0);
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

	public EditBoxType getEditBoxType(String name) {
		return this.editBoxTypes.get(name);
	}

	public MultiLineEditBox getMLEditBoxWidget(String name, int offsetX, int offsetY) {
		ScreenGridCoordinate coordinate = this.calculateOffsets(name, offsetX, offsetY);
		MultiLineEditBox editBox = this.mlEditBoxWidgets.get(name);
		if (coordinate == null || editBox == null) return null;
		editBox.setX(coordinate.x);
		editBox.setY(coordinate.y);
		editBox.setWidth(coordinate.width);
		editBox.setHeight(coordinate.height);
		return editBox;
	}

	public EditBoxType getMLEditBoxType(String name) {
		return this.editBoxTypes.get(name);
	}

	public NeoStringWidget getStringWidget(String name, int offsetX, int offsetY) {
		ScreenGridCoordinate coordinate = this.calculateOffsets(name, offsetX, offsetY);
		NeoStringWidget stringWidget = this.stringWidgets.get(name);
		if (coordinate == null || stringWidget == null) return null;
		stringWidget.setX(coordinate.x);
		stringWidget.setY(coordinate.y);
		stringWidget.setWidth(coordinate.width);
		stringWidget.setHeight(coordinate.height);
		return stringWidget;
	}

	public Button getButtonWidget(String name, int offsetX, int offsetY) {
		ScreenGridCoordinate coordinate = this.calculateOffsets(name, offsetX, offsetY);
		Button.Builder buttonBuilder = this.buttonWidgets.get(name);
		if (coordinate == null || buttonBuilder == null) return null;
		buttonBuilder.bounds(coordinate.x, coordinate.y, coordinate.width, coordinate.height);
		Button button = buttonBuilder.build();
		if (this.disabledButtonWidgets.contains(name)) button.active = false;
		return button;
	}

	public NeoItemWidget getItemWidget(String name, int offsetX, int offsetY) {
		ScreenGridCoordinate coordinate = this.calculateOffsets(name, offsetX, offsetY);
		NeoItemWidget itemWidget = this.itemWidgets.get(name);
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
		for (Map.Entry<String, MultiLineEditBox> entry : this.mlEditBoxWidgets.entrySet()) {
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
