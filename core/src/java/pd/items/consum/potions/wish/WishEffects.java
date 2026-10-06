/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.actors.buffs.Amok;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Corrosion;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Invulnerability;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.LuckyMoment;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.buffs.Weakness;
import pd.actors.hero.Hero;
import pd.items.consum.potions.PotionOfHealing;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * SPSEXPD: 许愿的效果类愿望——负面效果、死亡与增益效果。
 *
 * 效果强度随奖励等级（由幸运值决定）放大：运气越好，许下的负面愿望也越强。
 */
public final class WishEffects {

	private static final String[] DEATH_WORDS = {
			"死亡", "死去", "自尽", "自杀", "去死", "了结", "结束生命", "解脱",
			"death", "die", "dying", "kill me", "suicide", "perish"
	};

	/** 负面愿望关键词 → 效果 id。 */
	private static final Map<String, String> NEGATIVE = new LinkedHashMap<>();

	static {
		NEGATIVE.put("中毒", "poison");
		NEGATIVE.put("剧毒", "poison");
		NEGATIVE.put("poison", "poison");
		NEGATIVE.put("燃烧", "burning");
		NEGATIVE.put("着火", "burning");
		NEGATIVE.put("焚", "burning");
		NEGATIVE.put("burn", "burning");
		NEGATIVE.put("流血", "bleeding");
		NEGATIVE.put("bleed", "bleeding");
		NEGATIVE.put("迟缓", "slow");
		NEGATIVE.put("slow", "slow");
		NEGATIVE.put("虚弱", "weakness");
		NEGATIVE.put("weak", "weakness");
		NEGATIVE.put("失明", "blind");
		NEGATIVE.put("blind", "blind");
		NEGATIVE.put("眩晕", "vertigo");
		NEGATIVE.put("dizzy", "vertigo");
		NEGATIVE.put("麻痹", "paralysis");
		NEGATIVE.put("paraly", "paralysis");
		NEGATIVE.put("腐蚀", "corrosion");
		NEGATIVE.put("corro", "corrosion");
		NEGATIVE.put("酸蚀", "corrosion");
		NEGATIVE.put("恐惧", "terror");
		NEGATIVE.put("terror", "terror");
		NEGATIVE.put("fear", "terror");
		NEGATIVE.put("混乱", "amok");
		NEGATIVE.put("amok", "amok");
		NEGATIVE.put("残废", "cripple");
		NEGATIVE.put("cripple", "cripple");
	}

	/** 增益愿望关键词 → 效果 id。 */
	private static final Map<String, String> POSITIVE = new LinkedHashMap<>();

	static {
		POSITIVE.put("治疗", "heal");
		POSITIVE.put("回血", "heal");
		POSITIVE.put("治愈", "heal");
		POSITIVE.put("heal", "heal");
		POSITIVE.put("隐形", "invisibility");
		POSITIVE.put("invisib", "invisibility");
		POSITIVE.put("加速", "haste");
		POSITIVE.put("haste", "haste");
		POSITIVE.put("护盾", "shield");
		POSITIVE.put("护罩", "shield");
		POSITIVE.put("shield", "shield");
		POSITIVE.put("幸运", "luck");
		POSITIVE.put("luck", "luck");
		POSITIVE.put("力量", "strength");
		POSITIVE.put("strength", "strength");
		POSITIVE.put("防御", "defence");
		POSITIVE.put("defen", "defence");
		POSITIVE.put("感知", "mind_vision");
		POSITIVE.put("透视", "mind_vision");
		POSITIVE.put("vision", "mind_vision");
		POSITIVE.put("漂浮", "levitation");
		POSITIVE.put("浮空", "levitation");
		POSITIVE.put("levitat", "levitation");
		POSITIVE.put("充能", "recharge");
		POSITIVE.put("recharge", "recharge");
		POSITIVE.put("无敌", "invulnerable");
		POSITIVE.put("invulnerab", "invulnerable");
	}

	private WishEffects() {
	}

	/** 愿望是否提到死亡。 */
	public static boolean isDeath(String text) {
		return mentions(text, DEATH_WORDS);
	}

