/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.hero.Hero;
import pd.scenes.MemorySaveScene;
import render.noosa.Game;

import java.io.IOException;
import java.util.ArrayList;
import pd.messages.InlineText;

/** Test-mode emergency device which opens the legacy memory save slots. */
public class SaveYourLife extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SaveYourLife.class)
			.t("name", "紧急离线装置")
			.t("ac_save", "离线")
			.t("desc", "这是什么，这不应该在非测试模式下出现。");
	}




	private static final String AC_SAVE = "SAVE";

	{
		image = EquipmentNonEquipDict.SAVE_YOUR_LIFE;
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
			try {
				Dungeon.saveAll();
				Game.switchScene(MemorySaveScene.class);
			} catch (IOException exception) {
				ShatteredPixelDungeon.reportException(exception);
			}
		} else {
			super.execute(hero, action);
		}
	}

	static Class<? extends render.noosa.Scene> saveScene() {
		return MemorySaveScene.class;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
