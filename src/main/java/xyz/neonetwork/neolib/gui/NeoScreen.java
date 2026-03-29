package xyz.neonetwork.neolib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.NeoLib;
import xyz.neonetwork.neolib.servergui.NeoServerScreen;
import xyz.neonetwork.neolib.servergui.NeoServerScreenGrid;
import xyz.neonetwork.neolib.textures.NeoTexture;
import xyz.neonetwork.neolib.utilities.NeoComponent;

import java.util.*;

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
	private final List<StringWidget> stringWidgets = new ArrayList<>();
	private final List<Button> buttonWidgets = new ArrayList<>();
	private final List<ItemWidget> itemWidgets = new ArrayList<>();

	@Override
	public void init() {
		super.init();
		editBoxWidgets.clear();
		stringWidgets.clear();
		buttonWidgets.clear();
		itemWidgets.clear();
		clearWidgets();

		this.offsetX = (width - imageWidth) / 2;
		this.offsetY = (height - imageHeight) / 2;
		NeoLib.LOGGER.info("OffsetX: {}, OffsetY: {}", this.offsetX, this.offsetY);
		for (String name : this.grid.elementNames()) {
			NeoLib.LOGGER.info("Looping grid element: {}", name);
			ScreenElementType elementType = grid.getElementType(name);
			if (elementType == null) continue;
			switch (elementType) {
				case ScreenElementType.EDIT_BOX:
					EditBox editBox = grid.getEditBoxWidget(name, offsetX + 16, offsetY + 16);
					if (editBox == null) break;
					editBoxWidgets.put(name, editBox);
					addWidget(editBox);
					break;
				case ScreenElementType.STRING:
					StringWidget stringWidget = grid.getStringWidget(name, offsetX + 16, offsetY + 16);
					if (stringWidget == null) break;
					stringWidgets.add(stringWidget);
					addWidget(stringWidget);
					break;
				case ScreenElementType.BUTTON:
					Button button = grid.getButtonWidget(name, offsetX + 16, offsetY + 16);
					if (button == null) break;
					buttonWidgets.add(button);
					addWidget(button);
					break;
				case ScreenElementType.ITEM:
					ItemWidget itemWidget = grid.getItemWidget(name, offsetX + 16, offsetY + 16);
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

		for (EditBox editBox : editBoxWidgets.values()) {
			if (editBox == null) continue;
			editBox.render(gui, mouseX, mouseY, partialTick);
			if (editBox.getValue().isBlank() && !editBox.isFocused()) {
				gui.drawString(font, editBox.getMessage(), editBox.getX() + 4, editBox.getY() + 4, 0xff4A2D31, false);
			}
		}
		for (StringWidget stringWidget : stringWidgets) {
			if (stringWidget == null) continue;
			stringWidget.render(gui, mouseX, mouseY, partialTick);
		}
		for (Button button : buttonWidgets) {
			if (button == null) continue;
			button.render(gui, mouseX, mouseY, partialTick);
		}
		for (ItemWidget itemWidget : itemWidgets) {
			if (itemWidget == null) continue;
			itemWidget.render(gui, mouseX, mouseY, partialTick);
		}
	}

	public void show() {
		if (Minecraft.getInstance().screen != null) {
			Minecraft.getInstance().screen.onClose();
			Minecraft.getInstance().setScreen(null);
		}
		Minecraft.getInstance().setScreen(this);
	}

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
