package pd.items.food.vegetable;

import pd.actors.hero.Hero;
import pd.items.potions.PotionOfHealing;
import pd.sprites.ItemSpriteSheet;

public class DreamLeaf extends Vegetable {
	{ image = ItemSpriteSheet.DREAM_LEAF; }
	@Override protected void onEat(Hero hero) {
		PotionOfHealing.cure(hero);
	}
}
