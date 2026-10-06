/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Adventure journal concept adapted from Special Surprise Pixel Dungeon.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.quest;

import pd.atlas.items.SpecificPagesDict;

import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.items.DolyaSlate;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.armor.fusion.CatSharkArmor;
import pd.items.equipment.armor.fusion.LifeArmor;
import pd.items.equipment.artifacts.fusion.EyeOfSkadi;
import pd.items.equipment.artifacts.fusion.NoomlinCrown;
import pd.items.consum.food.Food;
import pd.items.consum.food.fusion.Nut;
import pd.items.specific.journalpages.JournalPage;
import pd.items.specific.journalpages.SafeSpotPage;
import pd.items.consum.medicine.MendingTonic;
import pd.items.consum.medicine.TimePill;
import pd.items.misc.LuckyBadge;
import pd.items.consum.potions.PotionOfHaste;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.consum.stones.StoneOfEnchantment;
import pd.items.equipment.weapon.melee.fusion.Harp;
import pd.items.equipment.weapon.melee.fusion.ReedPipe;
import pd.items.equipment.weapon.melee.fusion.RitualBlade;
import pd.items.equipment.weapon.melee.fusion.VerdantGuard;
import pd.items.equipment.weapon.melee.fusion.WarDrum;
import pd.levels.AdventureLevel;
import pd.levels.Level;
import pd.levels.SpsSokobanLevel;
import pd.levels.Transitions;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.InterlevelScene;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndMessage;
import pd.windows.WndOptions;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;

