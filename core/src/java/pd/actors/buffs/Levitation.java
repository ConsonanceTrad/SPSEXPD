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
import pd.ShatteredPixelDungeon;
import pd.actors.Char;
import pd.items.equipment.artifacts.TimekeepersHourglass;
import pd.plants.Swiftthistle;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import pd.messages.InlineText;

public class Levitation extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Levitation.class)
			.t("name", "飘浮")
			.t("desc", "一股魔力把你从地面托起，让你觉得自己身轻如燕。\n\n飘浮中的角色能够悄无声息地移动，并会无视所有以地面为基础的效果。陷阱不会被触发、火焰不会被水扑灭、植物不会被踩踏、根系无法缠绕你、并且深渊能被跨越。小心，在效果结束时上述效果都会失效！\n\n飘浮效果剩余时长：%s回合");
	}



	
	{
		type = buffType.POSITIVE;
	}

	public static final float DURATION	= 20f;
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			target.flying = true;
			Roots.detach( target, Roots.class );
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		target.flying = false;
		super.detach();
		//only press tiles if we're current in the game screen
		if (ShatteredPixelDungeon.scene() instanceof GameScene) {
			Dungeon.level.occupyCell(target );
		}
	}

	//used to determine if levitation is about to end
	public boolean detachesWithinDelay(float delay){
		if (target.buff(Swiftthistle.TimeBubble.class) != null){
			return false;
		}

		if (target.buff(TimekeepersHourglass.timeFreeze.class) != null){
			return false;
		}

		return cooldown() < delay;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.LEVITATION;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1f, 2.1f, 2.5f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
	
	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.LEVITATING);
		else target.sprite.remove(CharSprite.State.LEVITATING);
	}
}
