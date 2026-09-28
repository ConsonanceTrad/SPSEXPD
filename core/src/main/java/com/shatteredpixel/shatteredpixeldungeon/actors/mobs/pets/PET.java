/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenceUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HiddenShadow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpeedUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WatchOut;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorruptGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.NmGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.VenomGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;

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
