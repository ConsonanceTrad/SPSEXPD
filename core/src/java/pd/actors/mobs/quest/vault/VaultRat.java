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
import pd.actors.mobs.Rat;
import pd.items.quest.DwarfToken;
import pd.messages.Messages;
import pd.sprites.RatSprite;
import pd.sprites.SkeletonSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class VaultRat extends Rat {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(VaultRat.class)
			.t("desc", "这只老鼠行为有些许变化，以测试全新的宝库敌人AI：\n_-_其移动可透过墙壁而被“听见”\n_-_其游荡路径沿着预设的路线\n_-_游荡时，其移动方向的反方向上的侦测范围锐减\n_-_睡觉时，其侦测范围也会降低\n_-_侦测到你时，其会在进行追击之前先进行“搜查”。搜查状态的敌人会向你移动但并不会发动攻击，且更容易在门口与转角处跟丢你。");
	}




	{
		activateSteathGameplayBehaviour();

		defenseSkill = 18;

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
	public String description() {
		return Messages.get(Rat.class, "desc") + "\n\n" + super.description();
	}
}
