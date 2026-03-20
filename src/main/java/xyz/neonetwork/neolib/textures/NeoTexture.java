package xyz.neonetwork.neolib.textures;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xyz.neonetwork.neolib.NeoLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NeoTexture {
	public final String textureName;
	public final ResourceLocation BACKGROUND_MAIN_BODY;
	public final ResourceLocation BACKGROUND_TITLE_BOX;
	public final ResourceLocation BACKGROUND_TOAST;
	private static HashMap<String, NeoTexture> textures = new HashMap<>();

	public NeoTexture(@NotNull String textureName, @NotNull ResourceLocation background, @NotNull ResourceLocation title, @NotNull ResourceLocation toast) {
		this.textureName = textureName;
		this.BACKGROUND_MAIN_BODY = background;
		this.BACKGROUND_TITLE_BOX = title;
		this.BACKGROUND_TOAST = toast;
		textures.put(this.textureName, this);
	}

	public static final NeoTexture GENERIC = new NeoTexture(
		"generic",
		ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "textures/gui/generic/background.png"),
		ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "textures/gui/generic/titleplate.png"),
		ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "textures/gui/generic/toast.png")
	);

	public static final NeoTexture BANK = new NeoTexture(
		"bank",
		ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "textures/gui/bank/background.png"),
		ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "textures/gui/bank/titleplate.png"),
		ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "textures/gui/bank/toast.png")
	);

	public static final NeoTexture RESEARCH = new NeoTexture(
		"research",
		ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "textures/gui/research/background.png"),
		ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "textures/gui/research/titleplate.png"),
		ResourceLocation.fromNamespaceAndPath(NeoLib.MODID, "textures/gui/research/toast.png")
	);

	public static NeoTexture getTexture(String textureName) {
		return textures.get(textureName);
	}
}
