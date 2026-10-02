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
import pd.actors.buffs.FlavourBuff;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Wayward extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Wayward.class)
			.t("name", "妄为%s")
			.t("desc", "妄为诅咒的武器会时常变得极其不精准。触发时这种魔法会持续一小会，但会在成功使用妄为武器造成伤害后立刻消散。")
			.t("elestrike_desc", "武器拥有妄为诅咒时，元素打击对范围内的每个敌人都有50%概率造成持续6回合的幻惑。")
			.t("waywardbuff.name", "妄为")
			.t("waywardbuff.desc", "你的妄为武器上的魔法已被触发，现在它已变得极度不精准。这个魔法无法影响如伏击等必定命中的攻击行为，且成功使用妄为武器造成伤害会立刻驱散此效果。\n\n效果剩余回合：%s");
	}


	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		float procChance = 1/4f * procChanceMultiplier(attacker);

		if (attacker.buff(WaywardBuff.class) != null){
			Buff.detach(attacker, WaywardBuff.class);
		} else if (Random.Float() < procChance){
			Buff.prolong(attacker, WaywardBuff.class, WaywardBuff.DURATION);
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

	//see weapon.accuracyFactor for effect
	public static class WaywardBuff extends FlavourBuff {

		{
			type = buffType.NEGATIVE;
			announced = true;
		}

		public static final float DURATION	= 10f;

		@Override
		public int icon() {
			return BuffIndicator.WEAKNESS;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1, 1, 0);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

	}

}
