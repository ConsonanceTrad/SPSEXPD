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

package pd.items.equipment.weapon.missiles;

import java.util.ArrayList;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionForm;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

/**
 * SPSEXPD: 投掷注射针——安培的专用投射器（docs/alchemy-rework.md §4）。
 *
 * <p>安培本身不能使用：只有带着注射针投掷时，才会把背包里的一支安培注入目标体内。
 * 允许投向自己（给自己打针），此时按药水的饮用效果结算。
 */
public class ThrowingSyringe extends Item {

	/** SPSEXPD: 动作常量（项目惯例：各类自行声明）。 */
	public static final String AC_STAB = "STAB";

	static {
		InlineText.of(ThrowingSyringe.class)
			.t("name", "投掷注射针")
			.t("desc", "一支带配重的空心针。装上安培后掷向目标即可把药液注入体内，也可以扎在自己身上。")
			.t("no_ampoule", "你没有可用的安培。")
			.t("need_target", "需要瞄准一个目标（可以是你自己）。");
	}

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
		defaultAction = AC_THROW;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		return actions;
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
		return 15 * quantity;
	}

	/** 携带中的一支安培（找不到返回 null）。 */
	public static Potion findAmpoule(Hero hero) {
		for (Item item : hero.belongings.backpack.items) {
			if (item instanceof Potion && ((Potion) item).form == PotionForm.AMPOULE) {
				return (Potion) item;
			}
			if (item instanceof pd.items.equipment.bags.Bag) {
				for (Item inner : ((pd.items.equipment.bags.Bag) item).items) {
					if (inner instanceof Potion && ((Potion) inner).form == PotionForm.AMPOULE) {
						return (Potion) inner;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected void onThrow(int cell) {
		Hero hero = Dungeon.hero;
		Char target = Actor.findChar(cell);

		Potion ampoule = hero == null ? null : findAmpoule(hero);
		if (ampoule == null) {
			GLog.w(Messages.get(ThrowingSyringe.class, "no_ampoule"));
			super.onThrow(cell);
			return;
		}
		if (target == null) {
			GLog.w(Messages.get(ThrowingSyringe.class, "need_target"));
			super.onThrow(cell);
			return;
		}
		if (target != hero) {
			//SPSEXPD: 药水效果只对英雄结算，因此注射针目前只能扎自己
			GLog.w(Messages.get(ThrowingSyringe.class, "need_target"));
			super.onThrow(cell);
			return;
		}

		ampoule.detachAll(hero.belongings.backpack);
		if (curUser != null) curUser.sprite.zap(cell);
		ampoule.apply(hero);
	}

	@Override
	public String name() {
		return Messages.get(ThrowingSyringe.class, "name");
	}
}
