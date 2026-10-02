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
import pd.effects.particles.RainbowParticle;
import pd.items.equipment.trinkets.WondrousResin;
import pd.items.equipment.wands.CursedWand;
import pd.items.equipment.weapon.Weapon;
import pd.mechanics.Ballistica;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Wondrous extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Wondrous.class)
			.t("name", "奇迹%s")
			.t("desc", "奇迹诅咒的武器内含与诅咒法杖同源的混沌魔力。没人知道这个诅咒触发时到底会发生些什么！")
			.t("elestrike_desc", "武器拥有奇迹诅咒时，元素打击有概率对范围内的所有敌人施放一种不同的诅咒法杖效果。");
	}




	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		float procChance = 1/8f * procChanceMultiplier(attacker);

		if (Random.Float() < procChance){

			boolean positiveOnly = Random.Float() < WondrousResin.positiveCurseEffectChance();

			Ballistica aim = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
			defender.sprite.emitter().burst(RainbowParticle.BURST, 25);
			CursedWand.randomValidEffect(weapon, attacker, aim, positiveOnly).effect(weapon, attacker, aim, positiveOnly);
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
