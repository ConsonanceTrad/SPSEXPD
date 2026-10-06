/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Challenge journal concept adapted from Special Surprise Pixel Dungeon.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.quest;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.items.ChallengeBook;
import pd.items.Item;
import pd.items.specific.challengelists.ChallengeList;
import pd.levels.Level;
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
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;

public class ChallengeJournal extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ChallengeJournal.class)
			.t("name", "挑战日志")
			.t("ac_read", "传送")
			.t("ac_return", "返回")
			.t("ac_add", "添加")
			.t("cannot_enter", "挑战日志无法在这里打开稳定的传送通道。")
			.t("no_destinations", "挑战日志中还没有可用的地图碎片。")
			.t("completed_marker", "（已完成）")
			.t("choose", "选择一处已经记录的挑战地点。")
			.t("enter", "挑战日志打开了通往%s的传送通道。")
			.t("cannot_return", "返回主地牢的路线已经消失。")
			.t("leave", "挑战日志重新描绘出返回主地牢的路线。")
			.t("trial_unlocked", "%s已经出现在挑战日志中！")
			.t("challenge_complete", "%s已完成！")
			.t("prompt", "选择一张要加入日志的挑战纸片。")
			.t("add_page", "挑战记录成功。")
			.t("already_added", "这项挑战已经记录在日志中。")
			.t("desc", "一本普通的记事本，来自异世界的魔法使它可以记录特殊地点并开启传送。把挑战纸片夹进日志后，就能自由前往对应地点并返回。\n\n已记录：_%1$d/%2$d_")
			.t("challenge_0", "下水道挑战")
			.t("challenge_1", "监狱挑战")
			.t("challenge_2", "洞窟挑战")
			.t("challenge_3", "城市挑战")
			.t("challenge_4", "蜜雪冰城")
			.t("challenge_5", "勇气试炼")
			.t("challenge_6", "力量试炼")
			.t("challenge_7", "智慧试炼");
	}




	public static final int CHALLENGE_COUNT = 8;
	public static final int REGION_CHALLENGE_COUNT = 5;
	public static final int FIRST_BRANCH = 10;
	public static final int FIRST_VERSION = 913;

	public static final String AC_READ = "READ";
	public static final String AC_RETURN = "RETURN";
	public static final String AC_ADD = "ADD";

	private static final int[] ANCHOR_DEPTHS = {4, 9, 14, 19, 24, 9, 19, 24};
	private static final int[] LEGACY_DEPTHS = {90, 27, 28, 29, 30, 31, 32, 33};

	private int unlockedMask;
	private int completedMask;
	private int returnDepth = -1;
	private int returnBranch;
	private int returnPos = -1;
	private boolean legacyMigrationApplied;

	{
		image = SpecificTaskDict.TRIAL_BOOK;
		defaultAction = AC_READ;
		unique = true;
		keptThoughLostInvent = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = new ArrayList<>();
		if (isChallengeBranch(Dungeon.branch)) {
			actions.add(AC_RETURN);
		} else {
			actions.add(AC_READ);
			actions.add(AC_ADD);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		curUser = hero;
		curItem = this;

		if (AC_READ.equals(action)) {
			showDestinations(hero);
		} else if (AC_RETURN.equals(action)) {
			returnToDungeon(hero);
		} else if (AC_ADD.equals(action)) {
			GameScene.selectItem(pageSelector);
		}
	}

	private void showDestinations(final Hero hero) {
		if (Dungeon.branch != 0 || !Dungeon.interfloorTeleportAllowed()) {
			GLog.w(Messages.get(this, "cannot_enter"));
			return;
		}

		final ArrayList<Integer> destinations = new ArrayList<>();
		for (int i = 0; i < CHALLENGE_COUNT; i++) {
			if (isUnlocked(i)) destinations.add(i);
		}

		if (destinations.isEmpty()) {
			GameScene.show(new WndMessage(Messages.get(this, "no_destinations")));
			return;
		}

		String[] options = new String[destinations.size()];
		for (int i = 0; i < options.length; i++) {
			int challenge = destinations.get(i);
			options[i] = challengeName(challenge);
		}

		GameScene.show(new WndOptions(
				new ItemSprite(image(), null),
				Messages.titleCase(name()),
				Messages.get(this, "choose"),
				options) {
			@Override
			protected void onSelect(int index) {
				enterChallenge(hero, destinations.get(index));
			}
		});
	}

	private void enterChallenge(Hero hero, int challenge) {
		returnDepth = Dungeon.depth;
		returnBranch = Dungeon.branch;
		returnPos = hero.pos;

		pd.items.consum.eggs.Egg.recallProjection(hero);
		Transitions.beforeTransition();
		Invisibility.dispel();
		hero.spend(1f);

		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = anchorDepth(challenge);
		InterlevelScene.returnBranch = branchFor(challenge);
		InterlevelScene.returnPos = -1;
		GLog.h(Messages.get(this, "enter", challengeName(challenge)));
		Game.switchScene(InterlevelScene.class);
	}

	private void returnToDungeon(Hero hero) {
		int challenge = challengeForBranch(Dungeon.branch);
		if (challenge < 0) {
			GLog.w(Messages.get(this, "cannot_return"));
			return;
		}

		pd.items.consum.eggs.Egg.recallProjection(hero);
		Transitions.beforeTransition();
		Invisibility.dispel();
		hero.spend(1f);

		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = returnDepth > 0 ? returnDepth : anchorDepth(challenge);
		InterlevelScene.returnBranch = returnDepth > 0 ? returnBranch : 0;
		InterlevelScene.returnPos = returnDepth > 0 ? returnPos : -1;
		GLog.i(Messages.get(this, "leave"));
		Game.switchScene(InterlevelScene.class);
	}

	public boolean unlock(int challenge) {
		if (challenge < 0 || challenge >= CHALLENGE_COUNT || isUnlocked(challenge)) return false;
		unlockedMask |= 1 << challenge;
		updateQuickslot();
		return true;
	}

	public boolean addPage(ChallengeList page) {
		return page != null && unlock(page.challenge());
	}

	private final WndBag.ItemSelector pageSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(ChallengeJournal.class, "prompt"); }
		@Override public boolean itemSelectable(Item item) { return item instanceof ChallengeList; }
		@Override public void onSelect(Item item) {
			if (!(item instanceof ChallengeList)) return;
			ChallengeList page = (ChallengeList)item;
			if (!addPage(page)) {
				GLog.w(Messages.get(ChallengeJournal.class, "already_added"));
				return;
			}
			page.detach(Dungeon.hero.belongings.backpack);
			if (Dungeon.hero.sprite != null) Dungeon.hero.sprite.operate(Dungeon.hero.pos);
			Dungeon.hero.spend(2f);
			GLog.h(Messages.get(ChallengeJournal.class, "add_page"));
		}
	};

	public static ChallengeJournal ensureFor(Hero hero) {
		ChallengeJournal journal = hero.belongings.getItem(ChallengeJournal.class);
		if (journal != null) return journal;

		journal = new ChallengeBook();
		if (!journal.collect(hero.belongings.backpack)) {
			// Quest progression must survive a completely full backpack during save migration.
			hero.belongings.backpack.items.add(journal);
			Collections.sort(hero.belongings.backpack.items, Item.itemComparator);
			updateQuickslot();
		}
		return journal;
	}

	public void migrateLegacyRegions(int deepestFloor) {
		if (legacyMigrationApplied) return;
		for (int challenge = 0; challenge < REGION_CHALLENGE_COUNT; challenge++) {
			if (deepestFloor >= anchorDepth(challenge)) unlock(challenge);
		}
		legacyMigrationApplied = true;
	}

	public boolean isUnlocked(int challenge) {
		return (unlockedMask & (1 << challenge)) != 0;
	}

	public boolean isCompleted(int challenge) {
		return (completedMask & (1 << challenge)) != 0;
	}

	public static int fragmentForDepth(int depth) {
		switch (depth) {
			case 4: return 0;
			case 9: return 1;
			case 14: return 2;
			case 19: return 3;
			case 24: return 4;
			default: return -1;
		}
	}

	public static int anchorDepth(int challenge) {
		return ANCHOR_DEPTHS[Math.max(0, Math.min(CHALLENGE_COUNT - 1, challenge))];
	}

	public static int branchFor(int challenge) {
		return FIRST_BRANCH + challenge;
	}

	public static int challengeForBranch(int branch) {
		int challenge = branch - FIRST_BRANCH;
		return challenge >= 0 && challenge < CHALLENGE_COUNT ? challenge : -1;
	}

	public static boolean isChallengeBranch(int branch) {
		return challengeForBranch(branch) >= 0;
	}

	public static int legacyDepthForBranch(int branch) {
		int challenge = challengeForBranch(branch);
		return challenge < 0 ? -1 : LEGACY_DEPTHS[challenge];
	}

	public static int keyDepth(int depth, int branch) {
		int legacyDepth = legacyDepthForBranch(branch);
		return legacyDepth < 0 ? depth : legacyDepth;
	}

	public static String challengeName(int challenge) {
		return Messages.get(ChallengeJournal.class, "challenge_" + challenge);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Integer.bitCount(unlockedMask), CHALLENGE_COUNT);
	}

	@Override
	public String status() {
		return Integer.bitCount(unlockedMask) + "/" + CHALLENGE_COUNT;
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
	private static final String LEGACY_MIGRATION = "legacy_migration";

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
		bundle.put(LEGACY_MIGRATION, legacyMigrationApplied);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		unlockedMask = bundle.getInt(UNLOCKED);
		completedMask = bundle.getInt(COMPLETED);
		returnDepth = bundle.contains(RETURN_DEPTH) ? bundle.getInt(RETURN_DEPTH)
				: bundle.contains(LEGACY_DEPTH) ? bundle.getInt(LEGACY_DEPTH) : -1;
		returnBranch = bundle.getInt(RETURN_BRANCH);
		returnPos = bundle.contains(RETURN_POS) ? bundle.getInt(RETURN_POS)
				: bundle.contains(LEGACY_POS) ? bundle.getInt(LEGACY_POS) : -1;
		legacyMigrationApplied = bundle.getBoolean(LEGACY_MIGRATION);
	}
}
