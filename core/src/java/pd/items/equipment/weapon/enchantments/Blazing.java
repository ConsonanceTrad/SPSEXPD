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

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.effects.particles.FlameParticle;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite.Glowing;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Blazing extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Blazing.class)
			.t("name", "烈焰%s")
			.t("desc", "这个附魔会使火焰从武器中喷薄而出，能够点燃敌人并对正在燃烧的敌人造成额外伤害。")
			.t("elestrike_desc", "武器拥有烈焰附魔时，元素打击会将火焰扩散到范围内全部地块，持续8回合。");
	}




	private static ItemSprite.Glowing ORANGE = new ItemSprite.Glowing( 0xFF4400 );
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		int level = Math.max( 0, weapon.buffedLvl() );

		// lvl 0 - 33%
		// lvl 1 - 50%
		// lvl 2 - 60%
		float procChance = (level+1f)/(level+3f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);

			if (defender.buff(Burning.class) == null){
				Buff.affect(defender, Burning.class).reignite(defender, 8f);
				powerMulti -= 1;
			}

			if (powerMulti > 0){
				int burnDamage = Random.NormalIntRange( 1, 3 + Dungeon.scalingDepth()/4 );
				burnDamage = Math.round(burnDamage * 0.67f * powerMulti);
				if (burnDamage > 0) {
					defender.damage(burnDamage, this);
				}
			}
			
			defender.sprite.emitter().burst( FlameParticle.FACTORY, level + 1 );
			
		}

		return damage;

	}
	
	@Override
	public Glowing glowing() {
		return ORANGE;
	}
}
