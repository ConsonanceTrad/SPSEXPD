/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.Dungeon;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.items.quest.AdventureJournal;
import pd.levels.Level;
import pd.messages.Messages;
import pd.scenes.InterlevelScene;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

/** Honey Poooot's one-use invitation to the honey refuge. */
public class PotKey extends Item {
	public static final int BRANCH = AdventureJournal.FIRST_BRANCH + 15;
	public static final String AC_PORT = "PORT";
	private static final float TIME_TO_USE = 1f;

	private int returnDepth = -1;
	private int returnBranch;
	private int returnPos = -1;

	{
		image = ItemSpriteSheet.POT_KEY;
		stackable = false;
		unique = true;
		defaultAction = AC_PORT;
		keptThoughLostInvent = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_PORT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_PORT.equals(action)) {
			super.execute(hero, action);
			return;
		}
		PocketBallFull.removePet(hero);
		if (Dungeon.branch == BRANCH) {
			EmptyBody reward = new EmptyBody();
			if (!reward.collect(hero.belongings.backpack)) {
				Dungeon.level.drop(reward, hero.pos).sprite.drop();
			}
			detach(hero.belongings.backpack);
			transition(hero, returnDepth > 0 ? returnDepth : 14,
					returnDepth > 0 ? returnBranch : 0,
					returnDepth > 0 ? returnPos : -1);
			return;
		}
		if (Dungeon.branch != 0 || Dungeon.depth <= 1 || Dungeon.depth >= 25
				|| Dungeon.bossLevel() || !Dungeon.interfloorTeleportAllowed()) {
			hero.spend(TIME_TO_USE);
			GLog.w(Messages.get(Item.class, "not_here"));
			return;
		}
		returnDepth = Dungeon.depth;
		returnBranch = Dungeon.branch;
		returnPos = hero.pos;
		transition(hero, Dungeon.depth, BRANCH, -1);
	}

	private void transition(Hero hero, int depth, int branch, int pos) {
		Level.beforeTransition();
		Invisibility.dispel();
		hero.spend(TIME_TO_USE);
		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = depth;
		InterlevelScene.returnBranch = branch;
		InterlevelScene.returnPos = pos;
		Game.switchScene(InterlevelScene.class);
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return new ItemSprite.Glowing(0x000000);
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
