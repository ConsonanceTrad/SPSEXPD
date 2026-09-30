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
 */

package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.items.AdamantWand;
import pd.items.Heap;
import pd.items.Item;
import pd.items.quest.CorpseDust;
import pd.items.wands.Wand;
import pd.items.wands.WandOfAcid;
import pd.items.wands.WandOfCharm;
import pd.items.wands.WandOfDisintegration;
import pd.items.wands.WandOfFirebolt;
import pd.items.wands.WandOfFlock;
import pd.items.wands.WandOfFreeze;
import pd.items.wands.WandOfLight;
import pd.items.wands.WandOfLightning;
import pd.items.wands.WandOfMeteorite;
import pd.items.wands.WandOfSwamp;
import pd.items.wands.WandOfTCloud;
import pd.items.wands.fusion.WandOfBlood;
import pd.items.wands.fusion.WandOfFlow;
import pd.journal.Notes;
import pd.levels.GroundItems;
import pd.levels.PrisonLevel;
import pd.levels.Terrain;
import pd.levels.rooms.Room;
import pd.messages.Messages;
import pd.plants.Rotberry;
import pd.scenes.GameScene;
import pd.sprites.WandmakerSprite;
import pd.windows.WndQuest;
import pd.windows.WndWandmaker;
import render.noosa.Game;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class Wandmaker extends NPC {

	{
		spriteClass = WandmakerSprite.class;
		properties.add(Property.HUMAN);
		properties.add(Property.IMMOVABLE);
	}

	@Override
	public Notes.Landmark landmark() {
		return Notes.Landmark.WANDMAKER;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage(int dmg, Object src) {
	}

	@Override
	public boolean add(Buff buff) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public boolean interact(Char c) {
		if (sprite != null) sprite.turnTo(pos, Dungeon.hero.pos);
		if (c != Dungeon.hero) return super.interact(c);

		if (Quest.given) {
			Item item = Quest.alternative
					? Dungeon.hero.belongings.getItem(CorpseDust.class)
					: Dungeon.hero.belongings.getItem(Rotberry.Seed.class);
			if (item != null) {
				Game.runOnRenderThread(new Callback() {
					@Override public void call() {
						GameScene.show(new WndWandmaker(Wandmaker.this, item));
					}
				});
			} else {
				tell(Quest.alternative
						? Messages.get(this, "dust_2", Messages.titleCase(Dungeon.hero.name()))
						: Messages.get(this, "berry_2", Messages.titleCase(Dungeon.hero.name())));
			}
		} else {
			Quest.placeItem();
			if (Quest.given) {
				tell(Messages.get(this, Quest.alternative ? "dust_1" : "berry_1"));
				Notes.add(Notes.Landmark.WANDMAKER);
			}
		}
		return true;
	}

	private void tell(String text) {
		Game.runOnRenderThread(new Callback() {
			@Override public void call() {
				GameScene.show(new WndQuest(Wandmaker.this, text));
			}
		});
	}

	public static class Quest {

		private static boolean spawned;
		private static boolean alternative;
		private static boolean given;

		public static Wand wand1;
		public static Wand wand2;

		public static void reset() {
			spawned = false;
			alternative = false;
			given = false;
			wand1 = null;
			wand2 = null;
		}

		private static final String NODE = "wandmaker";
		private static final String SPAWNED = "spawned";
		private static final String ALTERNATIVE = "alternative";
		private static final String GIVEN = "given";
		private static final String WAND1 = "wand1";
		private static final String WAND2 = "wand2";

		public static void storeInBundle(Bundle bundle) {
			Bundle node = new Bundle();
			node.put(SPAWNED, spawned);
			if (spawned) {
				node.put(ALTERNATIVE, alternative);
				node.put(GIVEN, given);
				node.put(WAND1, wand1);
				node.put(WAND2, wand2);
			}
			bundle.put(NODE, node);
		}

		public static void restoreFromBundle(Bundle bundle) {
			Bundle node = bundle.getBundle(NODE);
			if (!node.isNull() && (spawned = node.getBoolean(SPAWNED))) {
				if (node.contains(ALTERNATIVE)) {
					alternative = node.getBoolean(ALTERNATIVE);
					given = node.getBoolean(GIVEN);
				} else {
					int prePortType = node.getInt("type");
					alternative = prePortType != 3;
					given = prePortType == 2 ? false : node.getBoolean(GIVEN);
				}
				wand1 = (Wand) node.get(WAND1);
				wand2 = (Wand) node.get(WAND2);
			} else {
				reset();
			}
		}

		public static void spawn(PrisonLevel level, Room room) {
			if (spawned || Dungeon.depth != 7) return;

			Wandmaker npc = new Wandmaker();
			int spawnPos = -1;
			for (int i = 0; i < 100 && spawnPos < 0; i++) {
				int candidate = level.pointToCell(room.random());
				if (candidate >= 0 && candidate < level.length()
						&& level.map[candidate] != Terrain.ENTRANCE
						&& level.map[candidate] != Terrain.SIGN
						&& level.map[candidate] != Terrain.DEW_BLESS
						&& level.passable[candidate]) {
					spawnPos = candidate;
				}
			}
			if (spawnPos < 0) spawnPos = level.randomRespawnCell(npc);
			if (spawnPos < 0) return;

			npc.pos = spawnPos;
			level.mobs().add(npc);
			spawned = true;
			alternative = Random.Int(2) == 0;
			given = false;
			wand1 = battleWand(Random.Int(7));
			wand2 = utilityWand(Random.Int(7));
			wand1.random().upgrade();
			wand2.random().upgrade();
		}

		private static Wand battleWand(int roll) {
			switch (roll) {
				case 0: return new WandOfLight();
				case 1: return new WandOfDisintegration();
				case 2: return new WandOfFirebolt();
				case 3: return new WandOfLightning();
				case 4: return new WandOfAcid();
				case 5: return new WandOfBlood();
				default: return new WandOfFreeze();
			}
		}

		private static Wand utilityWand(int roll) {
			switch (roll) {
				case 0: return new WandOfCharm();
				case 1: return new WandOfFlock();
				case 2: return new WandOfSwamp();
				case 3: return new WandOfMeteorite();
				case 4:
				case 6: return new WandOfFlow();
				default: return new WandOfTCloud();
			}
		}

		public static void placeItem() {
			if (Dungeon.level == null) return;
			if (alternative) {
				ArrayList<Heap> candidates = new ArrayList<>();
				for (Heap heap : Dungeon.level.heaps.valueList()) {
					if (heap.type == Heap.Type.SKELETON
							&& (Dungeon.level.heroFOV == null || !Dungeon.level.heroFOV[heap.pos])) {
						candidates.add(heap);
					}
				}
				if (!candidates.isEmpty()) {
					Random.element(candidates).drop(new CorpseDust());
					given = true;
					return;
				}
				int pos = freeRespawnCell();
				if (pos >= 0) {
					Heap heap = Dungeon.level.drop(new CorpseDust(), pos);
					heap.type = Heap.Type.SKELETON;
					if (heap.sprite != null) heap.sprite.link();
					given = true;
				}
			} else {
				int pos = freeRespawnCell();
				if (pos >= 0) {
					GroundItems.plant( Dungeon.level, new Rotberry.Seed(), pos);
					given = true;
				}
			}
		}

		private static int freeRespawnCell() {
			for (int i = 0; i < 200; i++) {
				int pos = Dungeon.level.randomRespawnCell(null);
				if (pos < 0) return -1;
				if (Dungeon.level.heaps.get(pos) == null) return pos;
			}
			return -1;
		}

		public static boolean active() {
			return false;
		}

		public static Item completionBonus() {
			return new AdamantWand();
		}

		public static void complete() {
			wand1 = null;
			wand2 = null;
			Notes.remove(Notes.Landmark.WANDMAKER);
		}
	}
}
