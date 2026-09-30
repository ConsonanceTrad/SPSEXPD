/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.specialarmor;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.armor.normalarmor.NormalArmor;
import pd.items.eggs.EasterEgg;
import pd.sprites.HeroSprite;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class RenBArmor extends NormalArmor {
	private static final String CHARGE = "charge";
	private int charge = 100;

	public RenBArmor() { super(1, 1f, 1f, 1, 0, 0, 0, 0, 0, ItemSpriteSheet.SPS_BUNNY_ARMOR); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		charge--;
		if (charge < 1 && defender instanceof Hero) {
			Hero hero = (Hero)defender;
			if (hero.belongings.armor == this) {
				hero.belongings.armor = null;
				deactivate(hero);
				if (hero.sprite instanceof HeroSprite) ((HeroSprite)hero.sprite).updateArmor();
			}
			if (Dungeon.level != null) {
				Heap heap = Dungeon.level.drop(new EasterEgg(), defender.pos);
				if (heap.sprite != null) heap.sprite.drop();
			}
		}
		return super.proc(attacker, defender, damage);
	}
	public int durability() { return charge; }
	public void durability(int value) { charge = Math.max(0, Math.min(100, value)); }
	@Override public String status() { return Integer.toString(charge); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); durability(bundle.getInt(CHARGE)); }
}
