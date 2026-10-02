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

import pd.items.Item;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class ScrollEmpower extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ScrollEmpower.class)
			.t("name", "卷轴赋能")
			.t("desc", "法师阅读卷轴时产生的能量会暂时强化他的法杖！\n\n法杖强化：+%1$d级\n剩余有效施法次数：%2$d");
	}


	{
		type = buffType.POSITIVE;
	}

	private int left = 0;

	public void reset(int left){
		this.left = Math.max(this.left, left);
		Item.updateQuickslot();
	}

	public void use(){
		left--;
		if (left <= 0){
			detach();
		}
	}

	@Override
	public void detach() {
		super.detach();
		Item.updateQuickslot();
	}

	@Override
	public int icon() {
		return BuffIndicator.WAND;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.84f, 0.79f, 0.65f); //scroll colors
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (3f - left) / 3f);
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString(left);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", 2, left);
	}

	private static final String LEFT = "left";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getInt(LEFT);
	}
}
