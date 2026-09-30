/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.armorbuff;

import pd.actors.blobs.weather.WeatherOfSun;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Charm;
import pd.actors.buffs.LightShootAttack;
import pd.actors.buffs.Vertigo;
import pd.actors.damagetype.DamageType;

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
