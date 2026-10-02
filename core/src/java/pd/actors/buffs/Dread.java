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
import pd.actors.mobs.Mob;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Dread extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Dread.class)
			.t("name", "魂飞魄散")
			.t("desc", "令人魂飞魄散的恐慌正在驱使目标尝试逃出这座地牢！\n\n魂飞魄散状态下的生物将快速逃离它们的敌人，并且会在脱离敌人视野后立即消失在整座地牢中。逃离的敌人不会掉落道具，只会给予玩家减半的经验值奖励。伤害所造成的痛楚将促使其镇定下来。\n\n魂飞魄散效果剩余时长：%d回合");
	}


	protected int left = (int)DURATION;
	public int object = 0;

	public static final float DURATION = 20f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	//dread overrides terror
	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)){
			Buff.detach( target, Terror.class );
			return true;
		} else {
			return false;
		}
	}

	{
		immunities.add(Terror.class);
	}

	@Override
	public boolean act() {

		if (!Dungeon.level.heroFOV[target.pos]
				&& Dungeon.level.distance(target.pos, Dungeon.hero.pos) >= 6) {
			if (target instanceof Mob){
				((Mob) target).EXP /= 2;
			}
			target.destroy();
			target.sprite.killAndErase();
			Dungeon.level.mobs().remove(target);
		} else {
			left--;
			if (left <= 0){
				detach();
			}
		}

		spend(TICK);
		return true;
	}

	public void extend( float duration ) {
		left += duration;
	}

	private static final String LEFT	= "left";
	private static final String OBJECT    = "object";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
		bundle.put(OBJECT, object);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		object = bundle.getInt( OBJECT );
		left = bundle.getInt( LEFT );
	}

	@Override
	public int icon() {
		return BuffIndicator.TERROR;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - left) / DURATION);
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString(left);
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1, 0, 0);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", left);
	}

	public void recover() {
		left -= 5;
		if (left <= 0){
			detach();
		}
	}

}
