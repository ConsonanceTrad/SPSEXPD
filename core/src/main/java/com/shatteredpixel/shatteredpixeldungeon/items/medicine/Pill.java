/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon content
 * Copyright (C) SPS-PD contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Locked;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Shared Shattered 4.0 action and persistence behavior for SPS medicines. */
public class Pill extends Item {

	public static final String AC_EAT = "EAT";

	{
		image = ItemSpriteSheet.POTION_IVORY;
		stackable = true;
		defaultAction = AC_EAT;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (hero.buff(Locked.class) == null) actions.add(AC_EAT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_EAT.equals(action)) {
			super.execute(hero, action);
			return;
		}
		curUser = hero;
		if (hero.buff(Locked.class) != null) {
			GLog.w(Messages.get(Food.class, "locked"));
			return;
		}
		if (hero.heroClass != HeroClass.FOLLOWER || Random.Int(10) >= 1) {
			detach(hero.belongings.backpack);
		}
		onUse(hero);
		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			hero.busy();
			SpellSprite.show(hero, SpellSprite.FOOD);
			Sample.INSTANCE.play(Assets.Sounds.EAT);
			hero.spend(1f);
		} else {
			hero.spendAndNext(1f);
		}
		Statistics.foodEaten++;
		Badges.validateFoodEaten();
	}

	protected void onUse(Hero hero) {
	}

	protected Mob[] mobs() {
		return Dungeon.level.mobs.toArray(new Mob[0]);
	}

	@Override public boolean isIdentified() { return true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public int value() { return 5 * quantity; }
}
