/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.Pushing;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.ui.ActionIndicator;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import watabou.utils.Bundle;
import watabou.utils.PathFinder;
import watabou.utils.Random;

public class BunnyCombo extends Buff implements ActionIndicator.Action {

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
	public float comboTime() { return comboTime; }
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
		if (count >= 10) return desc + "\n\n" + Messages.get(this, "e_desc");
		if (count >= 8) return desc + "\n\n" + Messages.get(this, "d_desc");
		if (count >= 6) return desc + "\n\n" + Messages.get(this, "c_desc");
		if (count >= 4) return desc + "\n\n" + Messages.get(this, "b_desc");
		if (count >= 2) return desc + "\n\n" + Messages.get(this, "a_desc");
		return desc;
	}

	@Override public String actionName() { return Messages.get(this, "name"); }
	@Override public int actionIcon() { return HeroIcon.COMBO; }
	@Override public int indicatorColor() {
		if (count >= 10) return 0x000000;
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
		if (type == Finisher.FINISH) damage = Math.round(damage * (count / 10 + 1));
		damage -= Random.IntRange(0, Math.max(0, enemy.drRoll()));
		damage = hero.attackProc(enemy, Math.max(0, damage));
		damage = enemy.defenseProc(hero, damage);
		enemy.damage(Math.max(0, damage), this);

		switch (type) {
			case CRIPPLE:
				Buff.prolong(enemy, Cripple.class, 5f);
				break;
			case BLIND:
				Buff.prolong(enemy, Blindness.class, 5f);
				break;
			case BLEED:
				Buff.affect(enemy, Bleeding.class).set(Math.max(0, damage));
				break;
			case IMPACT:
				if (enemy.isAlive()) {
					pushAway(hero, enemy);
					Buff.prolong(enemy, Vertigo.class, Random.NormalIntRange(3, 5));
				}
				break;
			case FINISH:
				break;
		}

		if (hero.buff(FireImbue.class) != null) hero.buff(FireImbue.class).proc(enemy);
		if (hero.buff(EarthImbue.class) != null) hero.buff(EarthImbue.class).proc(enemy);
		if (hero.buff(FrostImbue.class) != null) hero.buff(FrostImbue.class).proc(enemy);
		if (hero.buff(BloodImbue.class) != null) hero.buff(BloodImbue.class).proc(enemy);

		if (type == Finisher.FINISH) detach();
		else hit();
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
		if (count >= 10) return Finisher.FINISH;
		if (count >= 8) return Finisher.IMPACT;
		if (count >= 6) return Finisher.BLEED;
		if (count >= 4) return Finisher.BLIND;
		return Finisher.CRIPPLE;
	}

	private enum Finisher { CRIPPLE, BLIND, BLEED, IMPACT, FINISH }

	private final CellSelector.Listener finisher = new CellSelector.Listener() {
		@Override public void onSelect(Integer cell) {
			if (cell == null) return;
			if (!finish((Hero)target, Actor.findChar(cell))) GLog.w(Messages.get(BunnyCombo.class, "bad_target"));
		}
		@Override public String prompt() {
			if (count >= 10) return Messages.get(BunnyCombo.class, "e_prompt");
			if (count >= 8) return Messages.get(BunnyCombo.class, "d_prompt");
			if (count >= 6) return Messages.get(BunnyCombo.class, "c_prompt");
			if (count >= 4) return Messages.get(BunnyCombo.class, "b_prompt");
			return Messages.get(BunnyCombo.class, "a_prompt");
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
