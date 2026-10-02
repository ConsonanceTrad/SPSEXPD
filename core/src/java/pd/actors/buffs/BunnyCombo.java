/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.Pushing;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.ui.ActionIndicator;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class BunnyCombo extends Buff implements ActionIndicator.Action {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BunnyCombo.class)
			.t("name", "兔兔连击")
			.t("bad_target", "目标必须是攻击距离以内的敌人。")
			.t("a_prompt", "选择一个要击残的目标")
			.t("a_desc", "_击残_已经就绪。这一击会使目标致残5回合。")
			.t("b_prompt", "选择一个要致盲的目标")
			.t("b_desc", "_抛沙_已经就绪。这一击会使目标致盲5回合。")
			.t("c_prompt", "选择一个要割裂的目标")
			.t("c_desc", "_割裂_已经就绪。这一击会使目标流血。")
			.t("d_prompt", "选择一个要击退的目标")
			.t("d_desc", "_冲击_已经就绪。这一击会击退目标并使其眩晕。")
			.t("e_prompt", "选择一个要终结的目标")
			.t("e_desc", "_终结_已经就绪。这一击会按照累积连击数倍增伤害，并重置连击。")
			.t("desc", "成功攻击会积累连击。连续两次攻击落空或4回合内没有继续攻击都会重置连击。在2、4、6、8、10连击时会解锁不同的必中处决技，只有最高级处决技会重置连击。");
	}


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
