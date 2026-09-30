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

import pd.Badges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Silent;
import pd.actors.damagetype.DamageType;
import pd.items.Generator;
import pd.items.Item;
import pd.items.eggs.Egg;
import pd.items.weapon.enchantments.EnchantmentDark;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.WarlockSprite;
import pd.utils.GLog;
import render.utils.Random;

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
			pd.items.Heap heap =
					Dungeon.level.drop(Generator.randomUsingDefaults(Generator.Category.WAND), pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
	}

	@Override public Item SupercreateLoot() { return new Egg(); }
}
