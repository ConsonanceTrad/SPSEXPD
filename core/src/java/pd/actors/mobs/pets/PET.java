/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.actors.blobs.CorruptGas;
import pd.actors.blobs.NmGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.VenomGas;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.HiddenShadow;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.SpeedUp;
import pd.actors.buffs.WatchOut;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.items.equipment.wands.Wand;

/** SPS-PD 0.9.8's common pet rules, kept above the merged pet runtime. */
public abstract class PET extends LegacyPet {

	{
		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
		immunities.add(ToxicGas.class);
		immunities.add(VenomGas.class);
		immunities.add(Burning.class);
		immunities.add(ScrollOfPsionicBlast.class);
		immunities.add(CorruptGas.class);
		immunities.add(NmGas.class);
	}

	@Override
	public void damage(int damage, Object source) {
		if (source instanceof Hero || source instanceof Wand) return;
		if (staying() && !(source instanceof Mob)) return;
		super.damage(damage, source);
	}

	@Override
	public synchronized boolean add(Buff buff) {
		if (buff instanceof AttackUp
				|| buff instanceof DefenceUp
				|| buff instanceof ShieldArmor
				|| buff instanceof MagicArmor
				|| buff instanceof HasteBuff
				|| buff instanceof SpeedUp
				|| buff instanceof HiddenShadow
				|| buff instanceof WatchOut) {
			return super.add(buff);
		}
		return false;
	}
}
