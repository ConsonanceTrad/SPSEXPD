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
import pd.actors.buffs.Locked;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Item;
import pd.items.artifacts.GlassTotem;
import pd.items.weapon.melee.normalweapon.Club;
import pd.items.weapon.missiles.throwing.EscapeKnive;
import pd.sprites.GnollSprite;
import com.watabou.utils.Random;

public class Gnoll extends Mob {
	
	{
		spriteClass = GnollSprite.class;
		
		HP = HT = 70 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 7);
		defenseSkill = 9 + legacyDepthAdjustment(1);
		
		EXP = 10;
		maxLvl = 18;
		
		loot = Gold.class;
		lootChance = 0.5f;

		properties.add(Property.ORC);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(10 + legacyDepthAdjustment(0), 20 + legacyDepthAdjustment(0));
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if (buff(Locked.class) != null) {
			return Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
		}
		return Dungeon.level.distance(pos, enemy.pos) <= 2;
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 12 + legacyDepthAdjustment(0);
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(5, 8);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new EscapeKnive(2), new Club(), new GlassTotem());
	}

	@Override
	public void rollToDropLoot() {
		super.rollToDropLoot();
		if (Dungeon.hero != null && Dungeon.level != null && legacyLootLevelEligible()
				&& Random.Float() < legacySecondaryLootChance(0.5f)) {
			Dungeon.level.drop(Generator.random(Generator.Category.RANGEWEAPON), pos).sprite.drop();
		}
	}
}
