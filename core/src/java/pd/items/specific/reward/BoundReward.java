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
 */

package pd.items.specific.reward;

import pd.atlas.items.EquipmentBagsDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Item;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/** Reward dropped when an SPS dew floor is cleared within its par time. */
public class BoundReward extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BoundReward.class)
			.t("name", "清层奖励")
			.t("desc", "在露珠规定时间内消灭本层所有初始敌人获得的奖励。你可以选择一类奖品。")
			.t("ac_weapon", "装备")
			.t("ac_food", "补给")
			.t("ac_potion", "魔法物品");
	}




	public static final String AC_WEAPON = "WEAPON";
	public static final String AC_FOOD = "FOOD";
	public static final String AC_POTION = "POTION";

	private static final float TIME_TO_USE = 1f;

	{
		image = EquipmentBagsDict.BACKPACK_0;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_WEAPON);
		actions.add(AC_FOOD);
		actions.add(AC_POTION);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_WEAPON)) {
			drop(hero, Generator.random(Random.oneOf(
					Generator.Category.MELEEWEAPON,
					Generator.Category.ARMOR,
					Generator.Category.RING,
					Generator.Category.ARTIFACT,
					Generator.Category.WAND)));
			consume(hero);
		} else if (action.equals(AC_FOOD)) {
			for (int i = 0; i < 3; i++) {
				drop(hero, Generator.random(Random.oneOf(
						Generator.Category.HIGHFOOD,
						Generator.Category.FOOD,
						Generator.Category.SEED,
						Generator.Category.VEGETABLE,
						Generator.Category.GOLD)));
			}
			consume(hero);
		} else if (action.equals(AC_POTION)) {
			drop(hero, Generator.random(Random.oneOf(
					Generator.Category.POTION,
					Generator.Category.MEDICINE,
					Generator.Category.BERRY,
					Generator.Category.SCROLL,
					Generator.Category.STONE)));
			drop(hero, Generator.random(Random.oneOf(
					Generator.Category.HIGHFOOD,
					Generator.Category.FOOD,
					Generator.Category.SEED,
					Generator.Category.VEGETABLE,
					Generator.Category.STONE)));
			consume(hero);
		}
	}

	private void drop(Hero hero, Item item) {
		if (item != null) Dungeon.level.drop(item, hero.pos).sprite.drop(hero.pos);
	}

	private void consume(Hero hero) {
		detach(hero.belongings.backpack);
		hero.spend(TIME_TO_USE);
		hero.busy();
		hero.sprite.operate(hero.pos);
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
		return 100 * quantity;
	}
}
