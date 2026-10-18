/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.consum.materials.CyberMaterial;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;

/**
 * SPSEXPD: 重组机 —— 苦修者二档（王冠 B 路线）的神器。
 *
 * <p>在回收机的「击杀/食物 → 材料」之外，多一条反向路：_材料 → 随机食物_。</p>
 */
public class Recombiner extends Salvager {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T2_2_RECOMBINER;

	public static final String AC_COOK = "COOK";

	/** 做一份食物需要的材料数。 */
	public static final int COOK_COST = 2;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Recombiner.class)
			.t("name", "重组机")
			.t("ac_convert", "回收")
			.t("ac_cook", "合成食物")
			.t("prompt", "选择要回收的有机食物")
			.t("not_food", "那东西不是有机食物。")
			.t("converted", "重组机把它拆成了 %d 份义体材料。")
			.t("killed", "重组机从尸骸里抽出了 %d 份义体材料。")
			.t("no_material", "义体材料不够（需要 %d 份）。")
			.t("cooked", "重组机吐出了一份%s。")
			.t("desc", "一台会反着转的重组机，出料口偶尔还冒着热气。\n\n「回收」：把_有机食物_拆成 %1$d 份_义体材料_；_击杀生物_也会额外掉材料。\n\n「合成食物」：反过来消耗 %2$d 份_义体材料_，做出一份_随机食物_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		//SPSEXPD: 「回收」是默认动作（与升级线一致）；「合成食物」仍在动作列表里
		defaultAction = AC_CONVERT;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", MATERIALS_PER_FOOD, COOK_COST);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_COOK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_COOK.equals(action)) {
			cook(hero);
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 材料 → 随机食物。 */
	protected void cook(Hero hero) {
		CyberMaterial material = hero.belongings.getItem(CyberMaterial.class);
		int have = material == null ? 0 : material.quantity();
		if (have < COOK_COST) {
			GLog.w(Messages.get(this, "no_material", COOK_COST));
			return;
		}
		if (have > COOK_COST) {
			material.quantity(have - COOK_COST);
		} else {
			material.detach(hero.belongings.backpack);
		}

		//SPSEXPD: 随机挑一份食物
		pd.items.Item food = null;
		for (int i = 0; i < 40 && food == null; i++) {
			pd.items.Item candidate = pd.items.Generator.random();
			if (candidate instanceof pd.items.consum.food.Food) food = candidate;
		}
		if (food != null && food.collect()) {
			GLog.p(Messages.get(this, "cooked", food.name()));
		} else if (food != null && Dungeon.level != null) {
			Dungeon.level.drop(food, hero.pos);
		}
		hero.spendAndNext(1f);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
