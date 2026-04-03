package xyz.neonetwork.neolib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xyz.neonetwork.neolib.NeoLib;
import xyz.neonetwork.neolib.textures.NeoTexture;
import xyz.neonetwork.neolib.utilities.NeoComponent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NeoScreen extends Screen {

	private static void drawNineSlice(GuiGraphics gui, ResourceLocation texture, int segmentRes, int startX, int startY, int width, int height) {
		int tileXQuantity = (int) Math.ceil((double) width / segmentRes);
		int tileYQuantity = (int) Math.ceil((double) height / segmentRes);

		if (tileXQuantity < 2 || tileYQuantity < 2) return;

		int stopX = startX + width - segmentRes;
		int stopY = startY + height - segmentRes;

		int textureResolution = segmentRes * 3;

		int[] topleftUVOffset = new int[]{ 0, 0 }; // x, y
		int[] topUVOffset = new int[]{ segmentRes, 0 };
		int[] toprightUVOffset = new int[]{ segmentRes * 2, 0 };
		int[] leftUVOffset = new int[]{ 0, segmentRes };
		int[] middleUVOffset = new int[]{ segmentRes, segmentRes };
		int[] rightUVOffset = new int[]{ segmentRes * 2, segmentRes };
		int[] bottomleftUVOffset = new int[]{ 0, segmentRes * 2 };
		int[] bottomUVOffset = new int[]{ segmentRes, segmentRes * 2 };
		int[] bottomrightUVOffset = new int[]{ segmentRes * 2, segmentRes * 2 };

		gui.blit(texture, startX, startY, topleftUVOffset[0], topleftUVOffset[1], segmentRes, segmentRes, textureResolution, textureResolution);
		for (int tileIndexY = 1; tileIndexY < tileYQuantity - 1; tileIndexY++) {
			int offsetY = tileIndexY * segmentRes;
			gui.blit(texture, startX, startY + offsetY, leftUVOffset[0], leftUVOffset[1], segmentRes, segmentRes, textureResolution, textureResolution);
		}
		gui.blit(texture, startX, stopY, bottomleftUVOffset[0], bottomleftUVOffset[1], segmentRes, segmentRes, textureResolution, textureResolution);
		for (int tileIndexX = 1; tileIndexX < tileXQuantity - 1; tileIndexX++) {
			int offsetX = tileIndexX * segmentRes;
			gui.blit(texture, startX + offsetX, startY, topUVOffset[0], topUVOffset[1], segmentRes, segmentRes, textureResolution, textureResolution);
			for (int tileIndexY = 1; tileIndexY < tileYQuantity - 1; tileIndexY++) {
				int offsetY = tileIndexY * segmentRes;
				gui.blit(texture, startX + offsetX, startY + offsetY, middleUVOffset[0], middleUVOffset[1], segmentRes, segmentRes, textureResolution, textureResolution);
			}
			gui.blit(texture, startX + offsetX, stopY, bottomUVOffset[0], bottomUVOffset[1], segmentRes, segmentRes, textureResolution, textureResolution);
		}
		gui.blit(texture, stopX, startY, toprightUVOffset[0], toprightUVOffset[1], segmentRes, segmentRes, textureResolution, textureResolution);
		for (int tileIndexY = 1; tileIndexY < tileYQuantity - 1; tileIndexY++) {
			int offsetY = tileIndexY * segmentRes;
			gui.blit(texture, stopX, startY + offsetY, rightUVOffset[0], rightUVOffset[1], segmentRes, segmentRes, textureResolution, textureResolution);
		}
		gui.blit(texture, stopX, stopY, bottomrightUVOffset[0], bottomrightUVOffset[1], segmentRes, segmentRes, textureResolution, textureResolution);
	}

	private static void drawTitle(GuiGraphics gui, ResourceLocation texture, int segmentRes, int xPadding, int yPadding, Font font, Component title, int startX, int startY, int width) {
		int titleWidth = width - 48;
		if (title == null) title = Component.literal("");
		FormattedCharSequence formattedcharsequence = NeoComponent.autoTruncateText(font, title, titleWidth);
		int tileXQuantity = (int) Math.ceil((double) font.width(formattedcharsequence) / 2) + 2; // +2?

		int tileOffsetX = startX + ((width / 2) - tileXQuantity);
		int tileOffsetY = startY - yPadding;

		gui.blit(texture, tileOffsetX - xPadding, tileOffsetY, 0, 0, xPadding, segmentRes, segmentRes, segmentRes);
		for (int tileIndexX = 0; tileIndexX < tileXQuantity; tileIndexX++) {
			gui.blit(texture, tileOffsetX + (tileIndexX * 2), tileOffsetY, xPadding, 0, 2, segmentRes, segmentRes, segmentRes);
		}
		gui.blit(texture, tileOffsetX + (tileXQuantity * 2), tileOffsetY, 16-xPadding, 0, xPadding, segmentRes, segmentRes, segmentRes);

		gui.drawCenteredString(font, formattedcharsequence, startX + (width / 2), startY, 0xFFFFFF);
	}

	private static NeoScreen neoScreen;

	private final NeoTexture texture;
	private final Component title;
	private final NeoScreenGrid grid;
	private final OnClose onCloseCallback;
	private final int imageWidth;
	private final int imageHeight;

	private int offsetX;
	private int offsetY;

	private final UUID screenID;

	public NeoScreen(UUID screenID, Component title, NeoTexture texture, NeoScreenGrid grid, OnClose onCloseCallback) {
		super(title);
		this.texture = texture;
		this.title = title;
		this.grid = grid;
		this.onCloseCallback = onCloseCallback;
		this.screenID = screenID;
		ScreenGridCoordinate boundingBox = grid.getBoundingBox(0, 0);
		this.imageWidth = boundingBox.width + 32;
		this.imageHeight = boundingBox.height + 34;
		neoScreen = this;
	}

	private final Map<String, EditBox> editBoxWidgets = new HashMap<>();
	private final Map<String, EditBoxType> editBoxTypes = new HashMap<>();
	private final Map<String, MultiLineEditBox> mlEditBoxWidgets = new HashMap<>();
	private final List<NeoStringWidget> stringWidgets = new ArrayList<>();
	private final Map<String, Button> buttonWidgets = new HashMap<>();
	private final List<NeoItemWidget> itemWidgets = new ArrayList<>();

	@Override
	public void init() {
		super.init();
		editBoxWidgets.clear();
		mlEditBoxWidgets.clear();
		stringWidgets.clear();
		buttonWidgets.clear();
		itemWidgets.clear();
		clearWidgets();

		this.offsetX = (width - imageWidth) / 2;
		this.offsetY = (height - imageHeight) / 2;
		for (String name : this.grid.elementNames()) {
			ScreenElementType elementType = grid.getElementType(name);
			if (elementType == null) continue;
			switch (elementType) {
				case ScreenElementType.EDIT_BOX:
					EditBox editBox = grid.getEditBoxWidget(name, offsetX + 16, offsetY + 16);
					EditBoxType editBoxType = grid.getEditBoxType(name);
					if (editBox == null) break;
					if (editBoxType == null) editBoxType = EditBoxType.TEXT;
					editBoxWidgets.put(name, editBox);
					editBoxTypes.put(name, editBoxType);
					addWidget(editBox);
					break;
				case ScreenElementType.ML_EDIT_BOX:
					MultiLineEditBox mlEditBox = grid.getMLEditBoxWidget(name, offsetX + 16, offsetY + 16);
					EditBoxType mlEditBoxType = grid.getMLEditBoxType(name);
					if (mlEditBox == null) break;
					if (mlEditBoxType == null) mlEditBoxType = EditBoxType.TEXT;
					mlEditBoxWidgets.put(name, mlEditBox);
					editBoxTypes.put(name, mlEditBoxType);
					addWidget(mlEditBox);
					break;
				case ScreenElementType.STRING:
					NeoStringWidget stringWidget = grid.getStringWidget(name, offsetX + 16, offsetY + 16);
					if (stringWidget == null) break;
					stringWidgets.add(stringWidget);
					break;
				case ScreenElementType.BUTTON:
					Button button = grid.getButtonWidget(name, offsetX + 16, offsetY + 16);
					if (button == null) break;
					buttonWidgets.put(name, button);
					addWidget(button);
					break;
				case ScreenElementType.ITEM:
					NeoItemWidget itemWidget = grid.getItemWidget(name, offsetX + 16, offsetY + 16);
					if (itemWidget == null) break;
					itemWidgets.add(itemWidget);
					break;
			}
		}
	}

	@Override
	public void render(@NotNull GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(gui, mouseX, mouseY, partialTick);
		if (minecraft == null || this != minecraft.screen) return;

		drawNineSlice(gui, texture.BACKGROUND_MAIN_BODY, 16, offsetX, offsetY, imageWidth, imageHeight);
		drawTitle(gui, texture.BACKGROUND_TITLE_BOX, 16, 4, 4, this.font, title, offsetX, offsetY, imageWidth);

		for (Map.Entry<String, EditBox> entry : editBoxWidgets.entrySet()) {
			if (entry.getValue() == null) continue;
			EditBox editBox = entry.getValue();
			editBox.render(gui, mouseX, mouseY, partialTick);
			if (editBox.getValue().isBlank() && !editBox.isFocused()) {
				gui.drawString(font, NeoComponent.autoTruncateText(font, editBox.getMessage(), width - 8), editBox.getX() + 4, editBox.getY() + 4, 0xFF4A2D31, false);
			}
		}
		for (Map.Entry<String, MultiLineEditBox> entry : mlEditBoxWidgets.entrySet()) {
			if (entry.getValue() == null) continue;
			MultiLineEditBox editBox = entry.getValue();
			editBox.render(gui, mouseX, mouseY, partialTick);
			if (editBox.getValue().isBlank() && !editBox.isFocused()) {
				gui.drawString(font, NeoComponent.autoTruncateText(font, editBox.getMessage(), width - 8), editBox.getX() + 4, editBox.getY() + 4, 0xFF4A2D31, false);
			}
		}
		for (NeoStringWidget stringWidget : stringWidgets) {
			if (stringWidget == null) continue;
			stringWidget.render(gui, mouseX, mouseY, partialTick);
		}
		for (Map.Entry<String, Button> button : buttonWidgets.entrySet()) {
			if (button.getValue() == null) continue;
			button.getValue().render(gui, mouseX, mouseY, partialTick);
		}
		for (NeoItemWidget itemWidget : itemWidgets) {
			if (itemWidget == null) continue;
			itemWidget.render(gui, mouseX, mouseY, partialTick);
		}
	}

	public void show(boolean preventScreenClose) {
		if (!preventScreenClose && Minecraft.getInstance().screen != null) {
			Minecraft.getInstance().screen.onClose();
			Minecraft.getInstance().setScreen(null);
		}
		Minecraft.getInstance().setScreen(this);
	}

	private static final Pattern allowedASCIIRegex = Pattern.compile("^([ -~]*)$");
	private static final Pattern allowedIntRegex = Pattern.compile("^(-?\\d*)$");
	private static final Pattern allowedUintRegex = Pattern.compile("^(\\d*)$");

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		for (Map.Entry<String, EditBox> entry : this.editBoxWidgets.entrySet()) {
			EditBox editBox = entry.getValue();
			if (editBox == null || !editBox.isFocused()) continue;
			EditBoxType type = this.editBoxTypes.get(entry.getKey());
			if (type == null) continue;
			Pattern typePattern = switch (type) {
				case TEXT -> null;
				case ASCII -> allowedASCIIRegex;
				case INT -> allowedIntRegex;
				case UINT -> allowedUintRegex;
			};
			switch (type) {
				case ASCII:
				case INT:
				case UINT:
					String previousState = editBox.getValue();
					int previousCursorPos = editBox.getCursorPosition();
					Matcher matcher = typePattern.matcher(previousState);
					if (!matcher.find()) previousState = "";
					boolean charInserted = editBox.charTyped(codePoint, modifiers);
					if (!charInserted) return false;
					if (typePattern.matcher(editBox.getValue()).find()) {
						return true;
					} else {
						editBox.setValue(previousState);
						editBox.setCursorPosition(previousCursorPos);
						editBox.setHighlightPos(editBox.getCursorPosition());
						return false;
					}
				default: // Default + Text
					return editBox.charTyped(codePoint, modifiers);
			}
		}
		for (Map.Entry<String, MultiLineEditBox> entry : this.mlEditBoxWidgets.entrySet()) {
			MultiLineEditBox editBox = entry.getValue();
			if (editBox == null || !editBox.isFocused()) continue;
			EditBoxType type = this.editBoxTypes.get(entry.getKey());
			if (type == null) continue;
			Pattern typePattern = switch (type) {
				case TEXT -> null;
				case ASCII -> allowedASCIIRegex;
				case INT -> allowedIntRegex;
				case UINT -> allowedUintRegex;
			};
			switch (type) {
				case ASCII:
				case INT:
				case UINT:
					String previousState = editBox.getValue();
					Matcher matcher = typePattern.matcher(previousState);
					if (!matcher.find()) previousState = "";
					boolean charInserted = editBox.charTyped(codePoint, modifiers);
					if (!charInserted) return false;
					if (typePattern.matcher(editBox.getValue()).find()) {
						return true;
					} else {
						editBox.setValue(previousState);
						return false;
					}
				default: // Default + Text
					return editBox.charTyped(codePoint, modifiers);
			}
		}
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (keyCode == GLFW.GLFW_KEY_ENTER && hasShiftDown()) {
			Button submitButton = this.buttonWidgets.get("submit");
			if (submitButton == null) return true;
			submitButton.onPress();
			return false;
		}

		for (Map.Entry<String, EditBox> entry : this.editBoxWidgets.entrySet()) {
			if (entry.getValue() == null) continue;
			EditBox editBox = entry.getValue();
			if (!editBox.isFocused()) continue;

			if (this.editBoxTypes.get(entry.getKey()) == EditBoxType.INT
				|| this.editBoxTypes.get(entry.getKey()) == EditBoxType.UINT
				|| this.editBoxTypes.get(entry.getKey()) == EditBoxType.ASCII) {
				if (isPaste(keyCode)) {
					return false;
				}
			}

			if (!editBox.keyPressed(keyCode, scanCode, modifiers)) {
				return editBox.isFocused() && keyCode != 256 || super.keyPressed(keyCode, scanCode, modifiers);
			}
			return false;
		}
		for (Map.Entry<String, MultiLineEditBox> entry : this.mlEditBoxWidgets.entrySet()) {
			if (entry.getValue() == null) continue;
			MultiLineEditBox editBox = entry.getValue();
			if (!editBox.isFocused()) continue;

			if (this.editBoxTypes.get(entry.getKey()) == EditBoxType.INT
				|| this.editBoxTypes.get(entry.getKey()) == EditBoxType.UINT
				|| this.editBoxTypes.get(entry.getKey()) == EditBoxType.ASCII) {
				if (isPaste(keyCode)) {
					return false;
				}
			}

			if (!editBox.keyPressed(keyCode, scanCode, modifiers)) {
				return editBox.isFocused() && keyCode != 256 || super.keyPressed(keyCode, scanCode, modifiers);
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
//		return false;
	}

//	@Override
//	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
//		return super.keyReleased(keyCode, scanCode, modifiers);
//	}

	public static void processIncomingPacket(@NotNull ScreenEventData screenEventData) {
		switch (screenEventData.getType()) {
			case BUTTON:
				break;
			case CLOSE:
				close();
				break;
			default:
				break;
		}
	}

	public static void close() {
		if (Minecraft.getInstance().screen != null) {
			Minecraft.getInstance().screen.onClose();
			Minecraft.getInstance().setScreen(null);
		}
	}

	@Override
	public void onClose() {
		this.onCloseCallback.onClose(grid);
		neoScreen = null;
		NeoLib.LOGGER.info("Screen closed");
		super.onClose();
	}

	public interface OnClose {
		void onClose(NeoScreenGrid grid);
	}
}
