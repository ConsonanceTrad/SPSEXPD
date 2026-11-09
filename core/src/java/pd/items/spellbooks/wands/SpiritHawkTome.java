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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pd.items.spellbooks.wands;

import java.util.ArrayList;

import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.scenes.GameScene;

/**
 * SPSEXPD: 原典《灵鹰原典》（法术书化批次3d）——飞鹰法杖的书化：
 * 召出灵鹰盟友（已有则指示前往点选位置）。
 */
public class SpiritHawkTome extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(SpiritHawkTome.class)
			.t("name", "灵鹰原典")
			.t("cast_prompt", "选择灵鹰飞往的位置")
			.t("desc", "翻页带起的风会短暂地聚成一对翅膀。\n\n施法（消耗 5 法力）：召出一只_灵鹰_替你作战；已经有一只时，改为指示它飞往点选的位置。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public boolean targetRequired() { return true; }

	@Override
	public void castAt( Hero hero, int cell ) {
		pd.actors.hero.abilities.huntress.SpiritHawk.HawkAlly existing =
				pd.actors.hero.abilities.huntress.SpiritHawk.getHawk();
		if (existing != null) {
			existing.directTocell( cell );
			return;
		}

		ArrayList<Integer> spawnPoints = new ArrayList<>();
		for (int offset : pd.mechanics.pathfind.PathFinder.NEIGHBOURS8) {
			int p = hero.pos + offset;
			if (Actor.findChar( p ) == null && (pd.Dungeon.level.passable[p] || pd.Dungeon.level.avoid[p])) {
				spawnPoints.add( p );
			}
		}
		if (spawnPoints.isEmpty()) {
			pd.utils.GLog.w( pd.messages.Messages.get( SpiritHawkTome.class, "no_space" ) );
			return;
		}

		pd.actors.hero.abilities.huntress.SpiritHawk.HawkAlly ally =
				new pd.actors.hero.abilities.huntress.SpiritHawk.HawkAlly();
		ally.pos = render.utils.math.Random.element( spawnPoints );
		GameScene.add( ally );
		pd.utils.Teleport.appear( ally, ally.pos );
		pd.Dungeon.observe();
		pd.actors.buffs.Invisibility.dispel();
	}
}
