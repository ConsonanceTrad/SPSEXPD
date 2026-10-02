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

import pd.actors.Char;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.food.MysteryMeat;
import pd.sprites.CrabSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Crab extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Crab.class)
			.t("name", "下水道螃蟹")
			.t("desc", "这些巨型的螃蟹位居下水道食物链的顶端。它们行动极其迅速且其厚重的甲壳能承受沉重的打击。");
	}


	{
		spriteClass = CrabSprite.class;
		
		HP = HT = 50 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 3);
		defenseSkill = 5 + legacyDepthAdjustment(1);
		baseSpeed = 2f;
		
		EXP = 3;
		maxLvl = 9;
		
		loot = MysteryMeat.class;
		lootChance = 0.5f;

		properties.add(Property.FISHER);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(3, 6 + legacyDepthAdjustment(0));
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 12 + legacyDepthAdjustment(0);
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}

	@Override public Item SupercreateLoot() { return Generator.random(Generator.Category.ARMOR); }
}
