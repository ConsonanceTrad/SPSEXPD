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

package pd.actors.mobs;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.journal.Document;
import pd.scenes.GameScene;
import pd.sprites.SnakeSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Snake extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Snake.class)
			.t("name", "下水道巨蛇")
			.t("hint", "试试点击检查按钮再选择蛇，以了解如何击败它们。")
			.t("desc", "这些大号的蛇能通过快速滑行以闪避攻击，所以很难命中。不过伏击与魔法攻击能打它们个措手不及。\n\n在蛇的视野外攻击它，这次攻击就是伏击。有一种伏击方法是，在蛇追你时躲去门后，再_在蛇进门的瞬间发动攻击_。");
	}

	
	{
		spriteClass = SnakeSprite.class;
		
		HP = HT = 4;
		defenseSkill = 25;
		
		EXP = 2;
		maxLvl = 7;
		
		loot = Generator.Category.SEED;
		lootChance = 0.25f;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 4 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10;
	}

	private static int dodges = 0;

	@Override
	public String defenseVerb() {
		if (Dungeon.level.heroFOV[pos]) {
			dodges++;
		}
		if ((dodges >= 2 && !Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_SURPRISE_ATKS))
				|| (dodges >= 4 && !Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_1))){
			GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_SURPRISE_ATKS);
			dodges = 0;
		}
		return super.defenseVerb();
	}
}
