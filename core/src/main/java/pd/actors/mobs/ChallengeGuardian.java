/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Challenge enemy concept adapted from Special Surprise Pixel Dungeon.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.quest.ChallengeJournal;
import pd.messages.Messages;
import pd.sprites.EyeSprite;
import pd.sprites.GolemSprite;
import pd.sprites.GuardSprite;
import pd.sprites.MonkSprite;
import pd.sprites.RatSprite;
import pd.sprites.ScorpioSprite;
import pd.sprites.SkeletonSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ChallengeGuardian extends Mob {

	private int challenge;
	private boolean guardian;

	{
		EXP = 0;
		maxLvl = -1;
		configure(ChallengeJournal.challengeForBranch(Dungeon.branch), false);
	}

	public ChallengeGuardian configure(int challenge, boolean guardian) {
		this.challenge = Math.max(0, challenge);
		this.guardian = guardian;

		int stage = Math.max(0, (ChallengeJournal.anchorDepth(this.challenge) + 1) / 5 - 1);
		HT = guardian ? 22 + 20 * stage : 8 + 12 * stage;
		if (guardian && this.challenge == 6) HT = Math.round(HT * 1.35f);
		HP = HT;
		defenseSkill = (guardian ? 7 : 5) + 5 * stage;

		if (guardian) properties.add(Property.MINIBOSS);
		else properties.remove(Property.MINIBOSS);
		setSprite();
		return this;
	}

	private void setSprite() {
		switch (challenge) {
			case 0: spriteClass = RatSprite.class; break;
			case 1: spriteClass = SkeletonSprite.class; break;
			case 2: spriteClass = GuardSprite.class; break;
			case 3: spriteClass = MonkSprite.class; break;
			case 4: spriteClass = ScorpioSprite.class; break;
			case 5: spriteClass = GuardSprite.class; break;
			case 6: spriteClass = GolemSprite.class; break;
			case 7: spriteClass = EyeSprite.class; break;
			default: spriteClass = SkeletonSprite.class;
		}
	}

	@Override
	public String name() {
		return Messages.get(this, guardian ? "guardian_name" : "echo_name");
	}

	@Override
	public String description() {
		return Messages.get(this, guardian ? "guardian_desc" : "echo_desc");
	}

	@Override
	public int damageRoll() {
		int stage = Math.max(0, (ChallengeJournal.anchorDepth(challenge) + 1) / 5 - 1);
		int min = (guardian ? 2 : 1) + 3 * stage;
		int max = (guardian ? 8 : 6) + 6 * stage;
		if (guardian && challenge == 6) {
			min = Math.round(min * 1.2f);
			max = Math.round(max * 1.2f);
		}
		return Random.NormalIntRange(min, max);
	}

	@Override
	public int attackSkill(Char target) {
		int stage = Math.max(0, (ChallengeJournal.anchorDepth(challenge) + 1) / 5 - 1);
		return (guardian ? 12 : 10) + 6 * stage;
	}

	@Override
	public int drRoll() {
		int stage = Math.max(0, (ChallengeJournal.anchorDepth(challenge) + 1) / 5 - 1);
		return super.drRoll() + Random.NormalIntRange(0, (guardian ? 3 : 1) + 2 * stage);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
	}

	private static final String CHALLENGE = "challenge";
	private static final String GUARDIAN = "guardian";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHALLENGE, challenge);
		bundle.put(GUARDIAN, guardian);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		challenge = bundle.getInt(CHALLENGE);
		guardian = bundle.getBoolean(GUARDIAN);
		if (guardian) properties.add(Property.MINIBOSS);
		else properties.remove(Property.MINIBOSS);
		setSprite();
	}
}
