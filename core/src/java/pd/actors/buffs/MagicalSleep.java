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

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import pd.messages.InlineText;

public class MagicalSleep extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MagicalSleep.class)
			.t("name", "魔法睡眠")
			.t("toohealthy", "你十分健康，因此抵抗住了强烈的嗜睡感。")
			.t("fallasleep", "你深深地陷入了魔法睡眠。")
			.t("wakeup", "你醒来后，感觉浑身清爽并且十分健康。")
			.t("desc", "目标已深深陷入了魔法睡眠，不会自然醒来。\n\n魔法睡眠与一般的睡眠基本一样，但只有受伤会致使目标醒来。\n\n对于英雄和他们的盟友，魔法睡眠有一些恢复性的效果，使他们能够在睡眠中迅速愈合伤口。");
	}


	private static final float STEP = 1f;

	@Override
	public boolean attachTo( Char target ) {
		if (!target.isImmune(Sleep.class) && super.attachTo( target )) {
			
			target.paralysed++;
			
			if (target.alignment == Char.Alignment.ALLY) {
				if (target.HP == target.HT) {
					if (target instanceof  Hero) GLog.i(Messages.get(this, "toohealthy"));
					detach();
					return true;
				} else {
					if (target instanceof  Hero) GLog.i(Messages.get(this, "fallasleep"));
				}
			}

			if (target instanceof Mob) {
				((Mob) target).state = ((Mob) target).SLEEPING;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean act(){
		if (target instanceof Mob && ((Mob) target).state != ((Mob) target).SLEEPING){
			detach();
			return true;
		}
		if (target.alignment == Char.Alignment.ALLY) {
			target.HP = Math.min(target.HP+1, target.HT);
			if (target instanceof  Hero) ((Hero) target).resting = true;
			if (target.HP == target.HT) {
				if (target instanceof  Hero) GLog.p(Messages.get(this, "wakeup"));
				detach();
			}
		}
		spend( STEP );
		return true;
	}

	@Override
	public void detach() {
		if (target.paralysed > 0) {
			target.paralysed--;
		}
		if (target instanceof Hero) {
			((Hero) target).resting = false;
		} else if (target instanceof Mob && target.alignment == Char.Alignment.ALLY && ((Mob) target).state == ((Mob) target).SLEEPING){
			((Mob) target).state = ((Mob) target).WANDERING;
		}
		super.detach();
	}

	@Override
	public int icon() {
		return BuffIndicator.MAGIC_SLEEP;
	}

	@Override
	public void fx(boolean on) {
		if (!on && (target.paralysed <= 1) ) {
			//in case the character has visual paralysis from another source
			target.sprite.remove(CharSprite.State.PARALYSED);
		}
	}
}