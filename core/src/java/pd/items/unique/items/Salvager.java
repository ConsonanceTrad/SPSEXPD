/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.consum.food.Food;
import pd.items.equipment.artifacts.Artifact;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;

import java.util.ArrayList;

/**
 * SPSEXPD: 回收机 —— 苦修者一档（面具 B 路线）的神器。
 *
 * <p>比有机重组机多一条路：_击杀生物_也能回收材料，另外还能主动把食物拆成材料。</p>
 */
public class Salvager extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T1_2_SALVAGER;

	public static final String AC_CONVERT = "CONVERT";

	/** 每份食物产出的材料数。 */
	public static final int MATERIALS_PER_FOOD = 2;
	/** 每次击杀产出的材料数。 */
	public static final int MATERIALS_PER_KILL = 1;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Salvager.class)
			.t("name", "回收机")
			.t("ac_convert", "回收")
			.t("prompt", "选择要回收的有机食物")
			.t("not_food", "那东西不是有机食物。")
			.t("converted", "回收机把它拆成了 %d 份义体材料。")
			.t("killed", "回收机从尸骸里抽出了 %d 份义体材料。")
			.t("desc", "一台挂着钩爪的回收装置，爪尖总有东西在滴。\n\n「回收」：把一件_有机食物_拆成 %1$d 份_义体材料_。\n\n此外，你每次_击杀生物_，它都会从尸骸里额外抽出 %2$d 份材料。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", MATERIALS_PER_FOOD, MATERIALS_PER_KILL);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_CONVERT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CONVERT.equals(action)) {
			GameScene.selectItem(foodSelector);
			return;
		}
		super.execute(hero, action);
	}

	private final WndBag.ItemSelector foodSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(Salvager.class, "prompt"); }
		@Override public boolean itemSelectable(Item item) {
			return item instanceof Food;
		}
		@Override public void onSelect(Item item) {
			if (!(item instanceof Food)) {
				GLog.w(Messages.get(Salvager.class, "not_food"));
				return;
			}
			if (item.quantity() > 1) {
				item.quantity(item.quantity() - 1);
			} else {
				item.detach(Dungeon.hero.belongings.backpack);
			}
			OrganicRecycler.grantMaterials(MATERIALS_PER_FOOD);
			GLog.i(Messages.get(Salvager.class, "converted", MATERIALS_PER_FOOD));
		}
	};

	/** SPSEXPD: 击杀时回收材料（由 Hero 的击杀流程调用）。 */
	public static void onKill(Hero hero) {
		if (hero == null || hero.belongings.getItem(Salvager.class) == null) return;
		OrganicRecycler.grantMaterials(MATERIALS_PER_KILL);
		GLog.i(Messages.get(Salvager.class, "killed", MATERIALS_PER_KILL));
	}

	/** SPSEXPD: 神器不提供常驻 buff。 */
	@Override
	protected ArtifactBuff passiveBuff() {
		return new Idle();
	}

	public class Idle extends ArtifactBuff {
		@Override
		public boolean act() {
			spend(TICK);
			return true;
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
