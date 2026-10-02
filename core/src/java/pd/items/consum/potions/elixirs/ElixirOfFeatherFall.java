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

package pd.items.consum.potions.elixirs;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.items.consum.potions.PotionOfLevitation;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.Image;
import pd.messages.InlineText;

public class ElixirOfFeatherFall extends Elixir {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ElixirOfFeatherFall.class)
			.t("name", "羽落秘药")
			.t("light", "你觉得自己身轻如燕！")
			.t("desc", "这瓶秘药可为你提供更弱但更可控的悬浮效果，在短时间内使你身轻如燕，即便跳下悬崖深渊也能毫发无损。饮用这瓶秘药可以为你提供短时间内免除坠落伤害的效果。")
			.t("$featherbuff.name", "羽落")
			.t("$featherbuff.desc", "你正处于羽落秘药的作用效果之下，可以跳进深渊并坠落至下一层而不受到任何伤害！\n\n效果剩余时长：%s回合");
	}




	{
		image = ConsumPotionSeedBasicPotionDict.ELIXIR_FEATHER_0;

		talentChance = 1/(float)Recipe.OUT_QUANTITY;
	}

	@Override
	public void apply(Hero hero) {
		Buff.append(hero, FeatherBuff.class, FeatherBuff.DURATION);

		hero.sprite.emitter().burst(Speck.factory(Speck.JET), 20);
		GLog.p(Messages.get(this, "light"));
	}

	public static class FeatherBuff extends FlavourBuff {
		//does nothing, just waits to be triggered by chasm falling
		{
			type = buffType.POSITIVE;
		}

		public void processFall(){
			spend(-10f);
			if (cooldown() <= 0) {
				detach();
			}
		}

		public static final float DURATION	= 50f;

		@Override
		public int icon() {
			return BuffIndicator.LEVITATION;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1f, 2f, 1.25f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}
	}

	public static class Recipe extends pd.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 1;

		{
			inputs =  new Class[]{PotionOfLevitation.class};
			inQuantity = new int[]{1};

			cost = 10;

			output = ElixirOfFeatherFall.class;
			outQuantity = OUT_QUANTITY;
		}

	}

}
