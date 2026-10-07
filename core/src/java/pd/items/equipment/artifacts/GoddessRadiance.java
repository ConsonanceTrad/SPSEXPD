/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Daze;
import pd.actors.buffs.Dread;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Light;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Waterskin;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 移植自 Darkest PD 0.7.2 的神器「圣者之辉」（GoddessRadiance）。
 *
 * <p>装备期间随时间缓慢成长（也可用露珠瓶的露水加速）。充能满时可以「激活」：驱散隐形、
 * 获得光照，并让视野内一定范围内的敌人致盲、眩晕。满级后额外提供 1 点永久视野；
 * 装备期间还有几率「无视」精神类负面状态（恐惧/魅惑/眩晕/晕眩/恐惧印记/狂暴）。</p>
 */
public class GoddessRadiance extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GoddessRadiance.class)
			.t("name", "圣者之辉")
			.t("ac_activate", "激活")
			.t("ac_bless", "祝福")
			.t("bless", "你用清水冲刷了圣者之辉。")
			.t("too_less", "露水瓶里的露水太少了。")
			.t("no_charge", "圣者之辉尚未充能完毕。")
			.t("cursed", "圣者之辉已被诅咒，无法使用。")
			.t("charged", "圣者之辉已充能完毕。")
			.t("evaded", "圣者之辉替你挡下了%1$s。")
			.t("levelup", "圣者之辉的光芒更甚！")
			.t("desc", "散发着微弱光辉的神秘器物。女神把力量赐予虔诚之人，由其代为驱散污浊，却不要求以女神之名。\n\n它让你有一定几率无视精神类的负面状态，并能激活产生光耀。")
			.t("desc_hint", "圣者之辉会随着时间逐渐成长，也可以_使用露水瓶祝福_来加速这个过程。")
			.t("desc_max", "圣者之辉充满了能量，光华灼灼：永久提升佩戴者 1 点视野。")
			.t("desc_cursed", "被诅咒的圣者之辉释出暗紫色的浊气，令你十分不安。");
	}

	public static final String AC_ACTIVATE = "ACTIVATE";
	public static final String AC_BLESS = "BLESS";
	/** 祝福所需的最低露水量。 */
	public static final int BLESS_DEW = 10;

	/** 会被「无视」的精神类负面状态。 */
	private static final Class<? extends Buff>[] MENTAL = new Class[]{
			Terror.class, Charm.class, Daze.class, Vertigo.class, Dread.class, Amok.class
	};

	{
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		levelCap = 10;
		charge = 100;
		partialCharge = 0;
		chargeCap = 100;
		defaultAction = AC_ACTIVATE;
	}

	public int exp() {
		return exp;
	}

	public int charge() {
		return charge;
	}

	public int chargeCap() {
		return chargeCap;
	}

	/** 该状态是否属于会被圣者之辉无视的精神类。 */
	public static boolean isMental(Buff buff) {
		if (buff == null) return false;
		for (Class<? extends Buff> type : MENTAL) {
			if (type.isInstance(buff)) return true;
		}
		return false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero)) {
			if (!cursed && charge >= chargeCap) actions.add(AC_ACTIVATE);
			if (level() < levelCap() && hero.belongings.getItem(Waterskin.class) != null) actions.add(AC_BLESS);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (AC_ACTIVATE.equals(action)) {
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (cursed) {
				GLog.w(Messages.get(this, "cursed"));
			} else if (charge < chargeCap) {
				GLog.w(Messages.get(this, "no_charge"));
			} else {
				radiance(hero);
			}
		} else if (AC_BLESS.equals(action)) {
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
				return;
			}
			if (cursed) {
				GLog.w(Messages.get(this, "cursed"));
				return;
			}
			if (!bless(hero)) GLog.w(Messages.get(this, "too_less"));
		}
	}

	/** 用露珠瓶的露水换取成长。 */
	public boolean bless(Hero hero) {
		if (hero == null) return false;
		Waterskin flask = hero.belongings.getItem(Waterskin.class);
		if (flask == null || flask.checkVol() < BLESS_DEW) return false;

		int dew = flask.checkVol();
		earnExp(dew / 2);
		flask.upbook(dew);
		GLog.p(Messages.get(this, "bless"));
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		hero.spend(1f);
		hero.busy();
		return true;
	}

	/** 激活：光耀驱散污浊。 */
	public void radiance(Hero hero) {
		if (hero == null || Dungeon.level == null) return;

		Buff.prolong(hero, Light.class, level() * 3f + 10f);
		Invisibility invisibility = hero.buff(Invisibility.class);
		if (invisibility != null) invisibility.detach();

		int reach = level() / 2 + 4;
		Mob[] mobs = Dungeon.level.mobs().toArray(new Mob[0]);
		for (Mob mob : mobs) {
			if (mob == null || !mob.isAlive()) continue;
			if (Dungeon.level.heroFOV != null && !Dungeon.level.heroFOV[mob.pos]) continue;
			int dist = Dungeon.level.distance(hero.pos, mob.pos);
			if (dist > reach) continue;
			Buff.prolong(mob, Blindness.class, level() / 2f + 3f);
			Buff.prolong(mob, Daze.class, Math.max(1f, reach - dist));
		}

		charge = 0;
		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			Sample.INSTANCE.play(Assets.Sounds.BLAST);
		}
		GLog.p(Messages.get(this, "charged"));
		hero.spendAndNext(1f);
		updateQuickslot();
	}

	/** 成长：每级需要 等级×10 + 15 点经验。 */
	public void earnExp(int amount) {
		if (amount <= 0 || level() >= levelCap()) return;
		exp += amount;
		int need = level() * 10 + 15;
		while (level() < levelCap() && exp >= need) {
			exp -= need;
			upgrade();
			GLog.p(Messages.get(this, "levelup"));
			need = level() * 10 + 15;
		}
		updateQuickslot();
	}

	@Override
	public String desc() {
		String desc = super.desc();
		Hero hero = Dungeon.hero;
		if (hero == null || !isEquipped(hero)) return desc;

		if (cursed) return desc + "\n\n" + Messages.get(this, "desc_cursed");
		if (level() >= levelCap()) return desc + "\n\n" + Messages.get(this, "desc_max");
		return desc + "\n\n" + Messages.get(this, "desc_hint");
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Recharge();
	}

	/** 随时间的充能与成长，以及满级视野与精神抗性。 */
	public class Recharge extends ArtifactBuff {
		/** 满级时提供的额外视野。 */
		public int viewAmend() {
			return itemLevel() >= levelCap() ? 1 : 0;
		}

		/** 无视精神类状态的概率（0 级 0%，1 级 4%，10 级约 26%）。 */
		public float evadeRatio() {
			return 0.4f - (float) Math.pow(0.9, itemLevel()) * 0.4f;
		}

		@Override
		public boolean act() {
			if (charge < chargeCap && !isCursed()) {
				partialCharge += 100f / (itemLevel() * 0.6f + 4f);
				if (partialCharge >= 1f) {
					int gain = (int) partialCharge;
					charge = Math.min(chargeCap, charge + gain);
					partialCharge -= gain;
					if (charge >= chargeCap) {
						partialCharge = 0f;
						GLog.p(Messages.get(GoddessRadiance.class, "charged"));
					}
					updateQuickslot();
				}
			}
			GoddessRadiance.this.earnExp(1);
			spend(TICK);
			return true;
		}
	}
}
