package pd.items.food.fruit;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FullMoonStrength;
import pd.actors.buffs.Light;
import pd.actors.buffs.MoonFury;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class FullMoonberry extends Fruit {
	{ image = ItemSpriteSheet.FULLMOONBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, MoonFury.class);
		Buff.affect(hero, FullMoonStrength.class);
		Buff.prolong(hero, Light.class, Light.DURATION);
		if (Random.Int(2) == 1) Buff.affect(hero, Barkskin.class).set(hero.lvl, 1);
	}
	@Override public int value() { return 5 * quantity; }
}
