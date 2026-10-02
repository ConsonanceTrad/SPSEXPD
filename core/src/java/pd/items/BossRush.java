/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.levels.Level;
import pd.levels.Transitions;
import pd.messages.Messages;
import pd.scenes.InterlevelScene;
import pd.utils.GLog;
import render.noosa.Game;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;

/** The original unique Boss Rush invitation. Its destination is restored separately. */
public class BossRush extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BossRush.class)
			.t("name", "BossRush挑战")
			.t("ac_read", "使用")
			.t("desc", "坚果制作的终极挑战，使用它将会带你前往全新Boss面前。")
			.t("ac_return", "返回");
	}



	public static final int BRANCH = 45;
	private static final float TIME_TO_USE = 1f;

	public static final String AC_READ = "READ";
	public static final String AC_RETURN = "RETURN";

	private int returnDepth = -1;
	private int returnBranch;
	private int returnPos = -1;

	{
		image = SpecificTaskDict.BOOK_OF_ALL;
		unique = true;
		stackable = false;
		defaultAction = AC_READ;
		keptThoughLostInvent = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(Dungeon.branch == BRANCH ? AC_RETURN : AC_READ);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_READ.equals(action) && !AC_RETURN.equals(action)) {
			super.execute(hero, action);
			return;
		}
		PocketBallFull.removePet(hero);

		if (Dungeon.branch == BRANCH) {
			returnToDungeon(hero);
			return;
		}
		if (!AC_READ.equals(action) || Dungeon.branch != 0 || Dungeon.depth <= 1
				|| Dungeon.depth >= 25 || Dungeon.bossLevel() || !Dungeon.interfloorTeleportAllowed()) {
			hero.spend(TIME_TO_USE);
			GLog.w(Messages.get(Item.class, "not_here"));
			return;
		}

		returnDepth = Dungeon.depth;
		returnBranch = Dungeon.branch;
		returnPos = hero.pos;
		transition(hero, Dungeon.depth, BRANCH, -1);
	}

	private void returnToDungeon(Hero hero) {
		if (Dungeon.branch != BRANCH) {
			GLog.w(Messages.get(Item.class, "not_here"));
			return;
		}
		int depth = returnDepth > 0 ? returnDepth : Math.max(2, Math.min(24, Dungeon.depth));
		int branch = returnDepth > 0 ? returnBranch : 0;
		int pos = returnDepth > 0 ? returnPos : -1;
		transition(hero, depth, branch, pos);
	}

	private void transition(Hero hero, int depth, int branch, int pos) {
		Transitions.beforeTransition();
		Invisibility.dispel();
		hero.spend(TIME_TO_USE);
		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = depth;
		InterlevelScene.returnBranch = branch;
		InterlevelScene.returnPos = pos;
		Game.switchScene(InterlevelScene.class);
	}

	private static final String RETURN_DEPTH = "return_depth";
	private static final String RETURN_BRANCH = "return_branch";
	private static final String RETURN_POS = "return_pos";
	private static final String LEGACY_DEPTH = "depth";
	private static final String LEGACY_POS = "pos";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(RETURN_DEPTH, returnDepth);
		bundle.put(RETURN_BRANCH, returnBranch);
		bundle.put(RETURN_POS, returnPos);
		bundle.put(LEGACY_DEPTH, returnDepth);
		if (returnDepth != -1) bundle.put(LEGACY_POS, returnPos);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		returnDepth = bundle.contains(RETURN_DEPTH) ? bundle.getInt(RETURN_DEPTH)
				: bundle.contains(LEGACY_DEPTH) ? bundle.getInt(LEGACY_DEPTH) : -1;
		returnBranch = bundle.getInt(RETURN_BRANCH);
		returnPos = bundle.contains(RETURN_POS) ? bundle.getInt(RETURN_POS)
				: bundle.contains(LEGACY_POS) ? bundle.getInt(LEGACY_POS) : -1;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
