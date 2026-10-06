/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 特质（Perk）体系核心基类 —— 移植自 Darkest Pixel Dungeon 0.7.2，
 * 用于替代本项目原有的破碎天赋（Talent）体系。
 */

package pd.actors.hero.perks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.messages.Messages;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Reflection;

public abstract class Perk implements Bundlable {

	/** 特质分类，用于 UI 过滤与后续扩展 */
	public enum Tag {
		Bare, Crit, Melee, Ranged, Wand, Evade, Viability
	}

	private int level;
	private final int maxLevel;
	private final HashSet<Tag> tags = new HashSet<>();

	public Perk() { this(1, 1); }

	public Perk(int maxLevel) { this(maxLevel, 1); }

	public Perk(int maxLevel, int level) {
		this.maxLevel = maxLevel;
		this.level = level;
	}

	protected void addTags(Tag... ts) {
		Collections.addAll(tags, ts);
	}

	public boolean hasTag(Tag t) {
		return tags.contains(t);
	}

	/** 是否允许被获得（职业/状态限制），子类覆写 */
	protected boolean canBeGain(Hero hero) {
		return true;
	}

	/** 图标在 perks.png 中的帧号 */
	public int image() {
		return PerkImageSheet.NONE;
	}

	/** 获得特质时挂上效果 */
	public void onGain() {
	}

	/** 失去特质时卸下效果 */
	public void onLose() {
	}

	public String title() {
		return Messages.get(this, "title");
	}

	public String description() {
		return Messages.get(this, "desc");
	}

	public int level() {
		return level;
	}

	public int maxLevel() {
		return maxLevel;
	}

	public void setLevel(int l) {
		level = l;
	}

	public boolean upgradable() {
		return level < maxLevel && canBeGain(Dungeon.hero);
	}

	public void upgrade() {
		level++;
	}

	public void downgrade() {
		level--;
	}

	/** 是否可以（再次）获得：未拥有，或已拥有但还能升级 */
	public boolean isAcquireAllowed(Hero hero) {
		if (hero == null || hero.heroPerk == null || !canBeGain(hero)) return false;
		Perk owned = hero.heroPerk.get(getClass());
		return owned == null || owned.upgradable();
	}

	/** 条件型特质：条件满足时由 TraitCounters 自动授予，默认不适用 */
	public boolean conditionMet(Hero hero) {
		return false;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put("level", level);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		level = bundle.getInt("level");
	}

	// ------------------------------------------------------------------
	/** 数值型特质：升级/降级时需要先卸下旧效果再挂上新效果 */
	public static abstract class Additional extends Perk {

		public Additional() {
			super();
		}

		public Additional(int maxLevel) {
			super(maxLevel);
		}

		public Additional(int maxLevel, int level) {
			super(maxLevel, level);
		}

		@Override
		public final void upgrade() {
			onLose();
			super.upgrade();
			onGain();
		}

		@Override
		public final void downgrade() {
			onLose();
			super.downgrade();
			onGain();
		}
	}

	// ------------------------------------------------------------------
	/** 随机池与候选抽取 */
	public static final class Companion {

		private Companion() {
		}

		//SPSXPD: 调试用 —— 列出全部已注册特质（含权重 0、正常途径拿不到的）
		public static ArrayList<Class<? extends Perk>> allClasses() {
			return new ArrayList<>(POSITIVES.keySet());
		}

		/**
		 * 可随机获得的特质池及其权重（照暗黑的 Perk.INSTANCE.positives）。
		 * 权重 0 表示只在特定途径获得；压力体系已决定不引入，故池中不含压力类。
		 */
		private static final LinkedHashMap<Class<? extends Perk>, Float> POSITIVES = new LinkedHashMap<>();

