package xyz.neonetwork.neolib.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.NeoLib;
import xyz.neonetwork.neolib.textures.NeoTexture;
import xyz.neonetwork.neolib.toast.NeoToast;
import xyz.neonetwork.neolib.toast.NeoToastData;
import xyz.neonetwork.neolib.toast.NeoToastPacket;

import java.util.List;

public class NeoNotify {
	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendToast(@NotNull ServerPlayer player, @NotNull Component line1, @NotNull Component line2, @NotNull NeoTexture texture) {
		sendToast(player, line1, line2, texture, 5000);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendToast(@NotNull ServerPlayer player, @NotNull Component line1, @NotNull Component line2, @NotNull NeoTexture texture, int duration) {
		if (duration < 1000) duration = 1000;
		PacketDistributor.sendToPlayer(player, new NeoToastPacket(new NeoToastData(
			line1,
			line2,
			texture,
			duration
		)));
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendToastGlobal(@NotNull Component line1, @NotNull Component line2, @NotNull NeoTexture texture) {
		sendToastGlobal(line1, line2, texture, 5000);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendToastGlobal(@NotNull Component line1, @NotNull Component line2, @NotNull NeoTexture texture, int duration) {
		if (duration < 1000) duration = 1000;
		PacketDistributor.sendToAllPlayers(new NeoToastPacket(new NeoToastData(
			line1,
			line2,
			texture,
			duration
		)));
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendToast(@NotNull Component line1, @NotNull Component line2, @NotNull NeoTexture texture) {
		sendToast(line1, line2, texture, 5000);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendToast(@NotNull Component line1, @NotNull Component line2, @NotNull NeoTexture texture, int duration) {
		if (duration < 1000) duration = 1000;
		Toast toast = new NeoToast(line1, line2, texture, duration);
		Minecraft.getInstance().getToasts().addToast(toast);
	}



	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendChat(ServerPlayer player, String message) {
		sendChat(player, Component.literal(message));
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendChat(ServerPlayer player, Component message) {
		if (player.hasDisconnected()) return;
		player.createCommandSourceStack().sendSuccess(() -> message, false);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendChatGlobal(String message) {
		sendChatGlobal(Component.literal(message));
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendChatGlobal(Component message) {
		if (NeoLib.server.getPlayerCount() == 0) return;

		List<ServerPlayer> players = NeoLib.server.getPlayerList().getPlayers();
		for (ServerPlayer player : players) {
			sendChat(player, message);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendChat(String message) {
		sendChat(Component.literal(message));
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendChat(Component message) {
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
//		player.sendSystemMessage(message);
		Minecraft.getInstance().gui.getChat().addMessage(message);
	}



	// Status bar here?



	private static final int titleFadeIn = 10;
	private static final int titleStay = 70;
	private static final int titleFadeOut = 20;
	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendTitle(@NotNull ServerPlayer player, Component title, Component subtitle) {
		sendTitle(player, title, subtitle, titleFadeIn, titleStay, titleFadeOut);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendTitle(@NotNull ServerPlayer player, Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
		if (fadeIn < 0) fadeIn = 0;
		if (stay < 0) stay = 0;
		if (fadeOut < 0) fadeOut = 0;
		if (title == null && subtitle == null) return;
		ClientboundSetTitlesAnimationPacket animationPacket = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
		if (title != null) {
			ClientboundSetTitleTextPacket titleTextPacket = new ClientboundSetTitleTextPacket(title);
			player.connection.send(titleTextPacket);
		}
		if (subtitle != null) {
			ClientboundSetSubtitleTextPacket subtitleTextPacket = new ClientboundSetSubtitleTextPacket(subtitle);
			player.connection.send(subtitleTextPacket);
		}
		player.connection.send(animationPacket);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendTitleGlobal(Component title, Component subtitle) {
		sendTitleGlobal(title, subtitle, titleFadeIn, titleStay, titleFadeOut);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void sendTitleGlobal(Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
		if (NeoLib.server.getPlayerCount() == 0) return;

		List<ServerPlayer> players = NeoLib.server.getPlayerList().getPlayers();
		for (ServerPlayer player : players) {
			sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendTitle(Component title, Component subtitle) {
		sendTitleGlobal(title, subtitle, titleFadeIn, titleStay, titleFadeOut);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendTitle(Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
		if (fadeIn < 0) fadeIn = 0;
		if (stay < 0) stay = 0;
		if (fadeOut < 0) fadeOut = 0;
		if (title == null && subtitle == null) return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;

		if (title != null) mc.gui.setTitle(title);
		if (subtitle != null) mc.gui.setSubtitle(subtitle);
		mc.gui.setTimes(fadeIn, stay, fadeOut);
	}



	private static final SoundSource soundSource = SoundSource.RECORDS;
	private static final float soundVolume = 10f;
	private static final float soundPitch = 1f;
	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void playSound(ServerPlayer player, SoundEvent sound) {
		playSound(player, sound, soundSource, soundVolume, soundPitch);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void playSound(ServerPlayer player, SoundEvent sound, SoundSource source, float volume, float pitch) {
		player.serverLevel().playLocalSound(player, sound, source, volume, pitch);
//		player.serverLevel().playSound(
//			player,
//			player.getX(),
//			player.getY(),
//			player.getZ(),
//			sound,
//			source,
//			volume,
//			pitch
//		);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void playSoundGlobal(SoundEvent sound) {
		playSoundGlobal(sound, soundSource, soundVolume, soundPitch);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void playSoundGlobal(SoundEvent sound, SoundSource source, float volume, float pitch) {
		if (NeoLib.server.getPlayerCount() == 0) return;

		List<ServerPlayer> players = NeoLib.server.getPlayerList().getPlayers();
		for (ServerPlayer player : players) {
			playSound(player, sound, source, volume, pitch);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void playSound(SoundEvent sound) {
		playSound(sound, soundSource, soundVolume, soundPitch);
	}

	@OnlyIn(Dist.CLIENT)
	public static void playSound(SoundEvent sound, SoundSource source, float volume, float pitch) {
		if (Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) return;
		Minecraft.getInstance().level.playLocalSound(Minecraft.getInstance().player, sound, source, volume, pitch);
	}
}