	/** 愿望命中的负面效果 id；未命中返回 null。 */
	public static String negativeKeyword(String text) {
		return keyword(text, NEGATIVE);
	}

	/** 愿望命中的增益效果 id；未命中返回 null。 */
	public static String positiveKeyword(String text) {
		return keyword(text, POSITIVE);
	}

	private static String keyword(String text, Map<String, String> table) {
		if (text == null) return null;
		String lower = text.toLowerCase(Locale.ROOT);
		for (Map.Entry<String, String> entry : table.entrySet()) {
			if (lower.contains(entry.getKey())) return entry.getValue();
		}
		return null;
	}

	private static boolean mentions(String text, String... words) {
		if (text == null) return false;
		String lower = text.toLowerCase(Locale.ROOT);
		for (String word : words) {
			if (lower.contains(word)) return true;
		}
		return false;
	}

	/** 施加负面愿望，强度随等级放大。 */
	public static void applyNegative(Hero hero, String effect, int tier) {
		if (hero == null || effect == null) return;
		float duration = 5f + 2f * tier;
		switch (effect) {
			case "poison":
				Buff.affect(hero, Poison.class).set(3 + hero.lvl / 3 + 2 * tier);
				break;
			case "burning":
				//Burning 不是 FlavourBuff，无法用 prolong 指定时长
				Buff.affect(hero, Burning.class);
				break;
			case "bleeding":
				Buff.affect(hero, Bleeding.class).set(2f + tier);
				break;
			case "slow":
				Buff.prolong(hero, Slow.class, duration);
				break;
			case "weakness":
				Buff.prolong(hero, Weakness.class, duration);
				break;
			case "blind":
				Buff.prolong(hero, Blindness.class, duration);
				break;
			case "vertigo":
				Buff.prolong(hero, Vertigo.class, duration);
				break;
			case "paralysis":
				Buff.prolong(hero, Paralysis.class, 3f + tier);
				break;
			case "corrosion":
				Buff.affect(hero, Corrosion.class).set(5f + 2f * tier, Math.max(1, hero.HT / 20));
				break;
			case "terror":
				Buff.prolong(hero, Terror.class, duration);
				break;
			case "amok":
				Buff.prolong(hero, Amok.class, duration);
				break;
			case "cripple":
				Buff.prolong(hero, Cripple.class, duration);
				break;
			default:
				break;
		}
	}

	/** 施加增益愿望，强度随等级放大。 */
	public static void applyPositive(Hero hero, String effect, int tier) {
		if (hero == null || effect == null) return;
		switch (effect) {
			case "heal":
				PotionOfHealing.heal(hero);
				break;
			case "invisibility":
				Buff.prolong(hero, Invisibility.class, 6f + 2f * tier);
				break;
			case "haste":
				Buff.prolong(hero, HasteBuff.class, 10f + 5f * tier);
				break;
			case "shield":
				Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.HT / 5 * tier));
				break;
			case "luck":
				Buff.prolong(hero, LuckyMoment.class, 100f * tier);
				break;
			case "strength":
				Buff.affect(hero, AttackUp.class, 30f * tier).level(5 * tier);
				break;
			case "defence":
				Buff.affect(hero, DefenceUp.class, 30f * tier).level(5 * tier);
				break;
			case "mind_vision":
				Buff.prolong(hero, MindVision.class, 20f * tier);
				break;
			case "levitation":
				Buff.prolong(hero, Levitation.class, 10f * tier);
				break;
			case "recharge":
				Buff.prolong(hero, Recharging.class, 20f * tier);
				break;
			case "invulnerable":
				Buff.prolong(hero, Invulnerability.class, 2f + tier);
				break;
			default:
				break;
		}
	}

	/** 许愿死亡。 */
	public static void kill(Hero hero, String cause) {
		if (hero == null) return;
		hero.HP = 0;
		try {
			hero.die(cause);
		} catch (Throwable ignored) {
			//无 UI 环境（测试）下 die 可能无法走完全流程，HP 归零已足以表达死亡
		}
	}

	/** 该效果 id 属于负面表。 */
	public static boolean isNegativeEffect(String effect) {
		return effect != null && NEGATIVE.containsValue(effect);
	}
}
