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

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.Buff;
import pd.items.ChaosPack;
import pd.items.CurseBlood;
import pd.items.EmptyBody;
import pd.items.EquipableItem;
import pd.items.Generator;
import pd.items.Item;
import pd.items.Triforce;
import pd.items.TriforceOfCourage;
import pd.items.TriforceOfPower;
import pd.items.TriforceOfWisdom;
import pd.items.equipment.armor.Armor;
import pd.items.quest.DarkGold;
import pd.items.quest.Pickaxe;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.specific.sellitem.BrokenHammer;
import pd.items.equipment.trinkets.ParchmentScrap;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.special.ShadowEater;
import pd.journal.Notes;
import pd.levels.rooms.Room;
import pd.levels.rooms.quest.BlacksmithRoom;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.BlacksmithSprite;
import pd.utils.GLog;
import pd.windows.WndBlacksmith;
import pd.windows.WndBlacksmithLegacy;
import pd.windows.WndQuest;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import pd.messages.InlineText;

public class Blacksmith extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Blacksmith.class)
			.t("name", "巨魔铁匠")
			.t("intro_quest_warrior", "嘿，人类！你看起来还挺强悍的。")
			.t("intro_quest_mage", "嘿，人类！你看起来还挺聪明的。")
			.t("intro_quest_rogue", "嘿，人类！你看起来还挺鬼祟的。")
			.t("intro_quest_huntress", "嘿，人类！你看起来还挺敏捷的。")
			.t("intro_quest_duelist", "嘿，人类！你看起来还挺勇猛的。")
			.t("intro_quest_cleric", "嘿，人类！你看起来还挺正经的。")
			.t("intro_quest_spellsword", "嘿，人类！你看起来还挺机灵的。")
			.t("intro_quest_start", "不想当个没用的废物，对吗？正好，我这有活给你干。我在一座旧矿井上建造了这间铁匠铺，那里就是下去的梯子。拿着这把镐子然后给我挖点_暗金矿，40块_就够了。下面的石头不是很硬，_只要你用点力气连墙壁都能凿烂_。\n\n什么，要我如何报答你？真贪心...\n\n好吧，好吧，我可以给你打打铁。想想你运气有多好吧，我可是这附近唯一的铁匠。")
			.t("intro_quest_crystal", "不过小心点，矿井对于你这种弱不禁风的人类来说太危险了。矿井被废弃有一段时间了，还有不少水晶长了出来。水晶亮闪闪的，但太脆了，一文不值，而且它们还被一些魔物保护着。那些东西很难缠，所以_注意点你挖矿的位置_，不然你就会引起它们的注意。\n\n下面还有个_巨型水晶_，还挺结实。如果你打算拆了它那你指定得打上一场。我敢打赌敲碎它就能减缓水晶的生长速度，所以你要是能把这活干了的话，我会再多给你点报酬。")
			.t("intro_quest_gnoll", "不过小心点，矿井对于你这种弱不禁风的人类来说太危险了。一群豺狼人也想弄点暗金，正在下面搞破坏。这群蠢货，挖矿的时候弄出来一堆塌方落石。你应该得自己开条路出来了，所以_找找刚刚塌下来的岩石_。\n\n我记得有一个豺狼人是带头惹事的，可能是个_地卜萨满_之类的。你要是能拿下它的话，我会再多给你点报酬，不过我敢肯定它会还手。")
			.t("intro_quest_fungi", "不过小心点，矿井对于你这种弱不禁风的人类来说太危险了。下面有某种魔菇长了出来。它们之中还有蘑菇哨卫，要是它们看见了你，你可得吃点苦头，不过它们没长腿倒是追不了你。还想活命的话就_别进入它们的视野内_。\n\n你要是能拿下_巨型蘑菇_的话，我会再多给你点报酬。不过它有点难缠，它与隐藏在墙壁中的菌丝节孢相连。你得拆掉其中的大部分节孢才能真正地对它造成伤害。")
			.t("reminder", "还要接着浪费我的时间？矿井入口就在那呢。")
			.t("reminder_crystal", "暗金多多益善，最好再拆了巨型水晶。")
			.t("reminder_gnoll", "暗金多多益善，最好再杀了豺狼地卜师。")
			.t("reminder_fungi", "暗金多多益善，最好再杀了巨型蘑菇。")
			.t("lost_pick", "你在逗我？我的镐子呢？！")
			.t("quest_start_prompt", "你准备好下去了？记得给包里留出暗金的位置。\n\n_我只准你下去一次！_")
			.t("enter_yes", "我准备好了")
			.t("enter_no", "还没有")
			.t("exit_warn_none", "这就完了？你才挖到了几块暗金！怎么说你也能挖个_40块_。\n\n_别忘了，你爬上这个梯子就别想再下去了。_")
			.t("exit_warn_low", "这就完了？看起来你挖到的暗金不多。怎么说你也能挖个_40块_。\n\n_别忘了，你爬上这个梯子就别想再下去了。_")
			.t("exit_warn_med", "完了？你是挖到了几块暗金，不过下面肯定不止这点。怎么说你也能挖个_40块_。\n\n_别忘了，你爬上这个梯子就别想再下去了。_")
			.t("exit_warn_high", "完了？看起来你挖到的暗金不少，不过对我来说还是多多益善。怎么说你也能挖个_40块_。\n\n_别忘了，你爬上这个梯子就别想再下去了。_")
			.t("exit_warn_full", "完了？你是挖到了挺多暗金，不过可千万别在下面丢了什么东西。\n\n_别忘了，你爬上这个梯子就别想再下去了。_")
			.t("exit_warn_crystal", "那个巨型水晶还在下面。你要是能拆了它，我会再多给你点报酬。")
			.t("exit_warn_gnoll", "那个豺狼地卜师还在下面。你要是能杀了它，我会再多给你点报酬。")
			.t("exit_warn_fungi", "那个巨型蘑菇还在下面。你要是能杀了它，我会再多给你点报酬。")
			.t("exit_yes", "做完了")
			.t("exit_no", "还没有")
			.t("get_lost", "我忙着呢。滚开！")
			.t("entrance_blocked", "下去的路被堵死了。")
			.t("cant_enter_old", "那片区域在这场游戏中不可用。创建一个新游戏以尝试新任务！")
			.t("def_verb", "格挡")
			.t("desc", "这个巨魔铁匠看起来和任何其他巨魔一样：又高又瘦，皮肤的色泽和纹理都像是石头。这位巨魔铁匠正捏着一把与其体形极其不符的小锤子不停地修修补补。")
			.t("shadoweater", "这仨是啥玩意……我看看……天哪，我做了什么？！")
			.t("gold_1", "嘿，人类！不想当个没用的废物，对吗？拿着这把镐子，给我挖_15块暗金矿_就够了。什么？要我如何报答你？真贪心……\n好吧，好吧，我没钱付给你，但我可以给你打打铁。想想你运气有多好吧，我可是这附近唯一的铁匠。")
			.t("gold_2", "_暗金矿_。15块。说真的，有那么难？")
			.t("keeppickaxe", "拿好这把镐子。它非常好用。")
			.t("completed", "噢，你终于回来了……算了，总比回不来好。")
			.t("same_item", "选择两个不同的物品，不能把同一件物品选两次！")
			.t("un_ided", "我得知道拿什么干活，先把它们鉴定出来！")
			.t("cursed", "我可不碰诅咒的东西！")
			.t("degraded", "这就是垃圾，质量太差了！")
			.t("need_reinforced", "这件物品承受不了那么高的升级，我没法帮你合成。")
			.t("cant_reforge", "我不能重铸这些物品！")
			.t("triforce", "你居然找到了这三个碎片！好吧，我把它们合在一起。");
	}




	@Override public Item SupercreateLoot() { return new BrokenHammer(); }
	
	{
		spriteClass = BlacksmithSprite.class;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.TROLL);
	}

	@Override
	public Notes.Landmark landmark() {
		return (!Quest.completed() || Quest.rewardsAvailable()) ? Notes.Landmark.TROLL : null;
	}

	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			Notes.remove( landmark() );
			return true;
		} else if (!Quest.rewardsAvailable() && Quest.completed()){
			Notes.remove( landmark() );
		}
		return super.act();
	}
	
	@Override
	public boolean interact(Char c) {
		
		sprite.turnTo( pos, c.pos );

		if (c != Dungeon.hero){
			return true;
		}

		if (forgeTriforce()) {
			tell(Messages.get(this, "triforce"));
			return true;
		}

		if (forgeShadowEater()) {
			tell(Messages.get(this, "shadoweater"));
			return true;
		}

		if (Quest.isLegacy()) return interactLegacy();
		
		if (!Quest.given) {

			String msg1 = "";
			String msg2 = "";

			switch (Dungeon.hero.heroClass){
				case WARRIOR:   msg1 += Messages.get(Blacksmith.this, "intro_quest_warrior"); break;
				case MAGE:      msg1 += Messages.get(Blacksmith.this, "intro_quest_mage"); break;
				case ROGUE:     msg1 += Messages.get(Blacksmith.this, "intro_quest_rogue"); break;
				case HUNTRESS:  msg1 += Messages.get(Blacksmith.this, "intro_quest_huntress"); break;
				case DUELIST:   msg1 += Messages.get(Blacksmith.this, "intro_quest_duelist"); break;
				case CLERIC:    msg1 += Messages.get(Blacksmith.this, "intro_quest_cleric"); break;
				case SPELLSWORD: msg1 += Messages.get(Blacksmith.this, "intro_quest_spellsword"); break;
			}

			msg1 += "\n\n" + Messages.get(Blacksmith.this, "intro_quest_start");

			switch (Quest.type){
				case Quest.CRYSTAL: msg2 += Messages.get(Blacksmith.this, "intro_quest_crystal"); break;
				case Quest.GNOLL:   msg2 += Messages.get(Blacksmith.this, "intro_quest_gnoll"); break;
				case Quest.FUNGI:   msg2 += Messages.get(Blacksmith.this, "intro_quest_fungi"); break;
			}

			final String msg1Final = msg1;
			final String msg2Final = msg2;
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndQuest(Blacksmith.this, msg1Final) {
						@Override
						public void hide() {
							super.hide();

							Quest.given = true;
							Quest.completed = false;
							Item pick = Quest.pickaxe != null ? Quest.pickaxe : new Pickaxe();
							if (pick.doPickUp( Dungeon.hero )) {
								GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", pick.name()) ));
							} else {
								Dungeon.level.drop( pick, Dungeon.hero.pos ).sprite.drop();
							}
							Quest.pickaxe = null;

							if (msg2Final != ""){
								GameScene.show(new WndQuest(Blacksmith.this, msg2Final));
							}

						}
					} );
				}
			});
			
		} else if (!Quest.completed) {

			String msg = Messages.get(this, "reminder") + "\n\n";
			switch (Quest.type){
				case Quest.CRYSTAL: msg += Messages.get(Blacksmith.this, "reminder_crystal"); break;
				case Quest.GNOLL:   msg += Messages.get(Blacksmith.this, "reminder_gnoll"); break;
				case Quest.FUNGI:   msg += Messages.get(Blacksmith.this, "reminder_fungi"); break;
			}
			tell(msg);

		} else if (Quest.rewardsAvailable()) {

			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					//in case game was closed during smith reward selection
					if (Quest.smithRewards != null && Quest.smiths > 0){
						GameScene.show( new WndBlacksmith.WndSmith( Blacksmith.this, Dungeon.hero ) );
					} else {
						GameScene.show(new WndBlacksmith(Blacksmith.this, Dungeon.hero));
					}
				}
			});

		} else {
			
			tell( Messages.get(this, "get_lost") );
			
		}

		return true;
	}

	private boolean interactLegacy() {
		if (!Quest.given) {
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndQuest(Blacksmith.this, Messages.get(Blacksmith.this, "gold_1")) {
						@Override
						public void hide() {
							super.hide();
							Quest.given = true;
							Quest.completed = false;
							Pickaxe pickaxe = new Pickaxe();
							if (!pickaxe.doPickUp(Dungeon.hero)) {
								Dungeon.level.drop(pickaxe, Dungeon.hero.pos).sprite.drop();
							}
						}
					});
				}
			});
		} else if (!Quest.completed) {
			Pickaxe pickaxe = Dungeon.hero.belongings.getItem(Pickaxe.class);
			DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
			if (pickaxe == null) {
				tell(Messages.get(this, "lost_pick"));
			} else if (gold == null || gold.quantity() < 15) {
				tell(Messages.get(this, "gold_2"));
			} else {
				yell(Messages.get(this, "keeppickaxe"));
				tell(Messages.get(this, "completed"));
				Quest.completed = true;
				Quest.legacyReforged = false;
			}
		} else if (!Quest.legacyReforged) {
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndBlacksmithLegacy(Blacksmith.this, Dungeon.hero));
				}
			});
		} else {
			tell(Messages.get(this, "get_lost"));
		}
		return true;
	}

	public static String verifyLegacy(Item first, Item second) {
		if (first == second) return Messages.get(Blacksmith.class, "same_item");
		if (!first.isIdentified() || !second.isIdentified()) return Messages.get(Blacksmith.class, "un_ided");
		if (first.cursed || second.cursed) return Messages.get(Blacksmith.class, "cursed");
		if (first.level() < 0 || second.level() < 1) return Messages.get(Blacksmith.class, "degraded");
		if (first.level() + second.level() > 15 && !first.isReinforced()) {
			return Messages.get(Blacksmith.class, "need_reinforced");
		}
		if (!first.isUpgradable() || !second.isUpgradable()) {
			return Messages.get(Blacksmith.class, "cant_reforge");
		}
		return null;
	}

	public static boolean upgradeLegacy(Item first, Item second) {
		if (Dungeon.hero == null || verifyLegacy(first, second) != null) return false;
		Sample.INSTANCE.play(Assets.Sounds.EVOKE);
		ScrollOfUpgrade.upgrade(Dungeon.hero);
		Item.evoke(Dungeon.hero);

		if (first.isEquipped(Dungeon.hero)) {
			((EquipableItem)first).doUnequip(Dungeon.hero, true);
		}
		DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
		float upgradeChance = 0.5f + (gold == null ? 0 : gold.quantity() * 0.05f);
		for (int i = 0; i < second.level(); i++) {
			if (i < 2 || Random.Float() < upgradeChance) {
				first.upgrade();
				if (i >= 2) upgradeChance = Math.max(0.5f, upgradeChance - 0.1f);
			}
		}

		if (second.isEquipped(Dungeon.hero)) {
			((EquipableItem)second).doUnequip(Dungeon.hero, false);
		}
		second.detachAll(Dungeon.hero.belongings.backpack);
		if (gold != null) gold.detachAll(Dungeon.hero.belongings.backpack);
		Quest.legacyReforged = true;
		GLog.p(Messages.get(ScrollOfUpgrade.class, "looks_better", first.name()));
		Dungeon.hero.spendAndNext(2f);
		Badges.validateItemLevelAquired(first);
		Item.updateQuickslot();
		return true;
	}

	private static boolean forgeTriforce() {
		if (Dungeon.hero == null) return false;
		TriforceOfCourage courage = Dungeon.hero.belongings.getItem(TriforceOfCourage.class);
		TriforceOfPower power = Dungeon.hero.belongings.getItem(TriforceOfPower.class);
		TriforceOfWisdom wisdom = Dungeon.hero.belongings.getItem(TriforceOfWisdom.class);
		if (courage == null || power == null || wisdom == null) return false;
		courage.detach(Dungeon.hero.belongings.backpack);
		power.detach(Dungeon.hero.belongings.backpack);
		wisdom.detach(Dungeon.hero.belongings.backpack);
		Triforce result = new Triforce();
		if (!result.collect(Dungeon.hero.belongings.backpack) && Dungeon.level != null) {
			Dungeon.level.drop(result, Dungeon.hero.pos).sprite.drop();
		}
		return true;
	}

	/** Consumes the three original tester materials and creates the actual weapon. */
	public static boolean forgeShadowEater() {
		if (Dungeon.hero == null) return false;
		CurseBlood blood = Dungeon.hero.belongings.getItem(CurseBlood.class);
		EmptyBody body = Dungeon.hero.belongings.getItem(EmptyBody.class);
		ChaosPack contract = Dungeon.hero.belongings.getItem(ChaosPack.class);
		if (blood == null || body == null || contract == null) return false;

		blood.detach(Dungeon.hero.belongings.backpack);
		body.detach(Dungeon.hero.belongings.backpack);
		contract.detach(Dungeon.hero.belongings.backpack);
		ShadowEater result = new ShadowEater();
		if (!result.collect(Dungeon.hero.belongings.backpack) && Dungeon.level != null) {
			Dungeon.level.drop(result, Dungeon.hero.pos).sprite.drop();
		}
		return true;
	}
	
	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( Blacksmith.this, text ) );
			}
		});
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

	public static class Quest {

		private static int type = 0;
		public static final int CRYSTAL = 1;
		public static final int GNOLL = 2;
		public static final int FUNGI = 3; //The fungi quest is not implemented, only exists partially in code

		//quest state information
		private static boolean spawned;
		private static boolean oldQuest;
		private static boolean legacyReforged;
		private static boolean given;
		private static boolean started;
		private static boolean bossBeaten;
		private static boolean completed;

		//reward tracking. Stores remaining favor, the pickaxe, and how many of each reward has been chosen
		public static int favor;
		public static Item pickaxe;
		public static boolean freePickaxe;
		public static int reforges;
		public static int hardens;
		public static int upgrades;
		public static int smiths;

		//pre-generate these so they are consistent between seeds
		public static ArrayList<Item> smithRewards;
		public static Weapon.Enchantment smithEnchant;
		public static Armor.Glyph smithGlyph;
		
		public static void reset() {
			type        = 0;

			spawned		= false;
			oldQuest    = false;
			legacyReforged = false;
			given		= false;
			started     = false;
			bossBeaten  = false;
			completed	= false;

			favor       = 0;
			pickaxe     = new Pickaxe().identify(false);
			freePickaxe = false;
			reforges    = 0;
			hardens     = 0;
			upgrades    = 0;
			smiths      = 0;

			smithRewards = null;
			smithEnchant = null;
			smithGlyph = null;
		}
		
		private static final String NODE	= "blacksmith";

		private static final String TYPE    	= "type";
		private static final String ALTERNATIVE	= "alternative";
		private static final String OLD_QUEST = "old_quest";
		private static final String LEGACY_REFORGED = "reforged";

		private static final String SPAWNED		= "spawned";
		private static final String GIVEN		= "given";
		private static final String STARTED		= "started";
		private static final String BOSS_BEATEN	= "boss_beaten";
		private static final String COMPLETED	= "completed";

		private static final String FAVOR	    = "favor";
		private static final String PICKAXE	    = "pickaxe";
		private static final String FREE_PICKAXE= "free_pickaxe";
		private static final String REFORGES	= "reforges";
		private static final String HARDENS	    = "hardens";
		private static final String UPGRADES	= "upgrades";
		private static final String SMITHS	    = "smiths";
		private static final String SMITH_REWARDS = "smith_rewards";
		private static final String ENCHANT		= "enchant";
		private static final String GLYPH		= "glyph";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				node.put( OLD_QUEST, oldQuest );
				if (oldQuest) node.put( LEGACY_REFORGED, legacyReforged );
				node.put( TYPE, type );

				node.put( GIVEN, given );
				node.put( STARTED, started );
				node.put( BOSS_BEATEN, bossBeaten );
				node.put( COMPLETED, completed );

				node.put( FAVOR, favor );
				if (pickaxe != null) node.put( PICKAXE, pickaxe );
				node.put( FREE_PICKAXE, freePickaxe );
				node.put( REFORGES, reforges );
				node.put( HARDENS, hardens );
				node.put( UPGRADES, upgrades );
				node.put( SMITHS, smiths );

				if (smithRewards != null) {
					node.put( SMITH_REWARDS, smithRewards );
					if (smithEnchant != null) {
						node.put(ENCHANT, smithEnchant);
						node.put(GLYPH, smithGlyph);
					}
				}
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
				boolean migrateModernQuest = node.contains(OLD_QUEST)
						&& !node.getBoolean(OLD_QUEST);
				oldQuest = node.contains(OLD_QUEST)
						? node.getBoolean(OLD_QUEST) : node.contains(LEGACY_REFORGED);
				legacyReforged = oldQuest && node.getBoolean(LEGACY_REFORGED);
				type = node.getInt(TYPE);

				given = node.getBoolean( GIVEN );
				started = node.getBoolean( STARTED );
				bossBeaten = node.getBoolean( BOSS_BEATEN );
				completed = node.getBoolean( COMPLETED );

				favor = node.getInt( FAVOR );
				if (node.contains(PICKAXE)) {
					pickaxe = (Item) node.get(PICKAXE);
				} else {
					pickaxe = null;
				}
				if (node.contains(FREE_PICKAXE)){
					freePickaxe = node.getBoolean(FREE_PICKAXE);
				}
				reforges = node.getInt( REFORGES );
				hardens = node.getInt( HARDENS );
				upgrades = node.getInt( UPGRADES );
				smiths = node.getInt( SMITHS );

				if (node.contains( SMITH_REWARDS )){
					smithRewards = new ArrayList<>((Collection<Item>) ((Collection<?>) node.getCollection( SMITH_REWARDS )));
					if (node.contains(ENCHANT)) {
						smithEnchant = (Weapon.Enchantment) node.get(ENCHANT);
						smithGlyph   = (Armor.Glyph) node.get(GLYPH);
					}
				} else {
					smithRewards = null;
				}

				// Early SPS-SPD builds could expose Shattered's mining quest. Preserve
				// completed progress, but return unfinished saves to the 0.9.8 ore quest.
				if (migrateModernQuest) {
					oldQuest = true;
					legacyReforged = false;
					type = 0;
					given = completed;
					started = false;
					bossBeaten = false;
					favor = 0;
					pickaxe = null;
					freePickaxe = false;
					reforges = hardens = upgrades = smiths = 0;
					smithRewards = null;
					smithEnchant = null;
					smithGlyph = null;
				}

			} else {
				reset();
			}
		}
		
		public static ArrayList<Room> spawn( ArrayList<Room> rooms ) {
			if (!spawned && Dungeon.depth > 11 && Random.Int( 15 - Dungeon.depth ) == 0) {
				
				rooms.add(new BlacksmithRoom());
				spawned = true;
				oldQuest = false;

				//Currently cannot roll the fungi quest, as it is not fully implemented
				type = Random.IntRange(1, 2);
				
				given = false;
				generateRewards( true );
				
			}
			return rooms;
		}

		public static boolean canSpawnLegacy() {
			return !spawned;
		}

		public static void beginLegacy() {
			spawned = true;
			oldQuest = true;
			legacyReforged = false;
			type = 0;
			given = false;
			started = false;
			bossBeaten = false;
			completed = false;
			favor = 0;
			pickaxe = null;
			smithRewards = null;
		}

		public static boolean isLegacy() {
			return oldQuest;
		}

		public static boolean legacyReforged() {
			return legacyReforged;
		}

		public static void generateRewards( boolean useDecks ){
			smithRewards = new ArrayList<>();
			smithRewards.add(Generator.randomWeapon(3, useDecks));
			smithRewards.add(Generator.randomWeapon(3, useDecks));
			ArrayList<Item> toUndo = new ArrayList<>();
			while (smithRewards.get(0).getClass() == smithRewards.get(1).getClass()) {
				if (useDecks)   toUndo.add(smithRewards.get(1));
				smithRewards.remove(1);
				smithRewards.add(Generator.randomWeapon(3, useDecks));
			}
			for (Item i : toUndo){
				Generator.undoDrop(i);
			}
			smithRewards.add(Generator.randomMissile(3, useDecks));
			smithRewards.add(Generator.randomArmor(3));

			//30%:+0, 45%:+1, 20%:+2, 5%:+3
			int rewardLevel;
			float itemLevelRoll = Random.Float();
			if (itemLevelRoll < 0.3f){
				rewardLevel = 0;
			} else if (itemLevelRoll < 0.75f){
				rewardLevel = 1;
			} else if (itemLevelRoll < 0.95f){
				rewardLevel = 2;
			} else {
				rewardLevel = 3;
			}

			for (Item i : smithRewards){
				i.level(rewardLevel);
				if (i instanceof Weapon) {
					((Weapon) i).enchant(null);
				} else if (i instanceof Armor){
					((Armor) i).inscribe(null);
				}
				i.cursed = false;
			}

			// 30% base chance to be enchanted, stored separately so status isn't revealed early
			//we generate first so that the outcome doesn't affect the number of RNG rolls
			smithEnchant = Weapon.Enchantment.random();
			smithGlyph = Armor.Glyph.random();

			float enchantRoll = Random.Float();
			if (enchantRoll > 0.3f * ParchmentScrap.enchantChanceMultiplier()){
				smithEnchant = null;
				smithGlyph = null;
			}

		}

		public static int Type(){
			return type;
		}

		public static boolean given(){
			return given;
		}

		public static boolean started(){
			return started;
		}

		public static void start(){
			started = true;
		}

		public static boolean beatBoss(){
			return bossBeaten = true;
		}

		public static boolean bossBeaten(){
			return bossBeaten;
		}

		public static boolean completed(){
			return given && completed;
		}

		public static void complete(){
			completed = true;

			favor = 0;
			DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
			if (gold != null){
				favor += Math.min(2000, gold.quantity()*50);
				gold.detachAll(Dungeon.hero.belongings.backpack);
			}

			Pickaxe pick = Dungeon.hero.belongings.getItem(Pickaxe.class);
			if (pick.isEquipped(Dungeon.hero)) {
				boolean wasCursed = pick.cursed;
				pick.cursed = false; //so that it can always be removed
				pick.doUnequip(Dungeon.hero, false);
				pick.cursed = wasCursed;
			}
			pick.detach(Dungeon.hero.belongings.backpack);
			Quest.pickaxe = pick;

			if (bossBeaten) favor += 1000;

			Statistics.questScores[2] += favor;

			if (favor >= 2500){
				freePickaxe = true;
			}
		}

		public static boolean rewardsAvailable(){
			if (oldQuest) return completed && !legacyReforged;
			return favor > 0
					|| (Quest.smithRewards != null && Quest.smiths > 0)
					|| (pickaxe != null && freePickaxe);
		}

	}
}
