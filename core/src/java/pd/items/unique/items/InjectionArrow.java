/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.actors.mobs.SpsArrow;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.consum.potions.Potion;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.bags.Bag;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;

import java.util.ArrayList;

/**
 * SPSEXPD: 注射哨箭 —— 星兵二档（王冠 A 路线）的特殊神器（召唤类）。
 *
 * <p>与亚卡箭同源，但哨箭_飞得更快_（6 格/回合）；此外当哨箭_在你身边_时，
 * 可以往它里面_注入一瓶药水_，让它接下来数次命中附带那份药水效果。</p>
 */
public class InjectionArrow extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.SOLDIER_GUN_T2_1_INJECT;

	public static final String AC_COMMAND = "COMMAND";
	public static final String AC_INJECT = "INJECT";

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(InjectionArrow.class)
			.t("name", "注射哨箭")
			.t("ac_command", "吹哨指挥")
			.t("ac_inject", "注射药水")
			.t("prompt", "选择哨箭要飞向的地方")
			.t("summoned", "你吹响哨子，一支疾飞的注射哨箭应声而起。")
			.t("commanded", "哨箭朝你指的方向掠过。")
			.t("inject_prompt", "选择要注入哨箭的药水")
			.t("inject_need_arrow", "哨箭必须先在你身边，才能给它注射药水。")
			.t("injected", "你把药水注进了哨箭，它接下来的 %d 次命中都会附带这份效果。")
			.t("desc", "一支比亚卡箭更细的哨箭，箭头带着一枚透明的注药舱。\n\n「吹哨指挥」：唤出一支哨箭，或指挥它朝指定地点直线飞去。_指挥不消耗回合_。它每回合能飞 _6 格_，_可以穿过生物_，撞墙才停，沿途敌人都会受到物理伤害，也能提前变向。\n\n「注射药水」：当哨箭_就在你身边_时，可以往注药舱里注入一瓶药水——它接下来的 _3 次命中_都会附带那瓶药水的效果。\n\n代价与亚卡箭一样：_每次下令都会惊醒周围 6 格内的敌人_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		//SPSEXPD: 指挥是默认动作——快捷栏点一下就能吹哨；指挥本身不消耗回合
		defaultAction = AC_COMMAND;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_COMMAND);
		actions.add(AC_INJECT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (AC_COMMAND.equals(action)) {
			curUser = hero;
			GameScene.selectCell(commander);
		} else if (AC_INJECT.equals(action)) {
			SpsArrow arrow = YakaArrow.findArrow(SpsArrow.Kind.INJECTION);
			if (arrow == null || Dungeon.level == null || Dungeon.level.distance(hero.pos, arrow.pos) > 1) {
				GLog.w(Messages.get(this, "inject_need_arrow"));
				return;
			}
			curUser = hero;
			curItem = this;
			GameScene.selectItem(potionSelector);
		}
	}

	private final CellSelector.Listener commander = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || !(curUser instanceof Hero)) return;
			Hero hero = (Hero) curUser;
			YakaArrow.summonOrCommand(SpsArrow.Kind.INJECTION, cell, hero);
			//SPSEXPD: 指挥不消耗回合
			hero.spendAndNext(0f);
		}
		@Override
		public String prompt() {
			return Messages.get(InjectionArrow.class, "prompt");
		}
	};

	protected static WndBag.ItemSelector potionSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(InjectionArrow.class, "inject_prompt");
		}
		@Override
		public Class<? extends Bag> preferredBag() {
			return Belongings.Backpack.class;
		}
		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Potion;
		}
		@Override
		public void onSelect(Item item) {
			if (!(item instanceof Potion)) return;
			SpsArrow arrow = YakaArrow.findArrow(SpsArrow.Kind.INJECTION);
			if (arrow == null) return;
			arrow.inject((Potion) item);
			//SPSEXPD: 注入会消耗掉这瓶药水
			if (Dungeon.hero != null) item.detach(Dungeon.hero.belongings.backpack);
			GLog.p(Messages.get(InjectionArrow.class, "injected", SpsArrow.INJECTION_HITS));
			if (Dungeon.hero != null) Dungeon.hero.spendAndNext(1f);
		}
	};

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
