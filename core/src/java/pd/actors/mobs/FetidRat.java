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

import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.StenchGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.actors.mobs.npcs.Ghost;
import pd.scenes.GameScene;
import pd.sprites.FetidRatSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class FetidRat extends Rat {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FetidRat.class)
			.t("name", "腐臭老鼠")
			.t("rankings_desc", "被腐臭老鼠溶解")
			.t("desc", "很明显这只老鼠身上有着问题。其油腻的黑毛和腐烂的皮肤与你以前见过的健康老鼠很不同。浅绿色的眼睛让它显得更具威胁。\n\n这只老鼠周围围绕着一片可怕恶臭，其恶臭在近距离接触时尤其浓烈。\n\n黑色的淤泥从它的嘴中流出。淤泥腐蚀掉了地板，但其似乎能在水中溶解开来。")
			.t("discover_hint", "你可在某个任务中遇到该敌人。");
	}




	{
		spriteClass = FetidRatSprite.class;

		HP = HT = 45;
		defenseSkill = 5;

		EXP = 4;

		state = WANDERING;

		properties.add(Property.BEAST);
		properties.add(Property.MINIBOSS);
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int(3) == 0) {
			Buff.affect(enemy, Ooze.class).set(5f);
		}

		return damage;
	}

	@Override
	public int defenseProc( Char enemy, int damage ) {

		GameScene.add(Blob.seed(pos, 20, StenchGas.class));

		return super.defenseProc(enemy, damage);
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );

		Ghost.Quest.process();
	}

	{
		immunities.add( StenchGas.class );
	}
}
