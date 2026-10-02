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
import pd.items.equipment.wands.Wand;
import pd.items.equipment.wands.WandOfAcid;
import pd.items.equipment.wands.WandOfCharm;
import pd.items.equipment.wands.WandOfDisintegration;
import pd.items.equipment.wands.WandOfFirebolt;
import pd.items.equipment.wands.WandOfFlock;
import pd.items.equipment.wands.WandOfFreeze;
import pd.items.equipment.wands.WandOfLight;
import pd.items.equipment.wands.WandOfLightning;
import pd.items.equipment.wands.WandOfMeteorite;
import pd.items.equipment.wands.WandOfSwamp;
import pd.items.equipment.wands.WandOfTCloud;
import pd.items.equipment.wands.fusion.WandOfBlood;
import pd.items.equipment.wands.fusion.WandOfFlow;
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
import pd.messages.InlineText;

public class Wandmaker extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Wandmaker.class)
			.t("name", "法杖制作者")
			.t("berry_1", "啊，在这种地方遇到一个体面的人是多么惊喜！我来这里是为了寻找一样稀有的材料——一个腐莓种子。作为一个施法者，我可以轻易地对付这里的怪物，但我迷路了，而且魔法盾也在逐渐减弱，好尴尬！也许你能够帮助我?为了报答你的工作，我很愿意给你一把我制作的高质量法杖。")
			.t("berry_2", "腐莓找的怎么样了，%s?没有?不用担心，我不着急。")
			.t("dust_1", "啊，在这种地方遇到一个体面的人是多么惊喜！我来这里是为了寻找一样稀有的材料——尸骨灰烬。它可以在遗骸中被收集，其中有很强的诅咒。作为一个施法者，我可以轻易地对付这里的怪物，但我迷路了，而且魔法盾也在逐渐减弱，好尴尬！也许你能够帮助我?为了报答你的工作，我很愿意给你一把我制作的高质量法杖。")
			.t("dust_2", "尸骨灰烬找的怎么样了，%s?没有?遗骸应该是最有价值寻找的地方。")
			.t("intro_warrior", "哦，你好！能在这片压抑的地方遇见一位北境战士可真是个惊喜！想必你走了很远才来到此地。如果你对冒险有足够的兴趣，我这有个差事给你。")
			.t("intro_rogue", "天哪，你吓了我一跳！在这里这么久你是我见过的第一个尚有理智的强盗，所以你肯定是从地表下来的！如果你愿意帮助我这样一名陌生人的话，这有一个任务或许你能帮上忙。")
			.t("intro_mage", "哦，你好啊%s！我好像听说你在法师学院那里搞了不少麻烦？算了这没关系，反正我也不喜欢那些墨守成规的榆木脑袋。如果你愿意的话，我这有个任务可以让你帮忙。")
			.t("intro_huntress", "啊，你好啊姑娘！能在这下面见到一张友善的面孔真是太惊喜了，你说是吧？咦，说来，我敢打赌以前在哪见过你，但想不起具体...哦算了，不用在意，如果你是来这里冒险的话，我这有个任务交给你做。")
			.t("intro_duelist", "哦，小姐你好！多么压抑的地方，多么美妙的偶遇！如果你有兴趣帮助一位糟老头子的话，我有份小差事可以交给你。")
			.t("intro_cleric", "哦，您好陛下！能在如此压抑之地与您相遇实为一件幸事！我虽无意打扰，但我有一事请您相助。")
			.t("intro_spellsword", "哦，一位兼修剑术与法术的同行！你看起来比大多数冒险者更适合这个地方。如果你愿意的话，我这里正有一件能发挥你本领的差事。")
			.t("intro_1", "\n\n我在这里是为了寻找一个制作法杖需要的稀有材料，但我迷了路，而且身上的魔法盾也在慢慢减弱。我必须马上离开这里，但我不想空手而归。")
			.t("intro_dust", "我在寻找一些_尸尘_。这是一种通常出现在这种地方的被诅咒的特殊骨灰。它应该就在附近某个被隔住的房间里，我很肯定你能在那里找到一些尸尘。不过要小心，尸尘上的诅咒相当强大，_尽快将它带回来_，我会净化其中的诅咒。")
			.t("intro_ember", "我在寻找能从新生火焰元素身上获得的_元素余烬_。元素生物通常出现在失控的召唤仪式上，因此要召唤它只需要去找到一些蜡烛和一个仪式场地——我很确定你能找到这样的地方。另外，与其战斗时，你不会想要_与其近身肉搏_的，你可能会需要一些_有冻结效果的物品_。新生火焰元素强大而狂暴，但难以抵御寒冷。")
			.t("intro_berry", "这个监狱以前的典狱长养着一株_腐莓_，我想要一粒它的种子。现如今，这株植物的长势可能已经失控，所以想搞到它的种子绝非易事。它所在的花园应该就在这附近。你若想全身而退的话，就尽量_远离它的触手藤_。纵火烧死腐莓听起来很可行，但请别这么做，烧死它也会烧毁它的种子。")
			.t("intro_2", "\n\n如果你能把它带给我，我愿意用一根我精心制作的法杖当做酬劳！我带了两根过来，你可以挑一根自己喜欢的。")
			.t("reminder_dust", "尸尘找的怎么样了，%s？试着寻找一些_障碍物_。")
			.t("reminder_ember", "余烬找的怎么样了，%s？你需要找到_四根蜡烛_和一个_仪式场地_。")
			.t("reminder_berry", "腐莓种子找的怎么样了，%s？它就在一个_充满植被的房间_里。")
			.t("def_verb", "格挡")
			.t("desc", "这位老先生的表情看起来十分困扰。他正在被一个力场盾牌保护着。");
	}


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
