package xyz.neonetwork.neolib.toast;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.textures.NeoTexture;

public class NeoToast implements Toast {
	private static final int WIDTH = 160; // Width of the vanilla toast
	private static final int HEIGHT = 32;

	private final Component title;
	private final Component message;
	private final NeoTexture texture;
	private final long showTime;

	public NeoToast(@NotNull Component title, @NotNull Component message, @NotNull NeoTexture texture, long showTime) {
		this.title = title;
		this.message = message;
		this.texture = texture;
		this.showTime = showTime > 1000 ? showTime : 5000;
	}

	@Override
	public @NotNull Visibility render(GuiGraphics gui, @NotNull ToastComponent toastComponent, long delta) {
		gui.blit(texture.BACKGROUND_TOAST, 0, 0, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
		Font font = Minecraft.getInstance().font;
		gui.drawString(font, title,  12, 7, 0xFFFFFF);
		gui.drawString(font, message,  12, 17, 0xFFFFFF);
		if (delta > showTime) return Visibility.HIDE;
		return Visibility.SHOW;
	}
}
