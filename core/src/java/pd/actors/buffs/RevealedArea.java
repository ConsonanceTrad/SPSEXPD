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
import pd.actors.hero.Talent;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class RevealedArea extends FlavourBuff{
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(RevealedArea.class)
			.t("name", "区域揭示")
			.t("desc", "揭示地牢中一片区域的视野，无论你身处这层中的何处都能对那里一览无遗。\n\n效果剩余时长：%s回合");
	}




	{
		type = Buff.buffType.POSITIVE;
	}

	public int pos, depth, branch;

	@Override
	public void detach() {
		GameScene.updateFog(pos, 2);
		super.detach();
	}

	@Override
	public int icon() {
		return BuffIndicator.MIND_VISION;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0, 1, 1);
	}

	@Override
	public float iconFadePercent() {
		float max = 5*Dungeon.hero.pointsInTalent(Talent.SEER_SHOT);
		return Math.max(0, (max-visualcooldown()) / max);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", (int)visualcooldown());
	}

	private static final String BRANCH = "branch";
	private static final String DEPTH = "depth";
	private static final String POS = "pos";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DEPTH, depth);
		bundle.put(BRANCH, branch);
		bundle.put(POS, pos);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		depth = bundle.getInt(DEPTH);
		branch = bundle.getInt(BRANCH);
		pos = bundle.getInt(POS);
	}
}
