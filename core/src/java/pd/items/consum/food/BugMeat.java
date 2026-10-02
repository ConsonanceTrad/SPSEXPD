/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;

import java.util.ArrayList;
import pd.messages.InlineText;

public class BugMeat extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BugMeat.class)
			.t("name", "稽生虫")
			.t("desc", "占据背包空间的寄生虫，无法丢弃或投掷，会周期性使你减速；吃掉它会永久损失1点生命上限。")
			.t("eat_msg", "你强忍恶心吞下了寄生虫。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 50;
		hornValue = 1;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		if (hero.HT > 1) {
			hero.HTBoost--;
			hero.updateHT(false);
		}
	}

	@Override public int value() { return 350 * quantity; }
}
