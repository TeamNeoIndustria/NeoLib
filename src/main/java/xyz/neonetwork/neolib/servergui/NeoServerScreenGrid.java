package xyz.neonetwork.neolib.servergui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.NeoLib;
import xyz.neonetwork.neolib.gui.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeoServerScreenGrid {

	private final int columnWidth;
	private final int rowHeight;
	private final int padding;
	private final int columns;
	private final int rows;

	private final Boolean[] occupiedSpaces;
	private final Map<String, ScreenElementType> elementTypes = new HashMap<>();
	private final Map<String, ScreenGridCoordinate> grid = new HashMap<>();
	private final Map<String, MetaEditBoxWidget> metaEditBoxes = new HashMap<>();
	private final Map<String, MetaMultiLineEditBoxWidget> metaMLEditBoxes = new HashMap<>();
	private final Map<String, MetaStringWidget> metaStrings = new HashMap<>();
	private final Map<String, MetaButtonWidget> metaButtons = new HashMap<>();
	private final Map<String, OnPress> metaButtonCallbacks = new HashMap<>();
	private final Map<String, MetaItemWidget> metaItems = new HashMap<>();

	public NeoServerScreenGrid(int columnWidth, int rowHeight, int padding, int columns, int rows) {
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

	public NeoServerScreenGrid addEditBoxWidget(int column, int row, int colspan, int rowspan, @NotNull String name, @NotNull Component placeholder, int maxLength) {
		return addEditBoxWidget(column, row, colspan, rowspan, name, placeholder, maxLength, EditBoxType.TEXT);
	}

	public NeoServerScreenGrid addEditBoxWidget(int column, int row, int colspan, int rowspan, @NotNull String name, @NotNull Component placeholder, int maxLength, EditBoxType type) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.EDIT_BOX)) return this;

		MetaEditBoxWidget editBox = new MetaEditBoxWidget(name, placeholder, maxLength, type);
		this.metaEditBoxes.put(name, editBox);
		return this;
	}

	public NeoServerScreenGrid addMultiLineEditBox(int column, int row, int colspan, int rowspan, @NotNull String name, @NotNull Component placeholder, int maxLength) {
		return this.addMultiLineEditBox(column, row, colspan, rowspan, name, placeholder, maxLength, EditBoxType.TEXT);
	}

	public NeoServerScreenGrid addMultiLineEditBox(int column, int row, int colspan, int rowspan, @NotNull String name, @NotNull Component placeholder, int maxLength, EditBoxType type) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.ML_EDIT_BOX)) return this;

		MetaMultiLineEditBoxWidget editBox = new MetaMultiLineEditBoxWidget(name, placeholder, maxLength, type);
		this.metaMLEditBoxes.put(name, editBox);
		return this;
	}

	public NeoServerScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, Component label) {
		return this.addStringWidget(column, row, colspan, rowspan, name, List.of(label), NeoStringAlign.Horizontal.CENTER, NeoStringAlign.Vertical.MIDDLE, 10);
	}

	public NeoServerScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, List<Component> label) {
		return this.addStringWidget(column, row, colspan, rowspan, name, label, NeoStringAlign.Horizontal.CENTER, NeoStringAlign.Vertical.MIDDLE, 10);
	}

	public NeoServerScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, Component label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign) {
		return this.addStringWidget(column, row, colspan, rowspan, name, List.of(label), horizontalAlign, verticalAlign, 10);
	}

	public NeoServerScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, List<Component> label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign) {
		return this.addStringWidget(column, row, colspan, rowspan, name, label, horizontalAlign, verticalAlign, 10);
	}

	public NeoServerScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, Component label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign, int lineHeight) {
		return this.addStringWidget(column, row, colspan, rowspan, name, List.of(label), horizontalAlign, verticalAlign, lineHeight);
	}

	public NeoServerScreenGrid addStringWidget(int column, int row, int colspan, int rowspan, String name, List<Component> label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign, int lineHeight) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.STRING)) return this;

		MetaStringWidget stringWidget = new MetaStringWidget(name, label, horizontalAlign, verticalAlign, lineHeight);
		this.metaStrings.put(name, stringWidget);
		return this;
	}

	public NeoServerScreenGrid addButtonWidget(int column, int row, int colspan, int rowspan, String name, Component label, Component tooltip, boolean disabled, OnPress onPressCallback) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.BUTTON)) return this;

		MetaButtonWidget buttonWidget = new MetaButtonWidget(name, label, tooltip, disabled);
		this.metaButtons.put(name, buttonWidget);
		this.metaButtonCallbacks.put(name, onPressCallback);
		return this;
	}

	public NeoServerScreenGrid addItemWidget(int column, int row, int colspan, int rowspan, String name, Item item) {
		if (this.nameExists(name)) return this;
		if (!setOccupied(column, row, colspan, rowspan, name, ScreenElementType.ITEM)) return this;

		MetaItemWidget itemWidget = new MetaItemWidget(name, item);
		this.metaItems.put(name, itemWidget);
		return this;
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

	public Map<String, ScreenElementType> getScreenElementTypes() {
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

	public OnPress getCallback(String name) {
		return this.metaButtonCallbacks.get(name);
	}

	public interface OnPress {
		void onPress(NeoServerScreen screen, Map<String, String> values);
	}
}
