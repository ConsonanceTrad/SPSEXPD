/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.armorbuff;

import pd.actors.blobs.weather.WeatherOfDead;
import pd.actors.buffs.CountDown;
import pd.actors.buffs.DeadRaise;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Silent;
import pd.actors.damagetype.DamageType;
import pd.actors.mobs.DwarfLich;
import pd.actors.mobs.Fiend;
import pd.actors.mobs.Warlock;

public class GlyphDark extends ArmorGlyphBuff {
	{
		immunities.add(STRDown.class);
		immunities.add(CountDown.class);
		immunities.add(DeadRaise.class);
		immunities.add(Silent.class);
		immunities.add(WeatherOfDead.class);
		immunities.add(DamageType.Dark.class);
		resistances.add(DwarfLich.class);
		resistances.add(Warlock.class);
		resistances.add(Fiend.class);
	}
}
