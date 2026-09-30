/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.tiles.custom;

import pd.levels.Terrain;
import pd.tiles.CustomTilemap;
import pd.tiles.SpsTerrainFrames;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

/** Renders a complete SPS fixed layout using its original 16x16 tile atlas. */
public class SpsLegacyLevelVisual extends CustomTilemap {

	private static final String CELLS = "cells";

	private int[] cells;

	public SpsLegacyLevelVisual() {
	}

	public SpsLegacyLevelVisual(String texture, int width, int height, int[] cells) {
		this.texture = texture;
		tileW = width;
		tileH = height;
		this.cells = cells.clone();
	}

	public static SpsLegacyLevelVisual fromTerrainMap(String texture, int width, int height, int[] terrain) {
		int[] visuals = new int[terrain.length];
		for (int i = 0; i < terrain.length; i++) visuals[i] = terrainVisual(terrain[i]);
		return new SpsLegacyLevelVisual(texture, width, height, visuals);
	}

	public static int terrainVisual(int terrain) {
		//SPS: 全局 2D 帧映射（单一权威表 tiles/SpsTerrainFrames，回 2D 渲染共用）
		return SpsTerrainFrames.visual(terrain);
	}

	public void updateTerrainCell(int cell, int terrain) {
		if (cell < 0 || cell >= cells.length) return;
		cells[cell] = terrainVisual(terrain);
		if (vis != null) {
			vis.updateMapCell(cell);
		}
	}

	public int visualAt(int cell) {
		return cells[cell];
	}

	public String texturePath() {
		return (String) texture;
	}

	public void resetTerrainMap(int[] terrain) {
		if (terrain.length != cells.length) return;
		for (int i = 0; i < terrain.length; i++) cells[i] = terrainVisual(terrain[i]);
		if (vis != null) vis.updateMap();
	}

	@Override
	public Tilemap create() {
		Tilemap result = super.create();
		result.map(cells, tileW);
		return result;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CELLS, cells);
		bundle.put("texture", (String) texture);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		cells = bundle.getIntArray(CELLS);
		texture = bundle.getString("texture");
	}
}
