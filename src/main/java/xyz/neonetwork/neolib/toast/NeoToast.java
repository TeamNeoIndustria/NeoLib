package xyz.neonetwork.neolib.toast;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.textures.NeoTexture;

public class NeoToast implements Toast {
	private static final int WIDTH = 160; // Width of the vanilla toast
	private static final int HEIGHT = 32;
	private static final int X_PADDING = 12;
	private static final int Y_PADDING = 7;

	private final StringWidget titleWidget;
	private final StringWidget messageWidget;
	private final NeoTexture texture;
	private final long showTime;

	public NeoToast(@NotNull Component title, @NotNull Component message, @NotNull NeoTexture texture, long showTime) {
		Font font = Minecraft.getInstance().font;
		this.titleWidget = new StringWidget(X_PADDING, Y_PADDING, WIDTH - (X_PADDING * 2), 8, title, font);
		this.titleWidget.alignLeft();
		this.messageWidget = new StringWidget(X_PADDING, Y_PADDING + 10, WIDTH - (X_PADDING * 2), 8, message, font);
		this.messageWidget.alignLeft();
		this.texture = texture;
		this.showTime = showTime > 1000 ? showTime : 5000;
	}

	@Override
	public @NotNull Visibility render(GuiGraphics gui, @NotNull ToastComponent toastComponent, long delta) {
		gui.blit(texture.BACKGROUND_TOAST, 0, 0, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
		titleWidget.render(gui, 0, 0, delta);
		messageWidget.render(gui, 0, 0, delta);
		if (delta > showTime) return Visibility.HIDE;
		return Visibility.SHOW;
	}
}
