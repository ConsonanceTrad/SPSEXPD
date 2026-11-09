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

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.materials.DryGrass;
import pd.items.consum.materials.FreshGrass;
import pd.items.consum.materials.ManaCrystal;
import pd.messages.InlineText;
import pd.rituals.Ritual;
import pd.rituals.RitualMaterial;

/**
 * SPSEXPD: 结晶凝聚阵——鲜草与枯草的生机凝成魔力结晶（产物类）。
 */
public class CrystalConvergenceRitual extends Ritual {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(CrystalConvergenceRitual.class)
			.t("name", "结晶凝聚阵")
			.t("desc", "青草间的自然灵气在阵中来回折返，最终凝成一枚魔力结晶。\n\n材料：鲜草×3 + 枯草×3。");
	}

	@Override
	public String id() { return "crystal_convergence"; }


	@Override
	public List<RitualMaterial> materials() {
		return Arrays.asList(
				new RitualMaterial(FreshGrass.class, 3),
				new RitualMaterial(DryGrass.class, 3));
	}

	@Override
	public void act( Hero caster, int center ) {
		//SPSEXPD: 产物类——魔力结晶落于阵心
		if (Dungeon.level == null) return;
		Item crystal = new ManaCrystal();
		crystal.quantity(1);
		Dungeon.level.drop( crystal, center ).sprite.drop();
	}
}
