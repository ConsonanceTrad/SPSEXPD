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
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Light;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Poison;
import pd.items.Item;
import pd.items.food.MysteryMeat;
import pd.items.potions.PotionOfHealing;
import pd.items.weapon.melee.normalweapon.Dagger;
import pd.sprites.ScorpioSprite;
import watabou.utils.Random;

public class Scorpio extends Mob {
	
	{
		spriteClass = ScorpioSprite.class;
		
		HP = HT = 180 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 3);
		defenseSkill = 24 + legacyDepthAdjustment(1);
		viewDistance = Light.DISTANCE;
		
		EXP = 17;
		maxLvl = 35;
		
		loot = PotionOfHealing.class;
		lootChance = 0.2f;

		properties.add(Property.BEAST);
		resistances.add(Poison.class);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(20, 52 + legacyDepthAdjustment(0));
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 36 + legacyDepthAdjustment(1);
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(10, 20);
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		if (buff(Locked.class) != null) {
			return Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
		}
		return Dungeon.level.distance(pos, enemy.pos) <= 2;
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		if (Random.Int( 2 ) == 0) {
			Buff.prolong( enemy, Cripple.class, Cripple.DURATION );
		}
		
		return damage;
	}
	
	@Override
	protected boolean getCloser( int target ) {
		if (state == HUNTING) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}
	
	@Override
	public void rollToDropLoot() {
		super.rollToDropLoot();
		if (Dungeon.hero != null && Dungeon.level != null && legacyLootLevelEligible()
				&& Random.Float() < legacySecondaryLootChance(0.3f)) {
			pd.items.Heap heap = Dungeon.level.drop(new MysteryMeat(), pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
	}

	@Override public Item SupercreateLoot() { return new Dagger(); }
	
}
