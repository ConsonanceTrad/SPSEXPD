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

import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** While active, ordinary enemies killed by the hero can release SPS dew. */
public class Dewcharge extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Dewcharge.class)
			.t("name", "露珠爆炸")
			.t("desc", "当你击杀任何目标时，在它周围生成任意数量的露珠。\n\n剩余的露珠爆破效果时长：%s回合");
	}


	public static final float DURATION = 240f;

	{
		type = buffType.POSITIVE;
	}

	public boolean isDewing() {
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.BLESS;
	}
}
