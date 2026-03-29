package xyz.neonetwork.neolib.servergui;

import net.minecraft.network.chat.Component;
import xyz.neonetwork.neolib.utilities.NeoComponent;

import java.io.Serializable;

public class MetaEditBoxWidget implements Serializable {
	private final String name;
	private final String placeholder;
	private final int maxLength;

	public MetaEditBoxWidget(String name, Component placeholder, int maxLength) {
		this.name = name;
		this.placeholder = NeoComponent.toJson(placeholder);
		this.maxLength = maxLength;
	}

	public String getName() {
		return this.name;
	}

	public Component getPlaceholder() {
		return NeoComponent.fromJson(this.placeholder);
	}

	public int getMaxLength() {
		return this.maxLength;
	}
}
