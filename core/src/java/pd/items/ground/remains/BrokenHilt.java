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

package pd.items.ground.remains;

import pd.atlas.items.ConsumUsefulCorpseRelicsDict;

import pd.Assets;
import pd.actors.buffs.Buff;
import pd.actors.buffs.PhysicalEmpower;
import pd.actors.hero.Hero;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class BrokenHilt extends RemainsItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BrokenHilt.class)
			.t("name", "断折剑柄")
			.t("desc", "这把断折剑柄曾经应是一名败于此地的决斗者随身武器。即使如今你仍可感受到剑柄其上残留的武道气息，你可以运起这股气息，在接下来使用近战武器两次命中时造成些许额外伤害。但这意味着剑柄会随着武者气息的流失而烟消云散。");
	}


	{
		image = ConsumUsefulCorpseRelicsDict.BROKEN_HILT_0;
	}

	@Override
	protected void doEffect(Hero hero) {
		Buff.affect( hero, PhysicalEmpower.class).set(Math.max(2, hero.lvl/3), 2);
		Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
	}
}