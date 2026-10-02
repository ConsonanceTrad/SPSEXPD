/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items;

import pd.atlas.items.EquipmentEquipArmorUniqueArmorDict;

import pd.Dungeon;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Ooze;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Towel extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Towel.class)
			.t("name", "湿巾")
			.t("desc", "湿巾是一项十分便利的发明。它虽然使用次数有限，但可以清除多种异常状态。")
			.t("ac_towel", "使用")
			.t("apply", "你使用了湿巾。")
			.t("end", "这块湿巾没法再用了。");
	}


	public static final String AC_TOWEL = "TOWEL";

	{
		image = EquipmentEquipArmorUniqueArmorDict.TOWEL;
		level(20);
		defaultAction = AC_TOWEL;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_TOWEL);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_TOWEL.equals(action)) {
			super.execute(hero, action);
			return;
		}
		Buff.detach(hero, Bleeding.class);
		Buff.detach(hero, Ooze.class);
		Buff.detach(hero, Burning.class);
		Buff.detach(hero, Chill.class);
		Buff.detach(hero, Frost.class);
		GLog.i(Messages.get(this, "apply"));
		level(level() - 1);
		if (level() <= 0) {
			detach(Dungeon.hero.belongings.backpack);
			GLog.w(Messages.get(this, "end"));
		}
		hero.spendAndNext(1f);
		updateQuickslot();
	}

	@Override
	public int value() {
		return 500 * quantity;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
}
