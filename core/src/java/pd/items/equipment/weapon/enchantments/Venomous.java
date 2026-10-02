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
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.effects.CellEmitter;
import pd.effects.particles.PoisonParticle;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Venomous extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Venomous.class)
			.t("name", "猛毒%s")
			.t("desc", "猛毒附魔的武器可以施加延迟却致命的中毒效果，其每次触发都会使毒性更猛烈。")
			.t("elestrike_desc", "武器拥有猛毒附魔时，元素打击会对范围内除主目标外的敌人会施加额外的延迟中毒效果。");
	}


	private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing( 0x4400AA );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = Math.max( 0, weapon.buffedLvl() );

		// flat 33% proc chance, effect scales with level
		float procChance = 1/3f * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);

			//delays the poison's damage by 3 turns if the enemy had no poison on them already
			Poison poison = defender.buff(Poison.class);
			if (poison == null) {
				poison = Buff.affect(defender, Poison.class);
				poison.delay(3f);
				poison = defender.buff(Poison.class);
			}
			if (poison != null){
				poison.extend(powerMulti * ((level / 2f) + 3));
			}
			CellEmitter.center(defender.pos).burst(PoisonParticle.SPLASH, 5);

		}

		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return PURPLE;
	}
}
