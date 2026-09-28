/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Tar;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ZongZi extends CompleteFood {
	{
		image = ItemSpriteSheet.SPS_ZONGZI;
		energy = 600f;
		stackable = false;
	}
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, Tar.class);
		Buff.affect(hero, Slow.class, 30f);
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 4);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}
	@Override public int value() { return 60 * quantity; }
}
