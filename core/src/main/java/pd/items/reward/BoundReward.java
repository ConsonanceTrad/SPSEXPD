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

package pd.items.reward;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Item;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

import java.util.ArrayList;

/** Reward dropped when an SPS dew floor is cleared within its par time. */
public class BoundReward extends Item {

	public static final String AC_WEAPON = "WEAPON";
	public static final String AC_FOOD = "FOOD";
	public static final String AC_POTION = "POTION";

	private static final float TIME_TO_USE = 1f;

	{
		image = ItemSpriteSheet.BACKPACK;
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
