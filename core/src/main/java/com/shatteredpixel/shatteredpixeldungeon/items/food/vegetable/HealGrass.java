package com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class HealGrass extends Vegetable {
	{ image = ItemSpriteSheet.HEAL_LEAF; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Healing.class).setHeal(20, 0.25f, 0);
		Buff.affect(hero, ArcaneArmor.class).set(Math.max(1, hero.HT / 5), 20);
	}
}
