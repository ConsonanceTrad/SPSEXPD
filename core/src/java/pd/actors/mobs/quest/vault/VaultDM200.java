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
import pd.actors.mobs.DM200;
import pd.items.Item;
import pd.items.quest.DwarfToken;
import pd.sprites.DM200Sprite;
import pd.messages.InlineText;

public class VaultDM200 extends DM200 {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(VaultDM200.class)
			.t("name", "DM-200改")
			.t("discover_hint", "你可在某个任务中遇到该敌人。")
			.t("desc", "这些DM-200看起来和你在矿洞里见过的那些差不多，但它们的眼睛闪着明亮的青光，很可能搭载了更先进的矮人科技能源。\n\n和它们在矿洞的同型号机体一样，这些DM-200因体型太大无法穿过狭小空间，而且也能喷射毒气。");
	}


	{
		activateSteathGameplayBehaviour();
		spriteClass = DM200Sprite.Vault.class;

		defenseSkill = 15;

		maxLvl = 30;
		EXP = 0;
		loot = DwarfToken.class;
		lootChance = 1;
	}

	@Override
	public int attackSkill( Char target ) {
		return 28;
	}

	@Override
	public float lootChance() {
		return 1;
	}

	@Override
	public Item createLoot() {
		return new DwarfToken();
	}
}
