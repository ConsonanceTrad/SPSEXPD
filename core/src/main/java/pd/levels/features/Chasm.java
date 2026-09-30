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

package pd.levels.features;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.HolyStun;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import pd.effects.Wound;
import pd.items.artifacts.TimekeepersHourglass;
import pd.items.potions.elixirs.ElixirOfFeatherFall;
import pd.journal.Notes;
import pd.levels.Level;
import pd.levels.RegularLevel;
import pd.levels.Terrain;
import pd.levels.rooms.special.WeakFloorRoom;
import pd.levels.traps.Trap;
import pd.levels.traps.PitfallTrap;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.InterlevelScene;
import pd.scenes.PixelScene;
import pd.sprites.MobSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.audio.Sample;
import render.utils.Callback;
import render.utils.Random;

public class Chasm implements Hero.Doom {

	public static boolean jumpConfirmed = false;
	private static int heroPos;
	
	public static void heroJump( final Hero hero ) {
		heroPos = hero.pos;
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show(
						//SPS: 回 2D —— 悬崖图标取 SPS 帧 0（Terrain.CHASM 直映帧号；原破碎坐标 176,16 不适用 SPS 图布局）
						new WndOptions( new Image(Dungeon.level.tilesTex(), 0, 0, 16, 16),
								Messages.get(Chasm.class, "chasm"),
								Messages.get(Chasm.class, "jump"),
								Messages.get(Chasm.class, "yes"),
								Messages.get(Chasm.class, "no") ) {

							private float elapsed = 0f;

							@Override
							public synchronized void update() {
								super.update();
								elapsed += Game.elapsed;
							}

							@Override
							public void hide() {
								if (elapsed > 0.2f){
									super.hide();
								}
							}

							@Override
							protected void onSelect( int index ) {
								if (index == 0 && elapsed > 0.2f) {
									if (Dungeon.hero.pos == heroPos) {
										jumpConfirmed = true;
										hero.resume();
									}
								}
							}
						}
				);
			}
		});
	}
	
	public static void heroFall( int pos ) {
		jumpConfirmed = false;
		Sample.INSTANCE.play( Assets.Sounds.FALLING );

		Hero hero = Dungeon.hero;
		TimekeepersHourglass.timeFreeze freeze = hero.buff(TimekeepersHourglass.timeFreeze.class);
		if (freeze != null) freeze.detach();
		int depth = Dungeon.legacyDepth();
		Buff.affect(hero, Bleeding.class).set(Random.NormalIntRange(depth, depth * 2));
		Buff.affect(hero, HolyStun.class, 3f);
		Buff.affect(hero, Cripple.class, 5f);
		hero.damage(Random.IntRange(hero.HT / 4, hero.HT / 3), new Chasm());
		Wound.hit(pos);
		if (hero.isAlive()) hero.interrupt();
		else if (hero.sprite != null) hero.sprite.visible = false;
	}

	@Override
	public void onDeath() {
		Badges.validateDeathFromFalling();

		Dungeon.fail( Chasm.class );
		GLog.n( Messages.get(Chasm.class, "ondeath") );
	}

	public static void heroLand() {
		Hero hero = Dungeon.hero;
		if (hero.sprite != null) hero.sprite.burst(hero.sprite.blood(), 10);
		if (Game.instance != null) PixelScene.shake(4, 0.2f);
		Buff.prolong( hero, Cripple.class, Cripple.DURATION );
		hero.damage(Random.IntRange(hero.HT / 3, hero.HT / 2), new Chasm());
	}

	public static void mobFall( Mob mob ) {
		int pos = mob.pos;
		int depth = Dungeon.legacyDepth();
		Buff.affect(mob, Bleeding.class).set(Random.NormalIntRange(depth, depth * 2));
		Buff.affect(mob, HolyStun.class, 5f);
		Buff.affect(mob, Cripple.class, 10f);
		Buff.prolong(mob, Trap.HazardAssistTracker.class, Trap.HazardAssistTracker.DURATION);
		Wound.hit(mob);
		mob.damage(mob.HT / 5, new Chasm());
		Dungeon.level.setTrap(new PitfallTrap().hide(), pos);
		Level.set(pos, Terrain.SECRET_TRAP, Dungeon.level);
		GameScene.updateMap(pos);
		if (mob.sprite instanceof MobSprite) ((MobSprite)mob.sprite).fall();
	}
	
	public static class Falling extends Buff {
		
		{
			actPriority = VFX_PRIO;
		}
		
		@Override
		public boolean act() {
			heroLand();
			detach();
			return true;
		}
	}

}
