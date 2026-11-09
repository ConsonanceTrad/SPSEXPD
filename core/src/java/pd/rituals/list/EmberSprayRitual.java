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

import pd.Dungeon;
import pd.actors.blobs.Fire;
import pd.actors.hero.Hero;
import pd.items.consum.materials.DryTwig;
import pd.items.consum.materials.Tinder;
import pd.messages.InlineText;
import pd.scenes.GameScene;
import pd.rituals.Ritual;
import pd.rituals.RitualMaterial;
import pd.rituals.Rituals;
import pd.actors.blobs.Blob;

import java.util.Arrays;
import java.util.List;

/**
 * SPSEXPD: 爆裂余烬阵——火种 + 枯枝，以落点为中心的 5×5 火场（地面效果类）。
 */
public class EmberSprayRitual extends Ritual {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(EmberSprayRitual.class)
			.t("name", "爆裂余烬阵")
			.t("desc", "以枯枝为薪、火种引燃：布置完成后用法器激发，落点周围五格将燃起短暂而凶猛的火焰。\n\n材料：火种×1 + 枯枝×2。");
	}

	@Override
	public String id() { return "ember_spray"; }


	@Override
	public List<RitualMaterial> materials() {
		return Arrays.asList(
				new RitualMaterial(Tinder.class, 1),
				new RitualMaterial(DryTwig.class, 2));
	}

	@Override
	public void act( Hero caster, int center ) {
		//SPSEXPD: 地面效果——5×5 逐格播撒火焰，墙格跳过；Blob 由 GameScene 驱动，headless 时 GameScene.add 有 null 场景守卫
		if (Dungeon.level == null) return;
		for (int off : Rituals.radiusOffsets( Dungeon.level )) {
			int cell = center + off;
			if (!Dungeon.level.insideMap(cell) || Dungeon.level.solid[cell]) continue;
			GameScene.add( Blob.seed(cell, 2, Fire.class) );
		}
	}
}
