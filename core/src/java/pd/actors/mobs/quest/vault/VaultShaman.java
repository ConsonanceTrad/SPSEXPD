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
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hex;
import pd.actors.buffs.Vulnerable;
import pd.actors.buffs.Weakness;
import pd.actors.mobs.Shaman;
import pd.items.Item;
import pd.items.quest.DwarfToken;
import pd.sprites.ShamanSprite;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class VaultShaman extends Shaman {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(VaultShaman.class)
			.t("name", "受俘萨满")
			.t("discover_hint", "你可在某个任务中遇到该敌人。")
			.t("desc", "这些面覆着矮人一样的怪异金属面具的豺狼萨满显然是从洞穴被抓来的。面具紧勒在萨满们的脸上，而萨满们的动作也异常僵硬。面具是用来控制它们的吗？")
			.t("spell_desc", "这些豺狼萨满肯定还是会用魔法飞弹攻击你，但由于面具没有涂装，无法分辨它们会使用什么魔法类型。相比原版的木质面具，金属面具似乎还为它们提供了些许防护，而且它们的攻击相比一般的豺狼萨满也更强了。");
	}


	{
		activateSteathGameplayBehaviour();
		spriteClass = ShamanSprite.Vault.class;

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
	public float lootChance() {
		return 1;
	}

	@Override
	public Item createLoot() {
		return new DwarfToken();
	}

	int type = Random.Int(5);

	@Override
	protected void debuff( Char enemy ) {
		switch (type){
			case 0: case 1:
				Buff.prolong( enemy, Weakness.class, Weakness.DURATION );
				break;
			case 2: case 3:
				Buff.prolong( enemy, Vulnerable.class, Vulnerable.DURATION );
				break;
			case 4:
				Buff.prolong( enemy, Hex.class, Hex.DURATION );
				break;
		}
	}

	@Override
	public int damageRoll() {
		//buff to melee damage, equal to a brute (no rage), as shamans are otherwise weak in melee
		return Random.NormalIntRange( 5, 25 );
	}

	@Override
	public int drRoll() {
		//buff to DR to help offset high hero HP and bonus dmg from excess str
		return super.drRoll() + 5;
	}

	public static final String TYPE = "type";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TYPE, type);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		type = bundle.getInt(TYPE);
	}
}
