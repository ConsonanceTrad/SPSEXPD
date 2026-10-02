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

package pd.actors.mobs.quest.vault;

import pd.actors.Char;
import pd.actors.mobs.DM100;
import pd.items.quest.DwarfToken;
import pd.sprites.DM100Sprite;
import pd.messages.InlineText;

public class VaultDM100 extends DM100 {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(VaultDM100.class)
			.t("name", "DM-100改")
			.t("discover_hint", "你可在某个任务中遇到该敌人。")
			.t("desc", "这些DM-100看起来和你在监狱里见过的那些差不多，但它们的眼睛闪着明亮的青光，很可能搭载了更先进的矮人科技能源。\n\n它们的行动和普通的DM-100无异，尽管其能源有变，其使用闪电远程攻击的能力却没有变，而且其更好的运行状况也使它们更加耐用。");
	}




	{
		activateSteathGameplayBehaviour();
		spriteClass = DM100Sprite.Vault.class;

		defenseSkill = 18;

		maxLvl = 30;
		EXP = 0;
		loot = DwarfToken.class;
		lootChance = 1;
	}

	@Override
	public int attackSkill( Char target ) {
		return 25;
	}

	@Override
	public int drRoll() {
		//buff to DR to help offset high hero HP and bonus dmg from excess str
		return super.drRoll() + 4;
	}
}
