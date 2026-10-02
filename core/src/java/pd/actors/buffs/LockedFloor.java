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

import pd.Challenges;
import pd.Dungeon;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class LockedFloor extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LockedFloor.class)
			.t("name", "背水一战")
			.t("desc", "当前楼层被彻底封锁，你无法离开这里！\n\n封锁持续期间，你不会更加饥饿，或因极度饥饿减少生命值。此外，如果你没有在与Boss战斗，所有的被动回复都会停止。\n\n另外，如果你在楼层封锁时被未祝福的重生十字架复活了，封锁效果将被重置。\n\n击杀本层Boss以解除封锁。");
	}


	//the amount of turns remaining before beneficial passive effects turn off
	//starts at 50 turns normally, 20 with badder bosses
	private float left = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 20 : 50;

	@Override
	public boolean act() {
		spend(TICK);

		if (!Dungeon.level.locked)
			detach();

		if (left >= 1)
			left --;

		return true;
	}

	public void addTime(float time){
		left += time;
		left = Math.min(left, 50); //cannot build to more than 50
	}

	public void removeTime(float time){
		left -= time; //can go negative!
	}

	public boolean regenOn(){
		return left >= 1;
	}

	private final String LEFT = "left";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( LEFT, left );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat( LEFT );
	}

	@Override
	public int icon() {
		return BuffIndicator.LOCKED_FLOOR;
	}
}
