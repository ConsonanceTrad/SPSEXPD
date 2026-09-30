/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.meleethrow;

import com.badlogic.gdx.Gdx;
import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.items.KindOfWeapon;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import render.utils.math.Random;

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
