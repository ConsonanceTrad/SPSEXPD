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

package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.Item;
import pd.items.KindOfWeapon;
import pd.items.weapon.melee.normalweapon.FightGloves;
import pd.items.weapon.melee.normalweapon.Knuckles;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.PathFinder;

public class DisarmingTrap extends Trap {

	{
		color = ORANGE;
		shape = LARGE_DOT;
	}

	@Override
	public void activate() {
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) {
			int cell = Dungeon.level.randomRespawnCell(null);
			if (cell != -1) {
				Item item = heap.pickUp();
				if (item != null) {
					Dungeon.level.drop(item, cell).seen = true;
					revealAround(cell);
					showTeleportEffect();
				}
			}
		}

		if (Dungeon.hero != null && Dungeon.hero.pos == pos) {
			Hero hero = Dungeon.hero;
			KindOfWeapon weapon = hero.belongings.weapon;
			if (weapon != null && !(weapon instanceof Knuckles || weapon instanceof FightGloves)
					&& !weapon.cursed) {
				int cell = Dungeon.level.randomRespawnCell(null);
				if (cell != -1) {
					hero.belongings.weapon = null;
					Dungeon.quickslot.clearItem(weapon);
					weapon.updateQuickslot();
					Dungeon.level.drop(weapon, cell).seen = true;
					revealAround(cell);
					if (hero.sprite != null) GLog.w(Messages.get(this, "disarm"));
					showTeleportEffect();
				}
			}
		}
	}

	private void revealAround(int cell) {
		for (int offset : PathFinder.NEIGHBOURS9) {
			int visited = cell + offset;
			if (visited >= 0 && visited < Dungeon.level.length()) Dungeon.level.visited[visited] = true;
		}
		Dungeon.observe();
		GameScene.updateFog(cell, 1);
	}

	private void showTeleportEffect() {
		if (!Dungeon.level.heroFOV[pos] || Game.instance == null || Game.scene() == null) return;
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
	}
}
