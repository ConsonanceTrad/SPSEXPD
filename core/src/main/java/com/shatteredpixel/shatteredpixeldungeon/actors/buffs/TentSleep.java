/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Inferno;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Protection granted while the hero spends food energy resting in an SPS tent. */
public class TentSleep extends FlavourBuff {

	{
		type = buffType.NEUTRAL;
		immunities.add(Blizzard.class);
		immunities.add(ConfusionGas.class);
		immunities.add(CorrosiveGas.class);
		immunities.add(Electricity.class);
		immunities.add(Fire.class);
		immunities.add(Freezing.class);
		immunities.add(Inferno.class);
		immunities.add(ParalyticGas.class);
		immunities.add(StenchGas.class);
		immunities.add(StormCloud.class);
		immunities.add(ToxicGas.class);
		immunities.add(Web.class);
	}

	@Override
	public int icon() {
		return BuffIndicator.MAGIC_SLEEP;
	}

	@Override
	public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		Buff.detach(target, Ooze.class);
		Buff.detach(target, Slow.class);
		Buff.detach(target, Cripple.class);
		Buff.detach(target, Burning.class);
		Buff.detach(target, Frost.class);
		Buff.detach(target, Chill.class);
		Buff.detach(target, Paralysis.class);
		target.invisible++;
		Dungeon.observe();
		return true;
	}

	@Override
	public void detach() {
		if (target != null && target.invisible > 0) target.invisible--;
		super.detach();
		Dungeon.observe();
	}
}