		static {
			//SPSXPD: 驯兽大师 —— 魂石的献祭 / 炸环由它解锁
			POSITIVES.put(BeastMaster.class, 1f);

			//SPSXPD: 深渊巨口 —— 吞星花啃咬 + 击杀铭记（每种生物一次）
			POSITIVES.put(AbyssalMaw.class, 1f);

			//SPSXPD: 露珠研究 —— 露珠瓶 / 露珠瓶消耗打折
			POSITIVES.put(DewResearch.class, 1f);

			//SPSXPD: 血能护盾 —— 回血转为奥术护盾
			POSITIVES.put(BloodShield.class, 1f);

			POSITIVES.put(LuckFromAuthor.class, 0.01f);
			POSITIVES.put(GoodAppetite.class, 1f);
			//裁决：Optimistic 改写为「法术防御抵抗纯粹伤害」，入池
			POSITIVES.put(Optimistic.class, 1f);
			//裁决：RavenousAppetite（负向）确认保留并入池，权重压低
			POSITIVES.put(RavenousAppetite.class, 0.5f);
			POSITIVES.put(StrongConstitution.class, 1f);
			POSITIVES.put(Keen.class, 1f);
			POSITIVES.put(WandPerception.class, 0f);
			POSITIVES.put(NightVision.class, 0.75f);
			POSITIVES.put(Telepath.class, 1f);
			POSITIVES.put(Fearless.class, 1f);
			POSITIVES.put(Assassin.class, 0f);
			POSITIVES.put(IntendedTransportation.class, 0f);
			POSITIVES.put(Discount.class, 1f);
			POSITIVES.put(GreedyMidas.class, 1f);
			POSITIVES.put(VampiricCrit.class, 0.75f);
			POSITIVES.put(PureCrit.class, 1f);
			POSITIVES.put(ExtraCritProbability.class, 1f);
			POSITIVES.put(HardCrit.class, 1f);
			POSITIVES.put(LowHealthRegeneration.class, 1f);
			POSITIVES.put(LowHealthDexterous.class, 1f);
			POSITIVES.put(LowWeightDexterous.class, 1f);
			POSITIVES.put(ExtraEvasion.class, 1f);
			POSITIVES.put(CounterStrike.class, 1f);
			POSITIVES.put(ExtraDexterousGrowth.class, 1f);
			POSITIVES.put(EvasionTenacity.class, 1f);
			POSITIVES.put(Blur.class, 1f);
			POSITIVES.put(ExtraPerkChoice.class, 1f);
			POSITIVES.put(BrewEnhancedPotion.class, 1f);
			POSITIVES.put(Knowledgeable.class, 0.75f);
			POSITIVES.put(EfficientSearch.class, 1f);
			POSITIVES.put(ExtraStrengthPower.class, 1f);
			POSITIVES.put(FastRegeneration.class, 1f);
			POSITIVES.put(EfficientPotionOfHealing.class, 0.25f);
			POSITIVES.put(WandCharger.class, 1f);
			POSITIVES.put(WandArcane.class, 1f);
			POSITIVES.put(QuickZap.class, 1f);
			POSITIVES.put(StealthCaster.class, 1f);
			POSITIVES.put(ArcaneCrit.class, 1.2f);
			POSITIVES.put(WandPiercing.class, 1.25f);
			POSITIVES.put(CloseZap.class, 1f);
			POSITIVES.put(PreheatedZap.class, 1f);
			POSITIVES.put(ManaDrine.class, 1f);
			POSITIVES.put(ExplodeBrokenShot.class, 1f);
			POSITIVES.put(RangedShot.class, 1f);
			POSITIVES.put(FinishingShot.class, 1f);
			POSITIVES.put(ExtraStrength.class, 0.75f);
			POSITIVES.put(ExtraRuneRegularly.class, 0.8f);
			POSITIVES.put(BaredAngry.class, 1f);
			POSITIVES.put(BaredSwiftness.class, 1f);
			POSITIVES.put(BaredStealth.class, 1f);
			POSITIVES.put(ExtraMagicalResistance.class, 1f);
			POSITIVES.put(QuickLearner.class, 1f);
			POSITIVES.put(Maniac.class, 1f);
			POSITIVES.put(PolearmMaster.class, 0.8f);
			POSITIVES.put(Ease.class, 1f);
			POSITIVES.put(EnchantmentExtraDamage.class, 1f);
			POSITIVES.put(FastMoveOnKilling.class, 1f);

			//SPSEXPD: 由破碎天赋「合并 / 转为特质」而来的条目
			POSITIVES.put(MealRush.class, 1f);
			POSITIVES.put(MealSurge.class, 1f);
			POSITIVES.put(PotionAffinity.class, 1f);
			POSITIVES.put(ChainedStrike.class, 1f);
			POSITIVES.put(MomentumSlayer.class, 0.8f);
			POSITIVES.put(LethalPrep.class, 0.8f);
			POSITIVES.put(InnerPower.class, 0.8f);
			POSITIVES.put(HerbalGuard.class, 1f);
			POSITIVES.put(SteadyShield.class, 1f);
			POSITIVES.put(BowMastery.class, 1f);
			POSITIVES.put(SilentStalker.class, 1f);
			POSITIVES.put(ProvokedAnger.class, 1f);
			POSITIVES.put(IronWill.class, 1f);
			POSITIVES.put(ImprovisedProjectiles.class, 1f);
			POSITIVES.put(Steadfast.class, 1f);
			POSITIVES.put(EndlessRage.class, 0.8f);
			POSITIVES.put(DeathlessFury.class, 0.8f);
			POSITIVES.put(EnragedCatalyst.class, 1f);
			POSITIVES.put(EnhancedCombo.class, 0.8f);
			POSITIVES.put(InscribedPower.class, 1f);
			POSITIVES.put(WandPreservation.class, 0.8f);
			POSITIVES.put(DesperatePower.class, 1f);
			POSITIVES.put(MysticalCharge.class, 1f);
			POSITIVES.put(SoulEater.class, 0.8f);
			POSITIVES.put(SoulSiphon.class, 0.8f);
			POSITIVES.put(NecromancersMinions.class, 0.8f);
			POSITIVES.put(CachedRations.class, 1f);
			POSITIVES.put(RingEmpower.class, 1f);
			POSITIVES.put(ProjectileMomentum.class, 1f);
			POSITIVES.put(NaturesBounty.class, 1f);
			POSITIVES.put(RejuvenatingSteps.class, 1f);
			POSITIVES.put(Farsight.class, 1f);
			POSITIVES.put(SwiftEquip.class, 1f);
			POSITIVES.put(PreciseAssault.class, 1f);
			POSITIVES.put(TwinUpgrades.class, 0.8f);
		}

