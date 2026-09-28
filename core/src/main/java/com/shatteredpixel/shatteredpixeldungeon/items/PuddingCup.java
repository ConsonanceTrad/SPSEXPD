package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.scenes.MemorySaveScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PuddingCupScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;

import java.io.IOException;
import java.util.ArrayList;

public class PuddingCup extends Item {

	private static final String AC_SAVE = "SAVE";

	{
		image = ItemSpriteSheet.PUDDING_CUP;
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

	static Class<? extends com.watabou.noosa.Scene> saveScene() {
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
