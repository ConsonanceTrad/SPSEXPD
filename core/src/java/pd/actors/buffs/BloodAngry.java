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
 */

package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class BloodAngry extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BloodAngry.class)
			.t("name", "血怒")
			.t("desc", "圣杯里的血液和你交融，降低了你当前的生命上限，但大幅度提升了你的速度和伤害。\n\n剩余的效果时长：%s回合。");
	}


	private static final String LEFT = "left";

	private float left;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.FURY;
	}

	@Override
	public boolean act() {
		if (target.HP > target.HT / 3) {
			target.HP = Math.max(target.HT / 3, target.HP - 1);
		}
		spend(TICK);
		left -= TICK;
		if (left <= 0) {
			detach();
		} else if (target.HP < target.HT / 3) {
			target.HP = Math.max(target.HT / 3, target.HP + 1);
		}
		spend(TICK);
		left -= TICK;
		if (left <= 0) detach();
		return true;
	}

	public BloodAngry set(float duration) {
		left = duration;
		return this;
	}

	public float left() {
		return left;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat(LEFT);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(left));
	}
}
