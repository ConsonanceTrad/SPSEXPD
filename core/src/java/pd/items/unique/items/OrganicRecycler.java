/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.consum.materials.CyberMaterial;
import pd.items.equipment.artifacts.Artifact;
import pd.items.consum.food.Food;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;

import java.util.ArrayList;

/**
 * SPSEXPD: 有机重组机 —— 苦修者的初始神器（面具 A 路线）。
 *
 * <p>把_有机食物_丢进去，重组成_义体材料_。</p>
 */
public class OrganicRecycler extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_BASE_A;

	public static final String AC_CONVERT = "CONVERT";

	/** 每份食物产出的材料数。 */
	public static final int MATERIALS_PER_FOOD = 2;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(OrganicRecycler.class)
			.t("name", "有机重组机")
			.t("ac_convert", "重组")
			.t("prompt", "选择要重组的有机食物")
			.t("not_food", "那东西不是有机食物。")
			.t("converted", "重组机把那口吃的拆成了 %d 份义体材料。")
			.t("desc", "一台内脏外翻的小机器，进料口永远湿的。\n\n「重组」：把一件_有机食物_丢进去，它会拆出 %1$d 份_义体材料_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", MATERIALS_PER_FOOD);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_CONVERT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CONVERT.equals(action)) {
			GameScene.selectItem(foodSelector);
			return;
		}
		super.execute(hero, action);
	}

	private final WndBag.ItemSelector foodSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(OrganicRecycler.class, "prompt"); }
		@Override public boolean itemSelectable(Item item) {
			return item instanceof Food;
		}
		@Override public void onSelect(Item item) {
			if (!(item instanceof Food)) {
				GLog.w(Messages.get(OrganicRecycler.class, "not_food"));
				return;
			}
			int amount = MATERIALS_PER_FOOD;
			if (item.quantity() > 1) {
				item.quantity(item.quantity() - 1);
			} else {
				item.detach(Dungeon.hero.belongings.backpack);
			}
			grantMaterials(amount);
			GLog.i(Messages.get(OrganicRecycler.class, "converted", amount));
		}
	};

	/** SPSEXPD: 往背包里塞若干材料。 */
	public static void grantMaterials(int amount) {
		CyberMaterial material = new CyberMaterial();
		material.quantity(amount);
		if (!material.collect()) {
			Dungeon.level.drop(material, Dungeon.hero.pos);
		}
	}

	/** SPSEXPD: 神器不提供常驻 buff。 */
	@Override
	protected ArtifactBuff passiveBuff() {
		return new Idle();
	}

	public class Idle extends ArtifactBuff {
		@Override
		public boolean act() {
			spend(TICK);
			return true;
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
