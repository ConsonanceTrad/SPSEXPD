/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Kebab extends CompleteFood {
	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0xCC6600);
	{ image = ItemSpriteSheet.KEBAB; energy = 330f; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 4);
		Buff.affect(hero, AttackUp.class, 50f).level(40);
	}
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public int value() { return 5 * quantity; }
}
