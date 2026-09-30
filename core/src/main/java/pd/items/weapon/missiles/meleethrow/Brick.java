/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.meleethrow;

import com.badlogic.gdx.Gdx;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.items.KindOfWeapon;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
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
