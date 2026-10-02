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

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite.Glowing;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class Grim extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Grim.class)
			.t("name", "死神%s")
			.t("desc", "这个强力的附魔拥有瞬间斩杀敌人的力量。敌人越弱，附魔的触发几率越大。")
			.t("elestrike_desc", "武器拥有死神附魔时，元素打击范围内除主要目标外的每个敌人都有6~30%的几率被秒杀。(概率基于该敌人已损失的生命值)");
	}



	
	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {

		if (defender.isImmune(Grim.class)) {
			return damage;
		}

		int level = Math.max( 0, weapon.buffedLvl() );

		//scales from 0 - 50% based on how low hp the enemy is, plus 0-5% per level
		float maxChance = 0.5f + .05f*level;
		maxChance *= procChanceMultiplier(attacker);

		//we defer logic using a buff here so we can know the true final damage
		//see Char.damage
		Buff.affect(attacker, GrimTracker.class).maxChance = maxChance;

		if (attacker.buff(GrimTracker.class) != null
				&& attacker instanceof Hero
				&& weapon.hasEnchant(Grim.class, attacker)){
			attacker.buff(GrimTracker.class).qualifiesForBadge = true;
		}

		return damage;
	}
	
	@Override
	public Glowing glowing() {
		return BLACK;
	}

	public static class GrimTracker extends Buff {

		{
			actPriority = Actor.VFX_PRIO;
		}

		public float maxChance;
		public boolean qualifiesForBadge;

		@Override
		public boolean act() {
			detach();
			return true;
		}
	};

}
