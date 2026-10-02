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
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Chill extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Chill.class)
			.t("name", "冻伤")
			.t("freezes", "%s冻住了！")
			.t("desc", "还没有完全冻住，不过已经非常冷了。\n\n冻伤的目标所有行动都会更加缓慢，减速程度取决于剩余的冻伤效果时间。最严重的冻伤情况等同于迟缓效果。\n\n冻伤效果剩余时长：%1$s回合\n当前减少的速度：%2$s%%");
	}


	public static final float DURATION = 10f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean attachTo(Char target) {
		Buff.detach( target, Burning.class );

		return super.attachTo(target);
	}

	//reduces speed by 10% for every turn remaining, capping at 50%
	public float speedFactor(){
		return Math.max(0.5f, 1 - cooldown()*0.1f);
	}

	@Override
	public int icon() {
		return BuffIndicator.FROST;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.CHILLED);
		else target.sprite.remove(CharSprite.State.CHILLED);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(), Messages.decimalFormat("#.##", (1f-speedFactor())*100f));
	}
}
