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

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Invisibility;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Annoying extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Annoying.class)
			.t("name", "喧闹%s")
			.t("msg_1", "嘿，头儿！我们要去打谁！")
			.t("msg_2", "太爽了，击溃他们！")
			.t("msg_3", "嘿！听着！")
			.t("msg_4", "现在是打BOSS的时间吗！？")
			.t("msg_5", "我去，别这么用力！")
			.t("msg_6", "死吧，虫子！")
			.t("msg_7", "无理理理理理理理理理理理理理理理！")
			.t("msg_8", "我们能休息一下吗！？")
			.t("msg_9", "嘭，哈哈！")
			.t("msg_10", "漂亮的一击！")
			.t("msg_11", "我曾经不想成为武器，而是想成为伐木工人的斧头。")
			.t("msg_12", "要时刻警醒：骄傲自满是位缓慢却阴险的杀手。")
			.t("msg_13", "敌羞，吾去脱他衣！")
			.t("desc", "喧闹诅咒的武器真的是想帮你，只不过它的吵闹无比的“帮助”总是会吸引大量敌人。")
			.t("elestrike_desc", "武器拥有喧闹诅咒时，元素打击对范围内的每个敌人都有20%概率造成持续6回合的狂乱。");
	}




	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {

		float procChance = 1/20f * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {
			for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
				mob.beckon(attacker.pos);
			}
			attacker.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
			Sample.INSTANCE.play(Assets.Sounds.MIMIC);
			Invisibility.dispel();
			//~1/100 for each rare line, ~1/10 for each common line
			if (Random.Int(33) != 0) {
				GLog.n(Messages.get(this, "msg_" + Random.IntRange(1, 10)));
			} else {
				GLog.n(Messages.get(this, "msg_" + Random.IntRange(11, 13)));
			}
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