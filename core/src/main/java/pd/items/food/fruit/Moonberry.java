package pd.items.food.fruit;

import pd.actors.buffs.AdrenalineSurge;
import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class Moonberry extends Fruit {
	{ image = ItemSpriteSheet.MOONBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, AdrenalineSurge.class).reset(1, 40f);
		if (Random.Int(2) == 0) Buff.affect(hero, Barkskin.class).set(hero.lvl, 30);
	}
}
