/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.items.quest.AdventureJournal;
import pd.levels.Level;
import pd.levels.Transitions;
import pd.messages.Messages;
import pd.scenes.InterlevelScene;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import render.noosa.Game;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;

/** Legacy portal-shaped prototype of Shadow Eater, retained for save/content parity. */
public class ShadowEaterKey extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShadowEaterKey.class)
			.t("name", "暗噬")
			.t("ac_port", "装备")
			.t("desc", "由测试者们共同制作的受诅咒武器原型。\n休眠、双刃、低语。\n\n它实际上是一件通往暗噬领域的一次性传送道具。");
	}



	public static final int BRANCH = AdventureJournal.FIRST_BRANCH + 16;
	public static final String AC_PORT = "PORT";
	private static final float TIME_TO_USE = 1f;

	private int returnDepth = -1;
	private int returnBranch;
	private int returnPos = -1;

	{
		image = EquipmentEquipWeaponUniqueWeaponDict.CHAINSAW_SWORD;
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
		if (Dungeon.branch == BRANCH) {
			detach(hero.belongings.backpack);
			transition(hero, returnDepth > 0 ? returnDepth : 16,
					returnDepth > 0 ? returnBranch : 0,
					returnDepth > 0 ? returnPos : -1);
			return;
		}
		if (Dungeon.branch != 0 || Dungeon.depth <= 1 || Dungeon.depth >= 39
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
		Transitions.beforeTransition();
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

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(RETURN_DEPTH, returnDepth);
		bundle.put(RETURN_BRANCH, returnBranch);
		bundle.put(RETURN_POS, returnPos);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		returnDepth = bundle.contains(RETURN_DEPTH) ? bundle.getInt(RETURN_DEPTH) : -1;
		returnBranch = bundle.getInt(RETURN_BRANCH);
		returnPos = bundle.contains(RETURN_POS) ? bundle.getInt(RETURN_POS) : -1;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return false; }
}
