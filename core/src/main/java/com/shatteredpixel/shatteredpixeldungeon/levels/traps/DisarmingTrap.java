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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.FightGloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Knuckles;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

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
