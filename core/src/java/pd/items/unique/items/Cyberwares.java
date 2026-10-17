/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Haste;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.PhysicalEmpower;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * SPSEXPD: 12 种临时义体的具体实现。
 *
 * <p>全部效果都复用现成的 buff/机制，不引入新的战斗系统。</p>
 */
public final class Cyberwares {

	private Cyberwares() {
	}

	/** 全部义体种类（生成时随机取一种）。 */
	public static final ArrayList<Class<? extends Cyberware>> POOL = new ArrayList<>(Arrays.asList(
			HackDriver.class, BerserkDriver.class, SamuraiDriver.class, MantisBlade.class,
			GorillaArm.class, Nanowire.class, EvasionDriver.class, TendonBoost.class,
			OpticCamo.class, SubdermalArmor.class, Clairvoyance.class, Adrenaline.class));

	/** SPSEXPD: 随机造一台义体。 */
	public static Cyberware randomCyberware() {
		Class<? extends Cyberware> type = Random.element(POOL);
		try {
			return type.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			return new SubdermalArmor();
		}
	}

	/** 黑客驱动：让附近的构装体过载瘫痪一段时间。 */
	public static class HackDriver extends Cyberware {
		static { InlineText.of(HackDriver.class).t("name", "黑客驱动")
				.t("effect", "你广播出一串乱码，附近的构装体一齐僵在原地。"); }
		@Override protected int capacity() { return 6; }
		@Override protected void activateEffect(Hero hero) { paralyzeNearby(hero, 3f, 3); }
	}

	/** 狂暴驱动：短时间大幅提升伤害。 */
	public static class BerserkDriver extends Cyberware {
		static { InlineText.of(BerserkDriver.class).t("name", "狂暴驱动")
				.t("effect", "义体把抑制器全部关掉，你的手自己开始发抖。"); }
		@Override protected int capacity() { return 5; }
		@Override protected void activateEffect(Hero hero) {
			empower(hero, Math.max(2, hero.HT / 10), 8);
			GLog.i(Messages.get(BerserkDriver.class, "effect"));
		}
	}

	/** 武士驱动：攻速与移速一起拉高。 */
	public static class SamuraiDriver extends Cyberware {
		static { InlineText.of(SamuraiDriver.class).t("name", "武士驱动")
				.t("effect", "节律器接管了你的呼吸，连脚步都变得干脆。"); }
		@Override protected int capacity() { return 6; }
		@Override protected void activateEffect(Hero hero) {
			Buff.affect(hero, Haste.class, 10f);
			empower(hero, Math.max(1, hero.HT / 14), 10);
			GLog.i(Messages.get(SamuraiDriver.class, "effect"));
		}
	}

	/** 螳螂刀：对眼前目标一记斩击。 */
	public static class MantisBlade extends Cyberware {
		static { InlineText.of(MantisBlade.class).t("name", "螳螂刀")
				.t("effect", "刀刃从手背弹出，空气里只剩下一道横线。"); }
		@Override protected int capacity() { return 8; }
		@Override protected void activateEffect(Hero hero) {
			Char enemy = nearestEnemy(hero, 1);
			if (enemy == null) return;
			enemy.damage(hero.damageRoll() + hero.lvl * 3, this);
			GLog.i(Messages.get(MantisBlade.class, "effect"));
		}
	}

	/** 猩猩手臂：把目标推出去。 */
	public static class GorillaArm extends Cyberware {
		static { InlineText.of(GorillaArm.class).t("name", "猩猩手臂")
				.t("effect", "手臂里的液压泵一声闷响，对方被你推了出去。"); }
		@Override protected int capacity() { return 5; }
		@Override protected void activateEffect(Hero hero) {
			Char enemy = nearestEnemy(hero, 1);
			if (enemy == null || Dungeon.level == null) return;
			int target = hero.pos + (enemy.pos - hero.pos) * 2;
			if (Dungeon.level.insideMap(target) && Dungeon.level.passable[target]
					&& Actor.findChar(target) == null) {
				enemy.pos = target;
				Dungeon.level.occupyCell(enemy);
				if (enemy.sprite != null) enemy.sprite.place(target);
			}
			GLog.i(Messages.get(GorillaArm.class, "effect"));
		}
	}

	/** 纳米丝：把目标拽到身边。 */
	public static class Nanowire extends Cyberware {
		static { InlineText.of(Nanowire.class).t("name", "纳米丝")
				.t("effect", "细丝收紧，那人被拖到了你面前。"); }
		@Override protected int capacity() { return 6; }
		@Override protected void activateEffect(Hero hero) {
			Char enemy = nearestEnemy(hero, 3);
			if (enemy == null || Dungeon.level == null) return;
			if (Dungeon.level.distance(hero.pos, enemy.pos) <= 1) return;
			int step = enemy.pos + Integer.signum(hero.pos - enemy.pos);
			if (Dungeon.level.insideMap(step) && Dungeon.level.passable[step]
					&& Actor.findChar(step) == null) {
				enemy.pos = step;
				Dungeon.level.occupyCell(enemy);
				if (enemy.sprite != null) enemy.sprite.place(step);
			}
			GLog.i(Messages.get(Nanowire.class, "effect"));
		}
	}

