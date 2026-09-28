package com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class DreamLeaf extends Vegetable {
	{ image = ItemSpriteSheet.DREAM_LEAF; }
	@Override protected void onEat(Hero hero) {
		PotionOfHealing.cure(hero);
	}
}
