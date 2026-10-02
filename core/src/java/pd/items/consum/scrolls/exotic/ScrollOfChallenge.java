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

package pd.items.consum.scrolls.exotic;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.effects.particles.ChallengeParticle;
import pd.mechanics.ShadowCaster;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;
import render.utils.data.BArray;
import render.utils.geom.Point;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class ScrollOfChallenge extends ExoticScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfChallenge.class)
			.t("name", "决斗秘卷")
			.t("desc", "大声诵读此卷轴时，它将发出巨大的吼声，将敌人吸引到诵读者身边，同时在它们周围创建一个小型的竞技场。\n\n只要使用者在这个竞技场里，就将获得33%的伤害减免(在其它所有伤害减免计算之前)，并且不会损失饱食度。\n\n竞技场的大小将随着诵读者所在区域的大小而改变。在一些Boss战区域，竞技场会格外的小。")
			.t("$challengearena.name", "决斗区域")
			.t("$challengearena.desc", "一个由魔力构筑的竞技场在你周围浮现，其中翻腾着一阵猩红血雾。\n\n当你站在雾中时，饥饿值不会增加，并且受到的任何伤害都会减少33%%。如果你有任何其他减伤手段(例如护甲)，它们都会在33%%伤害减免之后生效。\n\n剩余回合数：%d回合");
	}



	
	{
		icon = ItemIconSheet.SCROLL_CHALLENGE;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		for (Mob mob : Dungeon.level.mobs().toArray( new Mob[0] )) {
			mob.beckon( curUser.pos );
		}

		Buff.affect(curUser, ChallengeArena.class).setup(curUser.pos);

		identify();
		
		curUser.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
		
		readAnimation();
	}


	public static class ChallengeArena extends Buff {

		private ArrayList<Integer> arenaPositions = new ArrayList<>();
		private ArrayList<Emitter> arenaEmitters = new ArrayList<>();

		private static final float DURATION = 100;
		int left = 0;

		{
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.ARMOR;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1f, 0f, 0f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - left) / DURATION);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(left);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", left);
		}

		public void setup(int pos){

			int dist;
			if (Dungeon.depth == 5 || Dungeon.depth == 10 || Dungeon.depth == 20){
				dist = 1; //smaller boss arenas
			} else {

				boolean[] visibleCells = new boolean[Dungeon.level.length()];
				Point c = Dungeon.level.cellToPoint(pos);
				ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), visibleCells, Dungeon.level.losBlocking, 8);
				int count=0;
				for (boolean b : visibleCells){
					if (b) count++;
				}

				if (count < 30){
					dist = 1;
				} else if (count >= 100) {
					dist = 3;
				} else {
					dist = 2;
				}
			}

			PathFinder.buildDistanceMap( pos, BArray.or( Dungeon.level.passable, Dungeon.level.avoid, null ), dist );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE && !arenaPositions.contains(i)) {
					arenaPositions.add(i);
				}
			}
			if (target != null) {
				fx(false);
				fx(true);
			}

			left = (int) DURATION;

		}

		public void extend( float duration ) {
			left += duration;
		}

		@Override
		public boolean act() {

			if (!arenaPositions.contains(target.pos)){
				detach();
			}

			left--;
			BuffIndicator.refreshHero();
			if (left <= 0){
				detach();
			}

			spend(TICK);
			return true;
		}

		@Override
		public void fx(boolean on) {
			if (on){
				for (int i : arenaPositions){
					Emitter e = CellEmitter.get(i);
					e.pour(ChallengeParticle.FACTORY, 0.05f);
					arenaEmitters.add(e);
				}
			} else {
				for (Emitter e : arenaEmitters){
					e.on = false;
				}
				arenaEmitters.clear();
			}
		}

		private static final String ARENA_POSITIONS = "arena_positions";
		private static final String LEFT = "left";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);

			int[] values = new int[arenaPositions.size()];
			for (int i = 0; i < values.length; i ++)
				values[i] = arenaPositions.get(i);
			bundle.put(ARENA_POSITIONS, values);

			bundle.put(LEFT, left);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			int[] values = bundle.getIntArray( ARENA_POSITIONS );
			for (int value : values) {
				arenaPositions.add(value);
			}

			left = bundle.getInt(LEFT);
		}
	}
	
}
