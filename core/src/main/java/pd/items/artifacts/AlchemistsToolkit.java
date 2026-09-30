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

package pd.items.artifacts;

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.effects.particles.ElmoParticle;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.bags.Bag;
import pd.items.potions.Potion;
import pd.items.potions.PotionOfExperience;
import pd.items.potions.PotionOfMight;
import pd.items.potions.PotionOfOverHealing;
import pd.items.potions.PotionOfStrength;
import pd.messages.Messages;
import pd.scenes.AlchemyScene;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.Bundle;
import render.utils.Random;

import java.util.ArrayList;

/** SPS-PD 0.9.8's potion-order toolkit. */
public class AlchemistsToolkit extends Artifact {

	public static final String AC_BREW = "BREW";
	public static final String AC_CREATE = "CREATE";
	public static final String AC_COOKING = "COOKING";

	public final ArrayList<Class<?>> combination = new ArrayList<>();
	public ArrayList<Class<?>> curGuess = new ArrayList<>();
	public ArrayList<Class<?>> bstGuess = new ArrayList<>();

	public int numWrongPlace;
	public int numRight;
	private int seedsToPotion;

	{
		image = ItemSpriteSheet.ARTIFACT_TOOLKIT;
		level(0);
		levelCap = 10;
		defaultAction = AC_BREW;
		charge = 0;
		partialCharge = 0;
	}

	public AlchemistsToolkit() {
		Generator.Category potions = Generator.Category.POTION;
		int attempts = 0;
		while (combination.size() < 3 && attempts++ < 256) {
			int index = Random.chances(potions.probs);
			if (index < 0 && potions.defaultProbs != null) index = Random.chances(potions.defaultProbs);
			if (index < 0 || index >= potions.classes.length) break;
			Class<?> potion = potions.classes[index];
			if (validCombinationPotion(potion) && !combination.contains(potion)) combination.add(potion);
		}
		if (combination.size() < 3) {
			for (Class<?> potion : potions.classes) {
				if (validCombinationPotion(potion) && !combination.contains(potion)) combination.add(potion);
				if (combination.size() == 3) break;
			}
		}
	}

