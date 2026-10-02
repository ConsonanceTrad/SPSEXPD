/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.items.Ankh;
import pd.items.Garbage;
import pd.items.Item;
import pd.items.equipment.armor.fusion.LifeArmor;
import pd.items.consum.eggs.AflyEgg;
import pd.items.consum.food.AflyFood;
import pd.items.consum.food.ChargrilledMeat;
import pd.items.consum.food.Food;
import pd.items.consum.food.FrozenCarpaccio;
import pd.items.consum.food.MeatPie;
import pd.items.consum.food.MysteryMeat;
import pd.items.consum.food.PhantomMeat;
import pd.items.consum.food.SmallMeat;
import pd.items.consum.food.StewedMeat;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.vegetable.Vegetable;
import pd.messages.InlineText;

/** Alfred's original three-ingredient cooking interface. */
public class WndAflyInfo extends WndSpsRecipe {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndAflyInfo.class)
			.t("title", "为了饥饿的阿比")
			.t("text", "阿比饿了。你能找来一份干粮、一份烤肉和一份浆果吗？放入其他材料可能会做出奇怪的东西。")
			.t("select", "选择一件食材")
			.t("combine", "合成")
			.t("cancel", "取消");
	}



	public WndAflyInfo() { super(new AflyFood(), 0); }
	@Override protected int goldCost() { return 0; }
	@Override protected boolean accepts(Item item) {
		return item instanceof Food || item instanceof Ankh;
	}
	@Override protected Item mix(Item[] items) { return createResult(items); }
	public static Item createResult(Item[] items) {
		int fruit = 0, vegetable = 0, staple = 0, meat = 0, ankh = 0, aflyFood = 0;
		for (Item item : items) {
			if (item instanceof AflyFood) aflyFood++;
			else if (item instanceof Fruit) fruit++;
			else if (item instanceof Vegetable) vegetable++;
			else if (isMeat(item)) meat++;
			else if (item instanceof Food) staple++;
			else if (item instanceof Ankh) ankh++;
		}
		if (fruit == 1 && staple == 1 && meat == 1) return new AflyFood();
		if (fruit == 1 && vegetable == 2) return new LifeArmor();
		if (aflyFood == 1 && ankh == 1) return new AflyEgg();
		return new Garbage();
	}
	private static boolean isMeat(Item item) {
		return item instanceof ChargrilledMeat || item instanceof FrozenCarpaccio
				|| item instanceof MeatPie || item instanceof MysteryMeat
				|| item instanceof PhantomMeat || item instanceof SmallMeat
				|| item instanceof StewedMeat;
	}
}
