/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Map fragment concept adapted from Special Surprise Pixel Dungeon.
 * Distributed under the GNU General Public License v3 or later.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class MapFragment extends Item {

	public static final String AC_ADD = "ADD";
	private int challenge;

	{
		defaultAction = AC_ADD;
		unique = true;
		keptThoughLostInvent = true;
		updateImage();
	}

	public MapFragment forChallenge(int challenge) {
		this.challenge = Math.max(0, Math.min(ChallengeJournal.REGION_CHALLENGE_COUNT - 1, challenge));
		updateImage();
		return this;
	}

	private void updateImage() {
		switch (challenge) {
			case 0: image = ItemSpriteSheet.SEWER_PAGE; break;
			case 1: image = ItemSpriteSheet.PRISON_PAGE; break;
			case 2: image = ItemSpriteSheet.CAVES_PAGE; break;
			case 3: image = ItemSpriteSheet.CITY_PAGE; break;
			case 4: image = ItemSpriteSheet.HALLS_PAGE; break;
		}
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = new ArrayList<>();
		actions.add(AC_ADD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		curUser = hero;
		curItem = this;
		if (!AC_ADD.equals(action)) return;

		ChallengeJournal journal = hero.belongings.getItem(ChallengeJournal.class);
		if (journal == null) {
			GLog.w(Messages.get(this, "missing_book"));
			return;
		}

		if (journal.unlock(challenge)) {
			GLog.p(Messages.get(this, "added", ChallengeJournal.challengeName(challenge)));
		} else {
			GLog.i(Messages.get(this, "already_added"));
		}
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
	}

	@Override
	public String name() {
		return Messages.get(this, "name_" + challenge);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", ChallengeJournal.challengeName(challenge));
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

	private static final String CHALLENGE = "challenge";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHALLENGE, challenge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		challenge = bundle.getInt(CHALLENGE);
		updateImage();
	}
}
