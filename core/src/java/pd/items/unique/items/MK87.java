/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.buffs.Barkskin;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: MK-87 —— 星兵二档（王冠 B 路线）的特殊护甲。
 *
 * <p>由炼金能量驱动的多功能装甲：可以消耗炼金能量（{@link Dungeon#energy}）触发一次「过载」，
 * 短时间内获得额外护甲，攻防一体。它同时是阶位 5 的高阶护甲。</p>
 */
public class MK87 extends NormalArmor implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.SOLDIER_GUN_T2_2_MK87;

	public static final String AC_OVERLOAD = "OVERLOAD";
	/** 一次过载消耗的炼金能量。 */
	public static final int ENERGY_COST = 10;
	/** 过载提供的额外护甲。 */
	public static final int OVERLOAD_ARMOR = 3;
	/** 过载持续的回合数。 */
	public static final int OVERLOAD_TURNS = 6;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(MK87.class)
			.t("name", "MK-87")
			.t("ac_overload", "过载")
			.t("no_energy", "炼金能量不足，无法过载。")
			.t("overloaded", "装甲表面泛起一层亮蓝的纹路——能量灌满全身！")
			.t("desc", "一套用炼金能量驱动的多功能装甲，外壳上刻满了导能的纹路。\n\n「过载」：消耗 _%1$d 点炼金能量_，在 _%2$d 回合_内额外获得 _%3$d 点护甲_——攻防一体，代价只是能量。\n\n它本身的防护也相当出色（阶位 5）。");
	}

	public MK87() {
		super(5, 1.2f, 1f, 5, 0, 10, 0, 1, 5, SpecificPlaceHolderDict.SPS_PH_ARMOR_SPARE);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", ENERGY_COST, OVERLOAD_TURNS, OVERLOAD_ARMOR);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_OVERLOAD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_OVERLOAD.equals(action)) return;

		if (Dungeon.energy < ENERGY_COST) {
			GLog.w(Messages.get(this, "no_energy"));
			return;
		}
		Dungeon.energy -= ENERGY_COST;
		Barkskin.conditionallyAppend(hero, OVERLOAD_ARMOR, OVERLOAD_TURNS);
		GLog.p(Messages.get(this, "overloaded"));
		hero.spendAndNext(1f);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
