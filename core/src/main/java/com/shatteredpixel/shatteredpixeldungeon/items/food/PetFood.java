package com.shatteredpixel.shatteredpixeldungeon.items.food;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class PetFood extends Food {
	{ image = ItemSpriteSheet.PET_FOOD; energy = 10f; }
	@Override public int value() { return quantity; }
}
