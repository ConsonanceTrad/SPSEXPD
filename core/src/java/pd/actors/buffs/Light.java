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
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Light extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Light.class)
			.t("name", "发光")
			.t("desc", "即使是在最黑暗的地牢中，身边有一个稳定的光源也总是令人欣慰。 \n\n光照能驱散黑暗，使你能够无视周遭的黑暗环境并拥有一个合理的视野范围。 \n\n发光效果剩余时长：%s回合");
	}



	
	{
		type = buffType.POSITIVE;
	}

	public static final float DURATION	= 250f;
	public static final int DISTANCE	= 6;   //SPSEXPD: 普通照明的最低保底视距，实际由 Dungeon.refreshHeroViewDistance() 统一计算
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			if (Dungeon.level != null) {
				//SPSEXPD: 英雄视野统一由 Dungeon 计算（普通照明保底 Light.DISTANCE）；
				//其它角色（怪物）维持原行为，避免改动它们的感知范围
				if (target == Dungeon.hero) {
					Dungeon.observe();
				} else {
					target.viewDistance = Math.max( Dungeon.level.viewDistance, DISTANCE );
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		//SPSEXPD: 先移除 buff 再观察/还原，否则重算时仍会看到 Light 挂着
		Char owner = target;
		super.detach();
		if (Dungeon.level != null && owner != null) {
			if (owner == Dungeon.hero) {
				Dungeon.observe();
			} else {
				owner.viewDistance = Dungeon.level.viewDistance;
			}
		}
	}

	public void weaken( int amount ){
		spend(-amount);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.LIGHT;
	}
	
	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
		else target.sprite.remove(CharSprite.State.ILLUMINATED);
	}
}
