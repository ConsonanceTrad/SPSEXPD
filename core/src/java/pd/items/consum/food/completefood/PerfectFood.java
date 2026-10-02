/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Light;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class PerfectFood extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PerfectFood.class)
			.t("name", "完美便当")
			.t("desc", "这是烹饪的最高杰作，完美便当。\n使用_蔬菜、原石、主食、水、水果各1份_炼金；或使用_1份鱼饼_炼金。");
	}



	{ image = ConsumFoodFoodDict.PERFECT_MEAL; energy = 600f; }
	@Override protected void doEat(Hero hero) {
		increaseMaxHealth(hero, 3, 7);
		Buff.affect(hero, Bless.class, 50f);
		Buff.affect(hero, Light.class, 50f);
		Buff.affect(hero, HasteBuff.class, 25f);
		Buff.affect(hero, Levitation.class, 25f);
	}
	@Override public int value() { return 50 * quantity; }
}
