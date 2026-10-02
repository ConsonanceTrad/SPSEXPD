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

package pd.items.equipment.weapon.curses;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.effects.Speck;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Friendly extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Friendly.class)
			.t("name", "友善%s")
			.t("desc", "友善诅咒的武器会偶尔触发让战斗无法进行的魔法，非常适合和平主义者。")
			.t("elestrike_desc", "武器拥有友善诅咒时，元素打击对范围内的每个敌人都有50%概率造成持续6回合的魅惑。");
	}

	
	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {

		if (attacker.buff(Charm.class) != null && attacker.buff(Charm.class).object == defender.id()){
			damage = 0;
		}

		float procChance = 1/10f * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {
			
			Buff.affect( attacker, Charm.class, Charm.DURATION ).object = defender.id();
			attacker.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
			
			Charm c = Buff.affect( defender, Charm.class, Charm.DURATION/2 );
			c.ignoreNextHit = true;
			c.object = attacker.id();
			defender.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
			
		}
		
		return damage;
	}
	
	@Override
	public boolean curse() {
		return true;
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

}
