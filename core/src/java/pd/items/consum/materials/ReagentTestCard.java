/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import java.util.ArrayList;

import pd.atlas.items.ConsumGoodsMaterialsMaterialsExtraDict;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.potions.BenignConcoction;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionOfFrost;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.potions.PotionOfLiquidFlame;
import pd.items.consum.potions.PotionOfMindVision;
import pd.items.consum.potions.PotionOfPurity;
import pd.items.consum.potions.PotionOfToxicGas;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;

/**
 * SPSEXPD: 试剂检测卡（docs/alchemy-rework.md §4.2，裁决 #2）。
 *
 * <p>商店贩售的简易试剂：能认出最常见的 6 种药剂（治疗、灵视、净化、火焰、冰霜、毒气）——
 * 直接揭示那一瓶；其它药剂只能判断「有益 / 有害」，不揭示具体类型。
 * **每次使用消耗 1 张。**
 */
public class ReagentTestCard extends Item {

	public static final String AC_TEST = "TEST";

	/** 检测卡能直接认出的 6 种常规药剂。 */
	private static final Class<?>[] COMMON = {
			PotionOfHealing.class,
			PotionOfMindVision.class,
			PotionOfPurity.class,
			PotionOfLiquidFlame.class,
			PotionOfFrost.class,
			PotionOfToxicGas.class
	};

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ReagentTestCard.class)
			.t("name", "试剂检测卡")
			.t("desc", "商店贩售的简易试剂卡。把它贴上药瓶，能认出最常见的六种药剂"
					+ "（治疗、灵视、净化、火焰、冰霜、毒气）；遇上认不出的药剂，"
					+ "它只会告诉你这似乎不是常见的药水类型。每张只能用一次。")
			.t("ac_test", "检测药剂")
			.t("prompt", "选择一瓶未鉴定药剂")
			.t("common", "检测卡上显出熟悉的反应：这是%1$s。")
			.t("uncommon", "这似乎不是常见的药水类型。")
			.t("no_potion", "你身上没有未鉴定的药剂可以检测。");
	}

	{
		image = ConsumGoodsMaterialsMaterialsExtraDict.DRY_TWIG;
		stackable = true;
		defaultAction = AC_TEST;
		bones = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_TEST);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_TEST.equals(action)) return;

		curUser = hero;
		boolean any = false;
		for (Item item : hero.belongings.backpack.items) {
			if (isTestable(item)) { any = true; break; }
		}
		if (!any) {
			GLog.w(Messages.get(this, "no_potion"));
			return;
		}
		GameScene.selectItem(selector);
	}

	private static boolean isTestable(Item item) {
		return item instanceof Potion && !((Potion) item).identified;
	}

	private static final WndBag.ItemSelector selector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(ReagentTestCard.class, "prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return isTestable(item);
		}

		@Override
		public void onSelect(Item item) {
			if (!(item instanceof Potion)) return;
			Potion potion = (Potion) item;

			//消耗 1 张检测卡（玩家取消选择时不消耗）
			Item card = curUser.belongings.getItem(ReagentTestCard.class);
			if (card != null) card.detach(curUser.belongings.backpack);

			boolean common = false;
			for (Class<?> type : COMMON) {
				if (type.isInstance(potion)) { common = true; break; }
			}

			if (common) {
				potion.identify();
				GLog.p(Messages.get(ReagentTestCard.class, "common", Messages.titleCase(potion.name())));
			} else {
				GLog.w(Messages.get(ReagentTestCard.class, "uncommon"));
			}

			curUser.spendAndNext(1f);
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
		return 30 * quantity;
	}
}
