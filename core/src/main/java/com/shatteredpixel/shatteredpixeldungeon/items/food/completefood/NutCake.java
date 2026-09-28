/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class NutCake extends CompleteFood {
	{
		image = ItemSpriteSheet.SPS_NUT_CAKE;
		energy = 450f;
	}
	@Override protected void doEat(Hero hero) {
		hero.HTBoost += Random.Int(7, 14);
		int permanentHT = hero.permanentHT();
		heal(hero, (permanentHT - hero.HP) / 2);
		Buff.affect(hero, ShieldArmor.class).level(hero.HT / 3);
		hero.updateHT(true);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
	}
	@Override public int value() { return quantity; }
}
