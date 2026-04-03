package xyz.neonetwork.neolib.servergui;

import net.minecraft.network.chat.Component;
import xyz.neonetwork.neolib.utilities.NeoComponent;

import java.io.Serializable;

public class MetaButtonWidget implements Serializable {
	private final String name;
	private final String label;
	private final String tooltip;
	private final boolean disabled;

	public MetaButtonWidget(String name, Component label, Component tooltip, boolean disabled) {
		this.name = name;
		this.label = NeoComponent.toJson(label);
		this.tooltip = NeoComponent.toJson(tooltip);
		this.disabled = disabled;
	}

	public String getName() {
		return this.name;
	}

	public Component getLabel() {
		return NeoComponent.fromJson(this.label);
	}

	public Component getTooltip() {
		return NeoComponent.fromJson(this.tooltip);
	}

	public boolean isDisabled() {
		return this.disabled;
	}
}
