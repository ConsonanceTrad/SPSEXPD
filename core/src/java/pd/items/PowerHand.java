/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificTaskDict;

import pd.Assets;
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.hero.Hero;
import pd.items.nornstone.NornStone;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PowerHandScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import pd.messages.InlineText;

/** The original five-stone ending item dropped by UYog. */
public class PowerHand extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PowerHand.class)
			.t("name", "力量之手")
			.t("desc", "我们对这件奇怪的道具一无所知。")
			.t("desc_stones", "已经镶嵌了%d枚魔法矿石。")
			.t("prompt", "选择要镶嵌的魔法矿石")
			.t("ac_add", "镶嵌")
			.t("ac_use", "使用")
			.t("nothing", "什么也没发生。")
			.t("already_fed", "这种魔法矿石已经镶嵌过了。")
			.t("absorb_stone", "你把这枚魔法矿石镶嵌在手套上。")
			.t("save_failed", "存档失败，力量之手没有启动。");
	}




	public static final int CHAOS_BRANCH = 47;
	public static final String AC_ADD = "ADD";
	public static final String AC_USE = "USE";
	private static final String STONE_TYPES = "stone_types";

	private final HashSet<Integer> stoneTypes = new HashSet<>();

	{
		image = SpecificTaskDict.POWER_HAND;
		unique = true;
		defaultAction = AC_ADD;
		keptThoughLostInvent = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ADD);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_ADD.equals(action)) {
			curUser = hero;
			GameScene.selectItem(stoneSelector);
		} else if (AC_USE.equals(action)) {
			if (stoneTypes.size() < 5) {
				GLog.i(Messages.get(this, "nothing"));
				return;
			}
			HashSet<Integer> consumedStones = new HashSet<>(stoneTypes);
			stoneTypes.clear();
			try {
				Dungeon.saveAll();
				Game.switchScene(PowerHandScene.class);
			} catch (IOException e) {
				stoneTypes.addAll(consumedStones);
				ShatteredPixelDungeon.reportException(e);
				GLog.w(Messages.get(this, "save_failed"));
			}
		} else {
			super.execute(hero, action);
		}
	}

	private final WndBag.ItemSelector stoneSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(PowerHand.class, "prompt"); }
		@Override public boolean itemSelectable(Item item) {
			return item instanceof NornStone && item.getClass() != NornStone.class;
		}
		@Override public void onSelect(Item item) {
			if (!(item instanceof NornStone)) return;
			NornStone stone = (NornStone) item;
			if (stoneTypes.contains(stone.type)) {
				GLog.w(Messages.get(PowerHand.class, "already_fed"));
				return;
			}
			stoneTypes.add(stone.type);
			if (curUser != null) {
				curUser.sprite.operate(curUser.pos);
				curUser.spend(2f);
			}
			Sample.INSTANCE.play(Assets.Sounds.PLANT);
			item.detach(Dungeon.hero.belongings.backpack);
			GLog.i(Messages.get(PowerHand.class, "absorb_stone"));
		}
	};

	@Override
	public String desc() {
		String desc = super.desc();
		if (!stoneTypes.isEmpty()) desc += "\n\n" + Messages.get(this, "desc_stones", stoneTypes.size());
		return desc;
	}

	public int stoneCount() {
		return stoneTypes.size();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		int[] values = new int[stoneTypes.size()];
		int i = 0;
		for (int type : stoneTypes) values[i++] = type;
		bundle.put(STONE_TYPES, values);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		stoneTypes.clear();
		if (bundle.contains(STONE_TYPES)) {
			for (int type : bundle.getIntArray(STONE_TYPES)) {
				if (type >= 1 && type <= 5) stoneTypes.add(type);
			}
		}
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
