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

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.scenes.GameScene;
import pd.sprites.HeroSprite;
import pd.ui.BuffIndicator;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class HeroDisguise extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HeroDisguise.class)
			.t("name", "伪装")
			.t("desc", "幻术魔法改变了你的外貌！虽然此效果是完全装饰性的，但无论如何感觉起来还是很奇怪。\n\n伪装效果剩余时长：%s回合");
	}


	{
		announced = true;
	}

	private HeroClass cls = null;

	public static float DURATION = 1000f;

	public HeroClass getDisguise(){
		return cls;
	}

	@Override
	public int icon() {
		return BuffIndicator.DISGUISE;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public void fx(boolean on) {
		if (target instanceof Hero && target.sprite instanceof HeroSprite){
			if (cls == null) {
				do {
					cls = Random.oneOf(HeroClass.playableClasses());
				} while (cls == ((Hero) target).heroClass);
			}

			if (on) ((HeroSprite)target.sprite).disguise(cls);
			else    ((HeroSprite)target.sprite).disguise(((Hero) target).heroClass);
			GameScene.updateAvatar();
		}
	}

	private static final String CLASS = "class";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CLASS, cls);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		cls = bundle.getEnum(CLASS, HeroClass.class);
	}
}
