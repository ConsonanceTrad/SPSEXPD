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
import pd.actors.hero.Hero;
import pd.actors.hero.perks.Perk;
import pd.actors.hero.perks.IntendedTransportation;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.mechanics.Ballistica;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.CellSelector;
import pd.utils.GLog;
import pd.utils.Teleport;
import render.noosa.audio.Sample;

/**
 * SPSEXPD: 混乱传送法杖。
 *
 * <p>把目标（或自己）随机扔到本层的别处。充能只有寻常法杖的一半，因为它的效果不看等级、
 * 只看运气。持有「定向传送」特质时，可以自己挑落点。
 */
public class WandOfChaosTeleport extends Wand {

	static {
		InlineText.of(WandOfChaosTeleport.class)
				.t("name", "混乱传送法杖")
				.t("staff_name", "混乱传送魔杖")
				.t("desc", "杖身缠着说不清方向的螺旋纹路，看久了会头晕。它不制造位移，只是把「此处」和「别处」换了个位置。")
				.t("typical_stats_desc", "这根法杖会把目标随机传送到本层的另一处。充能只有寻常法杖的一半。")
				.t("stats_desc", "这根法杖会把目标随机传送到本层的另一处。充能只有寻常法杖的一半。")
				.t("prompt", "选择要传送的目标")
				.t("no_tele", "传送失败了，目标纹丝不动。")
				.t("tele", "目标在一阵扭曲中消失了。");
	}

	{
		image = SpecificPlaceHolderDict.WAND_HOLDER_0;
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	@Override
	public int initialCharges() {
		//寻常法杖 2~3 点，这里取一半
		return 1;
	}

	@Override
	public int chargesPerLevel() {
		return 0;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target == null) target = curUser;
		if (target == null) return;

		Sample.INSTANCE.play(Assets.Sounds.ZAP);

		if (hasIntendedTransportation() && target != curUser) {
			pickDestinationAndTeleport(target);
			return;
		}

		if (Teleport.teleportChar(target)) {
			GLog.i(Messages.get(this, "tele"));
		} else {
			GLog.w(Messages.get(this, "no_tele"));
		}
		updateQuickslot();
	}

	/** 定向传送特质：让玩家自己挑落点。 */
	private static boolean hasIntendedTransportation() {
		Hero hero = Dungeon.hero;
		if (hero == null || hero.heroPerk == null) return false;
		for (Perk perk : hero.heroPerk.getPerks()) {
			if (perk instanceof IntendedTransportation) return true;
		}
		return false;
	}

	private void pickDestinationAndTeleport(final Char target) {
		GameScene.selectCell(new CellSelector.Listener() {
			@Override
			public void onSelect(Integer cell) {
				if (cell == null) return;
				if (Teleport.teleportToLocation(target, cell)) {
					GLog.i(Messages.get(WandOfChaosTeleport.class, "tele"));
				} else {
					GLog.w(Messages.get(WandOfChaosTeleport.class, "no_tele"));
				}
			}

			@Override
			public String prompt() {
				return Messages.get(WandOfChaosTeleport.class, "prompt");
			}
		});
	}

	@Override
	public void onHit(pd.items.equipment.weapon.melee.MagesStaff staff, Char attacker, Char defender, int damage) {
		//近战不附带额外效果
	}
}
