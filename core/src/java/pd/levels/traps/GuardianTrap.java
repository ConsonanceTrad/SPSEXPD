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

package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Statue;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.StatueSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class GuardianTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(GuardianTrap.class)
			.t("name", "守卫陷阱")
			.t("alarm", "陷阱产生的尖锐的警报声在地牢里回荡！")
			.t("desc", "这个陷阱有着奇怪的魔法机制，它将召唤守卫并向将使本层所有生物对这里产生警觉。")
			.t("guardian.name", "召唤守卫")
			.t("guardian.desc", "这个蓝色的幻影似乎是地牢中石像守卫的一个召唤映像。")
			.t("guardian.desc_weapon", "虽然雕像本身是几乎无形的，但它装备着的_%s_看起来是真家伙。")
			.t("guardian.discover_hint", "你可通过某个陷阱遇到该敌人。");
	}


	{
		color = GREEN;
		shape = LARGE_DOT;
	}

	@Override
	public void activate() {

		for (Mob mob : Dungeon.level.mobs()) {
			mob.beckon( pos );
		}

		if (Dungeon.level.heroFOV[pos]) {
			GLog.w( Messages.get(this, "alarm") );
			CellEmitter.center(pos).start( Speck.factory(Speck.SCREAM), 0.3f, 3 );
		}

		Sample.INSTANCE.play( Assets.Sounds.ALERT );

		for (int i = 0; i < (scalingDepth() - 5)/5; i++){
			Guardian guardian = new Guardian();
			guardian.createWeapon(false);
			guardian.state = guardian.WANDERING;
			guardian.pos = Dungeon.level.randomRespawnCell( guardian );
			if (guardian.pos != -1) {
				GameScene.add(guardian);
				guardian.beckon(Dungeon.hero.pos);
			}
		}

		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) heap.darkhit();

	}

	public static class Guardian extends Statue {

		{
			spriteClass = GuardianSprite.class;

			EXP = 0;
			state = WANDERING;

			levelGenStatue = false;
		}

		@Override
		public void createWeapon( boolean useDecks ) {
			weapon = (Weapon) Generator.randomUsingDefaults(Generator.Category.OLDWEAPON);
			weapon.cursed = false;
			weapon.enchant(null);
			weapon.level(0);
		}

		@Override
		public void beckon(int cell) {
			//Beckon works on these ones, unlike their superclass.
			if (sprite != null) notice();

			if (state != HUNTING) {
				state = WANDERING;
			}
			target = cell;
		}

	}

	public static class GuardianSprite extends StatueSprite {

		public GuardianSprite(){
			super();
			tint(0, 0, 1, 0.2f);
		}

		@Override
		public void resetColor() {
			super.resetColor();
			tint(0, 0, 1, 0.2f);
		}
	}
}