		/** 抽取 count 个互不重复的候选特质 */
		public static ArrayList<Perk> randomPositives(Hero hero, int count) {
			LinkedHashMap<Class<? extends Perk>, Float> pool = new LinkedHashMap<>();
			for (java.util.Map.Entry<Class<? extends Perk>, Float> e : POSITIVES.entrySet()) {
				if (e.getValue() <= 0f) continue;
				Perk probe = Reflection.newInstance(e.getKey());
				if (probe != null && probe.isAcquireAllowed(hero)) pool.put(e.getKey(), e.getValue());
			}

			ArrayList<Perk> result = new ArrayList<>();
			if (pool.size() <= count) {
				for (Class<? extends Perk> c : pool.keySet()) result.add(Reflection.newInstance(c));
				while (result.size() < count) result.add(new LuckFromAuthor());
			} else {
				while (result.size() < count && !pool.isEmpty()) {
					Class<? extends Perk> c = weightedPick(pool);
					if (c == null) break;
					result.add(Reflection.newInstance(c));
					pool.remove(c);
				}
			}
			return result;
		}

		public static Perk randomPositive(Hero hero) {
			ArrayList<Perk> l = randomPositives(hero, 1);
			return l.isEmpty() ? new LuckFromAuthor() : l.get(0);
		}

		/** 候选数：拥有 ExtraPerkChoice 时为 5，否则 3 */
		public static int candidateCount(Hero hero) {
			return hero != null && hero.heroPerk != null && hero.heroPerk.has(ExtraPerkChoice.class) ? 5 : 3;
		}

		private static Class<? extends Perk> weightedPick(LinkedHashMap<Class<? extends Perk>, Float> pool) {
			float total = 0f;
			for (float w : pool.values()) total += w;
			if (total <= 0f) return null;
			float r = Random.Float(total);
			for (java.util.Map.Entry<Class<? extends Perk>, Float> e : pool.entrySet()) {
				r -= e.getValue();
				if (r <= 0f) return e.getKey();
			}
			return pool.keySet().iterator().next();
		}
	}

	// ------------------------------------------------------------------
	/** 随机池为空时的兜底奖励 */
	public static final class LuckFromAuthor extends Perk {

		static {
			InlineText.of(LuckFromAuthor.class)
					.t("title", "作者的祝福")
					.t("desc", "玩的开心！——来自 Egoal\n（原版依赖压力/幸运体系，本项目暂以少量回血替代，待裁决）");
		}

		public LuckFromAuthor() {
			super(1000, 1);
		}

		@Override
		public int image() {
			return PerkImageSheet.LUCK_FROM_ME;
		}

		@Override
		public void onGain() {
			// TODO 待裁决：改为等价「幸运」效果（原版 = Relieve + Lucky，均属压力体系）
			if (Dungeon.hero != null) {
				if (!pd.actors.hero.perks.BloodShield.convert(Dungeon.hero, 1)) Dungeon.hero.HP = Math.min(Dungeon.hero.HT, Dungeon.hero.HP + 1);
			}
		}
	}
}
