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
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import pd.mechanics.ConeAOE;
import pd.messages.InlineText;
import pd.scenes.PixelScene;
import render.noosa.audio.Sample;
import render.utils.data.Callback;

/**
 * SPSEXPD: 震波法杖（战士向）。
 *
 * <p>把战士护甲技能「震波」的效果做成法杖：沿杖尖方向推出一个锥形冲击波，
 * 命中范围内的敌人会造成伤害并使其残废（移动变慢）。护甲技能本体保留不动。
 */
public class WandOfShockwave extends DamageWand {

	static {
		InlineText.of(WandOfShockwave.class)
				.t("name", "震波法杖")
				.t("staff_name", "震波魔杖")
				.t("desc", "杖身像一段被冲击压实的碎石柱，敲击时会发出沉闷的回响。它推出去的不是火也不是冰，而是一堵墙一样的力气。")
				.t("typical_stats_desc", "这根法杖会沿杖尖方向推出锥形冲击波，对命中的敌人造成_%1$d~%2$d点伤害_并使其_残废_。")
				.t("stats_desc", "这根法杖会沿杖尖方向推出锥形冲击波，对命中的敌人造成_%1$d~%2$d点伤害_并使其_残废_。");
	}

	{
		image = SpecificPlaceHolderDict.WAND_HOLDER_0;
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	@Override
	public int min(int lvl) {
		return 4 + lvl;
	}

	@Override
	public int max(int lvl) {
		return 8 + 2 * lvl;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char user = curUser;
		if (user == null || user.sprite == null) return;

		Ballistica aim = new Ballistica(user.pos, bolt.collisionPos, Ballistica.WONT_STOP);
		int dist = Math.min(aim.dist, 4 + buffedLvl() / 2);
		ConeAOE cone = new ConeAOE(aim, Math.max(1, dist), 50,
				Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

		for (Ballistica ray : cone.outerRays) {
			((MagicMissile) user.sprite.parent.recycle(MagicMissile.class)).reset(
					MagicMissile.FORCE_CONE, user.sprite, ray.path.get(ray.dist), null);
		}

		user.sprite.zap(bolt.collisionPos);
		Sample.INSTANCE.play(Assets.Sounds.BLAST, 1f, 0.5f);
		PixelScene.shake(2, 0.5f);

		MagicMissile.boltFromChar(user.sprite.parent, MagicMissile.FORCE_CONE, user.sprite,
				cone.coreRay.path.get(Math.max(1, dist * 2 / 3)), new Callback() {
					@Override
					public void call() {
						for (int cell : cone.cells) {
							Char ch = Actor.findChar(cell);
							if (ch == null || !ch.isAlive()) continue;
							if (ch.alignment == user.alignment) continue;

							wandProc(ch, chargesPerCast());
							ch.damage(Wand.spellAmplify(damageRoll()), user);
							if (ch.isAlive()) {
								Buff.affect(ch, Cripple.class, 5f);
							}
						}
					}
				});
	}

	@Override
	public void onHit(pd.items.equipment.weapon.melee.MagesStaff staff, Char attacker, Char defender, int damage) {
		//近战不附带额外效果
	}
}
