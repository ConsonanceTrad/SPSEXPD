package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Badges;
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.scenes.MemorySaveScene;
import pd.scenes.PuddingCupScene;
import render.noosa.Game;

import java.io.IOException;
import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;

public class PuddingCup extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PuddingCup.class)
			.t("name", "巧克力布丁")
			.t("ac_save", "记忆")
			.t("saved", "这一刻已经被保存。")
			.t("desc", "美味的布丁可以使你记住这一美好的瞬间，但仅仅是这一瞬间而已。");
	}




	private static final String AC_SAVE = "SAVE";

	{
		image = SpecificTaskDict.TASTY_PUDDING;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SAVE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_SAVE)) {
			curUser = hero;
			Badges.validateLearn();
			consumeForSave(hero);
			try {
				Dungeon.saveAll();
				Game.switchScene(MemorySaveScene.class);
			} catch (IOException e) {
				ShatteredPixelDungeon.reportException(e);
			}
		} else {
			super.execute(hero, action);
		}
	}

	static Class<? extends render.noosa.Scene> saveScene() {
		return MemorySaveScene.class;
	}

	void consumeForSave(Hero hero) {
		detach(hero.belongings.backpack);
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (!super.doPickUp(hero, pos)) return false;
		if (Dungeon.isTutorial()) Badges.validateLearn();
		hero.spend(-hero.cooldown());
		Actor.add(new Actor() {
			{
				actPriority = VFX_PRIO;
			}

			@Override
			protected boolean act() {
				Actor.remove(this);
				if (!Dungeon.isTutorial()) {
					try {
						Dungeon.saveAll();
					} catch (IOException e) {
						ShatteredPixelDungeon.reportException(e);
					}
				}
				Game.switchScene(PuddingCupScene.class);
				return false;
			}
		});
		return true;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
}