	private static boolean validCombinationPotion(Class<?> potion) {
		return potion != PotionOfExperience.class && potion != PotionOfOverHealing.class
				&& potion != PotionOfStrength.class && potion != PotionOfMight.class;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_COOKING);
		if (isEquipped(hero) && level() < levelCap && !cursed) actions.add(AC_BREW);
		if (level() > 0 && !isEquipped(hero)) actions.add(AC_CREATE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_COOKING.equals(action) && !AC_BREW.equals(action) && !AC_CREATE.equals(action)) {
			super.execute(hero, action);
			return;
		}
		setCurrent(hero);
		if (AC_COOKING.equals(action)) {
			AlchemyScene.clearToolkit();
			Game.switchScene(AlchemyScene.class);
		} else if (AC_BREW.equals(action)) {
			GameScene.selectItem(itemSelector);
		} else if (AC_CREATE.equals(action)) {
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
			hero.spendAndNext(1f);
			if (Dungeon.level != null) {
				for (int i = 0; i < level(); i++) {
					Heap heap = Dungeon.level.drop(creationItem(), hero.pos);
					if (heap.sprite != null) heap.sprite.drop();
				}
			}
			detach(hero.belongings.backpack);
		}
	}

	protected Item creationItem() {
		return Generator.random();
	}

	public void guessBrew() {
		if (curGuess.size() != 3) return;

		int wrongPlace = 0;
		int right = 0;
		for (Class<?> potion : curGuess) {
			if (combination.contains(potion)) {
				if (curGuess.indexOf(potion) == combination.indexOf(potion)) right++;
				else wrongPlace++;
			}
		}

		int score = right * 3 + wrongPlace;
		if (score == 9) score = 10;
		if (score == 0) {
			GLog.i(Messages.get(this, "waste"));
		} else if (score > level()) {
			level(score);
			seedsToPotion = 0;
			bstGuess = curGuess;
			numRight = right;
			numWrongPlace = wrongPlace;
			if (level() == 10) {
				bstGuess = new ArrayList<>();
				GLog.p(Messages.get(this, "prefect"));
			} else {
				GLog.w(brewDesc(wrongPlace, right) + Messages.get(this, "bestbrew"));
			}
		} else {
			GLog.w(brewDesc(wrongPlace, right) + Messages.get(this, "waste"));
		}
		curGuess = new ArrayList<>();
	}

	private String brewDesc(int wrongPlace, int right) {
		String result = "";
		if (wrongPlace > 0) result += wrongPlace + Messages.get(this, "bdorder");
		if (right > 0) result += right + Messages.get(this, "right");
		return result;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new alchemy();
	}

	@Override
	public String desc() {
		String result = Messages.get(this, "desc");
		if (Dungeon.hero != null && isEquipped(Dungeon.hero) && cursed) {
			result += "\n\n" + Messages.get(this, "desc_cursed");
		}
		if (level() == 0) {
			result += "\n\n" + Messages.get(this, "level_zero");
		} else if (level() == 10) {
			result += "\n\n" + Messages.get(this, "level_ten");
		} else if (!bstGuess.isEmpty()) {
			result += "\n\n" + Messages.get(this, "make_from")
					+ Messages.get(bstGuess.get(0), "name") + ", "
					+ Messages.get(bstGuess.get(1), "name") + ", "
					+ Messages.get(bstGuess.get(2), "name") + ", "
					+ brewDesc(numWrongPlace, numRight);
		} else {
			result += Messages.get(this, "need_fix");
		}
		return result;
	}

	private static final String COMBINATION = "combination";
	private static final String CUR_GUESS = "curguess";
	private static final String BEST_GUESS = "bstguess";
	private static final String NUM_WRONG_PLACE = "numwrongplace";
	private static final String NUM_RIGHT = "numright";
	private static final String SEEDS_TO_POTION = "seedstopotion";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(NUM_WRONG_PLACE, numWrongPlace);
		bundle.put(NUM_RIGHT, numRight);
		bundle.put(SEEDS_TO_POTION, seedsToPotion);
		bundle.put(COMBINATION, combination.toArray(new Class<?>[0]));
		bundle.put(CUR_GUESS, curGuess.toArray(new Class<?>[0]));
		bundle.put(BEST_GUESS, bstGuess.toArray(new Class<?>[0]));
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		numWrongPlace = bundle.getInt(NUM_WRONG_PLACE);
		numRight = bundle.getInt(NUM_RIGHT);
		seedsToPotion = bundle.getInt(SEEDS_TO_POTION);
		if (bundle.contains(COMBINATION)) {
			combination.clear();
			for (Class<?> type : bundle.getClassArray(COMBINATION)) combination.add(type);
			curGuess.clear();
			for (Class<?> type : bundle.getClassArray(CUR_GUESS)) curGuess.add(type);
			bstGuess.clear();
			for (Class<?> type : bundle.getClassArray(BEST_GUESS)) bstGuess.add(type);
		}
		charge = 0;
		partialCharge = 0;
	}

	public class alchemy extends ArtifactBuff {
		public boolean tryCook(int count) {
			if (seedsToPotion == 0) {
				if (Random.Int(20) < 10 + level()) {
					seedsToPotion = Random.Int(20) < level() ? 1 : 2;
				} else {
					seedsToPotion = 3;
				}
			}
			if (count >= seedsToPotion) {
				seedsToPotion = 0;
				return true;
			}
			return false;
		}
	}

	// Compatibility for the current alchemy scene and hidden Shattered class spell.
	// SPS does not generate artifact energy; the toolkit only expands ingredient slots.
	public int availableEnergy() {
		return 0;
	}

	public int consumeEnergy(int amount) {
		return amount;
	}

	protected static final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(AlchemistsToolkit.class, "prompt");
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return null;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Potion;
		}

		@Override
		public void onSelect(Item item) {
			if (!(item instanceof Potion) || !(curItem instanceof AlchemistsToolkit)) return;
			AlchemistsToolkit toolkit = (AlchemistsToolkit) curItem;
			Potion potion = (Potion) item;
			if (!potion.isIdentified()) {
				GLog.w(Messages.get(AlchemistsToolkit.class, "know_first"));
			} else if (toolkit.curGuess.contains(potion.getClass())) {
				GLog.w(Messages.get(AlchemistsToolkit.class, "have_add"));
			} else {
				Hero hero = Dungeon.hero;
				if (hero == null) return;
				if (hero.sprite != null) hero.sprite.operate(hero.pos);
				hero.busy();
				hero.spend(1f);
				Sample.INSTANCE.play(Assets.Sounds.DRINK);
				toolkit.curGuess.add(potion.getClass());
				if (toolkit.curGuess.size() == 3) toolkit.guessBrew();
				else GLog.i(Messages.get(AlchemistsToolkit.class, "addpotion"));
			}
		}
	};
}
