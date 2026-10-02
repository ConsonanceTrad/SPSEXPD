/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.actors.buffs;

import pd.actors.blobs.Blizzard;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.CorrosiveGas;
import pd.actors.blobs.Electricity;
import pd.actors.blobs.Fire;
import pd.actors.blobs.Freezing;
import pd.actors.blobs.Inferno;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.Regrowth;
import pd.actors.blobs.SmokeScreen;
import pd.actors.blobs.StenchGas;
import pd.actors.blobs.StormCloud;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.VaultFlameTraps;
import pd.actors.blobs.Web;
import pd.actors.mobs.Tengu;
import pd.levels.rooms.special.MagicalFireRoom;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class BlobImmunity extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BlobImmunity.class)
			.t("name", "净化屏障")
			.t("desc", "一种奇怪的能量环绕在你的周围，为你阻挡有害的环境效果。\n\n在净化屏障的持续时间内，你将免疫所有负面环境效果。\n\n免疫效果剩余时长：%s回合");
	}



	
	{
		type = buffType.POSITIVE;
	}
	
	public static final float DURATION	= 20f;
	
	@Override
	public int icon() {
		return BuffIndicator.IMMUNITY;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	{
		//all harmful blobs
		immunities.add( Blizzard.class );
		immunities.add( ConfusionGas.class );
		immunities.add( CorrosiveGas.class );
		immunities.add( Electricity.class );
		immunities.add( Fire.class );
		immunities.add( MagicalFireRoom.EternalFire.class );
		immunities.add( Freezing.class );
		immunities.add( Inferno.class );
		immunities.add( ParalyticGas.class );
		immunities.add( Regrowth.class );
		immunities.add( SmokeScreen.class );
		immunities.add( StenchGas.class );
		immunities.add( StormCloud.class );
		immunities.add( ToxicGas.class );
		immunities.add( Web.class );

		immunities.add(Tengu.FireAbility.FireBlob.class);

		immunities.add(VaultFlameTraps.class);
	}

}
