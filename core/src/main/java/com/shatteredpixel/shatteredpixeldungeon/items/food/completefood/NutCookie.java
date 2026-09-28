/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnergyArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GlassShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MechArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class NutCookie extends CompleteFood {

	{
		image = ItemSpriteSheet.NUT_COOKIE;
		energy = 10f;
	}

	public NutCookie() { this(6); }
	public NutCookie(int number) { quantity = number; }

	@Override
	protected void doEat(Hero hero) {
		if (Random.Int(10) == 0) {
			Buff.affect(hero, GlassShield.class).turns(6);
		} else {
			switch (Random.Int(4)) {
				case 0: Buff.affect(hero, MechArmor.class).level(10); break;
				case 1: Buff.affect(hero, EnergyArmor.class).level(10); break;
				case 2: Buff.affect(hero, ShieldArmor.class).level(10); break;
				default: Buff.affect(hero, MagicArmor.class).level(10); break;
			}
		}
	}

	@Override public Item random() { quantity = 6; return this; }
	@Override public int value() { return 10 * quantity; }
}
