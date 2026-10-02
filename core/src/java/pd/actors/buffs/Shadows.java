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

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Shadows extends Invisibility {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Shadows.class)
			.t("name", "暗影融合")
			.t("desc", "你和周围的阴影融为一体，使你隐形并减缓你的新陈代谢。\n\n当你在隐形时敌人无法追踪或攻击你。大部分物理攻击和魔法(比如卷轴和法杖)会立即解除隐形效果。此外，当你处于暗影融合状态下时，饥饿值增加的速率会降低。\n\n暗影融合状态会一直持续直到你离开阴影或与敌人直接接触。");
	}

	
	protected float left;
	
	private static final String LEFT	= "left";

	{
		announced = false;
		type = buffType.POSITIVE;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEFT, left );
		
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		left = bundle.getFloat( LEFT );
	}
	
	@Override
	public boolean attachTo( Char target ) {
		if (Dungeon.level != null) {
			for (Mob m : Dungeon.level.mobs()) {
				if (Dungeon.level.adjacent(m.pos, target.pos) && m.alignment != target.alignment) {
					return false;
				}
			}
		}
		if (super.attachTo( target )) {
			if (Dungeon.level != null) {
				Sample.INSTANCE.play( Assets.Sounds.MELD );
				Dungeon.observe();
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		super.detach();
		Dungeon.observe();
	}
	
	@Override
	public boolean act() {
		if (target.isAlive()) {
			
			spend( TICK );
			
			if (--left <= 0) {
				detach();
				return true;
			}

			for (Mob m : Dungeon.level.mobs()){
				if (Dungeon.level.adjacent(m.pos, target.pos) && m.alignment != target.alignment){
					detach();
					return true;
				}
			}
			
		} else {
			
			detach();
			
		}
		
		return true;
	}
	
	public void prolong() {
		left = 2;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.SHADOWS;
	}

	@Override
	public float iconFadePercent() {
		return 0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}
}
