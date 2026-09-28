/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.armorbuff;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSun;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LightShootAttack;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;

public class GlyphLight extends ArmorGlyphBuff {
	{
		immunities.add(Blindness.class);
		immunities.add(Vertigo.class);
		immunities.add(Charm.class);
		immunities.add(WeatherOfSun.class);
		immunities.add(LightShootAttack.class);
		immunities.add(DamageType.Light.class);
	}
}
