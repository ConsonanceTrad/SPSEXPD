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

package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.mobs.Mimic;
import pd.effects.FloatingText;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite.Glowing;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Vampiric extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Vampiric.class)
			.t("name", "血饮%s")
			.t("desc", "这个强力的附魔能在攻击敌人时吸取其生命能量并恢复使用者的生命。当使用者生命较少时效果更强。")
			.t("elestrike_desc", "武器拥有血饮附魔时，元素打击范围内每有一个敌人，决斗家就会恢复2.5点生命值。");
	}




	private static ItemSprite.Glowing RED = new ItemSprite.Glowing( 0x660022 );
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		
		//chance to heal scales from 5%-30% based on missing HP
		float missingPercent = (attacker.HT - attacker.HP) / (float)attacker.HT;
		float healChance = 0.05f + .25f*missingPercent;

		healChance *= procChanceMultiplier(attacker);
		
		if (Random.Float() < healChance
				&& attacker.alignment != defender.alignment
				&& (defender.alignment != Char.Alignment.NEUTRAL || defender instanceof Mimic)){

			float powerMulti = Math.max(1f, healChance);
			
			//heals for 50% of damage dealt
			int healAmt = Math.round(damage * 0.5f * powerMulti);
			healAmt = Math.min( healAmt, attacker.HT - attacker.HP );
			
			if (healAmt > 0 && attacker.isAlive()) {
				
				attacker.HP += healAmt;
				if (attacker.sprite != null) attacker.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString( healAmt ), FloatingText.HEALING );
				
			}
		}

		return damage;
	}
	
	@Override
	public Glowing glowing() {
		return RED;
	}
}
