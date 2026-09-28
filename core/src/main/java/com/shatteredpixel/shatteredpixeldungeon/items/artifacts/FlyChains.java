/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Locked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

/**
 * SPS-PD's alternate chains. Pulling uses Shattered's hardened chain implementation,
 * while charge, levelling and the exhausting seal retain the 0.9.8 rules.
 */
public class FlyChains extends EtherealChains {

	public static final String AC_LOCKED = "LOCKED";

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && level() > 1 && !cursed) actions.add(AC_LOCKED);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_LOCKED.equals(action)) {
			super.execute(hero, action);
			return;
		}
		curUser = hero;
		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			usesTargeting = false;
		} else if (charge < 1) {
			GLog.i(Messages.get(this, "no_charge"));
			usesTargeting = false;
		} else if (cursed) {
			GLog.w(Messages.get(this, "cursed"));
			usesTargeting = false;
		} else if (level() > 1) {
			usesTargeting = true;
			GameScene.selectCell(locker);
		}
	}

	public final CellSelector.Listener locker = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null || Dungeon.level == null || !Dungeon.level.insideMap(target)
					|| !(Dungeon.level.visited[target] || Dungeon.level.mapped[target])) return;
			Char affected = Actor.findChar(target);
			if (affected == null) {
				GLog.i(Messages.get(FlyChains.class, "nothing_to_grab"));
				return;
			}
			if (!sealTarget(affected)) return;
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			if (curUser != null && curUser.sprite != null) {
				curUser.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
			}
			if (curUser != null) curUser.spendAndNext(1f);
		}

		@Override public String prompt() { return Messages.get(FlyChains.class, "prompt"); }
	};

	boolean sealTarget(Char target) {
		if (target == null || level() <= 1 || cursed || charge < 1) return false;
		float duration = level() * 4f;
		Buff.affect(target, Locked.class, duration);
		Buff.affect(target, Silent.class, duration);
		Buff.affect(target, AttackDown.class, duration).level(90);
		Buff.affect(target, Slow.class, duration);
		level(level() - 1);
		updateQuickslot();
		return true;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new chainsRecharge2();
	}

	@Override
	public void charge(Hero target, float amount) {
		// The 0.9.8 FlyChains only gains energy from its own recharge and kill experience.
	}

	public int charge() { return charge; }
	public int experience() { return exp; }
	public int chargeTarget() { return 5 + level() * 2; }

	void advanceRecharge() {
		int target = chargeTarget();
		if (charge < target && !cursed) {
			partialCharge += 1f / (40f - (target - charge) * 2f);
		}
		if (partialCharge >= 1f) {
			partialCharge--;
			charge++;
		}
	}

	public class chainsRecharge2 extends ArtifactBuff {
		@Override
		public boolean act() {
			if (charge < chargeTarget() && !cursed) {
				advanceRecharge();
			} else if (cursed && Random.Int(100) == 0) {
				Buff.prolong(target, Cripple.class, 10f);
			}
			updateQuickslot();
			spend(TICK);
			return true;
		}

		public void gainExp(float levelPortion) {
			if (cursed || levelPortion == 0) return;
			exp += Math.round(levelPortion * 100);
			if (charge > chargeTarget()) levelPortion *= chargeTarget() / (float)charge;
			partialCharge += levelPortion * 10f;
			int threshold = 100 + level() * 50;
			if (exp > threshold && level() < levelCap) {
				exp -= threshold;
				GLog.p(Messages.get(this, "levelup"));
				upgrade();
			}
		}
	}
}