public class AdventureJournal extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AdventureJournal.class)
			.t("name", "异界日志")
			.t("ac_read", "传送")
			.t("ac_return", "返回")
			.t("ac_reset", "重置谜题")
			.t("cannot_enter", "异界日志无法在这里打开稳定的传送通道。")
			.t("no_destinations", "异界日志中还没有记录任何目的地。")
			.t("choose_category", "选择一组目的地。")
			.t("category_journal", "日志地图")
			.t("category_expedition", "远征地图")
			.t("category_endgame", "终局地图")
			.t("category_empty", "这一组中还没有解锁的目的地。")
			.t("choose_destination", "选择一处已经记录的目的地。只有在主地牢到达相应深度后，通往该地的路线才会稳定。")
			.t("completed_marker", "（已完成）")
			.t("too_early", "这条路线要到主地牢第%d层后才能稳定。")
			.t("amulet_required", "取得Yendor护符后，终局路线才会开启。")
			.t("enter", "异界日志打开了通往%s的传送通道。")
			.t("cannot_return", "返回主地牢的路线已经消失。")
			.t("leave", "异界日志重新描绘出返回主地牢的路线。")
			.t("destination_unlocked", "%s已经出现在异界日志中！")
			.t("destination_complete", "%s已完成！")
			.t("reward", "异界日志保存了一件独特奖励：%s。")
			.t("desc", "根据特别惊喜像素地牢重制的第二本旅行日志，记录安全区域、推箱子谜题、远征和终局世界，与挑战日志分开管理。所有目的地都会保存在存档中，不能用于反复刷取经验或随机物资。\n\n已记录：_%1$d/%3$d_　已完成：_%2$d/%3$d_")
			.t("destination_0", "安全居所")
			.t("destination_1", "推箱练习场")
			.t("destination_2", "推箱城堡")
			.t("destination_3", "传送迷阵")
			.t("destination_4", "推箱谜城")
			.t("destination_5", "多利亚镇")
			.t("destination_6", "春节庭院")
			.t("destination_7", "矿区核心")
			.t("destination_8", "新居")
			.t("destination_9", "寄生虫巢")
			.t("destination_10", "天狗隐匿处")
			.t("destination_11", "骷髅王陵")
			.t("destination_12", "巨蟹王巢")
			.t("destination_13", "盗贼王据点")
			.t("destination_14", "原野霸主竞技场")
			.t("destination_15", "陶罐迷宫")
			.t("destination_16", "暗影吞噬者领域")
			.t("destination_17", "龙之洞窟")
			.t("destination_18", "盗贼追捕")
			.t("destination_19", "深层矿区")
			.t("destination_20", "Zot前厅")
			.t("destination_21", "首领连续战")
			.t("destination_22", "混沌领域")
			.t("destination_23", "Zot神殿")
			.t("destination_24", "Zot王座")
			.t("ac_add", "添加")
			.t("prompt", "选择一张要加入冒险日志的地点纸片。")
			.t("add_page", "地点记录成功。")
			.t("already_added", "这个地点已经记录在日志中。")
			.t("missing", "你还没有取得Otiluck的旅行日志。");
	}




	public static final int DESTINATION_COUNT = 25;
	public static final int FIRST_BRANCH = 20;
	public static final int FIRST_VERSION = 914;
	public static final int FULL_CHARGE = 1000;
	public static final int PORTAL_CHARGE = 500;

	public static final String AC_READ = "READ";
	public static final String AC_RETURN = "RETURN";
	public static final String AC_RESET = "RESET";
	public static final String AC_ADD = "ADD";

	private static final int[] ANCHOR_DEPTHS = {
			1, 4, 9, 14, 19, 1, 9, 14, 19,
			4, 6, 8, 10, 12, 14, 16, 18, 20, 21, 23, 24,
			25, 25, 25, 25
	};

	/** SPS-PD kept branch combat scaling separate from the main-floor return anchor. */
	private static final int[] LEGACY_DEPTHS = {
			50, 51, 52, 53, 54, 55, 66, 67, 68,
			35, 36, 37, 38, 40, 43, 45, 47, 39, 41, 67, 59,
			71, 85, -1, -1
	};

	private int unlockedMask;
	private int completedMask;
	private int returnDepth = -1;
	private int returnBranch;
	private int returnPos = -1;
	private int charge;

	{
		image = SpecificTaskDict.SOUL_BOOK;
		defaultAction = AC_READ;
		unique = true;
		keptThoughLostInvent = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = new ArrayList<>();
		if (isAdventureBranch(Dungeon.branch)) {
			actions.add(AC_RETURN);
			int destination = destinationForBranch(Dungeon.branch);
			if (destination >= 1 && destination <= 4) actions.add(AC_RESET);
		} else {
			if (canUsePortal()) actions.add(AC_READ);
			actions.add(AC_ADD);
		}
		return actions;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (!super.doPickUp(hero, pos)) return false;
		Statistics.roomType = Random.Int(3);
		dropOrCollect(hero, new SafeSpotPage(), pos);
		dropOrCollect(hero, new LuckyBadge(), pos);
		return true;
	}

	private static void dropOrCollect(Hero hero, Item item, int pos) {
		if (Dungeon.level != null && pos >= 0 && pos < Dungeon.level.length()) {
			Heap heap = Dungeon.level.drop(item, pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		} else {
			item.collect(hero.belongings.backpack);
		}
	}

	@Override
	public void execute(Hero hero, String action) {
		curUser = hero;
		curItem = this;
		if (AC_READ.equals(action)) showCategories(hero);
		else if (AC_RETURN.equals(action)) returnToDungeon(hero);
		else if (AC_ADD.equals(action)) GameScene.selectItem(pageSelector);
		else if (AC_RESET.equals(action) && Dungeon.level instanceof AdventureLevel) {
			((AdventureLevel)Dungeon.level).resetPuzzle(hero);
		} else if (AC_RESET.equals(action) && Dungeon.level instanceof SpsSokobanLevel) {
			((SpsSokobanLevel)Dungeon.level).resetPuzzle(hero);
		}
	}

	public boolean addPage(JournalPage page) {
		if (page == null || !unlock(page.destination())) return false;
		charge = charge < PORTAL_CHARGE
				? FULL_CHARGE
				: Math.min(FULL_CHARGE, charge + PORTAL_CHARGE);
		updateQuickslot();
		return true;
	}

	private final WndBag.ItemSelector pageSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(AdventureJournal.class, "prompt"); }
		@Override public boolean itemSelectable(Item item) { return item instanceof JournalPage; }
		@Override public void onSelect(Item item) {
			if (!(item instanceof JournalPage)) return;
			JournalPage page = (JournalPage)item;
			if (!addPage(page)) {
				GLog.w(Messages.get(AdventureJournal.class, "already_added"));
				return;
			}
			page.detach(Dungeon.hero.belongings.backpack);
			if (Dungeon.hero.sprite != null) Dungeon.hero.sprite.operate(Dungeon.hero.pos);
			Dungeon.hero.spend(2f);
			GLog.h(Messages.get(AdventureJournal.class, "add_page"));
		}
	};

	private void showCategories(final Hero hero) {
		if (!canUsePortal()) {
			GLog.w(Messages.get(this, "not_enough_charge", PORTAL_CHARGE));
			return;
		}
		if (Dungeon.branch != 0 || !Dungeon.interfloorTeleportAllowed()) {
			GLog.w(Messages.get(this, "cannot_enter"));
			return;
		}

		if (unlockedMask == 0) {
			GameScene.show(new WndMessage(Messages.get(this, "no_destinations")));
			return;
		}

		GameScene.show(new WndOptions(
				new ItemSprite(image(), null),
				Messages.titleCase(name()),
				Messages.get(this, "choose_category"),
				Messages.get(this, "category_journal"),
				Messages.get(this, "category_expedition"),
				Messages.get(this, "category_endgame")) {
			@Override
			protected void onSelect(int index) {
				if (index == 0) showDestinations(hero, 0, 9);
				else if (index == 1) showDestinations(hero, 9, 21);
				else if (index == 2) showDestinations(hero, 21, DESTINATION_COUNT);
			}
		});
	}

	private void showDestinations(final Hero hero, int from, int to) {
		final ArrayList<Integer> destinations = new ArrayList<>();
		for (int i = from; i < to; i++) {
			if (isUnlocked(i)) destinations.add(i);
		}
		if (destinations.isEmpty()) {
			GameScene.show(new WndMessage(Messages.get(this, "category_empty")));
			return;
		}

		String[] options = new String[destinations.size()];
		for (int i = 0; i < options.length; i++) {
			int destination = destinations.get(i);
			options[i] = destinationName(destination);
			if (isCompleted(destination)) options[i] += Messages.get(this, "completed_marker");
		}

		GameScene.show(new WndOptions(
				new ItemSprite(image(), null),
				Messages.titleCase(name()),
				Messages.get(this, "choose_destination"), options) {
			@Override
			protected void onSelect(int index) {
				enterDestination(hero, destinations.get(index));
			}
		});
	}

	private void enterDestination(Hero hero, int destination) {
		if (Dungeon.depth < anchorDepth(destination)) {
			GLog.w(Messages.get(this, "too_early", anchorDepth(destination)));
			return;
		}
		if (destination >= 21 && !Statistics.amuletObtained) {
			GLog.w(Messages.get(this, "amulet_required"));
			return;
		}

		returnDepth = Dungeon.depth;
		returnBranch = Dungeon.branch;
		returnPos = hero.pos;
		pd.items.consum.eggs.Egg.recallProjection(hero);
		Transitions.beforeTransition();
		Invisibility.dispel();
		hero.spend(1f);

		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = anchorDepth(destination);
		InterlevelScene.returnBranch = branchFor(destination);
		InterlevelScene.returnPos = -1;
		consumePortalCharge();
		GLog.h(Messages.get(this, "enter", destinationName(destination)));
		Game.switchScene(InterlevelScene.class);
	}

	private void returnToDungeon(Hero hero) {
		int destination = destinationForBranch(Dungeon.branch);
		if (destination < 0) {
			GLog.w(Messages.get(this, "cannot_return"));
			return;
		}

		pd.items.consum.eggs.Egg.recallProjection(hero);
		Transitions.beforeTransition();
		Invisibility.dispel();
		hero.spend(1f);
		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = returnDepth > 0 ? returnDepth : anchorDepth(destination);
		InterlevelScene.returnBranch = returnDepth > 0 ? returnBranch : 0;
		InterlevelScene.returnPos = returnDepth > 0 ? returnPos : -1;
		GLog.i(Messages.get(this, "leave"));
		Game.switchScene(InterlevelScene.class);
	}

	public boolean unlock(int destination) {
		if (destination < 0 || destination >= DESTINATION_COUNT || isUnlocked(destination)) return false;
		unlockedMask |= 1 << destination;
		updateQuickslot();
		return true;
	}

	public boolean isUnlocked(int destination) {
		return (unlockedMask & (1 << destination)) != 0;
	}

	public boolean isCompleted(int destination) {
		return (completedMask & (1 << destination)) != 0;
	}

	public void gainCharge() {
		if (charge < FULL_CHARGE) {
			charge++;
			updateQuickslot();
		}
	}

	public void fillCharge() {
		charge = FULL_CHARGE;
		updateQuickslot();
	}

	public int charge() {
		return charge;
	}

	public boolean canUsePortal() {
		return charge >= PORTAL_CHARGE || Badges.checkOtilukeRescued();
	}

	private void consumePortalCharge() {
		charge -= Math.min(charge, PORTAL_CHARGE);
		updateQuickslot();
	}

	public static boolean complete(int destination) {
		if (Dungeon.hero == null) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		if (journal == null || journal.isCompleted(destination)) return false;
		journal.completedMask |= 1 << destination;
		grantReward(destination);

		int next = nextDestination(destination);
		if (next >= 0 && journal.unlock(next)) {
			GLog.p(Messages.get(journal, "destination_unlocked", destinationName(next)));
		}
		GLog.p(Messages.get(journal, "destination_complete", destinationName(destination)));
		updateQuickslot();
		return true;
	}

	private static void grantReward(int destination) {
		Item reward;
		switch (destination) {
			case 1: reward = new Nut().quantity(3); break;
			case 2: reward = new ScrollOfIdentify(); break;
			case 3: reward = new ScrollOfMagicMapping(); break;
			case 4: reward = new StoneOfEnchantment(); break;
			case 7: reward = new MendingTonic(); break;
			case 9: reward = new ReedPipe(); break;
			case 10: reward = new MendingTonic(); break;
			case 11: reward = new ScrollOfRemoveCurse(); break;
			case 12: reward = new RitualBlade(); break;
			case 14: reward = new CatSharkArmor(); break;
			case 15: reward = new VerdantGuard(); break;
			case 16: reward = new TimePill(); break;
			case 17: reward = new MendingTonic(); break;
			case 18: reward = new Harp(); break;
			case 19: reward = new LifeArmor(); break;
			case 20: reward = new EyeOfSkadi(); break;
			case 21: reward = new WarDrum(); break;
			case 22: reward = new PotionOfHaste(); break;
			case 23: reward = new NoomlinCrown(); break;
			case 24: reward = new MendingTonic().quantity(2); break;
			default: return;
		}
		reward.identify();
		if (!reward.collect()) Dungeon.level.drop(reward, Dungeon.hero.pos).sprite.drop();
		GLog.p(Messages.get(AdventureJournal.class, "reward", reward.name()));
	}

	private static int nextDestination(int destination) {
		if (destination >= 8 && destination < 24) return destination + 1;
		return -1;
	}

	public static AdventureJournal ensureFor(Hero hero) {
		AdventureJournal journal = hero.belongings.getItem(AdventureJournal.class);
		if (journal != null) return journal;
		journal = new DolyaSlate();
		if (!journal.collect(hero.belongings.backpack)) {
			hero.belongings.backpack.items.add(journal);
			Collections.sort(hero.belongings.backpack.items, Item.itemComparator);
			updateQuickslot();
		}
		return journal;
	}

	public static int anchorDepth(int destination) {
		return ANCHOR_DEPTHS[Math.max(0, Math.min(DESTINATION_COUNT - 1, destination))];
	}

	public static int branchFor(int destination) {
		return FIRST_BRANCH + destination;
	}

	public static int destinationForBranch(int branch) {
		int destination = branch - FIRST_BRANCH;
		return destination >= 0 && destination < DESTINATION_COUNT ? destination : -1;
	}

	public static boolean isAdventureBranch(int branch) {
		return destinationForBranch(branch) >= 0;
	}

	public static int legacyDepthForBranch(int branch) {
		int destination = destinationForBranch(branch);
		return destination < 0 ? -1 : LEGACY_DEPTHS[destination];
	}

	public static String destinationName(int destination) {
		return Messages.get(AdventureJournal.class, "destination_" + destination);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Integer.bitCount(unlockedMask),
				Integer.bitCount(completedMask), DESTINATION_COUNT);
	}

	@Override
	public String status() {
		return charge / 10 + "%";
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 0;
	}

	private static final String UNLOCKED = "unlocked";
	private static final String COMPLETED = "completed";
	private static final String RETURN_DEPTH = "return_depth";
	private static final String RETURN_BRANCH = "return_branch";
	private static final String RETURN_POS = "return_pos";
	private static final String LEGACY_DEPTH = "depth";
	private static final String LEGACY_POS = "pos";
	private static final String CHARGE = "charge";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(UNLOCKED, unlockedMask);
		bundle.put(COMPLETED, completedMask);
		bundle.put(RETURN_DEPTH, returnDepth);
		bundle.put(RETURN_BRANCH, returnBranch);
		bundle.put(RETURN_POS, returnPos);
		bundle.put(LEGACY_DEPTH, returnDepth);
		if (returnDepth != -1) bundle.put(LEGACY_POS, returnPos);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		unlockedMask = bundle.contains(UNLOCKED) ? bundle.getInt(UNLOCKED) : 0;
		completedMask = bundle.getInt(COMPLETED);
		returnDepth = bundle.contains(RETURN_DEPTH) ? bundle.getInt(RETURN_DEPTH)
				: bundle.contains(LEGACY_DEPTH) ? bundle.getInt(LEGACY_DEPTH) : -1;
		returnBranch = bundle.getInt(RETURN_BRANCH);
		returnPos = bundle.contains(RETURN_POS) ? bundle.getInt(RETURN_POS)
				: bundle.contains(LEGACY_POS) ? bundle.getInt(LEGACY_POS) : -1;
		charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE)));
	}
}
