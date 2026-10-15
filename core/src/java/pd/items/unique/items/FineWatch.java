/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Haste;
import pd.actors.buffs.SpsTimeWarp;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.artifacts.Artifact;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 精致怀表 —— 法师一档（面具 B 路线）的神器。
 *
 * <p>主动「上弦」：消耗_全部充能_，每点充能换来 _7 回合_的时间加速
 * （法杖充能加速 + 移动加速）。充能会随时间自然恢复。</p>
 */
public class FineWatch extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.MAGE_STAFF_T1_2_WATCH;

	public static final String AC_WIND = "WIND";

	/** 每点充能换来的加速回合数。 */
	public static final int TURNS_PER_CHARGE = 7;
	/** 充能上限。 */
	public static final int CHARGE_CAP = 5;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(FineWatch.class)
			.t("name", "精致怀表")
			.t("ac_wind", "上弦")
			.t("no_charge", "怀表还没走起来，暂时无法上弦。")
			.t("wound", "怀表在你口袋里飞快地滴答起来——时间在你身边稍稍加快了。")
			.t("desc", "一只走时精准得过分的怀表。\n\n「上弦」：把_全部充能_拧进发条，每 1 点充能换来 _%1$d 回合_的_时间加速_——期间你的法杖持续加速充能，移动也会变快。\n\n怀表会随时间自己上紧（每 20 回合恢复 1 点充能，上限 %2$d）。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;

		chargeCap = CHARGE_CAP;
		charge = CHARGE_CAP;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", TURNS_PER_CHARGE, CHARGE_CAP);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_WIND);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_WIND.equals(action)) return;

		if (charge <= 0) {
			GLog.w(Messages.get(this, "no_charge"));
			return;
		}
		int total = TURNS_PER_CHARGE * charge;
		charge = 0;
		partialCharge = 0;

		Buff.affect(hero, SpsTimeWarp.class).setTurns(total);
		Buff.affect(hero, Haste.class, total);

		GLog.p(Messages.get(this, "wound"));
		hero.spendAndNext(1f);
	}

	/** SPSEXPD: 每 20 回合自然恢复 1 点充能。 */
	@Override
	protected ArtifactBuff passiveBuff() {
		return new Windup();
	}

	public class Windup extends ArtifactBuff {
		@Override
		public boolean act() {
			if (charge < chargeCap) {
				partialCharge += 1f / 20f;
				if (partialCharge >= 1f) {
					partialCharge -= 1f;
					charge++;
					updateQuickslot();
				}
			}
			spend(TICK);
			return true;
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
