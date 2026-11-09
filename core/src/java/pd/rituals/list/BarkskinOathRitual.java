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

package pd.rituals.list;

import java.util.Arrays;
import java.util.List;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Barkskin;
import pd.actors.hero.Hero;
import pd.items.consum.materials.GrassSeed;
import pd.messages.InlineText;
import pd.rituals.Ritual;
import pd.rituals.RitualMaterial;

/**
 * SPSEXPD: 树皮誓约阵——草种献祭，施术者获得树肤强化（强力 buff 类）。
 */
public class BarkskinOathRitual extends Ritual {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(BarkskinOathRitual.class)
			.t("name", "树皮誓约阵")
			.t("desc", "以草种为誓，草木的坚韧借予施术者：激发后获得一段时间的树肤（增强物理护甲）。\n\n材料：草种×4。");
	}

	@Override
	public String id() { return "barkskin_oath"; }


	@Override
	public List<RitualMaterial> materials() {
		return Arrays.asList(
				new RitualMaterial(GrassSeed.class, 4));
	}

	@Override
	public void act( Hero caster, int center ) {
		//SPSEXPD: 强力 buff 类——树肤强度 2+等级/2、每 10 回合衰减一层（与果实类用法同源）
		if (caster == null) return;
		Buff.affect(caster, Barkskin.class).set(2 + caster.lvl / 2, 10);
	}
}
