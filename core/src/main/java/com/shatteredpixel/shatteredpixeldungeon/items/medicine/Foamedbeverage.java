package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EarthImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ToxicImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Foamedbeverage extends Pill {
	{ image = ItemSpriteSheet.FOAMED; }
	public Foamedbeverage() {}
	public Foamedbeverage(int number) { quantity = number; }
	@Override protected void onUse(Hero hero) {
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.affect(hero, Bless.class, 30f);
		Buff.affect(hero, BerryRegeneration.class).level(hero.HT / 4);
		switch (Random.Int(4)) {
			case 0: Buff.affect(hero, FireImbue.class).set(60f); break;
			case 1: Buff.affect(hero, FrostImbue.class, 60f); break;
			case 2: Buff.affect(hero, ToxicImbue.class).set(60f); break;
			default: Buff.affect(hero, EarthImbue.class, 60f); break;
		}
	}
}
