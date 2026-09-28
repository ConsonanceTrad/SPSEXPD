/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HolyStun;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Brick extends MeleeThrowWeapon {
	public Brick() { super(1, 8, 8, ItemSpriteSheet.SPS_EASTER_BRICK); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.prolong(defender, HolyStun.class, 2f);
		if (Random.Int(80) == 1) {
			destroy(attacker);
			dropRandomItems(defender.pos, 3);
			if (Gdx.app != null) GLog.n(Messages.get(KindOfWeapon.class, "destory"));
		}
		return super.proc(attacker, defender, damage);
	}
}
