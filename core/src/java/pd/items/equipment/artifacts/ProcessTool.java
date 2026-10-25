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

package pd.items.equipment.artifacts;

import java.util.ArrayList;

import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.messages.InlineText;
import pd.windows.WndCrafting;
import render.noosa.Game;

/**
 * SPSEXPD: 药水再加工工具（docs/alchemy-rework.md §4）。
 *
 * <p>两种可携带工具共用同一套加工界面（{@link WndCrafting} 的加工模式）：
 * <ul>
 *   <li>{@link Distiller} 蒸馏压片机 —— 果汁→硬糖、药水→药片、药片+盐→缓释片×2</li>
 *   <li>{@link AmpouleFiller} 分装灌液机 —— 药水+水+盐→安培×3</li>
 * </ul>
 * 工具只是「打开对应模式」的载体，加工行为本身不额外消耗工具。
 */
public abstract class ProcessTool extends Item {

	/** SPSEXPD: 本物品的动作常量（项目里动作常量是各类自己声明的）。 */
	public static final String AC_USE = "USE";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_USE;
	}

	/** 打开的加工模式（见 WndCrafting.spsMode）。 */
	protected abstract int mode();

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_USE.equals(action)) {
			//SPSEXPD: 这里不能 hero.busy()——它会让英雄一直处于忙碌状态，
			//打开窗口后所有点击都被吞掉（表现就是「分装器/蒸馏压片机打开后无法操作」）
			curUser = hero;
			final int m = mode();
			Game.runOnRenderThread(() -> WndCrafting.open(m));
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
	public int value() {
		return 120 * quantity;
	}

	/** 蒸馏压片机。 */
	public static class Distiller extends ProcessTool {

		static {
			InlineText.of(Distiller.class)
				.t("name", "蒸馏压片机")
				.t("desc", "一台锃亮的小型蒸馏压片机：可以把药水蒸成浓缩药片，加盐再压成缓释片；也能把魔法果汁熬成硬糖。");
		}

		@Override
		protected int mode() {
			return WndCrafting.MODE_DISTILL;
		}
	}

	/** 分装灌液机。 */
	public static class AmpouleFiller extends ProcessTool {

		static {
			InlineText.of(AmpouleFiller.class)
				.t("name", "分装灌液机")
				.t("desc", "把药水兑水加盐后分装成小安培。安培本身无法使用，需要配合投掷注射针扎进目标体内。");
		}

		@Override
		protected int mode() {
			return WndCrafting.MODE_FILL;
		}
	}
}
