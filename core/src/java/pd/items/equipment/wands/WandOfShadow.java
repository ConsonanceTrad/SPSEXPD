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
import pd.actors.hero.abilities.rogue.ShadowClone;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.math.Random;

/**
 * SPSEXPD: 暗影法杖（盗贼向）。
 *
 * <p>把盗贼护甲技能「暗影分身」召出的分身做成法杖：召出一个你自己的暗影分身（已有则改为指示它）。
 * 护甲技能本体保留不动。
 */
public class WandOfShadow extends Wand {

	static {
		InlineText.of(WandOfShadow.class)
				.t("name", "暗影法杖")
				.t("staff_name", "暗影魔杖")
				.t("desc", "杖身漆黑，摸上去比周围凉一截。你举起它时，地上会先出现影子，然后你才抬手。")
				.t("typical_stats_desc", "这根法杖会召出一个_暗影分身_替你作战；已经有一个时，改为指示它前往目标位置。")
				.t("stats_desc", "这根法杖会召出一个_暗影分身_替你作战；已经有一个时，改为指示它前往目标位置。")
				.t("no_space", "周围没有影子可以立起来的地方。");
	}

	{
		image = SpecificPlaceHolderDict.WAND_HOLDER_0;
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char user = curUser;
		if (user == null) return;

		ShadowClone.ShadowAlly existing = ShadowClone.getShadowAlly();
		if (existing != null) {
			existing.defendPos(bolt.collisionPos);
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

		ShadowClone.ShadowAlly ally = new ShadowClone.ShadowAlly(Dungeon.hero == null ? 1 : Dungeon.hero.lvl);
		ally.pos = Random.element(spawnPoints);
		GameScene.add(ally);
		ally.defendPos(bolt.collisionPos);
		Dungeon.observe();
	}

	@Override
	public void onHit(pd.items.equipment.weapon.melee.MagesStaff staff, Char attacker, Char defender, int damage) {
		//近战不附带额外效果
	}
}
