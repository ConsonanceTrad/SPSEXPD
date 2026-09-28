package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfMight extends SpsPotion {
	{ image = ItemSpriteSheet.SPS_POTION_MIGHT; }
	@Override public void apply(Hero hero) {
		Buff.affect(hero, Barkskin.class).set(8 + hero.lvl / 2, 360);
		Buff.affect(hero, ElixirOfMight.HTBoost.class).reset();
		hero.updateHT(true);
		hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 200 * quantity; }
}
