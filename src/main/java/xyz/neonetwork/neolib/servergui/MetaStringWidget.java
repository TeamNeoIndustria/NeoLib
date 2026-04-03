package xyz.neonetwork.neolib.servergui;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.gui.NeoStringAlign;
import xyz.neonetwork.neolib.utilities.NeoComponent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetaStringWidget implements Serializable {
	private final String name;
	private final List<String> label;
	private final int lineHeight;
	private final NeoStringAlign.Horizontal horizontalAlign;
	private final NeoStringAlign.Vertical verticalAlign;

	public MetaStringWidget(String name, @NotNull List<@NotNull Component> label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign, int lineHeight) {
		this.name = name;
		this.label = new ArrayList<>();
		for (Component currentLabel : label) {
			this.label.add(NeoComponent.toJson(currentLabel));
		}
		this.horizontalAlign = horizontalAlign;
		this.verticalAlign = verticalAlign;
		this.lineHeight = Math.max(lineHeight, 9);
	}
	public MetaStringWidget(String name, @NotNull Component label, NeoStringAlign.Horizontal horizontalAlign, NeoStringAlign.Vertical verticalAlign, int lineHeight) {
		this.name = name;
		this.label = List.of(NeoComponent.toJson(label));
		this.horizontalAlign = horizontalAlign;
		this.verticalAlign = verticalAlign;
		this.lineHeight = Math.max(lineHeight, 9);
	}

	public String getName() {
		return this.name;
	}

	public List<Component> getLabel() {
		List<Component> labels = new ArrayList<>();
		for (String currentLabel : this.label) {
			labels.add(NeoComponent.fromJson(currentLabel));
		}
		return labels;
	}

	public int getLineHeight() {
		return this.lineHeight;
	}

	public NeoStringAlign.Horizontal getHorizontalAlign() {
		return this.horizontalAlign;
	}

	public NeoStringAlign.Vertical getVerticalAlign() {
		return this.verticalAlign;
	}
}
