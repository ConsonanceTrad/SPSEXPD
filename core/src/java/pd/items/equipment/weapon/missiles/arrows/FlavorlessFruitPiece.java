/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.actors.buffs.Hunger;
import pd.items.consum.food.Food;
import pd.messages.InlineText;

/**
 * SPSEXPD: 无味果落点生成的小块无味果——饱食度为标准干粮的 1/3。
 */
public class FlavorlessFruitPiece extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FlavorlessFruitPiece.class)
			.t("name", "小块无味果")
			.t("desc", "从无味果上剥下的一小块果肉，几乎没有什么味道，但也能填饱一点肚子。");
	}

	{
		//SPSEXPD: 贴图待指认，暂用果实系占位图
		image = pd.atlas.items.SpecificPlaceHoldeFruitDict.FRUIT_HOLDER_0;
		energy = Hunger.HUNGRY / 3f;
		hornValue = 1;
		bones = false;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return quantity; }
}
