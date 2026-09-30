/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.items.Garbage;
import pd.items.Generator;
import pd.items.Item;
import pd.items.eggs.Egg;
import pd.items.rings.Ring;
import pd.items.summon.ActiveMrDestructo;
import pd.items.summon.FairyCard;
import pd.items.summon.Mobile;
import pd.items.wands.Wand;
import pd.items.wands.WandOfMagicMissile;
import watabou.utils.Random;

/** ARealMan's original ring-and-wand experiment. */
public class WndMix extends WndSpsRecipe {
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
