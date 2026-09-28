/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class DragonBoat extends MeleeThrowWeapon {
	public DragonBoat() { super(1, 5, 10, ItemSpriteSheet.SPS_DRAGON_BOAT); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.prolong(defender, Paralysis.class, 3f);
		if (Random.Int(100) == 1) {
			destroy(attacker);
			Buff.affect(defender, Bleeding.class).set(50);
			if (Gdx.app != null) GLog.n(Messages.get(KindOfWeapon.class, "destory"));
		}
		return super.proc(attacker, defender, damage);
	}
}
