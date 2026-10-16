/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.SpoonFatigue;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.wands.Wand;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.mechanics.Ballistica;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 银汤匙 —— 信徒一档（面具 A 路线）的特殊法杖。
 *
 * <p>_充能无限_，代价是每次使用都会累积_疲劳值_；疲劳超过上限后继续用就会受伤，
 * 停下不用则会慢慢回落。功能有两个：对自己使用可以获得_灵视_，对敌人使用会造成
 * _无弹道_的直接伤害。</p>
 */
public class SilverSpoon extends Wand implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.FOLLOWER_CUSHION_T1_1_SPOON;

	public static final String AC_USE = "USE";

	/** 灵视持续回合。 */
	public static final float MIND_VISION_TURNS = 12f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SilverSpoon.class)
			.t("name", "银汤匙")
			.t("ac_use", "使用")
			.t("use_prompt", "选择目标（对自己使用可获灵视）")
			.t("no_target", "那里什么也没有。")
			.t("blind", "你尝了尝汤匙里的东西……什么都不记得了。")
			.t("hurt", "疲劳沿着手臂爬上脑门，你眼前一黑（-%d）。")
			.t("desc", "一把沉甸甸的银汤匙，勺柄上刻着一行小字：我不是超能力者。\n\n它的_充能是无限的_，但每次使用都会累积_疲劳值_；疲劳超过上限还继续用，你就会受伤。停下不用，疲劳会慢慢回落。\n\n对它来说，_对自己_使用可以获得_灵视_；对_敌人_使用则是一道_没有弹道_的直接伤害。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
	}

	/** 疲劳上限。 */
	protected int fatigueCap() {
		return 100;
	}

	/** 无弹道伤害。 */
	protected int spoonDamage() {
		return 3 + level() * 2;
	}

	//SPSEXPD: 充能无限
	@Override public int initialCharges() { return 1; }
	@Override public int maxChargesAtLevel(int lvl) { return 1; }

	/** SPSEXPD: 汤匙不走弹道，实际逻辑在 execute 里。 */
	@Override
	public void onZap(Ballistica attack) {
	}

	/** SPSEXPD: 不参与法杖近战命中（汤匙只走自己的动作）。 */
	@Override
	public void onHit(pd.items.equipment.weapon.melee.MagesStaff staff, Char attacker, Char defender, int damage) {
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			curUser = hero;
			usesTargeting = true;
			GameScene.selectCell(user);
			return;
		}
		super.execute(hero, action);
	}

	private final CellSelector.Listener user = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || !(curUser instanceof Hero)) return;
			Hero hero = (Hero) curUser;

			SpoonFatigue fatigue = Buff.affect(hero, SpoonFatigue.class).setup(fatigueCap());

			Char target = Actor.findChar(cell);
			if (target == hero || cell == hero.pos) {
				//SPSEXPD: 对自己 → 灵视
				Buff.affect(hero, MindVision.class, MIND_VISION_TURNS);
				GLog.i(Messages.get(SilverSpoon.this, "blind"));
			} else if (target != null && target.alignment == Char.Alignment.ENEMY) {
				//SPSEXPD: 对敌人 → 无弹道直接伤害
				target.damage(spoonDamage(), SilverSpoon.this);
			} else {
				GLog.w(Messages.get(SilverSpoon.this, "no_target"));
				return;
			}

			if (fatigue.addFatigue()) {
				int dmg = Math.max(1, Math.round(hero.HT * SpoonFatigue.OVERLOAD_FRACTION));
				hero.damage(dmg, SilverSpoon.this);
				GLog.w(Messages.get(SilverSpoon.this, "hurt", dmg));
			}
			hero.spendAndNext(1f);
		}
		@Override
		public String prompt() {
			return Messages.get(SilverSpoon.this, "use_prompt");
		}
	};

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
