/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Pet collection adapted from Special Surprise Pixel Dungeon.
 * Distributed under the GNU General Public License v3 or later.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.FusionPet;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;
import java.util.Collections;

public class PetCompendium extends Item {

	public static final String AC_CALL = "CALL";
	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_FEED = "FEED";

	private int selectedType;
	private int petID = -1;
	private int lastSummonDepth = -1;
	private int lastSummonBranch = -1;

	{
		image = ItemSpriteSheet.ARTIFACT_ROSE1;
		defaultAction = AC_CALL;
		unique = true;
		keptThoughLostInvent = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = new ArrayList<>();
		actions.add(AC_CALL);
		actions.add(AC_CHOOSE);
		if (findPet() != null) actions.add(AC_FEED);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		curUser = hero;
		curItem = this;
		if (AC_CALL.equals(action)) callPet(hero);
		else if (AC_CHOOSE.equals(action)) chooseFamily();
		else if (AC_FEED.equals(action)) GameScene.selectItem(foodSelector);
	}

	private void chooseFamily() {
		GameScene.show(new WndOptions(Messages.titleCase(name()), Messages.get(this, "choose_family"),
				Messages.get(this, "family_0"), Messages.get(this, "family_1"),
				Messages.get(this, "family_2"), Messages.get(this, "family_3")) {
			@Override
			protected void onSelect(int index) {
				chooseType(index * 4);
			}
		});
	}

	private void chooseType(final int first) {
		String[] options = new String[4];
		for (int i = 0; i < options.length; i++) {
			options[i] = Messages.get(FusionPet.class, "name_" + (first + i));
		}
		GameScene.show(new WndOptions(Messages.titleCase(name()), Messages.get(this, "choose_type"), options) {
			@Override
			protected void onSelect(int index) {
				selectedType = first + index;
				GLog.p(Messages.get(PetCompendium.class, "selected",
						Messages.get(FusionPet.class, "name_" + selectedType)));
			}
		});
	}

	private void callPet(Hero hero) {
		FusionPet pet = findPet();
		int cell = adjacentCell(hero);
		if (cell < 0) {
			GLog.w(Messages.get(this, "no_space"));
			return;
		}
		if (pet != null) {
			ScrollOfTeleportation.appear(pet, cell);
			pet.followHero();
			GLog.i(Messages.get(this, "recalled", pet.name()));
			return;
		}
		if (lastSummonDepth == Dungeon.depth && lastSummonBranch == Dungeon.branch) {
			GLog.w(Messages.get(this, "spent"));
			return;
		}

		pet = new FusionPet().configure(selectedType);
		pet.pos = cell;
		petID = pet.id();
		lastSummonDepth = Dungeon.depth;
		lastSummonBranch = Dungeon.branch;
		GameScene.add(pet, 1f);
		Dungeon.level.occupyCell(pet);
		hero.spendAndNext(1f);
		GLog.p(Messages.get(this, "called", pet.name()));
	}

	private int adjacentCell(Hero hero) {
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = hero.pos + offset;
			if (cell >= 0 && cell < Dungeon.level.length() && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null) return cell;
		}
		return -1;
	}

	private FusionPet findPet() {
		if (petID >= 0 && Actor.findById(petID) instanceof FusionPet) {
			return (FusionPet)Actor.findById(petID);
		}
		if (Dungeon.level != null) {
			for (Mob mob : Dungeon.level.mobs) {
				if (mob instanceof FusionPet && mob.isAlive()) {
					petID = mob.id();
					return (FusionPet)mob;
				}
			}
		}
		petID = -1;
		return null;
	}

	private final WndBag.ItemSelector foodSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(PetCompendium.class, "feed_prompt");
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return null;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Food;
		}

		@Override
		public void onSelect(Item item) {
			FusionPet pet = findPet();
			if (item instanceof Food && pet != null) {
				int healed = pet.feed();
				if (healed <= 0) {
					GLog.i(Messages.get(PetCompendium.class, "full"));
					return;
				}
				item.detach(Dungeon.hero.belongings.backpack);
				Dungeon.hero.spendAndNext(1f);
				GLog.p(Messages.get(PetCompendium.class, "fed", pet.name(), healed));
			}
		}
	};

	public static PetCompendium ensureFor(Hero hero) {
		PetCompendium item = hero.belongings.getItem(PetCompendium.class);
		if (item != null) return item;
		item = new PetCompendium();
		if (!item.collect(hero.belongings.backpack)) {
			hero.belongings.backpack.items.add(item);
			Collections.sort(hero.belongings.backpack.items, Item.itemComparator);
			updateQuickslot();
		}
		GLog.p(Messages.get(PetCompendium.class, "obtained"));
		return item;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Messages.get(FusionPet.class, "name_" + selectedType));
	}

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
		return 0;
	}

	private static final String SELECTED = "selected_type";
	private static final String PET_ID = "pet_id";
	private static final String SUMMON_DEPTH = "summon_depth";
	private static final String SUMMON_BRANCH = "summon_branch";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SELECTED, selectedType);
		bundle.put(PET_ID, petID);
		bundle.put(SUMMON_DEPTH, lastSummonDepth);
		bundle.put(SUMMON_BRANCH, lastSummonBranch);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		selectedType = Math.max(0, Math.min(FusionPet.TYPE_COUNT - 1, bundle.getInt(SELECTED)));
		petID = bundle.contains(PET_ID) ? bundle.getInt(PET_ID) : -1;
		lastSummonDepth = bundle.contains(SUMMON_DEPTH) ? bundle.getInt(SUMMON_DEPTH) : -1;
		lastSummonBranch = bundle.contains(SUMMON_BRANCH) ? bundle.getInt(SUMMON_BRANCH) : -1;
	}
}
