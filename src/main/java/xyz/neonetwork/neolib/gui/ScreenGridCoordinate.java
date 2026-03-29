package xyz.neonetwork.neolib.gui;

import java.io.Serializable;

public class ScreenGridCoordinate implements Serializable {
	public final int x;
	public final int y;
	public final int width;
	public final int height;

	public ScreenGridCoordinate(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
