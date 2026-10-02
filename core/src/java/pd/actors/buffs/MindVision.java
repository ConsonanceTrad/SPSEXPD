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

import pd.Dungeon;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class MindVision extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MindVision.class)
			.t("name", "灵视")
			.t("desc", "你可以在脑海中以某种方式看到这一层的所有生物。这种感觉非常奇异。\n\n只要灵视效果依然存在，这层的所有生物都会在你的视野当中。通过灵视看到的生物同样算作视野中的目标，可以被很多魔法效果影响。\n\n灵视效果剩余时长：%s回合");
	}




	public static final float DURATION = 20f;
	
	public int distance = 2;

	{
		type = buffType.POSITIVE;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.MIND_VISION;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public void detach() {
		super.detach();
		Dungeon.observe();
		GameScene.updateFog();
	}
}
