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
import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Feed;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.effects.SpellSprite;
import pd.effects.particles.ElmoParticle;
import pd.items.Item;
import pd.items.bags.Bag;
import pd.items.food.Food;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import pd.windows.WndBag;
import watabou.noosa.audio.Sample;
import watabou.utils.Bundle;

import java.util.ArrayList;

/** SPS-PD 0.9.8's thirty-level, time-recharging Horn of Plenty. */
public class HornOfPlenty extends Artifact {

	private static final float TIME_TO_EAT = 3f;
	private static final float ENERGY_PER_CHARGE = 40f;
	private static final String OBSOLETE_STORED_ENERGY = "stored";

	public static final String AC_EAT = "EAT";
	public static final String AC_STORE = "STORE";
	public static final String AC_FEED = "FEED";

	{
		image = ItemSpriteSheet.ARTIFACT_HORN1;
		levelCap = 30;
		charge = 0;
		partialCharge = 0;
		chargeCap = 10;
		defaultAction = AC_EAT;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge > 0) actions.add(AC_EAT);
		if (isEquipped(hero) && level() < levelCap && !cursed) actions.add(AC_STORE);
		if (isEquipped(hero) && level() > 0 && !cursed) actions.add(AC_FEED);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_EAT.equals(action) && !AC_STORE.equals(action) && !AC_FEED.equals(action)) {
			super.execute(hero, action);
			return;
		}

		if (AC_EAT.equals(action)) {
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (charge == 0) {
				GLog.i(Messages.get(this, "no_food"));
			} else {
				consumeCharges(hero, charge);
			}
		} else if (AC_STORE.equals(action)) {
			GameScene.selectItem(itemSelector);
		} else if (AC_FEED.equals(action)) {
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (!cursed && level() > 0) {
				Buff.affect(hero, Feed.class, level() * 3f);
				hero.spend(1f);
				hero.busy();
				if (hero.sprite != null) {
					hero.sprite.operate(hero.pos);
					hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
				}
				Sample.INSTANCE.play(Assets.Sounds.BURNING);
				level(0);
				updateQuickslot();
			}
		}
	}

	private void consumeCharges(Hero hero, int amount) {
		int consumed = Math.min(charge, Math.max(0, amount));
		if (consumed == 0) return;

		Buff.affect(hero, Hunger.class).satisfy(ENERGY_PER_CHARGE * consumed);
		if (consumed >= 3) Statistics.foodEaten++;
		charge -= consumed;

		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			SpellSprite.show(hero, SpellSprite.FOOD);
		}
		hero.busy();
		Sample.INSTANCE.play(Assets.Sounds.EAT);
		GLog.i(Messages.get(this, "eat"));
		hero.spend(TIME_TO_EAT);
		Badges.validateFoodEaten();
		updateImage();
		updateQuickslot();
	}

	/** Retained only for hidden Shattered spell compatibility; normal SPS play has no snack action. */
	public void doEatEffect(Hero hero, int chargesToUse) {
		consumeCharges(hero, chargesToUse);
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new hornRecharge();
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero)) {
			if (!cursed && level() < levelCap) {
				desc += "\n\n" + Messages.get(this, "desc_hint");
			} else if (cursed) {
				desc += "\n\n" + Messages.get(this, "desc_cursed");
			}
		}
		return desc;
	}

	public void gainFoodValue(Food food) {
		if (level() >= levelCap) return;
		upgrade(food.hornValue);
		if (level() >= levelCap) {
			level(levelCap);
			GLog.p(Messages.get(this, "maxlevel"));
		} else {
			GLog.p(Messages.get(this, "levelup"));
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		// The obsolete Shattered food-energy field is deliberately not written.
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(OBSOLETE_STORED_ENERGY)) {
			int migratedLevel = level() * 3 + Math.round(bundle.getInt(OBSOLETE_STORED_ENERGY) / 100f);
			level(Math.min(levelCap, migratedLevel));
		}
		updateImage();
	}

	private void updateImage() {
		if (charge == chargeCap) image = ItemSpriteSheet.ARTIFACT_HORN4;
		else if (charge >= 7) image = ItemSpriteSheet.ARTIFACT_HORN3;
		else if (charge >= 3) image = ItemSpriteSheet.ARTIFACT_HORN2;
		else image = ItemSpriteSheet.ARTIFACT_HORN1;
	}

	public class hornRecharge extends ArtifactBuff {
		@Override
		public boolean act() {
			if (charge < chargeCap && !cursed) {
				partialCharge += 0.25f + 0.015f * level();
				if (partialCharge >= 80f) {
					charge++;
					partialCharge -= 80f;
					updateImage();
					if (charge == chargeCap) {
						GLog.p(Messages.get(HornOfPlenty.class, "full"));
						partialCharge = 0;
					}
					updateQuickslot();
				}
			} else {
				partialCharge = 0;
			}
			spend(TICK);
			return true;
		}
	}

	protected static WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(HornOfPlenty.class, "prompt");
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Food;
		}

		@Override
		public void onSelect(Item item) {
			if (!(item instanceof Food) || !(curItem instanceof HornOfPlenty)) return;
			Hero hero = Dungeon.hero;
			if (hero == null) return;
			if (hero.sprite != null) hero.sprite.operate(hero.pos);
			hero.busy();
			hero.spend(TIME_TO_EAT);
			((HornOfPlenty) curItem).gainFoodValue((Food) item);
			item.detach(hero.belongings.backpack);
		}
	};
}
