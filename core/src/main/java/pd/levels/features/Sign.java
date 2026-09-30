/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package pd.levels.features;

import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.effects.CellEmitter;
import pd.effects.particles.ElmoParticle;
import pd.levels.ChaosLevel;
import pd.levels.DeadEndLevel;
import pd.levels.Level;
import pd.levels.NewRoomLevel;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.tiles.CustomTilemap;
import pd.tiles.custom.SpsFeatureVisual;
import pd.utils.GLog;
import pd.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.Iterator;

/** Handles the readable, flammable signs used by SPS fixed maps. */
public final class Sign {

	private static final int LAST_TIP_DEPTH = 25;

	public static void read(int pos) {
		if (pos == Dungeon.level.pitSign) {
			//SPS: UI 构造必须切回渲染线程（Sign.read 由 Hero 的 actor 流程调用，直接 new 会崩）
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndMessage(Messages.get(Sign.class, "pit_message")));
				}
			});
			return;
		}

		String key = messageKey(Dungeon.level, Dungeon.depth, Statistics.roomType);
		if (key == null) return;
		final String fkey = key;
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show(new WndMessage(Messages.get(Sign.class, fkey)));
			}
		});
		if (key.startsWith("tip_") && Dungeon.depth >= 22) burn(pos);
	}

	static String messageKey(Level level, int depth, int roomType) {
		if (level instanceof DeadEndLevel) return "dead_end";
		if (level instanceof ChaosLevel) return "chaos";
		if (level instanceof NewRoomLevel) {
			return "new_room_" + (roomType >= 0 && roomType <= 4 ? roomType : 0);
		}
		return depth >= 1 && depth <= LAST_TIP_DEPTH ? "tip_" + depth : null;
	}

	private static void burn(int pos) {
		Level.set(pos, Terrain.EMBERS);
		Iterator<CustomTilemap> iterator = Dungeon.level.customTiles.iterator();
		while (iterator.hasNext()) {
			CustomTilemap tile = iterator.next();
			if (tile instanceof SpsFeatureVisual
					&& ((SpsFeatureVisual) tile).feature() == SpsFeatureVisual.SIGN
					&& tile.tileX == pos % Dungeon.level.width()
					&& tile.tileY == pos / Dungeon.level.width()) {
				((SpsFeatureVisual) tile).destroy();
				iterator.remove();
			}
		}
		GameScene.updateMap(pos);
		GLog.w(Messages.get(Sign.class, "burn"));
		CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
	}

	private Sign() {
	}
}
