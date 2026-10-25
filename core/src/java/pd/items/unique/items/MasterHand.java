/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.utils.Teleport;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 神偷之手 —— 盗贼二档（王冠 A 路线）的神器。
 *
 * <p>继承大盗之手的潜行与偷取，另加_对空地瞬移_（一定距离内）。</p>
 */
public class MasterHand extends GreatHand {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ROGUE_CLOAK_T2_1_HAND;

	public static final String AC_BLINK = "BLINK";
	/** 瞬移的最大距离。 */
	public static final int BLINK_RANGE = 6;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(MasterHand.class)
			.t("name", "神偷之手")
			.t("ac_blink", "瞬移")
			.t("blink_prompt", "选择要瞬移到的空地")
			.t("blink_bad", "你不能瞬移到那里。")
			.t("blinked", "你的身影一闪，人已经站在了别处。")
			.t("desc", "一只连影子都跟不上的手，指尖还留着别处空气的温度。\n\n它保留了大盗之手的一切：_潜行_（隐身并准备刺杀）与_偷取_。\n\n此外还能「瞬移」——对_空地_出手，最多闪到 _%1$d 格_ 之外的空白处。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", BLINK_RANGE);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_BLINK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_BLINK.equals(action)) {
			curUser = hero;
			GameScene.selectCell(blinkListener);
			return;
		}
		super.execute(hero, action);
	}

	private final CellSelector.Listener blinkListener = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || !(curUser instanceof Hero)) return;
			Hero hero = (Hero) curUser;
			if (Dungeon.level == null
					|| !Dungeon.level.insideMap(cell)
					|| Dungeon.level.distance(hero.pos, cell) > BLINK_RANGE
					|| Dungeon.level.solid[cell]
					|| !Dungeon.level.passable[cell]
					|| pd.actors.Actor.findChar(cell) != null) {
				GLog.w(Messages.get(this, "blink_bad"));
				return;
			}
			Teleport.appear(hero, cell);
			Dungeon.observe();
			GameScene.updateFog();
			GLog.p(Messages.get(this, "blinked"));
			hero.spendAndNext(1f);
		}
		@Override
		public String prompt() {
			return Messages.get(MasterHand.class, "blink_prompt");
		}
	};

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
