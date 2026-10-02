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

package pd.items.equipment.weapon.missiles;

import pd.atlas.items.ConsumThrowsDict;

import pd.Assets;
import pd.actors.Char;
import pd.actors.buffs.FlavourBuff;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import pd.messages.InlineText;

public class Shuriken extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Shuriken.class)
			.t("name", "手里剑")
			.t("stats_desc", "手里剑每经20回合可以不消耗回合地投掷一次。")
			.t("desc", "一些轻便到可以在移动时快速掷出的锋利星形金属刃片。")
			.t("shurikeninstanttracker.name", "手里剑冷却")
			.t("shurikeninstanttracker.desc", "你近期不消耗回合地投掷了一个手里剑，必须等待冷却结束才能再次瞬间投掷手里剑。在此期间，手里剑仅能以武器攻速投掷。\n\n剩余回合数：%s");
	}


	{
		image = ConsumThrowsDict.SHURIKEN_0;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		
		tier = 2;
		baseUses = 5;
	}
	
	@Override
	public int max(int lvl) {
		return  4 * tier +                      //8 base, down from 10
				(tier == 1 ? 2*lvl : tier*lvl); //scaling unchanged
	}

	@Override
	protected void onThrow(int cell) {
		super.onThrow(cell);
		if (curUser.buff(ShurikenInstantTracker.class) == null) {
			//1 less turn as the attack will be instant
			FlavourBuff.affect(curUser, ShurikenInstantTracker.class, ShurikenInstantTracker.DURATION-1);
		}
	}

	@Override
	public float castDelay(Char user, int cell) {
		return user.buff(ShurikenInstantTracker.class) != null ? super.castDelay(user, cell) : 0;
	}

	public static class ShurikenInstantTracker extends FlavourBuff {

		public static int DURATION = 20;

		@Override
		public int icon() {
			return BuffIndicator.THROWN_WEP;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.6f, 0.6f, 0.6f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

	}

}
