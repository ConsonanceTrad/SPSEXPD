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
import pd.actors.mobs.Skeleton;
import pd.items.Item;
import pd.items.quest.DwarfToken;
import pd.sprites.SkeletonSprite;
import pd.messages.InlineText;

public class VaultSkeleton extends Skeleton {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(VaultSkeleton.class)
			.t("name", "矮人骷髅")
			.t("discover_hint", "你可在某个任务中遇到该敌人。")
			.t("desc", "如果不是那浓密得惊人的胡须，这些骷髅看起来和你在监狱里见过的那些差不多。看来即使是在新王掌权之前，也早就有一些矮人在钻研死灵法术了。\n\n它们的行动和普通骷髅无异，包括死亡时造成的骨头爆炸。诡异的是，它们的大胡子似乎能为它们提供些许保护。");
	}


	{
		activateSteathGameplayBehaviour();
		spriteClass = SkeletonSprite.Vault.class;

		defenseSkill = 20;

		maxLvl = 30;
		EXP = 0;
		loot = DwarfToken.class;
		lootChance = 1;
	}

	@Override
	public int attackSkill( Char target ) {
		return 24;
	}

	@Override
	public float lootChance() {
		return 1;
	}

	@Override
	public Item createLoot() {
		return new DwarfToken();
	}

	@Override
	public int drRoll() {
		//buff to DR to help offset high hero HP and bonus dmg from excess str
		return super.drRoll() + 4;
	}
}
