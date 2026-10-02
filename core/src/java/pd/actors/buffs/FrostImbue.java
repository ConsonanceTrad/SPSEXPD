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
import pd.effects.particles.SnowParticle;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import pd.messages.InlineText;

public class FrostImbue extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FrostImbue.class)
			.t("name", "寒霜之力")
			.t("desc", "你被灌注了寒霜的力量！\n\n所有的物理攻击都会在敌人身上累加冻伤效果。与此同时你对寒冷完全免疫。\n\n寒霜之力剩余时长：%s回合");
	}



	
	{
		type = buffType.POSITIVE;
		announced = true;
	}
	
	public static final float DURATION	= 50f;
	
	public void proc(Char enemy){
		Buff.affect(enemy, Chill.class, 3f);
		enemy.sprite.emitter().burst( SnowParticle.FACTORY, 3 );
	}
	
	@Override
	public int icon() {
		return BuffIndicator.IMBUE;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0, 2f, 3f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
	
	{
		immunities.add( Frost.class );
		immunities.add( Chill.class );
	}

	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)){
			Buff.detach(target, Frost.class);
			Buff.detach(target, Chill.class);
			return true;
		} else {
			return false;
		}
	}
}
