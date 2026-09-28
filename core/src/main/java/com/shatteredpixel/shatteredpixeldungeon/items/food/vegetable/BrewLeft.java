package com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BrewLeft extends Vegetable {
	{ image = ItemSpriteSheet.BREW_LEFT; energy = Hunger.HUNGRY / 10f; hornValue = 0; }
}
