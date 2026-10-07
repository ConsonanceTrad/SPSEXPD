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

package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.Buff;
import pd.actors.mobs.Golem;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Monk;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.armor.PlateArmor;
import pd.items.equipment.artifacts.Artifact;
import pd.items.quest.DwarfToken;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.journal.Notes;
import pd.levels.rooms.Room;
import pd.levels.rooms.quest.AmbitiousImpRoom;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ImpSprite;
import pd.windows.WndImpOld;
import pd.windows.WndQuest;
import render.noosa.Game;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import pd.messages.InlineText;

public class Imp extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Imp.class)
			.t("name", "野心勃勃的小恶魔")
			.t("old_intro", "你是个冒险家吗?我爱冒险家！如果有什么东西需要被解决的话，他们永远都能把活干好。我说的对吗?当然是在有赏金的前提下 ;)")
			.t("old_golems_1", "总之，我需要你杀一些_魔像_。你看，我要在这里开始搞点小本生意，但这些愚蠢的傀儡只会毁掉我的生意！跟这些发着光的大个子花岗岩根本没法交流，真是该死！所以请，杀死……我想想，_6个魔像_，然后奖励就是你的了。")
			.t("old_monks_1", "总之，我需要你杀一些_武僧_。你看，我要在这里开始搞点小本生意，但这些疯子不买任何东西还会吓跑顾客。所以请，杀死……我想想，_8个武僧_，然后奖励就是你的了。")
			.t("old_golems_2", "魔像猎杀得怎么样了?你已经击杀了_6只魔像_了吗?")
			.t("old_monks_2", "喔，你还活着！我就知道你得功夫很好；) 只要别忘了拿来那些武僧的标记就好，我需要_8个标记_。")
			.t("cya", "我们会再见的，%s！")
			.t("hey", "喂喂喂，%s！")
			.t("quest_intro_1", "哦你好啊，我猜你该是位冒险家？我最爱冒险家了！在需要人搭把手的时候，你们这些冒险家最可靠了。当然当然，我已经备好报酬了，这我懂的 ;)\n\n这下面有一座巨大的矮人宝库，里面可是装满了各种奇珍异宝。我也算是位收藏家，而宝库里有一尊我朝思暮想的雕像。\n\n不过，这宝库里面全是守卫和陷阱，所以希望你能帮我一点小忙！")
			.t("quest_intro_2", "宝库入口被魔法封印了。我早准备了一道魔法可以把你传送进去，不过_我没法把你的装备也一起传送进去_。好在宝库里应该也有不少疏于看管的装备用来防身，_在找到合适的装备之前，你大可一边躲开守卫一边搜刮_。\n\n这道魔法能让你带两件宝库里的藏品回来。其中一件当然得是那座雕像，而你还可以按自己心意_再拿一件东西出来_！\n\n我还有些价值不菲的收藏，我想你会很感兴趣的！乐意赏光帮我个忙的话，我愿意拿出收藏来开个小店供您光顾做些买卖，_店面就设在不远的楼下_。")
			.t("quest_in_progress", "只要你准备好了就走到宝库入口中间去，然后我就把你传送进去。")
			.t("enter_text", "准备好了？你的东西先放我这，_等你回来再还你_。\n\n先穿上这件_布甲_，这块_逃脱棱晶_也拿上，拿到我要的雕像了就用它出来。······呃，_你要是遇到麻烦了也可以提前用这个脱身_，但是没能带出雕像的话，报酬可是也要大打折扣的！\n\n宝库已经很久没人造访过了，里面的守卫想必也很松懈。躲过他们的巡查应该不难，只要_跟在他们背后就不会被发现_。就算你被他们注意到了，_如果只暴露了一小会，只要马上躲起来的话守卫们也不会追上来搜查的_。稍加注意的话，你大概还能_听到他们走到你附近的脚步声_。")
			.t("quest_completed_bad", "好吧，至少感谢你的时间。\n\n我用的传送魔法还需要很长时间才能再次发动，好走不送。")
			.t("quest_completed_good", "感谢你的帮助！\n\n准备好传送魔法还要点时间，这期间我得用你提供的信息给接下来的行动做些准备。\n\n我打包完东西就去楼下，咱们待会店里见。不过你懂的，我毕竟还得赚点钱用呢，店里的东西可不能白送你 ;)")
			.t("quest_completed_great", "干得漂亮，非常感谢！\n\n现在万事俱备，我打包好行李就去楼下，待会请您务必来店里坐坐。不过您要知道，店里的收藏都是精品，我实在难以割爱白白送人，起码也得让我赚点小钱嘛 ;)\n\n什么，您问我为什么想要那座雕像？其实这玩意也没什么稀奇的，或许和您想象的大相径庭了。它来自一个早已不存于世的边陲之地，要不是被这帮贪婪的矮人掠夺到了这里，恐怕这座雕像也已和那里的一切一样不复存在了...")
			.t("enter_yes", "降入宝库")
			.t("enter_no", "留在原地")
			.t("desc", "这个红色的小东西看起来像个小恶魔。小恶魔是一种低等恶魔，力量不强但头脑精明。\n\n在古老的民间故事中，恶魔常常被描述为来自异世界的旅者。你仅仅是如是听闻过，从未见过能证明它们真正存在的证据，不过你面前的这位小恶魔貌似货真价实，而且还挺乐意见到你的。");
	}




	{
		spriteClass = ImpSprite.class;

		properties.add(Property.DEMONIC);
		properties.add(Property.IMMOVABLE);
	}
	
	private boolean seenBefore = false;

	@Override
	public Notes.Landmark landmark() {
		return Quest.isCompleted() ? null : Notes.Landmark.IMP;
	}

	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			return true;
		}

		//extra logic in case imp is holding the quest reward
		if (Quest.isCompleted() && Quest.reward != null){
			Dungeon.level.drop(Quest.reward, pos);
			throwItems();
			Quest.reward = null;
		}

		if (Quest.isCompleted() && Quest.score > 2000
				&& fieldOfView != null && !fieldOfView[Dungeon.hero.pos]){
			flee();
		} else if (!Quest.given && Dungeon.level.visited[pos]) {
			if (!seenBefore && Dungeon.level.heroFOV[pos]) {
				yell(Messages.get(this, "hey", Messages.titleCase(Dungeon.hero.name())));
				seenBefore = true;
			}
		} else {
			seenBefore = false;
		}
		
		return super.act();
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage( int dmg, Object src ) {
		//do nothing
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	public boolean interact(Char c) {
		
		sprite.turnTo( pos, Dungeon.hero.pos );

		if (c != Dungeon.hero){
			return true;
		}

		//pre v4.4.0 logic
		if (Quest.oldQuest) {
			if (Quest.given) {

				DwarfToken tokens = Dungeon.hero.belongings.getItem(DwarfToken.class);
				if (tokens != null && tokens.quantity() >= Quest.legacyTokenGoal()) {
					Game.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							GameScene.show(new WndImpOld(Imp.this, tokens));
						}
					});
				} else {
					tell(Quest.alternative ?
							Messages.get(this, "old_monks_2", Messages.titleCase(Dungeon.hero.name()))
							: Messages.get(this, "old_golems_2", Messages.titleCase(Dungeon.hero.name())));
				}

			} else {
				tell(Messages.get(this, "old_intro") + "\n" + (Quest.alternative ?
						Messages.get(this, "old_monks_1", Messages.titleCase(Dungeon.hero.name()))
						: Messages.get(this, "old_golems_1", Messages.titleCase(Dungeon.hero.name()))));
				Quest.given = true;
				Quest.completed = false;
			}
		} else {
			if (!Quest.given()){
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndQuest(Imp.this, Messages.get(Imp.this, "quest_intro_1")) {
							@Override
							public void hide() {
								super.hide();

								Quest.given = true;
								Quest.completed = false;

								tell(Messages.get(Imp.this, "quest_intro_2"));
							}
						});
					}
				});
			} else if (!Quest.isCompleted()) {
				tell(Messages.get(Imp.this, "quest_in_progress"));
			} else {
				if (Quest.score <= 2000){
					tell(Messages.get(Imp.this, "quest_completed_bad"));
				} else if (Quest.score < 4000){
					tell(Messages.get(Imp.this, "quest_completed_good"));
				} else {
					tell(Messages.get(Imp.this, "quest_completed_great"));
				}
			}
		}

		return true;
	}
	
	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( Imp.this, text ));
			}
		});
	}

	public void flee() {
		
		yell( Messages.get(this, "cya", Messages.titleCase(Dungeon.hero.name())) );
		
		destroy();
		sprite.die();
	}

	public static class Quest {

		private static boolean spawned;

		//variables exclusive to old, pre-4.0.0 Imp quest
		private static boolean oldQuest = false;
		private static boolean alternative; //true= golems, false = monks

		//variables shared by both quests
		private static boolean given;
		private static boolean completed;
		public static Item reward; //just used to hold the reward if her's inventory is full in new version

		//variacles exclusive to new quest
		public static ArrayList<Item> rewardOptions = new ArrayList<>();
		public static int hazardFreebies; //player gets two free hits from hazards before they start penalizing score
		public static boolean mirrorUsed = false;
		private static int score; //Not the score used in rankings! This score has no penalty applied
		
		public static void reset() {
			spawned = false;
			oldQuest = false;
			alternative = false;
			given = false;
			completed = false;

			reward = null;
			rewardOptions.clear();
			hazardFreebies = 2;
			mirrorUsed = false;
			score = 0;
		}
		
		private static final String NODE        = "demon";

		private static final String SPAWNED     = "spawned";

		private static final String OLD_QUEST   = "old_quest";
		private static final String ALTERNATIVE = "alternative";
		private static final String REWARD      = "reward";

		private static final String GIVEN       = "given";
		private static final String COMPLETED   = "completed";

		private static final String HAZRD_FREEBIES = "hazard_freebies";
		private static final String SCORE       = "score";
		private static final String REWARD_OPTIONS = "reward_options";
		private static final String MIRROR_USED = "mirror_used";

		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				node.put( OLD_QUEST, oldQuest );
				node.put( ALTERNATIVE, alternative );
				
				node.put( GIVEN, given );
				node.put( COMPLETED, completed );
				node.put( REWARD, reward );

				node.put( HAZRD_FREEBIES, hazardFreebies );
				node.put( SCORE, score );
				node.put( REWARD_OPTIONS, rewardOptions );
				node.put( MIRROR_USED, mirrorUsed );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
				boolean migrateModernQuest = node.contains(OLD_QUEST)
						&& !node.getBoolean(OLD_QUEST);

				if (node.contains( OLD_QUEST )){
					oldQuest = node.getBoolean( OLD_QUEST );
				} else {
					oldQuest = true;
				}
				if (oldQuest){
					alternative	= node.getBoolean( ALTERNATIVE );
					score = 0;
					rewardOptions.clear();
					mirrorUsed = false;
				} else {
					alternative = false;
					hazardFreebies = node.getInt( HAZRD_FREEBIES );
					mirrorUsed = node.getBoolean( MIRROR_USED );
					score = node.getInt( SCORE );
					rewardOptions = new ArrayList<>((Collection<Item>) (Collection<?>) node.getCollection( REWARD_OPTIONS ));
				}

				reward = (Item)node.get( REWARD );
				
				given = node.getBoolean( GIVEN );
				completed = node.getBoolean( COMPLETED );

				// Early SPS-SPD builds could save Shattered's vault quest. Completed
				// saves keep their shop unlock; unfinished ones restart as the 0.9.8 hunt.
				if (migrateModernQuest) {
					oldQuest = true;
					alternative = Random.Int(2) == 0;
					given = completed;
					reward = completed ? null : createLegacyReward();
					hazardFreebies = 2;
					score = 0;
					rewardOptions.clear();
					mirrorUsed = false;
				}
			} else {
				reset();
			}
		}

		public static ArrayList<Room> spawn( ArrayList<Room> rooms ) {
			//SPSEXPD: 每章 8 层——小恶魔房出现在大厅/终局（33-40），越深越容易
			if (!spawned && Dungeon.depth > 32 && Random.Int( Math.max(1, 40 - Dungeon.depth) ) == 0) {

				rooms.add(new AmbitiousImpRoom());
				spawned = true;

				oldQuest = false;
				reward = null;
				score = 0;
				
				given = false;
				mirrorUsed = false;

				rewardOptions.clear();
				Item artif = Generator.randomArtifact();
				//generate a ring instead
				if (artif != null){
					((Artifact)artif.identify(false)).transferUpgrade(5);
				} else {
					artif = Generator.random(Generator.Category.RING);
					//we delay the ID on rings until the boss is defeated
					artif.level(Random.IntRange(2, 4));
				}
				rewardOptions.add(artif);

				Item ring;
				do {
					ring = Generator.random(Generator.Category.RING);
				} while (ring.getClass() == artif.getClass()); //rare cases of the same kind of ring twice
				//we delay the ID on rings until the boss is defeated
				ring.level(Random.IntRange(2, 4));
				rewardOptions.add(ring);

				if (Random.Int(2) == 0) {
					rewardOptions.add(((Weapon)Generator.random(Generator.Category.WEP_T5)).enchant().identify(false).level(Random.IntRange(2, 4)));
					rewardOptions.add(((Weapon)Generator.random(Generator.Category.MIS_T4)).enchant().identify(false).level(Random.IntRange(3, 5)));
				} else {
					rewardOptions.add(((Weapon)Generator.random(Generator.Category.MIS_T5)).enchant().identify(false).level(Random.IntRange(2, 4)));
					rewardOptions.add(((Weapon)Generator.random(Generator.Category.WEP_T4)).enchant().identify(false).level(Random.IntRange(3, 5)));
				}
				rewardOptions.add(new PlateArmor().inscribe().identify(false).level(Random.IntRange(2, 4)));
				Wand w = (Wand) Generator.random(Generator.Category.WAND);
				w.identify(false).level(Random.IntRange(2, 4));
				w.curCharges = w.maxCharges;
				rewardOptions.add(w);

				for (Item i : rewardOptions){
					i.cursed = false;
				}
			}

			return rooms;
		}

		public static boolean spawnLegacy(pd.levels.CityLevel level) {
			if (spawned || Dungeon.depth <= 16 || level == null) return false;

			Imp npc = new Imp();
			int cell = -1;
			for (int attempt = 0; attempt < 30 && cell == -1; attempt++) {
				int candidate = level.randomRespawnCell(npc);
				if (candidate >= 0 && level.heaps.get(candidate) == null) cell = candidate;
			}
			if (cell == -1) return false;

			npc.pos = cell;
			level.mobs().add(npc);
			spawned = true;
			oldQuest = true;
			alternative = Random.Int(2) == 0;
			given = false;
			completed = false;
			score = 0;
			rewardOptions.clear();
			mirrorUsed = false;

			reward = createLegacyReward();
			return true;
		}

		private static Ring createLegacyReward() {
			Ring ring = null;
			for (int attempt = 0; attempt < 100; attempt++) {
				Item candidate = Generator.random(Generator.Category.RING);
				if (candidate instanceof Ring && !candidate.cursed) {
					ring = (Ring)candidate;
					break;
				}
			}
			if (ring == null) ring = (Ring)Generator.randomUsingDefaults(Generator.Category.RING);
			ring.upgrade(2);
			ring.cursed = true;
			return ring;
		}

		public static boolean given(){
			return given;
		}

		public static boolean isOld(){
			return oldQuest;
		}

		public static int legacyTokenGoal() {
			return alternative ? 8 : 6;
		}

		public static void oldProcess( Mob mob ) {
			if (spawned && oldQuest && given && !completed) {
				if ((alternative && mob instanceof Monk) ||
					(!alternative && mob instanceof Golem)) {
					
					pd.items.Heap heap =
							Dungeon.level.drop(new DwarfToken(), mob.pos);
					if (heap.sprite != null) heap.sprite.drop();
				}
			}
		}
		
		public static void oldComplete() {
			reward = null;
			completed = true;

			Statistics.questScores[3] = 4000;
			Notes.remove( Notes.Landmark.IMP );
		}

		public static void complete( int score ){
			completed = true;

			Imp.Quest.score = score;
			Statistics.questScores[3] += score;
			Notes.remove( Notes.Landmark.IMP );
		}
		
		public static boolean isCompleted() {
			return spawned && completed;
		}

		public static boolean earnedShop() {
			return completed && (oldQuest || score > 2000);
		}
	}
}
