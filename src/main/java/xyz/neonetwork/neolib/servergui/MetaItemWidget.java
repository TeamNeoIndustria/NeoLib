package xyz.neonetwork.neolib.servergui;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.Serializable;

public class MetaItemWidget implements Serializable {
	private final String name;
	private final String item;

	public MetaItemWidget(String name, Item item) {
		this.name = name;
		this.item = item.toString();
	}

	public String getName() {
		return this.name;
	}

	public Item getItem() {
		ResourceLocation r = ResourceLocation.bySeparator(this.item, ':');
		return BuiltInRegistries.ITEM.get(r);
	}
}
