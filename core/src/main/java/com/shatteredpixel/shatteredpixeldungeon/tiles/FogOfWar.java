/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.badlogic.gdx.graphics.Pixmap;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.utils.Rect;

/** SPS-PD 0.9.8's four-cell-corner fog mask, adapted to libGDX pixmaps. */
public class FogOfWar extends Image {

	static final int VISIBLE = 0x00000000;
	static final int VISITED = 0xCC111111;
	static final int MAPPED = 0xCC442211;
	static final int INVISIBLE = 0xFF000000;

	private final int mapWidth;
	private final int pWidth;
	private final int pHeight;
	private final int width2;
	private final int height2;
	private final String textureKey;

	private volatile boolean dirty = true;

	public FogOfWar(int mapWidth, int mapHeight) {
		super();

		this.mapWidth = mapWidth;
		pWidth = mapWidth + 1;
		pHeight = mapHeight + 1;

		int textureWidth = 1;
		while (textureWidth < pWidth) textureWidth <<= 1;
		width2 = textureWidth;

		int textureHeight = 1;
		while (textureHeight < pHeight) textureHeight <<= 1;
		height2 = textureHeight;

		float size = DungeonTilemap.SIZE;
		width = width2 * size;
		height = height2 * size;

		textureKey = "SpsFogOfWar" + width2 + "x" + height2;
		texture(TextureCache.create(textureKey, width2, height2));
		Pixmap fog = texture.bitmap;
		fog.setBlending(Pixmap.Blending.None);
		fog.setColor(toRgba(INVISIBLE));
		fog.fill();
		texture.bitmap(fog);
		texture.bind();

		scale.set(size, size);
		x = y = -size / 2f;
	}

	public synchronized void updateFog() {
		dirty = true;
	}

	public synchronized void updateFog(Rect update) {
		dirty = true;
	}

	public synchronized void updateFog(int cell, int radius) {
		dirty = true;
	}

	public synchronized void updateFogArea(int x, int y, int w, int h) {
		dirty = true;
	}

	private synchronized boolean takeDirty() {
		if (!dirty) return false;
		dirty = false;
		return true;
	}

	private void updateTexture(boolean[] visible, boolean[] visited, boolean[] mapped) {
		Pixmap fog = texture.bitmap;
		fog.setBlending(Pixmap.Blending.None);

		for (int y = 1; y < pHeight - 1; y++) {
			for (int x = 1; x < pWidth - 1; x++) {
				fog.drawPixel(x, y, toRgba(legacyFogColor(
						visible, visited, mapped, mapWidth, x, y)));
			}
		}

		texture.bitmap(fog);
	}

	static int legacyFogColor(boolean[] visible, boolean[] visited, boolean[] mapped,
			int mapWidth, int x, int y) {
		int pos = mapWidth * y + x;
		if (allFour(visible, pos, mapWidth)) {
			return VISIBLE;
		} else if (allFour(visited, pos, mapWidth)) {
			return VISITED;
		} else if (allFour(mapped, pos, mapWidth)) {
			return MAPPED;
		} else {
			return INVISIBLE;
		}
	}

	private static boolean allFour(boolean[] cells, int pos, int mapWidth) {
		return cells[pos] && cells[pos - mapWidth]
				&& cells[pos - 1] && cells[pos - mapWidth - 1];
	}

	private static int toRgba(int argb) {
		return (argb << 8) | (argb >>> 24);
	}

	@Override
	protected NoosaScript script() {
		return NoosaScriptNoLighting.get();
	}

	@Override
	public void draw() {
		if (takeDirty()) {
			updateTexture(Dungeon.level.heroFOV, Dungeon.level.visited, Dungeon.level.mapped);
		}
		super.draw();
	}

	@Override
	public void destroy() {
		super.destroy();
		if (texture != null) {
			TextureCache.remove(textureKey);
		}
	}
}
