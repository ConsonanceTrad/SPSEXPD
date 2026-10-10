/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.IconEntry;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.weapon.curses.Wayward;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.messages.Messages;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Shared implementation of SPS-PD 0.9.8's mutable melee-weapon statistics. */
public abstract class NormalMeleeWeapon extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NormalMeleeWeapon.class)
			.t("sps_stats", "这件武器的命中倍率为_%1$s_，攻击延迟为_%2$s_，攻击距离为_%3$d_。");
	}




	private final int baseMin;
	private final int baseMax;
	private final float baseAccuracy;
	private final float baseDelay;
	private final int baseReach;
	private final int baseStrength;

	protected NormalMeleeWeapon(int tier, float accuracy, float delay, int reach,
			int min, int max, IconEntry image) {
		this.tier = tier;
		baseAccuracy = ACC = accuracy;
		baseDelay = DLY = delay;
		baseReach = RCH = reach;
		baseStrength = 8 + tier * 2;
		baseMin = min;
		baseMax = max;
		this.image = image;
	}

	protected void applyLegacyUpgrade(Stats stats) {
	}

	private Stats stats(int level) {
		Stats result = new Stats(baseMin, baseMax, baseAccuracy, baseDelay, baseReach, baseStrength);
		for (int i = 0; i < Math.max(0, level); i++) {
			applyLegacyUpgrade(result);
			result.min++;
			result.max += 1 + tier / 2;
		}
		return result;
	}

	//SPSEXPD: 数值曲线统一到破碎式（MeleeWeapon）——min/max 走 tier 曲线、STRReq 随等级下降。
	// 这里不再覆写 min/max/STRReq，旧版「线性成长 + 恒定力量需求 + 子类 min/max 增量」因此失效；
	// stats() 仍保留（其 min/max 结果不再被读取），只为 legacyAccuracy/legacyDelay/legacyReach
	// 提供 SPS 的 acc/delay/reach 手感。

	public float legacyAccuracy(int level) { return stats(level).accuracy; }
	public float legacyDelay(int level) { return stats(level).delay; }
	public int legacyReach(int level) { return stats(level).reach; }

	@Override
	public float accuracyFactor(Char owner, Char target) {
		int encumbrance = owner instanceof Hero ? STRReq() - ((Hero)owner).STR() : 0;
		float accuracy = legacyAccuracy(level());
		if (owner.buff(Wayward.WaywardBuff.class) != null && enchantment instanceof Wayward) accuracy /= 5f;
		return encumbrance > 0 ? (float)(accuracy / Math.pow(1.5, encumbrance)) : accuracy;
	}

	@Override
	protected float baseDelay(Char owner) {
		float delay = augment.delayFactor(legacyDelay(level()));
		if (owner instanceof Hero) {
			int encumbrance = STRReq() - ((Hero)owner).STR();
			if (encumbrance > 0) delay *= Math.pow(1.2, encumbrance);
		}
		return delay;
	}

	@Override public int reachFactor(Char owner) { return reachFactor(owner, legacyReach(level())); }

	@Override
	public int damageRoll(Char owner) {
		int damage = owner instanceof Hero
				? Hero.heroDamageIntRange(min(), max())
				: Random.NormalIntRange(min(), max());
		damage = augment.damageFactor(damage);
		//SPSEXPD: 超额力量改为随机骰（与破碎式 MeleeWeapon 一致），不再是每点定值 +1，
		// 让「堆力量 = 堆伤害」的线性收益降半。
		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) damage += Hero.heroDamageIntRange(0, exStr);
		}
		return damage;
	}

	@Override
	public Item random() {
		int generatedLevel = 0;
		if (Random.Float() < 0.4f) {
			generatedLevel = 1;
			if (Random.Int(3) == 0) {
				generatedLevel++;
				if (Random.Int(3) == 0) generatedLevel++;
			}
			if (Random.Int(2) != 0) {
				generatedLevel = -generatedLevel;
				cursed = true;
			}
		}
		level(generatedLevel);
		if (generatedLevel > 0 && usesSpsAbrasion()) {
			for (int i = 0; i < generatedLevel; i++) upgradeAbrasionDurability();
		}
		if (Random.Int(Math.max(1, 5 + level())) == 0) enchant();
		return this;
	}

	@Override
	public String statsInfo() {
		return Messages.get(NormalMeleeWeapon.class, "sps_stats",
				Messages.decimalFormat("#.##", legacyAccuracy(level())),
				Messages.decimalFormat("#.##", legacyDelay(level())), legacyReach(level()));
	}

	@Override
	public int value() {
		int price = 50;
		if (enchantment != null) price = (int)(price * 1.5f);
		if (cursedKnown && cursed) price /= 2;
		if (levelKnown && level() > 0) price *= level() + 1;
		else if (levelKnown && level() < 0) price /= 1 - level();
		return Math.max(1, price);
	}

	protected static int safeRandom(int min, int max) {
		min = Math.max(0, min);
		return max <= min ? min : Random.Int(min, max);
	}

	protected static int attackerRoll(Char attacker) {
		return Math.max(0, attacker.damageRoll());
	}

	protected static class Stats {
		public int min;
		public int max;
		public float accuracy;
		public float delay;
		public int reach;
		public int strength;

		Stats(int min, int max, float accuracy, float delay, int reach, int strength) {
			this.min = min;
			this.max = max;
			this.accuracy = accuracy;
			this.delay = delay;
			this.reach = reach;
			this.strength = strength;
		}
	}
}
