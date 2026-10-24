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

package pd.items.equipment.tools;

import java.util.ArrayList;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.EquipmentNonEquipDict;
import pd.items.Item;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.windows.WndCrafting;

/**
 * SPSEXPD: 菜板——把多份食材直接组合成冷食（不需要火，docs/alchemy-rework.md §8）。
 *
 * <p>与平底锅（{@code MeleePan}）的分工：平底锅负责需要加热的简餐，菜板负责无需加热的组合，
 * 含水配方统一归分装灌液机。
 */
public class CuttingBoard extends Item {

	static {
		InlineText.of(CuttingBoard.class)
				.t("name", "菜板")
				.t("ac_cut", "组合食材")
				.t("desc", "一块结实的木菜板。使用「组合食材」可以把多份食材直接搭配成冷食，不需要生火。\n\n含水分的配方要用分装灌液机，需要加热的菜要用平底煎锅。");
	}

	public static final String AC_CUT = "CUT";

	{
		image = EquipmentNonEquipDict.PURE_DEWDROP_WATER;
		stackable = false;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_CUT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_CUT.equals(action)) {
			WndCrafting.open(WndCrafting.MODE_BOARD);
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public int value() {
		return 30 * quantity;
	}
}
