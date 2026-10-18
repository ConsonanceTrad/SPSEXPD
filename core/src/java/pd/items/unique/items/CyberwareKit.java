/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.consum.materials.CyberMaterial;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 义体工具箱 —— 苦修者的开局道具（面具 B 路线）。
 *
 * <p><b>不是神器</b>：它是放在背包里直接使用的物品，消耗若干_义体材料_，
 * 随机造出一台_临时义体_（电量有限，用完损毁）。</p>
 */
public class CyberwareKit extends Item implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_BASE_B;

	public static final String AC_BUILD = "BUILD";

	/** 造一台义体需要的材料数。 */
	public static final int MATERIAL_COST = 2;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(CyberwareKit.class)
			.t("name", "义体工具箱")
			.t("ac_build", "制造义体")
			.t("no_material", "你没有足够的义体材料（需要 %d 份）。")
			.t("built", "工具箱咔哒一声，弹出了一台%s。")
			.t("full", "背包塞不下了。")
			.t("desc", "一只永远在微微震动的工具箱，打开时能听见里面有东西在拧螺丝。\n\n它放在背包里就能用——「制造义体」：消耗 %1$d 份_义体材料_，随机造出一台_临时义体_。\n\n临时义体靠_电量_驱动，用几次就会损毁。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		//SPSEXPD: 背包道具——默认动作就是制造义体，可直接放进快捷栏
		defaultAction = AC_BUILD;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", materialCost());
	}

	/** 制造需要的材料数。 */
	protected int materialCost() {
		return MATERIAL_COST;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_BUILD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_BUILD.equals(action)) {
			build(hero);
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 扣材料 → 随机造一台义体。 */
	protected void build(Hero hero) {
		CyberMaterial material = hero.belongings.getItem(CyberMaterial.class);
		int have = material == null ? 0 : material.quantity();
		if (have < materialCost()) {
			GLog.w(Messages.get(this, "no_material", materialCost()));
			return;
		}
		if (have > materialCost()) {
			material.quantity(have - materialCost());
		} else {
			material.detach(hero.belongings.backpack);
		}

		Cyberware ware = Cyberwares.randomCyberware();
		if (ware.collect()) {
			GLog.p(Messages.get(this, "built", ware.name()));
		} else {
			GLog.w(Messages.get(this, "full"));
			Dungeon.level.drop(ware, hero.pos);
		}
		hero.spendAndNext(1f);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
