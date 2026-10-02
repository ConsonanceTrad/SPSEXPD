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

package pd.items.ground.remains;

import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.items.Item;
import pd.journal.Catalog;

import java.util.ArrayList;
import pd.messages.InlineText;

public abstract class RemainsItem extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RemainsItem.class)
			.t("ac_use", "使用")
			.t("discover_hint", "你可从上一局某位英雄的遗骸中找到该物品。");
	}


	{
		bones = false;

		defaultAction = AC_USE;
	}

	public static final String AC_USE =  "USE";

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_USE)){
			hero.sprite.operate(hero.pos);

			Catalog.countUse(getClass());
			doEffect(hero);

			hero.spendAndNext(Actor.TICK);
			detach(hero.belongings.backpack);
		}
	}

	protected abstract void doEffect(Hero hero);

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 50;
	}

	public static RemainsItem get(HeroClass cls){
		switch (cls){
			case WARRIOR: default:
				return new SealShard();
			case MAGE:
				return new BrokenStaff();
			case ROGUE:
				return new CloakScrap();
			case HUNTRESS:
				return new BowFragment();
			case DUELIST:
				return new BrokenHilt();
			case CLERIC:
				return new TornPage();
			case SPELLSWORD:
				return new BrokenStaff();
			case PERFORMER:
				return new CloakScrap();
			case SOLDIER:
				return new BrokenHilt();
			case FOLLOWER:
				return new TornPage();
			case ASCETIC:
				return new BrokenStaff();
		}
	}

}
