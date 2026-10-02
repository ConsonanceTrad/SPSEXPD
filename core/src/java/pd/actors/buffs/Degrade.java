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

package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Degrade extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Degrade.class)
			.t("name", "降级")
			.t("heromsg", "你的装备被暂时弱化了！")
			.t("desc", "强大的黑暗魔法正在吞噬升级卷轴注入你装备的魔力！\n\n降级状态下的装备会被视作比起原有等级更低的状态。_超过3级的每次升级都会遭受愈加严重的反噬。_物品的描述也会根据降级的影响而改变。\n\n不过，降级不会影响装备的力量需求，法杖充能，投武耐久以及神器。\n\n降级效果剩余时长：%s回合\n\n使用一张升级卷轴或祛邪卷轴可以立即驱散这种黑暗魔法。");
	}




	public static final float DURATION = 30f;
	
	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)){
			Item.updateQuickslot();
			if (target == Dungeon.hero) ((Hero) target).updateHT(false);
			return true;
		}
		return false;
	}

	@Override
	public void detach() {
		super.detach();
		if (target == Dungeon.hero) ((Hero) target).updateHT(false);
		Item.updateQuickslot();
	}

	//called in Item.buffedLevel()
	public static int reduceLevel( int level ){
		if (level <= 0){
			//zero or negative levels are unaffected
			return level;
		} else {
			//Otherwise returns the rounded result of sqrt(2*(lvl-1)) + 1
			// This means that levels 1/2/3/4/5/6/7/8/9/10/11/12/...
			// Are now instead:       1/2/3/3/4/4/4/5/5/ 5/ 5/ 6/...
			// Basically every level starting with 3 sticks around for 1 level longer than the last
			return (int)Math.round(Math.sqrt(2*(level-1)) + 1);
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.DEGRADE;
	}

	@Override
	public float iconFadePercent() {
		return (DURATION - visualcooldown())/DURATION;
	}
	
}
