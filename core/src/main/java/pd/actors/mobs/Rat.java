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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.items.Item;
import pd.items.food.meatfood.Meat;
import pd.items.weapon.missiles.meleethrow.Brick;
import pd.scenes.GameScene;
import pd.sprites.RatSprite;
import render.utils.Bundle;
import render.utils.PathFinder;
import render.utils.Random;

public class Rat extends Mob {
	private static final float SPAWN_DELAY = 2f;

	{
		spriteClass = RatSprite.class;
		
		HP = HT = 40 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 3);
		defenseSkill = 3 + legacyDepthAdjustment(1);

		EXP = 1;
		maxLvl = 4;
		loot = Meat.class;
		lootChance = 0.5f;

		properties.add(Property.BEAST);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 5 + legacyDepthAdjustment(0));
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 5 + legacyDepthAdjustment(0);
	}
	
	@Override
	public int drRoll() {
		return 1;
	}

	@Override public Item SupercreateLoot() { return new Brick(); }

	public static void spawnAround(int pos) {
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = pos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null) spawnAt(cell);
		}
	}

	public static Rat spawnAt(int pos) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(pos)
				|| !Dungeon.level.passable[pos] || Actor.findChar(pos) != null) return null;
		Rat rat = new Rat();
		rat.pos = pos;
		rat.state = rat.HUNTING;
		GameScene.add(rat, SPAWN_DELAY);
		return rat;
	}

	private static final String RAT_ALLY = "rat_ally";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (alignment == Alignment.ALLY) bundle.put(RAT_ALLY, true);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(RAT_ALLY)) alignment = Alignment.ALLY;
	}
}
