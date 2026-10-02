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

package pd.items.ground.remains;

import pd.atlas.items.ConsumUsefulCorpseRelicsDict;

import pd.Assets;
import pd.actors.buffs.ArtifactRecharge;
import pd.actors.hero.Hero;
import pd.items.consum.scrolls.ScrollOfRecharging;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class CloakScrap extends RemainsItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CloakScrap.class)
			.t("name", "残破织物")
			.t("desc", "这块半透明的织物残片似是源于一位盗贼同行的暗影斗篷。你可以感受到一丝神秘的能量仍残留其中，你可以用它来给你的神器提供一些充能。但是如此残片也会随着神秘能量耗尽而烟消云散。");
	}


	{
		image = ConsumUsefulCorpseRelicsDict.CLOAK_SCRAP_0;
	}

	@Override
	protected void doEffect(Hero hero) {
		ArtifactRecharge.chargeArtifacts(hero, 4f);
		ScrollOfRecharging.charge(hero);
		Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
	}
}
