/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Vegetablekebab extends CompleteFood {

	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x22CC44);

	{
		image = ItemSpriteSheet.KEBAB;
		energy = 150f;
	}

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 2);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}

	@Override public ItemSprite.Glowing glowing() { return GREEN; }
	@Override public int value() { return 2 * quantity; }
}
