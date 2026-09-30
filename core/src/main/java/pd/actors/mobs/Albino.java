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
import pd.actors.blobs.CorruptGas;
import pd.actors.blobs.SandStorm;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.items.food.meatfood.Meat;
import pd.items.Generator;
import pd.items.Item;
import pd.items.wands.Wand;
import pd.scenes.GameScene;
import pd.sprites.AlbinoSprite;
import render.utils.Random;

public class Albino extends Rat {

	{
		spriteClass = AlbinoSprite.class;
		
		HP = HT = 10 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 3);
		EXP = 1;
		
		loot = Meat.class;
		lootChance = 1f;
		properties.add(Property.BEAST);
		properties.add(Property.DEMONIC);
	}
	
	@Override
	public boolean act() {
		if (Dungeon.level != null) {
			for (int offset : pd.mechanics.pathfind.PathFinder.NEIGHBOURS9) {
				int cell = pos + offset;
				if (Dungeon.level.insideMap(cell) && (cell == pos || Dungeon.level.adjacent(pos, cell))) {
					GameScene.add(Blob.seed(cell, 2, SandStorm.class));
				}
			}
		}
		return super.act();
	}

	@Override
	public void damage(int damage, Object source) {
		if (Dungeon.level != null && Dungeon.level.insideMap(pos)) {
			GameScene.add(Blob.seed(pos, 15, CorruptGas.class));
		}
		super.damage(damage, source);
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(Generator.Category.HIGHFOOD);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.HIGHFOOD;
	}

	{
		resistances.add(Wand.class);
		immunities.add(Amok.class);
		immunities.add(Terror.class);
		immunities.add(CorruptGas.class);
		immunities.add(Vertigo.class);
		immunities.add(SandStorm.class);
	}
}
