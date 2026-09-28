/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class HighLight extends FlavourBuff {
	public static final float DURATION = 500f;
	public static final int DISTANCE = 10;
	{ type = buffType.NEUTRAL; announced = true; }

	@Override public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		if (Dungeon.level != null) {
			target.viewDistance = Math.max(Dungeon.level.viewDistance, DISTANCE);
			Dungeon.observe();
		}
		return true;
	}

	@Override public void detach() {
		if (Dungeon.level != null) {
			target.viewDistance = Dungeon.level.viewDistance;
			Dungeon.observe();
		}
		super.detach();
	}

	@Override public int icon() { return BuffIndicator.LIGHT; }
	@Override public void fx(boolean on) {
		if (target.sprite == null) return;
		if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
		else target.sprite.remove(CharSprite.State.ILLUMINATED);
	}
}
