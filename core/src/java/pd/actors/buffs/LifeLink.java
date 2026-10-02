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
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Talent;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class LifeLink extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LifeLink.class)
			.t("name", "生命联结")
			.t("ondeath", "你因生命联结的共享伤害而亡...")
			.t("desc", "该单位的生命力与附近的另一个单位相连。双方会共同承担所有所受伤害。\n\n每当该单位受伤时，半数伤害会转移到与该单位有生命联结的单位上。\n\n生命联结剩余回合数：%s");
	}


	public int object = 0;

	private static final String OBJECT    = "object";

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public void detach() {
		super.detach();
		Char ch = (Char)Actor.findById(object);
		if (!target.isActive() && ch != null){
			for (LifeLink l : ch.buffs(LifeLink.class)){
				if (l.object == target.id()){
					l.detach();
				}
			}
		}
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( OBJECT, object );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		object = bundle.getInt( OBJECT );
	}

	@Override
	public int icon() {
		return BuffIndicator.HERB_HEALING;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1, 0, 1);
	}

	@Override
	public float iconFadePercent() {
		int duration = Math.round(6.67f + 3.33f*Dungeon.hero.pointsInTalent(Talent.LIFE_LINK));
		return Math.max(0, (duration - visualcooldown()) / duration);
	}

}
