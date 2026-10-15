/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
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
 * SPSEXPD: 暗夜手环 —— 盗贼一档（面具 B 路线）的充能型神器。
 *
 * <p>装备后随时间积攒充能（上限 {@link #CHARGE_CAP}）；充能越满，一次「匿形」换来的隐身处越久。
 * 它可以用升级卷轴升级。</p>
 */
public class NightBracelet extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ROGUE_CLOAK_T1_2_NIGHT;

	public static final String AC_HIDE = "HIDE";

	/** 充能上限。 */
	public static final int CHARGE_CAP = 15;
	/** 每 1 点充能换来的隐身回合数。 */
	public static final int TURNS_PER_CHARGE = 2;
	/** 每多少回合恢复 1 点充能。 */
	public static final float TURNS_PER_CHARGE_REGEN = 10f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(NightBracelet.class)
			.t("name", "暗夜手环")
			.t("ac_hide", "匿形")
			.t("no_charge", "手环还没有蓄满夜色。")
			.t("hidden", "夜色顺着你的手腕漫上来，把你裹了进去。")
			.t("desc", "一只吸饱了夜色的手环，摸上去凉得像刚下过雨的石头。\n\n「匿形」：把_全部充能_一次用掉，每 1 点充能换来 _%1$d 回合_隐身。\n\n它会随时间自行蓄能（每 %2$d 回合恢复 1 点，上限 %3$d），也可以用_升级卷轴_继续强化。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", TURNS_PER_CHARGE, (int)TURNS_PER_CHARGE_REGEN, chargeCapValue());
	}

	protected int chargeCapValue() {
		return CHARGE_CAP;
	}

	protected float turnsPerCharge() {
		return TURNS_PER_CHARGE_REGEN;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_HIDE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_HIDE.equals(action)) return;

		if (charge <= 0) {
			GLog.w(Messages.get(this, "no_charge"));
			return;
		}
		int turns = TURNS_PER_CHARGE * charge;
		charge = 0;
		partialCharge = 0;
		Buff.affect(hero, Invisibility.class, turns);
		GLog.p(Messages.get(this, "hidden"));
		hero.spendAndNext(1f);
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Recharge();
	}

	public class Recharge extends ArtifactBuff {
		@Override
		public boolean act() {
			chargeCap = chargeCapValue();
			if (charge < chargeCap) {
				partialCharge += 1f / turnsPerCharge();
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
