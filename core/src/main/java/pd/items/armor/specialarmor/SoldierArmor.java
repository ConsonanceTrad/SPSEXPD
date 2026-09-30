/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.specialarmor;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.TargetShoot;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.armor.normalarmor.NormalArmor;
import pd.items.weapon.guns.GunWeapon;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class SoldierArmor extends NormalArmor {
	public SoldierArmor() { super(5, 1f, 1f, 2, 20, 40, 1, 0, 3, ItemSpriteSheet.SPS_ARMOR_SOLDIER); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) {
			if (defender instanceof Hero) {
				for (Item item : ((Hero)defender).belongings) if (item instanceof GunWeapon) ((GunWeapon)item).addRound();
			}
			Buff.affect(defender, TargetShoot.class, 10f);
		}
		return super.proc(attacker, defender, damage);
	}
}
