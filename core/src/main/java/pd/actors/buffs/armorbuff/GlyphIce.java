/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.armorbuff;

import pd.actors.blobs.weather.WeatherOfSnow;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Cold;
import pd.actors.buffs.Frost;
import pd.actors.buffs.FrostIce;
import pd.actors.damagetype.DamageType;

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
