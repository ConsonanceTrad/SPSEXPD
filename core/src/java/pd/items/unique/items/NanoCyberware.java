/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Haste;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.MindVision;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;

/**
 * SPSEXPD: 纳米义体 —— 苦修者二档（王冠 B 路线）的神器。
 *
 * <p>它既能像义体工具箱那样造临时义体，也能_把自己随机拟态成某一种义体的功能_，
 * 想换就一直换下去。</p>
 */
public class NanoCyberware extends CyberwareKit {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T2_2_NANO;

	public static final String AC_MORPH = "MORPH";

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(NanoCyberware.class)
			.t("name", "纳米义体")
			.t("ac_build", "制造义体")
			.t("ac_morph", "拟态")
			.t("no_material", "你没有足够的义体材料（需要 %d 份）。")
			.t("built", "纳米团块咔哒一声，弹出了一台%s。")
			.t("full", "背包塞不下了。")
			.t("morphed", "纳米团块把自己重排成了_%s_的形态。")
			.t("desc", "一小团会自己动的纳米材料，捏起来像活的。\n\n它能像工具箱一样「制造义体」，也可以「拟态」——_随机获得一种义体的功能_，想换就一直换。\n\n拟态不消耗材料，但每次都要花一回合。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_MORPH);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_MORPH.equals(action)) {
			morph(hero);
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 随机拟态成一种义体的功能。 */
	protected void morph(Hero hero) {
		int pick = Random.Int(4);
		String label;
		switch (pick) {
			case 0:
				Buff.affect(hero, Haste.class, 12f);
				label = "强化肌腱";
				break;
			case 1:
				Buff.affect(hero, Invisibility.class, 10f);
				label = "光学迷彩";
				break;
			case 2:
				Buff.affect(hero, MindVision.class, 20f);
				label = "千里眼";
				break;
			default:
				Buff.affect(hero, Barkskin.class).set(Math.max(1, hero.HT / 5), 1);
				label = "皮下护甲";
				break;
		}
		GLog.p(Messages.get(this, "morphed", label));
		hero.spendAndNext(1f);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
