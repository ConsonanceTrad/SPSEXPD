/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.buffs.Buff;
import pd.actors.buffs.SandevistanTime;
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
 * SPSEXPD: 斯安威斯坦 —— 苦修者一档（面具 A 路线）的神器。
 *
 * <p>开启后进入_子弹时间_：这段时间里只有你在动，敌人全部停住；
 * 你的攻击_照常即时结算_，充能耗尽或时间走完才恢复。</p>
 */
public class Sandevistan extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T1_1_SANDEVISTAN;

	public static final String AC_ACTIVATE = "ACTIVATE";

	/** 每点充能换来的子弹时间回合。 */
	public static final int TURNS_PER_CHARGE = 3;
	/** 充能上限。 */
	public static final int CHARGE_CAP = 4;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Sandevistan.class)
			.t("name", "斯安威斯坦")
			.t("ac_activate", "开启")
			.t("no_charge", "斯安威斯坦还没充上电。")
			.t("activated", "世界忽然慢了半拍——只有你还在动。")
			.t("desc", "一截脊椎形的义体，接上时你会先听见自己血流的声音。\n\n「开启」：把_全部充能_一次性放掉，每 1 点充能换来 _%1$d 回合_的_子弹时间_。\n\n子弹时间里_只有你在动_，敌人全部停住；你的攻击_照常即时结算_，不用等到结束。\n\n充能会随时间自行恢复（上限 %2$d）。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		chargeCap = chargeCapValue();
		charge = chargeCap;
		//SPSEXPD: 「开启」是默认动作——可放进快捷栏，点一下直接开子弹时间
		defaultAction = AC_ACTIVATE;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", TURNS_PER_CHARGE, chargeCapValue());
	}

	/** 充能上限（二代斯安威斯坦覆写为加倍）。 */
	protected int chargeCapValue() {
		return CHARGE_CAP;
	}

	/** 每多少回合恢复 1 点充能。 */
	protected float turnsPerCharge() {
		return 10f;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ACTIVATE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_ACTIVATE.equals(action)) {
			chargeCap = chargeCapValue();
			if (charge <= 0) {
				GLog.w(Messages.get(this, "no_charge"));
				return;
			}
			int turns = TURNS_PER_CHARGE * charge;
			charge = 0;
			partialCharge = 0;
			Buff.affect(hero, SandevistanTime.class).setup(turns);
			GLog.p(Messages.get(this, "activated"));
			hero.spendAndNext(1f);
			return;
		}
		super.execute(hero, action);
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
