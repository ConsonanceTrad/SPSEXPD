/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.armorbuff;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.TarGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hot;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Tar;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FireElemental;

public class GlyphFire extends ArmorGlyphBuff {
	{
		immunities.add(Burning.class);
		immunities.add(Fire.class);
		immunities.add(Tar.class);
		immunities.add(TarGas.class);
		immunities.add(Hot.class);
		immunities.add(DamageType.Fire.class);
		resistances.add(FireElemental.class);
	}
}
