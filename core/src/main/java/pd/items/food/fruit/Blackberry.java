package pd.items.food.fruit;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.buffs.MindVision;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

public class Blackberry extends Fruit {
	{ image = ItemSpriteSheet.BLACKBERRY; }
	@Override protected void onEat(Hero hero) {
		int healing = Math.max(hero.HT / (Random.Int(5) == 0 ? 8 : 10), 15);
		Buff.affect(hero, Healing.class).setHeal(healing, 0.25f, 0);
		if (Random.Int(5) == 0) {
			Buff.prolong(hero, MindVision.class, MindVision.DURATION);
			Dungeon.observe();
		}
	}
}
