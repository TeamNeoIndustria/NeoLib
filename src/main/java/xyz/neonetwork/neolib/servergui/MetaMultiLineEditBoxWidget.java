package xyz.neonetwork.neolib.servergui;

import net.minecraft.network.chat.Component;
import xyz.neonetwork.neolib.gui.EditBoxType;
import xyz.neonetwork.neolib.utilities.NeoComponent;

import java.io.Serializable;

public class MetaMultiLineEditBoxWidget implements Serializable {
	private final String name;
	private final String placeholder;
	private final int maxLength;
	private final EditBoxType type;

	public MetaMultiLineEditBoxWidget(String name, Component placeholder, int maxLength, EditBoxType type) {
		this.name = name;
		this.placeholder = NeoComponent.toJson(placeholder);
		this.maxLength = maxLength;
		this.type = type;
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

	public EditBoxType getType() {
		return this.type;
	}
}
