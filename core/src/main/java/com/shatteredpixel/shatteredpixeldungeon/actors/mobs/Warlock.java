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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.Egg;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentDark;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WarlockSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Warlock extends Mob {
	
	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = WarlockSprite.class;
		
		HP = HT = 120 + legacyDepthAdjustment(0) * Random.NormalIntRange(5, 7);
		defenseSkill = 18 + legacyDepthAdjustment(0);
		
		EXP = 11;
		maxLvl = 30;
		
		loot = Generator.Category.POTION;
		lootChance = 0.83f;

		properties.add(Property.DWARF);
		properties.add(Property.MAGICER);

		resistances.add(EnchantmentDark.class);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(12, 24 + legacyDepthAdjustment(0));
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 25 + legacyDepthAdjustment(0);
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(4, 8);
	}
	
	@Override
	public int attackProc(Char enemy, int damage) {
		enemy.damage(damage / 2, DamageType.DARK_DAMAGE);
		return damage / 2;
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		if (buff(Silent.class) != null) {
			return Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
		}
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos != enemy.pos) {
			
			return super.doAttack( enemy );
			
		} else {
			
			if (sprite != null && (sprite.visible || enemy.sprite != null && enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class DarkBolt{}
	
	protected void zap() {
		spend( TIME_TO_ZAP );

		Char enemy = this.enemy;
		if (hit( this, enemy, true )) {
			if (enemy == Dungeon.hero && Random.Int( 2 ) == 0) {
				Buff.prolong(enemy, STRDown.class, 10f);
			}
			
			int dmg = Random.Int(16, 24 + legacyDepthAdjustment(0));
			enemy.damage(dmg, DamageType.DARK_DAMAGE);
			
			if (enemy == Dungeon.hero && !enemy.isAlive()) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail( this );
				GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else if (enemy.sprite != null) {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}
	
	public void onZapComplete() {
		zap();
		next();
	}

	@Override
	public void rollToDropLoot() {
		super.rollToDropLoot();
		if (Dungeon.hero != null && Dungeon.level != null && legacyLootLevelEligible()
				&& Random.Float() < legacySecondaryLootChance(0.02f)) {
			com.shatteredpixel.shatteredpixeldungeon.items.Heap heap =
					Dungeon.level.drop(Generator.randomUsingDefaults(Generator.Category.WAND), pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
	}

	@Override public Item SupercreateLoot() { return new Egg(); }
}
