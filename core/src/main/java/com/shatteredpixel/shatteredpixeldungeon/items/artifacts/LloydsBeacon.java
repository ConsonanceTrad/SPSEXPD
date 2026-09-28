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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

/** The non-equippable Lloyd's beacon from SPS-PD 0.9.8. */
public class LloydsBeacon extends Item {

	public static final float TIME_TO_USE = 1f;

	public static final String AC_SET = "SET";
	public static final String AC_RETURN = "RETURN";

	private int returnDepth = -1;
	private int returnPos;

	{
		image = ItemSpriteSheet.BEACON;
		unique = true;
	}

	private static final String DEPTH = "depth";
	private static final String POS = "pos";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DEPTH, returnDepth);
		if (returnDepth != -1) {
			bundle.put(POS, returnPos);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		// Builds before the SPS port stored this class as an upgradeable artifact.
		// Keep those saves readable without retaining the incompatible artifact level.
		level(0);
		returnDepth = bundle.getInt(DEPTH);
		returnPos = bundle.getInt(POS);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SET);
		if (returnDepth != -1) {
			actions.add(AC_RETURN);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SET.equals(action) || AC_RETURN.equals(action)) {
			if (Dungeon.bossLevel() || Dungeon.depth > 24) {
				hero.spend(TIME_TO_USE);
				GLog.w(Messages.get(this, "preventing"));
				return;
			}

			for (int offset : PathFinder.NEIGHBOURS8) {
				if (Actor.findChar(hero.pos + offset) != null) {
					GLog.w(Messages.get(this, "creatures"));
					return;
				}
			}
		}

		if (AC_SET.equals(action)) {
			returnDepth = Dungeon.depth;
			returnPos = hero.pos;

			hero.spend(TIME_TO_USE);
			hero.busy();
			if (hero.sprite != null) {
				hero.sprite.operate(hero.pos);
			}
			Sample.INSTANCE.play(Assets.Sounds.BEACON);
			GLog.i(Messages.get(this, "return"));

		} else if (AC_RETURN.equals(action)) {
			if (returnDepth == Dungeon.depth) {
				ScrollOfTeleportation.appear(hero, returnPos);
				for (Mob mob : Dungeon.level.mobs) {
					if (mob.pos == hero.pos) {
						for (int offset : PathFinder.NEIGHBOURS8) {
							int destination = mob.pos + offset;
							if (Actor.findChar(destination) == null
									&& Dungeon.level.insideMap(destination)
									&& Dungeon.level.passable[destination]) {
								mob.pos = destination;
								if (mob.sprite != null) {
									mob.sprite.point(mob.sprite.worldToCamera(mob.pos));
								}
								break;
							}
						}
					}
				}
				Dungeon.level.occupyCell(hero);
				Dungeon.observe();
				GameScene.updateFog();
			} else {
				Level.beforeTransition();
				InterlevelScene.mode = InterlevelScene.Mode.RETURN;
				InterlevelScene.returnDepth = returnDepth;
				InterlevelScene.returnBranch = 0;
				InterlevelScene.returnPos = returnPos;
				Game.switchScene(InterlevelScene.class);
			}

		} else {
			super.execute(hero, action);
		}
	}

	@Override
	public void reset() {
		super.reset();
		returnDepth = -1;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	private static final Glowing WHITE = new Glowing(0xFFFFFF);

	@Override
	public Glowing glowing() {
		return returnDepth != -1 ? WHITE : null;
	}

	@Override
	public String desc() {
		return super.desc() + (returnDepth == -1
				? "" : "\n\n" + Messages.get(this, "desc_set", returnDepth));
	}
}
