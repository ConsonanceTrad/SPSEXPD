/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.effects.particles.ElmoParticle;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPS-PD's alternate chains. Pulling uses Shattered's hardened chain implementation,
 * while charge, levelling and the exhausting seal retain the 0.9.8 rules.
 */
public class FlyChains extends EtherealChains {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FlyChains.class)
			.t("name", "翔虫")
			.t("ac_cast", "施放")
			.t("ac_locked", "耗竭-封印")
			.t("no_charge", "你的翔虫充能不足。")
			.t("cursed", "你不能使用受诅咒的翔虫。")
			.t("does_nothing", "这样并没有用。")
			.t("cant_pull", "你的翔虫不能拉动那个目标。")
			.t("inside_wall", "你的翔虫只能带你越过墙壁，不能把你拉进墙里。")
			.t("nothing_to_grab", "目标区域没有可供抓取的物件。")
			.t("prompt", "选择要瞄准的地方")
			.t("desc", "这些翔虫可以用来把你拉向一些地形，或将敌人拉向你。其飞行高度甚至可以允许你越过墙壁！")
			.t("desc_cursed", "被诅咒的翔虫将自己锁在了你的身边，不断地在周围晃动，试图绊倒或绑住你。")
			.t("desc_equipped", "翔虫围绕在你的身边，缓慢地收集被你击败者的精神能量。")
			.t("chainsrecharge2.levelup", "你的翔虫变得更强大了！");
	}


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
