package com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PhysicalEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BattleFlower extends Vegetable {
	{ image = ItemSpriteSheet.STAR_FLOWER; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Bless.class, 30f);
		Buff.affect(hero, ArcaneArmor.class).set(3 + hero.lvl / 4, 30);
		Buff.affect(hero, PhysicalEmpower.class).set(3 + hero.lvl / 3, 5);
	}
}
