/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.items.Garbage;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.eggs.Egg;
import pd.items.equipment.rings.Ring;
import pd.items.summon.ActiveMrDestructo;
import pd.items.summon.FairyCard;
import pd.items.summon.Mobile;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.wands.WandOfMagicMissile;
import render.utils.math.Random;
import pd.messages.InlineText;

/** ARealMan's original ring-and-wand experiment. */
public class WndMix extends WndSpsRecipe {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndMix.class)
			.t("title", "矮人的奇妙实验")
			.t("text", "放入法杖或戒指，然后进行一次奇妙的实验吧。这会花费1000金币。")
			.t("select", "选择一件道具")
			.t("combine", "合成")
			.t("cancel", "取消");
	}

	private static final int COST = 1000;
	public WndMix() { super(new WandOfMagicMissile(), COST); }
	@Override protected boolean accepts(Item item) { return item instanceof Ring || item instanceof Wand; }
	@Override protected int goldCost() { return COST; }
	@Override protected Item mix(Item[] items) { return createResult(items); }
	public static Item createResult(Item[] items) {
		int rings = 0, wands = 0;
		for (Item item : items) {
			if (item instanceof Ring) rings++;
			else if (item instanceof Wand) wands++;
		}
		if (rings == 3 || wands == 3) return fallback(Generator.random(Generator.Category.ARTIFACT));
		if (rings == 2 && wands == 1 || rings == 1 && wands == 2) return new Egg();
		if (rings == 1 && wands == 1) {
			switch (Random.Int(3)) {
				case 0: return new FairyCard();
				case 1: return new Mobile();
				default: return new ActiveMrDestructo();
			}
		}
		if (rings == 2) return fallback(Generator.random(Generator.Category.RING));
		if (rings == 1) return fallback(Generator.random(Generator.Category.WAND));
		if (wands == 2) return fallback(Generator.random(Generator.Category.WAND));
		if (wands == 1) return fallback(Generator.random(Generator.Category.RING));
		return new Garbage();
	}
	private static Item fallback(Item item) { return item == null ? new Garbage() : item; }
}
