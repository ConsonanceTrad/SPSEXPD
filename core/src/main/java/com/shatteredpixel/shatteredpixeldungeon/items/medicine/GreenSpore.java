package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dewcharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class GreenSpore extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_GREEN_SPORE; }
	@Override protected void onUse(Hero hero) {
		if (!Dungeon.dewWater && !Dungeon.dewDraw) {
			GLog.w(Messages.get(this, "not_time"));
			return;
		}
		Buff.affect(hero, Dewcharge.class, 100f);
	}
	@Override public int value() { return 20 * quantity; }
}
