/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.armorbuff;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfDead;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CountDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DeadRaise;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfLich;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Fiend;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;

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
