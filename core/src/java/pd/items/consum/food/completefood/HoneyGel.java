/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class HoneyGel extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HoneyGel.class)
			.t("name", "蜂蜜布丁")
			.t("desc", "蜂蜜布满了凝胶。\n使用_1份凝胶、1份蜂蜜_炼金。");
	}



	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 20f; }
	@Override protected void doEat(Hero hero) { increaseMaxHealth(hero, 3, 6); }
	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
	@Override public int value() { return 400 * quantity; }
}
