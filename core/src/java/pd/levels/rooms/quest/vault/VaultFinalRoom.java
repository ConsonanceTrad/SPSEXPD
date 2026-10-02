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

package pd.levels.rooms.quest.vault;

import pd.Assets;
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.Imp;
import pd.actors.mobs.quest.vault.VaultBossElemental;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.quest.ImpStatue;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.levels.FieldOfView;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import pd.levels.rooms.Room;
import pd.levels.rooms.special.SpecialRoom;
import pd.levels.rooms.standard.EmptyRoom;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ImpSprite;
import pd.tiles.CustomTilemap;
import pd.tiles.DungeonTileSheet;
import pd.tiles.custom.Carpet;
import pd.utils.GLog;
import pd.windows.WndTitledMessage;
import render.noosa.Image;
import render.noosa.Tilemap;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.geom.Point;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class VaultFinalRoom extends SpecialRoom {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(VaultFinalRoom.class)
			.t("imp_warning_unprepared", "当你踏进大房间时，时间的流逝似乎放缓了些，与此同时你听见了小恶魔的声音：\n\n“你现在应该在宝库最重要的保险室里，我猜我要的雕像就在门后！我敢肯定前面的陷阱和守卫绝非善类，所以现在_先好好搜集其他地方的装备和补给，准备好了再前进_！我可不希望你丢掉小命，你得活着才能帮我把雕像带出来！”")
			.t("imp_warning_prepared", "当你踏进大房间时，时间的流逝似乎放缓了些，与此同时你听见了小恶魔的声音：\n\n“好了，你现在应该在宝库最重要的保险室里，我猜我要的雕像就在门后！我敢肯定前面陷阱和守卫绝非善类，所以现在_调整好状态准备大打一场吧！_我可不希望你丢掉小命，毕竟你得活着才能帮我把雕像带出来。”")
			.t("final_warning", "你听到了法阵正在为某物蓄能的声音，只要再往前一步就肯定会触发些什么！")
			.t("markertiles.name", "巨型标记")
			.t("markertiles.desc", "地板上的魔法回路与监狱里的召唤法阵不无相似，但此处的法阵要宏伟得多。\n\n整个地面因蓄积着能量而鸣响震颤着，仿佛有一股磅礴的力量即将被释放。")
			.t("vaulttreasure.desc", "这里堆满了金币，可惜你不能把它们带出宝库。");
	}


	@Override
	public int minWidth() {
		return 21;
	}

	@Override
	public int maxWidth() {
		return 21;
	}

	@Override
	public int minHeight() {
		return 21;
	}

	@Override
	public int maxHeight(){
		return 21;
	}

	@Override
	public boolean isExit() {
		return true;
	}

	@Override
	public boolean canConnect(Point p) {
		return (Math.abs(p.x - center().x) <= 5 || Math.abs(p.y - center().y) <= 5);
	}

	@Override
	public boolean canConnect(Room r) {
		if (r.isEntrance()){
			return false;
		}

		//must have at least 3 rooms between it and the entrance room
		for (Room r1 : r.connected.keySet()) {
			if (r1.isEntrance()){
				return false;
			}
			for (Room r2 : r1.connected.keySet()) {
				if (r2.isEntrance()){
					return false;
				}
				for (Room r3 : r2.connected.keySet()) {
					if (r3.isEntrance()){
						return false;
					}
				}
			}
		}

		return super.canConnect(r);
	}

	@Override
	public boolean canPlaceGrass(Point p) {
		return false;
	}

	@Override
	public boolean canPlaceWater(Point p) {
		return false;
	}

	@Override
	public boolean canPlaceItem(Point p, Level l) {
		return false;
	}

	private Point entryDoor;
	private Point lockedDoor;

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fillEllipse( level, this, 5, Terrain.EMPTY_SP );

		Point c = center();

		MarkerTiles marker = new MarkerTiles();
		marker.pos(c.x-4, c.y-4);
		level.customTiles.add(marker);

		Door entrance = entrance();
		entrance.set( Door.Type.REGULAR );
		Room entry = new EmptyRoom();
		Room treasure = new EmptyRoom();;
		if (entrance.x == left) {
			entry.set(left + 1, top + 5, left + 3, bottom - 5);
			entryDoor = new Point(left+4, c.y);
			treasure.set(right - 3,  top + 3, right - 1, bottom - 3);
			lockedDoor = new Point(right-4, c.y);
		} else if (entrance.x == right){
			treasure.set(left + 1, top + 3, left + 3, bottom - 3);
			lockedDoor = new Point(left+4, c.y);
			entry.set(right - 3,  top + 5, right - 1, bottom - 5);
			entryDoor = new Point(right-4, c.y);
		} else if (entrance.y == top) {
			entry.set(left + 5, top + 1, right-5, top + 3);
			entryDoor = new Point(c.x, top+4);
			treasure.set(left + 3, bottom - 3, right-3, bottom - 1);
			lockedDoor = new Point(c.x, bottom-4);
		} else {
			treasure.set(left + 3, top + 1, right-3, top + 3);
			lockedDoor = new Point(c.x, top+4);
			entry.set(left + 5, bottom - 3, right-5, bottom - 1);
			entryDoor = new Point(c.x, bottom-4);
		}

		Painter.set(level, entryDoor, Terrain.DOOR);
		Painter.set(level, lockedDoor, Terrain.LOCKED_DOOR);
		Painter.fill(level, entry, Terrain.CUSTOM_DECO_EMPTY);

		Carpet carpet = new Carpet();
		carpet.setRect(entry.left, entry.top, entry.width(), entry.height());
		level.customTiles.add(carpet);

		ArrayList<Integer> treasureSpots = new ArrayList<>();

		if (entry.width() > entry.height()){
			Painter.set(level, entry.left+1, entry.top+1, Terrain.REGION_DECO);
			carpet.overrideTile(level, entry.left+1, entry.top+1, Carpet.CITY_PEDESTAL);
			Painter.set(level, entry.left+3, entry.top+1, Terrain.REGION_DECO);
			carpet.overrideTile(level, entry.left+3, entry.top+1, Carpet.CITY_PEDESTAL);
			Painter.set(level, entry.left+7, entry.top+1, Terrain.REGION_DECO);
			carpet.overrideTile(level, entry.left+7, entry.top+1, Carpet.CITY_PEDESTAL);
			Painter.set(level, entry.left+9, entry.top+1, Terrain.REGION_DECO);
			carpet.overrideTile(level, entry.left+9, entry.top+1, Carpet.CITY_PEDESTAL);

			treasureSpots.add(treasure.left+1 + (treasure.top+1)*level.width());
			treasureSpots.add(treasure.left+3 + (treasure.top+1)*level.width());
			treasureSpots.add(treasure.left+5 + (treasure.top+1)*level.width());
			treasureSpots.add(treasure.left+7 + (treasure.top+1)*level.width());
			treasureSpots.add(treasure.left+9 + (treasure.top+1)*level.width());
			treasureSpots.add(treasure.left+11 + (treasure.top+1)*level.width());
			treasureSpots.add(treasure.left+13 + (treasure.top+1)*level.width());
		} else {
			Painter.set(level, entry.left+1, entry.top+1, Terrain.REGION_DECO);
			carpet.overrideTile(level, entry.left+1, entry.top+1, Carpet.CITY_PEDESTAL);
			Painter.set(level, entry.left+1, entry.top+3, Terrain.REGION_DECO);
			carpet.overrideTile(level, entry.left+1, entry.top+3, Carpet.CITY_PEDESTAL);
			Painter.set(level, entry.left+1, entry.top+7, Terrain.REGION_DECO);
			carpet.overrideTile(level, entry.left+1, entry.top+7, Carpet.CITY_PEDESTAL);
			Painter.set(level, entry.left+1, entry.top+9, Terrain.REGION_DECO);
			carpet.overrideTile(level, entry.left+1, entry.top+9, Carpet.CITY_PEDESTAL);

			treasureSpots.add(treasure.left+1 + (treasure.top+1)*level.width());
			treasureSpots.add(treasure.left+1 + (treasure.top+3)*level.width());
			treasureSpots.add(treasure.left+1 + (treasure.top+5)*level.width());
			treasureSpots.add(treasure.left+1 + (treasure.top+7)*level.width());
			treasureSpots.add(treasure.left+1 + (treasure.top+9)*level.width());
			treasureSpots.add(treasure.left+1 + (treasure.top+11)*level.width());
			treasureSpots.add(treasure.left+1 + (treasure.top+13)*level.width());
		}

		Painter.fill(level, treasure, Terrain.EMPTY_SP);

		for (int cell : treasureSpots){
			Painter.set(level, cell, Terrain.PEDESTAL);
		}

		//always place imp statue in the center
		level.drop(new ImpStatue(), treasureSpots.remove(3));

		Random.shuffle(treasureSpots);

		for (Item i : Imp.Quest.rewardOptions){
			level.drop(i, treasureSpots.remove(0));
		}
		Imp.Quest.rewardOptions.clear();

		VaultTreasure vis = new VaultTreasure();
		vis.setRect(treasure.left, treasure.top-1, treasure.width(), treasure.height()+1);
		level.customTiles.add(vis);

	}

	private int warnState = 0;
	private boolean lockTriggered = false;

	public void processHeroStep(Hero hero){
		if (!lockTriggered){
			Point heroPos = Dungeon.level.cellToPoint(hero.pos);
			int distance = Math.max(Math.abs(heroPos.x - lockedDoor.x), Math.abs(heroPos.y - lockedDoor.y));
			//clear warned state if hero leaves
			if (distance <= 3){
				Level.set(Dungeon.level.pointToCell(entryDoor), Terrain.LOCKED_DOOR);
				GameScene.updateMap(Dungeon.level.pointToCell(entryDoor));
				VaultBossElemental boss = new VaultBossElemental();
				boss.pos = Dungeon.level.pointToCell(center());
				GameScene.add(boss, 1);
				//we add a 1 turn delay, but compute FOV to prevent an opening surprise attack
				boss.fieldOfView = new boolean[Dungeon.level.length()];
				FieldOfView.update( Dungeon.level,  boss, boss.fieldOfView );
				boss.aggro(Dungeon.hero);
				boss.sprite.turnTo(boss.pos, Dungeon.hero.pos);
				boss.setElementalForm(boss.curForm()); //re-assert default form for particle fx
				Dungeon.level.seal();
				lockTriggered = true;
			} else if (distance == 4 && warnState < 2) {
				GLog.n(Messages.get(VaultFinalRoom.class, "final_warning"));
				Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
				hero.interrupt();
				warnState = 2;
			} else if (distance >= 5 && warnState == 2){
				warnState = 1;
			} else if (distance <= 10 && warnState < 1){
				hero.interrupt();
				ShatteredPixelDungeon.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						//we check to ensure the hero has at least a T2 each of:
						// melee weapon, thrown weapon, armor, and wand

						boolean[] prepCriteria = new boolean[4];
						for (Item i : Dungeon.hero.belongings){
							//+2 or higher items means they're T2 or T3 vault loot
							if (i.level() >= 2){
								if (i instanceof MagesStaff){
									if (((MagesStaff) i).wandClass() != null){
										prepCriteria[2] = true;
									}
								}
								else if (i instanceof MeleeWeapon) prepCriteria[0] = true;
								else if (i instanceof MissileWeapon) prepCriteria[1] = true;
								else if (i instanceof Armor) prepCriteria[2] = true;
								else if (i instanceof Wand) prepCriteria[3] = true;
							}
						}

						boolean prepared = true;
						for (Boolean b : prepCriteria){
							prepared = prepared && b;
						}

						if (!Imp.Quest.mirrorUsed){
							prepared = false;
						}

						//if they don't, they are likely unprepared, and get a warning
						if (!prepared) {
							GameScene.show(new WndTitledMessage(new ImpSprite(),
									Messages.titleCase(Messages.get(Imp.class, "name")),
									Messages.get(VaultFinalRoom.class, "imp_warning_unprepared")));
						} else {
							GameScene.show(new WndTitledMessage(new ImpSprite(),
									Messages.titleCase(Messages.get(Imp.class, "name")),
									Messages.get(VaultFinalRoom.class, "imp_warning_prepared")));
						}
					}
				});
				warnState = 1;
			} else if (distance > 12){
				warnState = 0;
			}
		}
	}

	public boolean elementalWasSummoned(){
		return lockTriggered;
	}

	public void unlock(){
		Level.set(Dungeon.level.pointToCell(entryDoor), Terrain.DOOR);
		GameScene.updateMap(Dungeon.level.pointToCell(entryDoor));
		Level.set(Dungeon.level.pointToCell(lockedDoor), Terrain.DOOR);
		GameScene.updateMap(Dungeon.level.pointToCell(lockedDoor));
		for (Heap h : Dungeon.level.heaps.valueList()){
			Item i = h.peek();
			//only final treasure rings don't have level known
			if (i instanceof Ring && !i.levelKnown){
				i.identify(false);
			}
		}
	}

	private static final String ENTRY_DOOR_X = "entry_door_x";
	private static final String ENTRY_DOOR_Y = "entry_door_y";
	private static final String LOCKED_DOOR_X = "locked_door_x";
	private static final String LOCKED_DOOR_Y = "locked_door_y";
	private static final String WARN_STATE = "warn_state";
	private static final String LOCK_TRIGGERED = "lock_triggered";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);

		bundle.put(ENTRY_DOOR_X, entryDoor.x);
		bundle.put(ENTRY_DOOR_Y, entryDoor.y);

		bundle.put(LOCKED_DOOR_X, lockedDoor.x);
		bundle.put(LOCKED_DOOR_Y, lockedDoor.y);

		bundle.put(WARN_STATE, warnState);
		bundle.put(LOCK_TRIGGERED, lockTriggered);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);

		entryDoor = new Point();
		entryDoor.x = bundle.getInt(ENTRY_DOOR_X);
		entryDoor.y = bundle.getInt(ENTRY_DOOR_Y);

		lockedDoor = new Point();
		lockedDoor.x = bundle.getInt(LOCKED_DOOR_X);
		lockedDoor.y = bundle.getInt(LOCKED_DOOR_Y);

		warnState = bundle.getInt(WARN_STATE);
		lockTriggered = bundle.getBoolean(LOCK_TRIGGERED);
	}

	public static class MarkerTiles extends CustomTilemap {

		{
			texture = Assets.Environment.CITY_QUEST;
			tileW = tileH = 9;
		}

		final int TEX_WIDTH = 256;

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			v.map(mapSimpleImage(5, 4, TEX_WIDTH), 9);
			return v;
		}

		@Override
		public Image image(int tileX, int tileY) {
			//no examine image on sides
			if (tileX == 0 || tileY == 0 || tileX == tileW-1 || tileY == tileH-1){
				return null;
			}
			//...or corners 1 in from the sides
			if ((tileX == 1 || tileX == tileW-2) && (tileY == 1 || tileY == tileH-2)){
				return null;
			}
			return super.image(tileX, tileY);
		}

		@Override
		public String name(int tileX, int tileY) {
			return Messages.get(this, "name");
		}

		@Override
		public String desc(int tileX, int tileY) {
			return Messages.get(this, "desc");
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			//prior to BETA-4
			if (tileW == 7){
				tileW = tileH = 9;
				tileX -= 1;
				tileY -= 1;
			}

		}
	}

	public static class VaultTreasure extends CustomTilemap {

		{
			texture = Assets.Environment.CITY_QUEST;
		}

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];
			//up to five banners, which we place unless there's a door

			for (int i = 0; i < data.length; i++){
				data[i] = -1;
				if (i < tileW) {
					if (i == 0)         data[i] = 5*16 + 4;
					if (i == tileW-1)   data[i] = 5*16 + 3;
				} else {
					int cell = tileX + Dungeon.level.width()*tileY;
					cell += i%tileW + (i/tileW)*Dungeon.level.width();
					if (Dungeon.level.map[cell] == Terrain.PEDESTAL){
						data[i] = 7*16 + 3;
					} else if (Dungeon.level.map[cell-Dungeon.level.width()] == Terrain.WALL
							|| Dungeon.level.map[cell-Dungeon.level.width()] == Terrain.WALL_DECO) {
						data[i] = 6 * 16 + 2;
						if (Dungeon.level.map[cell+1] == Terrain.WALL){
							data[i] += 1;
						} else if (Dungeon.level.map[cell-1] == Terrain.WALL){
							data[i] += 2;
						}
					} else {
						if (Dungeon.level.map[cell+1] == Terrain.WALL){
							data[i] = 6 * 16 + 0;
						} else if (Dungeon.level.map[cell-1] == Terrain.WALL){
							data[i] = 6 * 16 + 1;
						} else {
							//plus 0 1 or 2
							data[i] = 7*16 + DungeonTileSheet.tileVariance[cell]/34;
						}
					}
				}
			}
			v.map(data, tileW);
			return v;
		}

		@Override
		public Image image(int tileX, int tileY) {
			if (tileY < 1){
				return null;
			}
			return super.image(tileX, tileY);
		}

		@Override
		public String desc(int tileX, int tileY) {
			int cell = tileX+this.tileX + (tileY+this.tileY)*Dungeon.level.width();
			if (Dungeon.level.map[cell] != Terrain.PEDESTAL){
				return Messages.get(this, "desc");
			} else {
				return super.desc(tileX, tileY);
			}
		}
	}
}
