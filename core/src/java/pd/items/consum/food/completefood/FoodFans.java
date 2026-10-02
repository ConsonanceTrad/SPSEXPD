/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class FoodFans extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FoodFans.class)
			.t("name", "粉丝")
			.t("desc", "注意煮熟后食用。\n使用_1份药水、2份坚果_炼金。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 150f; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, ShieldArmor.class).level(hero.HT / 2);
		Buff.affect(hero, Bless.class, 50f);
	}
	@Override public int value() { return 20 * quantity; }
}
