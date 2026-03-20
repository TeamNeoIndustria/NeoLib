package xyz.neonetwork.neolib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xyz.neonetwork.neolib.toast.NeoToast;
import xyz.neonetwork.neolib.toast.NeoToastData;
import xyz.neonetwork.neolib.toast.NeoToastPacket;

public class ClientPayloadHandler {
	public static void handleDataOnNetwork(final NeoToastPacket toastPacket, final IPayloadContext context) {
		NeoToastData toastData = toastPacket.toastData();
		if (toastData == null) return;
		Toast toast = new NeoToast(toastData.getTitle(), toastData.getMessage(), toastData.getTexture(), toastData.getDuration());
		Minecraft.getInstance().getToasts().addToast(toast);
	}
}
