/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.rings.fusion;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfMagic extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_ARCANA;
		buffClass = RingMagic.class;
	}

	@Override
	public String statsInfo() {
		return isIdentified() ? Messages.get(this, "stats", Math.min(30, level())) : "???";
	}

	@Override
	public String upgradeStat1(int level) {
		return Integer.toString(Math.min(30, level));
	}

	@Override
	protected RingBuff buff() {
		return new RingMagic();
	}

	public static int magicSkillBonus(Char target) {
		int bonus = 0;
		for (RingMagic buff : target.buffs(RingMagic.class)) {
			bonus += Math.min(30, buff.level());
		}
		return bonus;
	}

	public class RingMagic extends RingBuff {
		@Override public int level() { return RingOfMagic.this.level(); }
		@Override public int buffedLvl() { return level(); }
	}
}
