/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.armorbuff;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs.ElectriShock;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Locked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shocked;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shell;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;

public class GlyphElectricity extends ArmorGlyphBuff {
	{
		resistances.add(Shell.class);
		immunities.add(WandOfLightning.class);
		immunities.add(Shocked.class);
		immunities.add(ElectriShock.class);
		immunities.add(Electricity.class);
		immunities.add(Locked.class);
		immunities.add(DamageType.Shock.class);
	}
}
