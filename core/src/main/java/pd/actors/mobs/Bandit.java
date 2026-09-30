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
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.DarkGas;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.Poison;
import pd.items.Generator;
import pd.items.Item;
import pd.scenes.GameScene;
import pd.sprites.BanditSprite;
import render.utils.Bundle;
import render.utils.PathFinder;
import render.utils.Random;

public class Bandit extends Thief {

	private static final String BREAKS = "breaks";
	private static final String SKILL_USED = "skill_used";

	private int breaks;
	private boolean skillUsed;

	{
		spriteClass = BanditSprite.class;
		properties.add(Property.ELF);
		immunities.add(Blindness.class);
		immunities.add(DarkGas.class);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BREAKS, breaks);
		bundle.put(SKILL_USED, skillUsed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		breaks = bundle.getInt(BREAKS);
		skillUsed = bundle.getBoolean(SKILL_USED);
	}

	@Override
	protected boolean act() {
		if (2 - breaks > 3 * HP / HT) {
			breaks++;
			skillUsed = false;
			return true;
		}

		seedDarkGas();
		return super.act();
	}

	protected void seedDarkGas() {
		if (Dungeon.level == null) return;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int cell = pos + offset;
			if (Dungeon.level.insideMap(cell)) {
				GameScene.add(Blob.seed(cell, 10, DarkGas.class));
			}
		}
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Dungeon.level != null && enemy != null
				&& Dungeon.level.distance(pos, enemy.pos) <= 2;
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.MELEEWEAPON;
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(specialLootCategory());
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (!skillUsed && enemy == Dungeon.hero) {
			skillUsed = true;
			int gold = Math.max(0, Dungeon.gold);
			Buff.affect(this, EnergyArmor.class).level(gold / 40);
			Dungeon.gold = gold - gold / 20;
		}

		if (skillUsed && Random.Int(3) == 1) {
			Buff.affect(enemy, Poison.class).set(Random.IntRange(2, 3));
		}
		return damage;
	}

	@Override
	public void damage(int damage, Object src) {
		super.damage(Math.min(damage, Math.max(HT / 6, 1)), src);
	}
}
