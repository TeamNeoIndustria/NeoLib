package xyz.neonetwork.neolib.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.NeoLib;

import java.util.ArrayList;
import java.util.List;

public class NeoStringWidget {

	private int x;
	private int y;
	private int width;
	private int height;
	private List<Component> label;
	private final Font font;
	private int lineHeight = 10;
	private List<FormattedCharSequence> lines = new ArrayList<>();
	private boolean linesCalculated = false;
	private NeoStringAlign.Horizontal horizontal;
	private NeoStringAlign.Vertical vertical;
	private int visualHeight = 0;

	public NeoStringWidget(int x, int y, int width, int height, @NotNull List<@NotNull Component> label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign, int lineHeight, Font font) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.label = label;
		this.font = font;
		this.horizontal = horizontalAlign;
		this.vertical = verticalAlign;
		this.lineHeight = lineHeight;
	}

	public NeoStringWidget(int x, int y, int width, int height, @NotNull Component label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign, int lineHeight, Font font) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.label = List.of(label);
		this.font = font;
		this.horizontal = horizontalAlign;
		this.vertical = verticalAlign;
		this.lineHeight = lineHeight;
	}

	private void calculateLines() {
		this.lines.clear();
		for (Component currentLabel : this.label) {
			if (currentLabel.getString().isEmpty()) {
				this.lines.add(currentLabel.getVisualOrderText());
				continue;
			}
			this.lines.addAll(this.font.split(currentLabel, this.width));
		}
//		double a = ((double) this.height / this.lineHeight);
//		double b = (double) (this.lineHeight - font.lineHeight) / this.lineHeight;
//		int c = (int) (((a - (int) a) / b) + (int) a);
//		NeoLib.LOGGER.info("A: {}, B: {}, C: {}", a, b, c);

		int c = (this.height / this.lineHeight);
		this.lines = this.lines.subList(0, Math.min(c, this.lines.size()));
		this.linesCalculated = true;
	}

	public void setX(int x) {
		this.x = x;
		this.linesCalculated = false;
	}

	public void setY(int y) {
		this.y = y;
		this.linesCalculated = false;
	}

	public void setWidth(int width) {
		this.width = width;
		this.linesCalculated = false;
	}

	public void setHeight(int height) {
		this.height = height;
		this.linesCalculated = false;
	}

	public void setLineHeight(int lineHeight) {
		if (lineHeight < font.lineHeight) return;
		this.lineHeight = lineHeight;
		this.linesCalculated = false;
	}

	public void setAlign(@NotNull NeoStringAlign.Horizontal horizontal, @NotNull NeoStringAlign.Vertical vertical) {
		this.horizontal = horizontal;
		this.vertical = vertical;
		this.linesCalculated = false;
	}

	public void setLabel(@NotNull List<@NotNull Component> label) {
		this.label = label;
		this.linesCalculated = false;
	}

	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		if (!this.linesCalculated) this.calculateLines();
		int offsetY = switch (this.vertical) {
			case NeoStringAlign.Vertical.MIDDLE -> (this.height - (this.lineHeight * this.lines.size()) + this.lineHeight - font.lineHeight) / 2;
			case NeoStringAlign.Vertical.BOTTOM -> this.height - (this.lineHeight * this.lines.size()) + this.lineHeight - font.lineHeight;
			default -> 0;
		};
		for(FormattedCharSequence line : this.lines) {
			this.font.width(line);
			int offsetX = switch (this.horizontal) {
				case NeoStringAlign.Horizontal.CENTER -> (this.width - this.font.width(line)) / 2;
				case NeoStringAlign.Horizontal.RIGHT -> this.width - this.font.width(line);
				default -> 0;
			};
			gui.drawString(this.font, line, this.x + offsetX, this.y + offsetY, 0xFFFFFF, true);
			offsetY += this.lineHeight;
		}
	}
}
