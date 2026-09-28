/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.armorbuff;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSnow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cold;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostIce;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;

public class GlyphIce extends ArmorGlyphBuff {
	{
		immunities.add(Frost.class);
		immunities.add(Cold.class);
		immunities.add(Chill.class);
		immunities.add(WeatherOfSnow.class);
		immunities.add(FrostIce.class);
		immunities.add(DamageType.Ice.class);
	}
}
