/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.Buff;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.GlassShield;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.MechArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

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
