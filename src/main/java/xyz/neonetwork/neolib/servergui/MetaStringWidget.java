package xyz.neonetwork.neolib.servergui;

import net.minecraft.network.chat.Component;
import xyz.neonetwork.neolib.utilities.NeoComponent;

import java.io.Serializable;

public class MetaStringWidget implements Serializable {
	private final String name;
	private final String label;

	public MetaStringWidget(String name, Component label) {
		this.name = name;
		this.label = NeoComponent.toJson(label);
	}

	public String getName() {
		return this.name;
	}

	public Component getLabel() {
		return NeoComponent.fromJson(this.label);
	}
}
