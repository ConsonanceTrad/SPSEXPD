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

package pd.items.consum.scrolls;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Roots;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.levels.CellFlags;
import pd.levels.RegularLevel;
import pd.levels.Terrain;
import pd.levels.rooms.Room;
import pd.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import pd.levels.rooms.secret.SecretRoom;
import pd.levels.rooms.special.SpecialRoom;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import render.noosa.Camera;
import render.noosa.audio.Sample;
import render.noosa.tweeners.AlphaTweener;
import render.utils.data.BArray;
import render.utils.geom.Point;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class ScrollOfTeleportation extends Scroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfTeleportation.class)
			.t("name", "传送卷轴")
			.t("tele", "眨眼之间你就被传送到本层的另外一个位置。")
			.t("no_tele", "传送魔法失败了。")
			.t("cant_reach", "你不能传送到那个位置。")
			.t("prompt", "选择想要传送的地点")
			.t("desc", "羊皮纸上的咒语能立刻让阅读者传送到本层的另一处。卷轴会优先选择阅读者还未探索的地方，但无法将人传送到门被锁或被路障堵住入口的密室中。不过，它能帮助发现通向未知区域的隐藏门。");
	}




	{
		icon = ItemIconSheet.SCROLL_TELEPORT;
	}

	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		Sample.INSTANCE.play( Assets.Sounds.READ );
		
		if (teleportPreferringUnseen( curUser )){
			readAnimation();
		}
		identify();

	}
	
	public static boolean teleportToLocation(Char ch, int pos){
		PathFinder.buildDistanceMap(pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
		if (PathFinder.distance[ch.pos] == Integer.MAX_VALUE
				|| (!Dungeon.level.passable[pos] && !Dungeon.level.avoid[pos])
				|| (Actor.findChar(pos) != null && Actor.findChar(pos) != ch)){
			if (ch == Dungeon.hero){
				GLog.w( Messages.get(ScrollOfTeleportation.class, "cant_reach") );
			}
			return false;
		}
		
		appear( ch, pos );
		Dungeon.level.occupyCell( ch );
		Buff.detach(ch, Roots.class);
		if (ch == Dungeon.hero) {
			Dungeon.observe();
			GameScene.updateFog();
		}
		return true;
		
	}

	@Override
	public void empoweredRead() {
		if (Dungeon.bossLevel()) {
			GLog.w(Messages.get(this, "no_tele"));
			return;
		}
		GameScene.selectCell(new CellSelector.Listener() {
			@Override public void onSelect(Integer target) {
				if (target != null) teleportToLocation(curUser, target);
			}
			@Override public String prompt() {
				return Messages.get(ScrollOfTeleportation.class, "prompt");
			}
		});
	}

	public static boolean teleportChar( Char ch ) {
		return teleportChar( ch, ScrollOfTeleportation.class );
	}

	public static boolean teleportChar( Char ch, Class source ) {

		//in locked levels, we must do a pathfind check, so just default to non-regular level logic
		if (!(Dungeon.level instanceof RegularLevel) || Dungeon.level.locked){
			return teleportInNonRegularLevel( ch, false );
		}

		if (Char.hasProp(ch, Char.Property.IMMOVABLE) || ch.isImmune(source)){
			GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
			return false;
		}
		
		int count = 20;
		int pos;
		do {
			pos = Dungeon.level.randomRespawnCell( ch );
			if (count-- <= 0) {
				break;
			}
		} while (pos == -1 || Dungeon.level.secret[pos]);
		
		if (pos == -1) {
			
			GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
			return false;
			
		} else {
			
			appear( ch, pos );
			Dungeon.level.occupyCell( ch );
			Buff.detach(ch, Roots.class);
			
			if (ch == Dungeon.hero) {
				GLog.i( Messages.get(ScrollOfTeleportation.class, "tele") );
				
				Dungeon.observe();
				GameScene.updateFog();
				Dungeon.hero.interrupt();
			}
			return true;
			
		}
	}
	
	public static boolean teleportPreferringUnseen( Hero hero ){

		//in locked levels, we must do a pathfind check, so just default to non-regular level logic
		if (!(Dungeon.level instanceof RegularLevel) || Dungeon.level.locked){
			return teleportInNonRegularLevel( hero, true );
		}
		
		RegularLevel level = (RegularLevel) Dungeon.level;
		ArrayList<Integer> candidates = new ArrayList<>();
		
		for (Room r : level.rooms()){
			if (r instanceof SpecialRoom || r instanceof VaultTreasureRoom){
				int terr;
				boolean locked = false;
				for (Point p : r.getPoints()){
					terr = level.map[level.pointToCell(p)];
					if (terr == Terrain.LOCKED_DOOR || terr == Terrain.CRYSTAL_DOOR || terr == Terrain.BARRICADE){
						locked = true;
						break;
					}
				}
				if (locked){
					continue;
				}
			}
			
			int cell;
			for (Point p : r.charPlaceablePoints(level)){
				cell = level.pointToCell(p);
				if (level.passable[cell] && !level.visited[cell] && !level.secret[cell] && Actor.findChar(cell) == null){
					candidates.add(cell);
				}
			}
		}
		
		if (candidates.isEmpty()){
			return teleportChar( hero );
		} else {
			int pos = Random.element(candidates);
			boolean secretDoor = false;
			int doorPos = -1;
			Room room = level.room(pos);
			if (room instanceof SpecialRoom || room instanceof VaultTreasureRoom){
				Room.Door entrance = null;
				if (room instanceof SpecialRoom) entrance = ((SpecialRoom) room).entrance();
				if (room instanceof VaultTreasureRoom) entrance = ((VaultTreasureRoom) room).entrance();
				if (entrance != null){
					doorPos = level.pointToCell(entrance);
					for (int i : PathFinder.NEIGHBOURS8){
						if (!room.inside(level.cellToPoint(doorPos + i))
								&& level.passable[doorPos + i]
								&& Actor.findChar(doorPos + i) == null){
							secretDoor = room instanceof SecretRoom;
							pos = doorPos + i;
							break;
						}
					}
				}
			}
			GLog.i( Messages.get(ScrollOfTeleportation.class, "tele") );
			appear( hero, pos );
			Dungeon.level.occupyCell( hero );
			Buff.detach(hero, Roots.class);
			if (secretDoor && level.map[doorPos] == Terrain.SECRET_DOOR){
				Sample.INSTANCE.play( Assets.Sounds.SECRET );
				int oldValue = Dungeon.level.map[doorPos];
				GameScene.discoverTile( doorPos, oldValue );
				CellFlags.discover( Dungeon.level,  doorPos );
				ScrollOfMagicMapping.discover( doorPos );
			}
			Dungeon.observe();
			GameScene.updateFog();
			return true;
		}
		
	}

	//teleports to a random pathable location on the floor
	//prefers not seen(optional) > not visible > visible
	private static boolean teleportInNonRegularLevel(Char ch, boolean preferNotSeen ){

		if (Char.hasProp(ch, Char.Property.IMMOVABLE)){
			GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
			return false;
		}

		ArrayList<Integer> visibleValid = new ArrayList<>();
		ArrayList<Integer> notVisibleValid = new ArrayList<>();
		ArrayList<Integer> notSeenValid = new ArrayList<>();

		boolean[] passable = Dungeon.level.passable;

		if (Char.hasProp(ch, Char.Property.LARGE)){
			passable = BArray.and(passable, Dungeon.level.openSpace, null);
		}

		PathFinder.buildDistanceMap(ch.pos, passable);

		for (int i = 0; i < Dungeon.level.length(); i++){
			if (PathFinder.distance[i] < Integer.MAX_VALUE
					&& !Dungeon.level.secret[i]
					&& Actor.findChar(i) == null){
				if (preferNotSeen && !Dungeon.level.visited[i]){
					notSeenValid.add(i);
				} else if (Dungeon.level.heroFOV[i]){
					visibleValid.add(i);
				} else {
					notVisibleValid.add(i);
				}
			}
		}

		int pos;

		if (!notSeenValid.isEmpty()){
			pos = Random.element(notSeenValid);
		} else if (!notVisibleValid.isEmpty()){
			pos = Random.element(notVisibleValid);
		} else if (!visibleValid.isEmpty()){
			pos = Random.element(visibleValid);
		} else {
			GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
			return false;
		}

		appear( ch, pos );
		Dungeon.level.occupyCell( ch );

		Buff.detach(ch, Roots.class);

		if (ch == Dungeon.hero) {
			GLog.i( Messages.get(ScrollOfTeleportation.class, "tele") );

			Dungeon.observe();
			GameScene.updateFog();
			Dungeon.hero.interrupt();
		}

		return true;

	}

	public static void appear( Char ch, int pos ) {
		if (ch.sprite == null) {
			ch.pos = pos;
			return;
		}

		ch.sprite.interruptMotion();

		if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[ch.pos]){
			Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		}

		if (Dungeon.level.heroFOV[ch.pos] && ch != Dungeon.hero ) {
			CellEmitter.get(ch.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
		}

		ch.move( pos, false );
		if (ch.pos == pos) {
			ch.sprite.interruptMotion();
			ch.sprite.place(pos);
		}

		if (ch.invisible == 0) {
			ch.sprite.alpha( 0 );
			ch.sprite.parent.add( new AlphaTweener( ch.sprite, 1, 0.4f ) );
		}

		if (Dungeon.level.heroFOV[pos] || ch == Dungeon.hero ) {
			ch.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3);
		} else {
			if (Camera.main.followTarget() == ch.sprite){
				//clear the follow in this case as the teleport target is going out of vision
				Camera.main.panFollow(null, 5f);
			}
		}
	}

	//just plays the VFX for teleporting, without any position changes, does re-press cells though
	public static void appearVFX( Char ch ){
		if (ch.sprite == null) {
			Dungeon.level.occupyCell(ch);
			return;
		}
		if (Dungeon.level.heroFOV[ch.pos]){
			Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		}

		Dungeon.level.occupyCell(ch);

		if (ch.invisible == 0) {
			ch.sprite.alpha( 0 );
			ch.sprite.parent.add( new AlphaTweener( ch.sprite, 1, 0.4f ) );
		}

		if (Dungeon.level.heroFOV[ch.pos]) {
			ch.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3);
		}
	}
	
	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}
