/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.armorbuff;

import pd.actors.blobs.SwampGas;
import pd.actors.blobs.weather.WeatherOfSand;
import pd.actors.buffs.AcidOoze;
import pd.actors.buffs.GrowSeed;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.damagetype.DamageType;
import pd.items.equipment.weapon.enchantments.EnchantmentEarth2;
import pd.items.equipment.weapon.enchantments.EnchantmentEarth;

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
