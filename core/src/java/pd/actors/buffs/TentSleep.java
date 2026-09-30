/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.Blizzard;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.CorrosiveGas;
import pd.actors.blobs.Electricity;
import pd.actors.blobs.Fire;
import pd.actors.blobs.Freezing;
import pd.actors.blobs.Inferno;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.StenchGas;
import pd.actors.blobs.StormCloud;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.Web;
import pd.ui.BuffIndicator;

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
