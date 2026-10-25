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

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.mechanics.Ballistica;
import pd.messages.InlineText;
import pd.mechanics.pathfind.PathFinder;
import render.noosa.audio.Sample;

/**
 * SPSEXPD: 烟幕法杖（盗贼向）。
 *
 * <p>把盗贼护甲技能「烟幕弹」做成法杖：在目标处炸开一团浓烟，范围内的敌人失明，
 * 施法者自身同时获得短暂隐形，方便脱离战斗。护甲技能本体保留不动。
 */
public class WandOfSmokeBomb extends Wand {

	static {
		InlineText.of(WandOfSmokeBomb.class)
				.t("name", "烟幕法杖")
				.t("staff_name", "烟幕魔杖")
				.t("desc", "杖头裹着一层永远散不掉的灰布，轻轻一抖就落下细白的灰。它喷出的烟会把光、声音和别人对你的注意力一并吞掉。")
				.t("typical_stats_desc", "这根法杖会在目标处炸开烟幕，使范围内的敌人_失明_，并让你自己短暂_隐形_。")
				.t("stats_desc", "这根法杖会在目标处炸开烟幕，使范围内的敌人_失明 %1$d 回合_，并让你自己_隐形 %2$d 回合_。");
	}

	{
		image = SpecificPlaceHolderDict.WAND_HOLDER_0;
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char user = curUser;
		if (user == null) return;

		int pos = bolt.collisionPos;
		Sample.INSTANCE.play(Assets.Sounds.PUFF);
		CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 12);

		int blindTurns = 6 + buffedLvl();
		for (int i : PathFinder.NEIGHBOURS9) {
			Char ch = Actor.findChar(pos + i);
			if (ch == null || !ch.isAlive()) continue;
			if (ch.alignment == user.alignment) continue;
			Buff.prolong(ch, Blindness.class, blindTurns);
			if (ch instanceof pd.actors.mobs.Mob
					&& ((pd.actors.mobs.Mob) ch).state == ((pd.actors.mobs.Mob) ch).HUNTING) {
				((pd.actors.mobs.Mob) ch).state = ((pd.actors.mobs.Mob) ch).WANDERING;
			}
		}

		int invisTurns = 2 + buffedLvl() / 2;
		Buff.affect(user, Invisibility.class, invisTurns);

		CellEmitter.get(user.pos).burst(Speck.factory(Speck.WOOL), 8);
		Dungeon.observe();
		pd.scenes.GameScene.updateFog();
	}

	@Override
	public void onHit(pd.items.equipment.weapon.melee.MagesStaff staff, Char attacker, Char defender, int damage) {
		//近战不附带额外效果
	}
}
