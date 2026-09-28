package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BlueMilk extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_BLUEMILK; }
	public BlueMilk() { this(1); }
	public BlueMilk(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			Buff.affect(mob, Slow.class, 50f);
			Buff.affect(mob, AttackDown.class, 50f).level(50);
		}
		Buff.affect(hero, HasteBuff.class, 10f);
		Buff.affect(hero, BerryRegeneration.class).level(hero.HP / 2);
	}
}