	/** 闪避驱动：短时间内护盾覆盖。 */
	public static class EvasionDriver extends Cyberware {
		static { InlineText.of(EvasionDriver.class).t("name", "闪避驱动")
				.t("effect", "义体开始预测弹道，你的身体先一步让开了。"); }
		@Override protected int capacity() { return 5; }
		@Override protected void activateEffect(Hero hero) {
			Buff.affect(hero, Barkskin.class).set(Math.max(1, hero.HT / 8), 1);
			GLog.i(Messages.get(EvasionDriver.class, "effect"));
		}
	}

	/** 强化肌腱：短时大幅提速。 */
	public static class TendonBoost extends Cyberware {
		static { InlineText.of(TendonBoost.class).t("name", "强化肌腱")
				.t("effect", "肌腱被拉紧到极限，你几乎是被弹出去的。"); }
		@Override protected int capacity() { return 6; }
		@Override protected void activateEffect(Hero hero) {
			Buff.affect(hero, Haste.class, 12f);
			GLog.i(Messages.get(TendonBoost.class, "effect"));
		}
	}

	/** 光学迷彩：隐身。 */
	public static class OpticCamo extends Cyberware {
		static { InlineText.of(OpticCamo.class).t("name", "光学迷彩")
				.t("effect", "皮肤表面折了一下光，你就地消失了。"); }
		@Override protected int capacity() { return 5; }
		@Override protected void activateEffect(Hero hero) {
			Buff.affect(hero, Invisibility.class, 10f);
			GLog.i(Messages.get(OpticCamo.class, "effect"));
		}
	}

	/** 皮下护甲：一层硬壳。 */
	public static class SubdermalArmor extends Cyberware {
		static { InlineText.of(SubdermalArmor.class).t("name", "皮下护甲")
				.t("effect", "皮肤下浮起一层鳞片，硬得像石头。"); }
		@Override protected int capacity() { return 6; }
		@Override protected void activateEffect(Hero hero) {
			Buff.affect(hero, Barkskin.class).set(Math.max(1, hero.HT / 5), 1);
			GLog.i(Messages.get(SubdermalArmor.class, "effect"));
		}
	}

	/** 千里眼：灵视 + 一定回合的视野。 */
	public static class Clairvoyance extends Cyberware {
		static { InlineText.of(Clairvoyance.class).t("name", "千里眼")
				.t("effect", "你的视野忽然被扯开，墙后的一切都浮了出来。"); }
		@Override protected int capacity() { return 4; }
		@Override protected void activateEffect(Hero hero) {
			Buff.affect(hero, MindVision.class, 20f);
			GLog.i(Messages.get(Clairvoyance.class, "effect"));
		}
	}

	/** 肾上腺素增强：爆发式的伤害与速度。 */
	public static class Adrenaline extends Cyberware {
		static { InlineText.of(Adrenaline.class).t("name", "肾上腺素增强")
				.t("effect", "一股化学药剂推进血管，你的心跳盖过了周围的声音。"); }
		@Override protected int capacity() { return 5; }
		@Override protected void activateEffect(Hero hero) {
			empower(hero, Math.max(2, hero.HT / 8), 6);
			Buff.affect(hero, Haste.class, 6f);
			GLog.i(Messages.get(Adrenaline.class, "effect"));
		}
	}

	// ---------------------------------------------------------------- helpers

	private static void empower(Hero hero, int bonus, int turns) {
		PhysicalEmpower emp = Buff.affect(hero, PhysicalEmpower.class);
		emp.dmgBoost = Math.max(emp.dmgBoost, bonus);
		emp.left = Math.max(emp.left, turns);
	}

	private static Char nearestEnemy(Hero hero, int range) {
		if (Dungeon.level == null) return null;
		Char best = null;
		int bestDist = Integer.MAX_VALUE;
		for (Char ch : Actor.chars()) {
			if (ch == null || ch == hero || ch.alignment != Char.Alignment.ENEMY) continue;
			int d = Dungeon.level.distance(hero.pos, ch.pos);
			if (d <= range && d < bestDist) {
				best = ch;
				bestDist = d;
			}
		}
		return best;
	}

	private static void paralyzeNearby(Hero hero, float turns, int range) {
		if (Dungeon.level == null) return;
		for (Char ch : Actor.chars()) {
			if (ch == null || ch == hero || ch.alignment != Char.Alignment.ENEMY) continue;
			if (Dungeon.level.distance(hero.pos, ch.pos) <= range) {
				Buff.prolong(ch, Paralysis.class, turns);
			}
		}
	}
}
