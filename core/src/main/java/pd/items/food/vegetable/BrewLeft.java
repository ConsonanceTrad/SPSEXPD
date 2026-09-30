package pd.items.food.vegetable;

import pd.actors.buffs.Hunger;
import pd.sprites.ItemSpriteSheet;

public class BrewLeft extends Vegetable {
	{ image = ItemSpriteSheet.BREW_LEFT; energy = Hunger.HUNGRY / 10f; hornValue = 0; }
}
