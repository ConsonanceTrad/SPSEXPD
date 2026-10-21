/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import java.util.ArrayList;

import pd.atlas.items.ConsumGoodsMaterialsMaterialsExtraDict;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;

/**
 * SPSEXPD: 真实之眼（docs/alchemy-rework.md §4.4，裁决 #3）。
 *
 * <p>由鉴定卷轴一线炼制的结晶。对一格地面物品堆或商店货位使用，
 * 揭示该格所有物品的真实身份（含未鉴定药剂的具体类型）。
 * **单件消耗**：对一格使用、鉴定该格 Heap/商品，随后消耗。
 */
public class EyeOfTruth extends Item {

	public static final String AC_USE = "USE";

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(EyeOfTruth.class)
			.t("name", "真实之眼")
			.t("desc", "鉴定卷轴炼出的结晶，对着货物看过去时，事物的本来面目会自己浮现出来。"
					+ "指向一格地面物品或商店货位，就能看穿那上面所有东西的真面目。")
			.t("ac_use", "凝视")
			.t("prompt", "选择要凝视的物品堆或商店货位")
			.t("nothing", "那里没有你还没看透的东西。");
	}

	{
		image = ConsumGoodsMaterialsMaterialsExtraDict.DRY_TWIG;
		stackable = true;
		defaultAction = AC_USE;
		bones = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_USE.equals(action)) return;
		curUser = hero;
		curItem = this;
		GameScene.selectCell(picker);
	}

	private static final CellSelector.Listener picker = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;

			Heap heap = Dungeon.level.heaps.get(cell);
			if (heap == null || !Dungeon.level.heroFOV[cell]) {
				GLog.w(Messages.get(EyeOfTruth.class, "nothing"));
				return;
			}

			boolean revealed = false;
			for (Item item : heap.items) {
				if (!item.isIdentified()) {
					item.identify();
					revealed = true;
				}
			}
			if (!revealed) {
				GLog.i(Messages.get(EyeOfTruth.class, "nothing"));
				return;
			}

			//单件消耗
			if (curItem != null) curItem.detach(curUser.belongings.backpack);
			curUser.spendAndNext(1f);
		}

		@Override
		public String prompt() {
			return Messages.get(EyeOfTruth.class, "prompt");
		}
	};

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 60 * quantity;
	}
}
