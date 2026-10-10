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

package pd.items.consum.potions;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MightSurge;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import pd.messages.InlineText;

/**
 * SPSEXPD: 力量药剂改成「临时提高力量的稀有战术道具」。
 *
 * 永久力量由力量之瓶（{@code pd.items.StrBottle}）提供；药剂只在
 * {@link MightSurge#DURATION} 回合内 +{@link MightSurge#STRENGTH_BONUS} 力量，
 * 用来临时穿起更重的装备或多打一点伤害，不再永久加力量。
 */
public class PotionOfStrength extends Potion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfStrength.class)
			.t("name", "力量药剂")
			.t("msg", "一股力量涌动全身，你的力量暂时提升至%d点！")
			.t("desc", "这种强力的液体会在短时间内激发你的肌肉，临时增加3点力量。\n\n效果结束后力量会恢复原值；想要永久力量，请饮用力量之瓶。");
	}




	{
		icon = ItemIconSheet.POTION_STRENGTH;

		unique = true;

		talentFactor = 2f;
	}
	
	@Override
	public void apply( Hero hero ) {
		identify();

		Buff.prolong(hero, MightSurge.class, MightSurge.DURATION);
		if (hero.sprite != null) hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, "+" + MightSurge.STRENGTH_BONUS, FloatingText.STRENGTH);

		GLog.p( Messages.get(this, "msg", hero.STR()) );
	}
	
	@Override
	public int value() {
		return isKnown() ? 50 * quantity : super.value();
	}
	
	@Override
	public int energyVal() {
		return isKnown() ? 10 * quantity : super.energyVal();
	}
}
