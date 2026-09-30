/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs.randomone;

import pd.actors.hero.Hero;
import pd.items.eggs.RandomEasterEgg;
import pd.items.sellitem.VIPcard;
import watabou.utils.Random;

/** Opens into one of all twelve monthly pet soul packs. */
public class RandomMonthEgg extends RandomEgg {
	@Override
	public void execute(Hero hero, String action) {
		if (!AC_USE.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (Random.Int(10) == 0) drop(new VIPcard(), hero);
		drop(Random.Int(10) == 0 ? new RandomEasterEgg() : monthEgg(Random.Int(12)), hero);
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
	}
}
