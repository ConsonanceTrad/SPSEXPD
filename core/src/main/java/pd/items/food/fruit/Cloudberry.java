package pd.items.food.fruit;

import pd.Dungeon;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Levitation;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class Cloudberry extends Fruit {
	{ image = ItemSpriteSheet.CLOUDBERRY; }
	@Override protected void onEat(Hero hero) {
		int roll = Random.Int(10);
		Buff.prolong(hero, HasteBuff.class, HasteBuff.DURATION);
		if (roll >= 6 && Dungeon.legacyDepth() < 51) {
			Buff.prolong(hero, Levitation.class, roll == 9 ? 20f : 10f);
		}
		if (roll == 9) {
			Buff.affect(hero, BerryRegeneration.class).level(Math.max(hero.HT / 10, 15));
		}
	}
}
