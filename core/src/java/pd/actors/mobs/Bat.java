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
import pd.actors.buffs.BeOld;
import pd.effects.FloatingText;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.potions.PotionOfHealing;
import pd.sprites.BatSprite;
import pd.sprites.CharSprite;
import render.utils.math.Random;

public class Bat extends Mob {

	{
		spriteClass = BatSprite.class;
		
		HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(2, 5);
		defenseSkill = 15 + legacyDepthAdjustment(0);
		baseSpeed = 2f;
		
		EXP = 9;
		maxLvl = 25;
		
		flying = true;
		
		loot = Generator.Category.SEED;
		lootChance = 0.15f;

		properties.add(Property.BEAST);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(15, 22 + legacyDepthAdjustment(0));
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 16 + legacyDepthAdjustment(0);
	}
	
	@Override
	public int drRoll() {
		return legacyDepthAdjustment(0);
	}

	@Override
	public void die(Object cause) {
		flying = false;
		super.die(cause);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		int reg = buff(BeOld.class) == null ? Math.min(damage, HT - HP) : 0;
		
		if (reg > 0) {
			HP += reg;
			if (sprite != null) sprite.showStatusWithIcon(CharSprite.POSITIVE,
					Integer.toString(reg), FloatingText.HEALING);
		}
		
		return damage;
	}
	
	@Override
	public void rollToDropLoot() {
		super.rollToDropLoot();
		if (Dungeon.hero != null && Dungeon.level != null && legacyLootLevelEligible()
				&& Random.Float() < legacySecondaryLootChance(0.3f)) {
			Dungeon.level.drop(new pd.items.consum.food.meatfood.Meat(), pos).sprite.drop();
		}
	}

	@Override public Item SupercreateLoot() { return Generator.random(Generator.Category.MUSHROOM); }
	
}
