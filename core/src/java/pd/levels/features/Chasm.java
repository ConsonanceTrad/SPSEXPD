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
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import pd.items.equipment.artifacts.TimekeepersHourglass;
import pd.items.consum.potions.elixirs.ElixirOfFeatherFall;
import pd.journal.Notes;
import pd.levels.GroundItems;
import pd.levels.Level;
import pd.levels.RegularLevel;
import pd.levels.Transitions;
import pd.levels.rooms.special.WeakFloorRoom;
import pd.levels.traps.Trap;
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
import render.utils.data.Callback;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Chasm implements Hero.Doom {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(Chasm.class)
			.t("chasm", "深渊")
			.t("yes", "是的，我知道我在做什么")
			.t("no", "不，我改主意了")
			.t("jump", "你确定要跳入深渊中？从这么高的地方摔下去一定很疼。")
			.t("ondeath", "你落地过猛摔死了...")
			.t("rankings_desc", "死于撞击");
	}




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

		//SPSEXPD: 对齐破碎原版——换层前先做地图转换收尾
		Transitions.beforeTransition();

		if (Dungeon.hero.isAlive()) {
			Dungeon.hero.interrupt();
			InterlevelScene.mode = InterlevelScene.Mode.FALL;
			//SPSEXPD: 落在「薄地板房」（WeakFloorRoom）时按破碎置 fallIntoPit，
			//让下一层用 pit 落点（并移除远处的井地标）；普通深渊则用普通落点
			if (Dungeon.level instanceof RegularLevel &&
					((RegularLevel)Dungeon.level).room( pos ) instanceof WeakFloorRoom){
				InterlevelScene.fallIntoPit = true;
				Notes.remove(Notes.Landmark.DISTANT_WELL, Dungeon.depth - 1);
			} else {
				InterlevelScene.fallIntoPit = false;
			}
			Game.switchScene( InterlevelScene.class );
		} else {
			//SPSEXPD: 摔死了就只是隐藏精灵，不换层
			Hero hero = Dungeon.hero;
			TimekeepersHourglass.timeFreeze freeze = hero.buff(TimekeepersHourglass.timeFreeze.class);
			if (freeze != null) freeze.detach();
			if (hero.sprite != null) hero.sprite.visible = false;
		}
	}

	@Override
	public void onDeath() {
		Badges.validateDeathFromFalling();

		Dungeon.fail( Chasm.class );
		GLog.n( Messages.get(Chasm.class, "ondeath") );
	}

	public static void heroLand() {
		Hero hero = Dungeon.hero;

		//SPSEXPD: 羽落药剂——落地时只喷一下气流、由 buff 自行结算免伤，不受落地摔伤
		ElixirOfFeatherFall.FeatherBuff b = hero.buff(ElixirOfFeatherFall.FeatherBuff.class);
		if (b != null){
			if (hero.sprite != null) hero.sprite.emitter().burst( Speck.factory( Speck.JET ), 20);
			b.processFall();
			return;
		}

		//SPSEXPD: 抖动时长按破碎的 1f（原为 0.2f，落地表现一闪而过、观感像「摔得太快」）
		if (Game.instance != null) PixelScene.shake( 4, 1f );

		Dungeon.level.occupyCell( hero );
		Buff.prolong( hero, Cripple.class, Cripple.DURATION );

		//SPSEXPD: 按破碎原版——HP 越低流血越多、即死伤害越少
		Buff.affect( hero, Bleeding.class).set( Math.round(hero.HT / (6f + (6f*(hero.HP/(float)hero.HT)))), Chasm.class);
		hero.damage( Math.max( hero.HP / 2, Random.NormalIntRange( hero.HP / 2, hero.HT / 4 )), new Chasm() );
	}

	public static void mobFall( Mob mob ) {
		if (mob.isAlive()) {
			Buff.prolong(mob, Trap.HazardAssistTracker.class, Trap.HazardAssistTracker.DURATION);
			mob.die( Chasm.class );
		}
		if (mob.sprite != null) ((MobSprite)mob.sprite).fall();
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
