/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.actors.mobs.FutonSentinel;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 蒲团 —— 信徒开局的两件专属物品之一。
 *
 * <p>每隔一段时间可以在地上「放置蒲团」，生成一个待命的蒲团单位；与它交互即可让它
 * 长成三种固定哨兵植物之一（豌豆射手 / 土豆雷 / 坚果墙，见 {@link FutonSentinel}）。</p>
 */
public class Futon extends Item implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.FOLLOWER_CUSHION_BASE_A;

	public static final String AC_PLACE = "PLACE";
	/** 两次放置之间的冷却回合。 */
	public static final float COOLDOWN = 30f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Futon.class)
			.t("name", "蒲团")
			.t("ac_place", "放置蒲团")
			.t("prompt", "选择蒲团要落下的地方")
			.t("bad_cell", "那里放不下蒲团。")
			.t("cooldown", "蒲团还没准备好再次展开（剩余 %d 回合）。")
			.t("placed", "你把蒲团摊在地上，它开始慢慢鼓胀起来。")
			.t("desc", "一只供人盘坐的圆垫，编得松散而柔软。\n\n每隔 %1$d 回合，你可以把它_摊在地上_，化成一个待命的蒲团。与那个蒲团交互，它就会舒展成一株_哨兵植物_——豌豆射手、土豆雷或坚果墙，替你看守这一小片地面。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_PLACE;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", (int)COOLDOWN);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_PLACE);
		return actions;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_PLACE.equals(action)) return;

		CushionCooldown cd = hero.buff(CushionCooldown.class);
		if (cd != null) {
			GLog.w(Messages.get(this, "cooldown", Math.max(1, (int)Math.ceil(cd.cooldown()))));
			return;
		}
		curUser = hero;
		GameScene.selectCell(picker);
	}

	private final CellSelector.Listener picker = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || curUser == null) return;
			if (!Dungeon.level.adjacent(curUser.pos, cell)
					|| !Dungeon.level.passable[cell]
					|| Actor.findChar(cell) != null) {
				GLog.w(Messages.get(Futon.class, "bad_cell"));
				return;
			}

			FutonSentinel cushion = new FutonSentinel();
			cushion.pos = cell;
			GameScene.add(cushion);
			Buff.affect(curUser, CushionCooldown.class, COOLDOWN);

			GLog.p(Messages.get(Futon.class, "placed"));
			curUser.spendAndNext(1f);
		}
		@Override
		public String prompt() {
			return Messages.get(Futon.class, "prompt");
		}
	};

	/** SPSEXPD: 放置蒲团后的冷却（纯计时，无图标）。 */
	public static class CushionCooldown extends FlavourBuff {
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
