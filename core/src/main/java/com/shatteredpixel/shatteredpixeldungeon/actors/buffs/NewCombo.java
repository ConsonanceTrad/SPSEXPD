/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class NewCombo extends Buff implements ActionIndicator.Action {

	private static final String COUNT = "count", TIME = "combotime", MISSES = "misses";
	private int count;
	private float comboTime;
	private int misses;

	{ type = buffType.POSITIVE; }

	@Override public int icon() { return BuffIndicator.COMBO; }
	@Override public String iconTextDisplay() { return Integer.toString(count); }

	public void hit() {
		count++;
		comboTime = 4f;
		misses = 0;
		if (count >= 2) ActionIndicator.setAction(this);
		BuffIndicator.refreshHero();
	}

	public void miss() {
		misses++;
		comboTime = 4f;
		if (misses >= 2) detach();
	}

	public int count() { return count; }
	public int misses() { return misses; }
	public boolean finisherReady() { return count >= 2; }

	@Override public boolean act() {
		comboTime -= TICK;
		spend(TICK);
		if (comboTime <= 0) detach();
		return true;
	}

	@Override public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}

	@Override public String desc() {
		String desc = Messages.get(this, "desc");
		if (count >= 8) return desc + "\n\n" + Messages.get(this, "crush_desc");
		if (count >= 6) return desc + "\n\n" + Messages.get(this, "slam_desc");
		if (count >= 4) return desc + "\n\n" + Messages.get(this, "cleave_desc");
		if (count >= 2) return desc + "\n\n" + Messages.get(this, "clobber_desc");
		return desc;
	}

	@Override public String actionName() { return Messages.get(this, "name"); }
	@Override public int actionIcon() { return HeroIcon.COMBO; }
	@Override public int indicatorColor() {
		if (count >= 8) return 0xFFCC00;
		if (count >= 6) return 0xFFFF00;
		if (count >= 4) return 0xCCFF00;
		return 0x00FF00;
	}
	@Override public void doAction() { GameScene.selectCell(finisher); }

	public boolean finish(Hero hero, Char enemy) {
		if (hero == null || enemy == null || enemy == hero || !finisherReady()
				|| Dungeon.level == null || !hero.canAttack(enemy) || hero.isCharmedBy(enemy)) return false;
		Finisher type = finisher();
		int damage = hero.damageRoll();
		if (type == Finisher.SLAM) damage = Math.max(damage, hero.damageRoll());
		else if (type == Finisher.CRUSH) for (int i = 1; i < 4; i++) damage = Math.max(damage, hero.damageRoll());
		switch (type) {
			case CLOBBER: damage = Math.round(damage * 1.6f); break;
			case CLEAVE: damage = Math.round(damage * 2.5f); break;
			case SLAM: damage = Math.round(damage * 2.6f); break;
			case CRUSH: damage = Math.round(damage * 3.5f); break;
		}
		damage -= Random.IntRange(0, Math.max(0, enemy.drRoll()));
		damage = hero.attackProc(enemy, Math.max(0, damage));
		damage = enemy.defenseProc(hero, damage);
		enemy.damage(Math.max(0, damage), this);

		if (type == Finisher.CLOBBER && enemy.isAlive()) {
			pushAway(hero, enemy);
			Buff.prolong(enemy, Vertigo.class, Random.NormalIntRange(1, 4));
		} else if (type == Finisher.SLAM) {
			Buff.affect(hero, ShieldArmor.class).level(Math.max(0, damage / 5));
		}

		if (hero.buff(FireImbue.class) != null) hero.buff(FireImbue.class).proc(enemy);
		if (hero.buff(EarthImbue.class) != null) hero.buff(EarthImbue.class).proc(enemy);
		if (hero.buff(FrostImbue.class) != null) hero.buff(FrostImbue.class).proc(enemy);
		if (hero.buff(BloodImbue.class) != null) hero.buff(BloodImbue.class).proc(enemy);

		if (type == Finisher.CLEAVE && !enemy.isAlive()) {
			hit();
			comboTime = 10f;
		} else {
			detach();
		}
		hero.spendAndNext(hero.attackDelay());
		return true;
	}

	private static void pushAway(Hero hero, Char enemy) {
		int direction = enemy.pos - hero.pos;
		for (int offset : PathFinder.NEIGHBOURS8) {
			if (direction != offset) continue;
			int destination = enemy.pos + offset;
			if (Dungeon.level.insideMap(destination)
					&& (Dungeon.level.passable[destination] || Dungeon.level.avoid[destination])
					&& Actor.findChar(destination) == null) {
				int origin = enemy.pos;
				enemy.pos = destination;
				Dungeon.level.occupyCell(enemy);
				Actor.addDelayed(new Pushing(enemy, origin, destination), -1f);
			}
			break;
		}
	}

	private Finisher finisher() {
		if (count >= 8) return Finisher.CRUSH;
		if (count >= 6) return Finisher.SLAM;
		if (count >= 4) return Finisher.CLEAVE;
		return Finisher.CLOBBER;
	}

	private enum Finisher { CLOBBER, CLEAVE, SLAM, CRUSH }

	private final CellSelector.Listener finisher = new CellSelector.Listener() {
		@Override public void onSelect(Integer cell) {
			if (cell == null) return;
			if (!finish((Hero)target, Actor.findChar(cell))) GLog.w(Messages.get(NewCombo.class, "bad_target"));
		}
		@Override public String prompt() {
			if (count >= 8) return Messages.get(NewCombo.class, "crush_prompt");
			if (count >= 6) return Messages.get(NewCombo.class, "slam_prompt");
			if (count >= 4) return Messages.get(NewCombo.class, "cleave_prompt");
			return Messages.get(NewCombo.class, "clobber_prompt");
		}
	};

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COUNT, count);
		bundle.put(TIME, comboTime);
		bundle.put(MISSES, misses);
	}

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		count = Math.max(0, bundle.getInt(COUNT));
		comboTime = Math.max(0, bundle.getFloat(TIME));
		misses = Math.max(0, bundle.getInt(MISSES));
		if (count >= 2) ActionIndicator.setAction(this);
	}
}
