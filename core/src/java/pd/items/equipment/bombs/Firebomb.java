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
package pd.items.equipment.bombs;

import pd.atlas.items.EquipmentEquipWeaponBombDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.effects.CellEmitter;
import pd.effects.particles.FlameParticle;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;
import render.utils.data.BArray;
import pd.messages.InlineText;

public class Firebomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Firebomb.class)
			.t("name", "燃烧弹")
			.t("desc", "这枚改造过的炸弹的爆炸范围更大，在2格范围内造成_%1$d~%2$d点伤害_并释放出持续燃烧的烈火。")
			.t("discover_hint", "你可通过炼金合成该物品。");
	}



	{ image = EquipmentEquipWeaponBombDict.FIRE_BOMB_0; }
	@Override protected int explosionRange() { return 2; }
	@Override public void explode(int cell) {
		super.explode(cell);
		PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), explosionRange());
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				GameScene.add(Blob.seed(i, Dungeon.level.pit[i] ? 2 : 10, Fire.class));
				CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
			}
		}
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
	}
	@Override public int value() { return quantity * (20 + 30); }
}
