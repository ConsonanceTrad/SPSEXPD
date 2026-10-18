/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/**
 * SPSEXPD: 次声波发生器 —— 演员一档（面具 B 路线）的<b>特殊物品</b>，不是武器。
 *
 * <p>与铁铲同类：不占武器位、不能装备，自带随时间恢复的充能；按「施放」对准
 * <b>视野内的目标</b>放一记次声波（无弹道、直接生效），使其_麻痹_。</p>
 */
public class InfrasonicGen extends Item implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_T1_2_INFRA;

	public static final String AC_USE = "USE";

	/** 充能上限（与铁铲同节奏：每回合 +1）。 */
	public static final int FULL_CHARGE = 120;
	/** 单次施放消耗的充能。 */
	public static final int USE_COST = 40;

	private static final String CHARGE = "charge";

	private int charge;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(InfrasonicGen.class)
			.t("name", "次声波发生器")
			.t("ac_use", "施放")
			.t("prompt", "选择视野内的目标")
			.t("no_charge", "次声波发生器的充能不足。")
			.t("no_target", "那里没有可以被次声波击中的目标。")
			.t("hit", "次声波扫过，目标僵在原地。")
			.t("charge", "充能：%1$d / %2$d。")
			.t("desc", "一台方头方脑的机器，按下开关时你会先觉得耳朵发闷。\n\n它不装在手上：按「施放」对准_视野内的目标_，放出一记次声波，让其_麻痹_ %1$d 回合。\n\n充能会随时间自行恢复。它差点杀死了一个英雄。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_USE;
		unique = true;
		usesTargeting = true;
	}

	/** 单次施放的麻痹回合数（脉冲单元覆写为更久）。 */
	public int paralysisTurns() {
		return 1;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= USE_COST) actions.add(AC_USE);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			if (charge < USE_COST) {
				GLog.i(Messages.get(this, "no_charge"));
				return;
			}
			curUser = hero;
			GameScene.selectCell(listener);
			return;
		}
		super.execute(hero, action);
	}

	/**
	 * SPSEXPD: 无弹道施法——直接对目标生效（不生成 Ballistica 飞行物）。
	 *
	 * @return 是否成功施放
	 */
	public boolean fireAt(Hero hero, int cell) {
		if (hero == null || Dungeon.level == null) return false;
		if (charge < USE_COST) {
			GLog.i(Messages.get(this, "no_charge"));
			return false;
		}
		if (!Dungeon.level.insideMap(cell) || !Dungeon.level.heroFOV[cell]) return false;
		Char target = Actor.findChar(cell);
		if (target == null || target == hero || !target.isAlive()) {
			GLog.i(Messages.get(this, "no_target"));
			return false;
		}

		Buff.prolong(target, Paralysis.class, paralysisTurns());
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
		charge = Math.max(0, charge - USE_COST);
		GLog.p(Messages.get(this, "hit"));
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	private final CellSelector.Listener listener = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell != null) fireAt(curUser, cell);
		}

		@Override
		public String prompt() {
			return Messages.get(InfrasonicGen.this, "prompt");
		}
	};

	public void gainCharge() { if (charge < FULL_CHARGE) charge++; }
	public void gainCharge(int amount) { charge = Math.min(FULL_CHARGE, charge + Math.max(0, amount)); }
	public int charge() { return charge; }

	@Override public String status() { return Integer.toString(charge / USE_COST); }

	@Override
	public String desc() {
		return Messages.get(this, "desc", paralysisTurns());
	}

	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = bundle.contains(CHARGE) ? Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))) : FULL_CHARGE; }

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
