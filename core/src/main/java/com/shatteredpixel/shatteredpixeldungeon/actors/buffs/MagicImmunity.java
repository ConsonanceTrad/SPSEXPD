/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorruptGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BrokenRobot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class MagicImmunity extends FlavourBuff {

	public static final float DURATION = 10f;

	{
		type = buffType.POSITIVE;
		announced = true;
		immunities.add(ParalyticGas.class);
		immunities.add(ToxicGas.class);
		immunities.add(ConfusionGas.class);
		immunities.add(StenchGas.class);
		immunities.add(Burning.class);
		immunities.add(Poison.class);
		immunities.add(Electricity.class);
		immunities.add(Warlock.class);
		immunities.add(Eye.class);
		immunities.add(YogFist.BurningFist.class);
		immunities.add(BrokenRobot.class);
		immunities.add(CorruptGas.class);
	}

	@Override
	public int icon() {
		return BuffIndicator.IMMUNITY;
	}
}
