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

package pd.items.scrolls;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.MirrorImage;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class ScrollOfMirrorImage extends Scroll {

	{
		icon = ItemIconSheet.SCROLL_MIRRORIMG;
	}

	private static final int NIMAGES	= 3;
	
	@Override
	public void doRead() {
		detach(curUser.belongings.backpack);
		if (legacyMagicBlocked()) {
			GLog.w(Messages.get(Scroll.class, "prevent"));
			Sample.INSTANCE.play(Assets.Sounds.READ);
			Invisibility.dispel();
			setKnown();
			curUser.spendAndNext(TIME_TO_READ);
			return;
		}
		if ( spawnImages(curUser, NIMAGES) > 0){
			GLog.i(Messages.get(this, "copies"));
		} else {
			GLog.i(Messages.get(this, "no_copies"));
		}
		identify();
		
		Sample.INSTANCE.play( Assets.Sounds.READ );
		
		readAnimation();
	}

	public static boolean legacyMagicBlocked() {
		return Dungeon.legacyDepth() > 50;
	}

	public static int spawnImages( Hero hero, int nImages ){
		return spawnImages( hero, hero.pos, nImages);
	}

	@Override
	public void empoweredRead() {
		new DelayedImageSpawner(6 - spawnImages(curUser, 2), 2, 3f).attachTo(curUser);
		setKnown();
		Sample.INSTANCE.play(Assets.Sounds.READ);
		Invisibility.dispel();
		curUser.spendAndNext(TIME_TO_READ);
	}

	public static class DelayedImageSpawner extends Buff {
		private static final String TOTAL = "images";
		private static final String PER_ROUND = "per_round";
		private static final String DELAY = "delay";
		private int totalImages;
		private int imagesPerRound;
		private float delay;

		public DelayedImageSpawner() { this(NIMAGES, NIMAGES, 1f); }
		public DelayedImageSpawner(int total, int perRound, float delay) {
			totalImages = Math.max(0, total);
			imagesPerRound = Math.max(1, perRound);
			this.delay = Math.max(0, delay);
		}
		@Override public boolean attachTo(Char target) {
			if (!super.attachTo(target)) return false;
			spend(delay);
			return true;
		}
		@Override public boolean act() {
			int spawned = target instanceof Hero
					? spawnImages((Hero) target, Math.min(totalImages, imagesPerRound)) : 0;
			totalImages -= spawned;
			if (totalImages <= 0 || spawned == 0) detach();
			else spend(delay);
			return true;
		}
		@Override public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TOTAL, totalImages);
			bundle.put(PER_ROUND, imagesPerRound);
			bundle.put(DELAY, delay);
		}
		@Override public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			totalImages = Math.max(0, bundle.getInt(TOTAL));
			imagesPerRound = Math.max(1, bundle.getInt(PER_ROUND));
			delay = Math.max(0, bundle.getFloat(DELAY));
		}
	}

	//returns the number of images spawned
	public static int spawnImages( Hero hero, int pos, int nImages ){
		
		ArrayList<Integer> respawnPoints = new ArrayList<>();
		
		for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
			int p = pos + PathFinder.NEIGHBOURS9[i];
			if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
				respawnPoints.add( p );
			}
		}
		
		int spawned = 0;
		while (nImages > 0 && respawnPoints.size() > 0) {
			int index = Random.index( respawnPoints );
			
			MirrorImage mob = new MirrorImage();
			mob.duplicate( hero );
			GameScene.add( mob );
			ScrollOfTeleportation.appear( mob, respawnPoints.get( index ) );
			
			respawnPoints.remove( index );
			nImages--;
			spawned++;
		}
		
		return spawned;
	}

	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}
