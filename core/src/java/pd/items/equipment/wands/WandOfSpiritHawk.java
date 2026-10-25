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

package pd.items.equipment.wands;

import java.util.ArrayList;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.abilities.huntress.SpiritHawk;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.utils.Teleport;
import render.utils.math.Random;

/**
 * SPSEXPD: 飞鹰法杖（女猎手向）。
 *
 * <p>把女猎护甲技能「灵鹰」召出的鹰做成法杖：召出一只灵鹰盟友替你作战（已有则改为指示它前往目标）。
 * 护甲技能本体保留不动。
 */
public class WandOfSpiritHawk extends Wand {

	static {
		InlineText.of(WandOfSpiritHawk.class)
				.t("name", "飞鹰法杖")
				.t("staff_name", "飞鹰魔杖")
				.t("desc", "杖顶是一只展翅的鹰形木雕，眼睛用两颗琥珀封着。风从它身边经过时会短暂地变成一个形状。")
				.t("typical_stats_desc", "这根法杖会召出一只_灵鹰_替你作战；已经有一只时，改为指示它前往目标位置。")
				.t("stats_desc", "这根法杖会召出一只_灵鹰_替你作战；已经有一只时，改为指示它前往目标位置。")
				.t("no_space", "周围没有灵鹰落脚的地方。");
	}

	{
		image = SpecificPlaceHolderDict.WAND_HOLDER_0;
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char user = curUser;
		if (user == null) return;

		SpiritHawk.HawkAlly existing = SpiritHawk.getHawk();
		if (existing != null) {
			existing.directTocell(bolt.collisionPos);
			return;
		}

		ArrayList<Integer> spawnPoints = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int p = user.pos + offset;
			if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
				spawnPoints.add(p);
			}
		}
		if (spawnPoints.isEmpty()) {
			GLog.w(Messages.get(this, "no_space"));
			return;
		}

		SpiritHawk.HawkAlly ally = new SpiritHawk.HawkAlly();
		ally.pos = Random.element(spawnPoints);
		GameScene.add(ally);
		Teleport.appear(ally, ally.pos);
		Dungeon.observe();
		pd.actors.buffs.Invisibility.dispel();
	}

	@Override
	public void onHit(pd.items.equipment.weapon.melee.MagesStaff staff, Char attacker, Char defender, int damage) {
		//近战不附带额外效果
	}
}
