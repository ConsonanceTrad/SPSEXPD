/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SuperArcane;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Meatroll extends CompleteFood {

	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0xCC6600);

	{
		image = ItemSpriteSheet.HOTDOG;
		energy = 250f;
	}

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, Recharging.class, 20f);
		Buff.affect(hero, SuperArcane.class, 40f).level(5);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}

	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public int value() { return 3 * quantity; }
}
