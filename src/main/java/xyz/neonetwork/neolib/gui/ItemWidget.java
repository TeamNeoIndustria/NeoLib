package xyz.neonetwork.neolib.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.NeoLib;

public class ItemWidget {
	private final ItemStack itemStack;
	float x;
	float y;
	float scale;

	public ItemWidget(@NotNull ItemStack itemStack, float centerX, float centerY, float scale) {
		this.itemStack = itemStack;
		this.x = centerX;
		this.y = centerY;
		this.scale = scale;
	}

	public void setX(float x) {
		this.x = x;
	}
	public void setY(float y) {
		this.y = y;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}

	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		gui.pose().pushPose();
		gui.pose().translate(this.x, this.y, -10000f);
		gui.pose().scale(scale, scale, scale);
		gui.renderItem(this.itemStack, -8, -8);
		gui.pose().popPose();
	}
}
