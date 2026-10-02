/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.effects.particles.ElmoParticle;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;

public class MechPocket extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MechPocket.class)
			.t("name", "机械口袋")
			.t("ac_use", "使用")
			.t("desc", "一个充满神秘感的未来口袋。使用后会一次性倒出20件随机物品，随后消失。");
	}



	public static final String AC_USE = "USE";
	public static final int ITEM_COUNT = 20;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_USE;
		unique = true;
		stackable = false;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) use(hero);
		else super.execute(hero, action);
	}

	public int use(Hero hero) {
		if (hero == null || Dungeon.level == null) return 0;
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
		if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		hero.spendAndNext(1f);
		for (int i = 0; i < ITEM_COUNT; i++) {
			Heap heap = Dungeon.level.drop(Generator.random(), hero.pos);
			if (heap.sprite != null) heap.sprite.drop();
		}
		detach(hero.belongings.backpack);
		return ITEM_COUNT;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
}
