/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.armorbuff;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SwampGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSand;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GrowSeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AcidOoze;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentEarth2;

public class GlyphEarth extends ArmorGlyphBuff {
	{
		immunities.add(Roots.class);
		immunities.add(AcidOoze.class);
		immunities.add(Poison.class);
		immunities.add(WeatherOfSand.class);
		immunities.add(EnchantmentEarth.class);
		immunities.add(EnchantmentEarth2.class);
		immunities.add(GrowSeed.class);
		immunities.add(SwampGas.class);
		immunities.add(DamageType.Earth.class);
	}
}
