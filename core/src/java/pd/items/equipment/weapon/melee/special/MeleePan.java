/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.Torch;
import pd.items.consum.food.Food;
import pd.items.consum.food.MysteryMeat;
import pd.items.consum.food.cook.*;
import pd.items.consum.food.meatfood.FireMeat;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.ranges.RangePan;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * Coconut2's pan in its fixed-damage melee form.
 *
 * SPSEXPD: 血源风简单烹饪——煎锅新增「烹饪」动作：
 * 选一份可烹制食材、消耗 1 个火把生火，按烹饪技巧产出对应菜肴（每烹饪一次技巧 +1）。
 */
public class MeleePan extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MeleePan.class)
			.t("name", "平底煎锅")
			.t("ac_change", "切换")
			.t("ac_cook", "烹饪")
			.t("cook_prompt", "选择一份可烹制的食材")
			.t("cook_need_fire", "你需要一个火把生火才能烹饪！")
			.t("cook_done", "你用煎锅做好了%1$s。")
			.t("desc", "用于烹饪的煎锅。\n高级钝器\n\n使用「烹饪」可以把可烹制的食材做成菜肴（需要消耗 1 个火把生火），产物取决于你的烹饪技巧。");
	}




	public static final String AC_CHANGE = "CHANGE";
	public static final String AC_COOK = "COOK";
	//SPSEXPD: 烹饪耗时（参考血源的 2 回合）
	public static final float TIME_TO_COOK = 2f;

	{
		image = SpecificPlaceHolderDict.SPS_PH_WEAPON_BAD;
		tier = 1;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return 8 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 8 + Math.max(0, lvl); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30) Buff.prolong(defender, HolyStun.class, 2f);
		return super.proc(attacker, defender, damage);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero)) actions.add(AC_CHANGE);
		//SPSEXPD: 血源风烹饪——手持或背包里的煎锅都能用
		actions.add(AC_COOK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHANGE.equals(action) && isEquipped(hero)) changeToRange(hero);
		else if (AC_COOK.equals(action)) GameScene.selectItem(cookSelector);
		else super.execute(hero, action);
	}

	//SPSEXPD: 只接受 canBeCook 的食材
	private final WndBag.ItemSelector cookSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(MeleePan.class, "cook_prompt"); }
		@Override public boolean itemSelectable(Item item) {
			return item instanceof Food && ((Food) item).canBeCook;
		}
		@Override
		public void onSelect(Item item) {
			if (item == null || curUser == null) return;
			cook(curUser, (Food) item);
		}
	};

	/** SPSEXPD: 生火（消耗 1 个火把）并烹制一份食材。 */
	public static boolean cook(Hero hero, Food ingredient) {
		if (hero == null || ingredient == null) return false;
		Torch torch = hero.belongings.getItem(Torch.class);
		if (torch == null) {
			GLog.w(Messages.get(MeleePan.class, "cook_need_fire"));
			return false;
		}
		torch.detach(hero.belongings.backpack);

		Item dish = dishFor(ingredient, hero.cookingSkill);
		//SPSEXPD: 每烹饪一次技巧 +1（上限 100，参考血源）
		if (hero.cookingSkill < 100) hero.cookingSkill++;

		ingredient.detach(hero.belongings.backpack);
		if (!dish.collect(hero.belongings.backpack) && Dungeon.level != null) {
			Dungeon.level.drop(dish, hero.pos);
		}
		GLog.p(Messages.get(MeleePan.class, "cook_done", dish.name()));

		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		hero.busy();
		hero.spend(TIME_TO_COOK);
		return true;
	}

	/** SPSEXPD: 按食材与烹饪技巧决定菜肴（阈值沿用血源 25/50/95）。 */
	public static Item dishFor(Food ingredient, int skill) {
		if (ingredient instanceof DeadRat) {
			return skill < 25 ? new TailedCutlet() : new SlowRoastedRat();
		}
		if (ingredient instanceof WildCarrot) {
			return skill < 50 ? new FriedCarrot() : new TenderRoastedBabyCarrots();
		}
		if (ingredient instanceof BurdockRoot) {
			return skill < 50 ? new FriedBurdockRoot() : new SpicedSauteeBurdockRoot();
		}
		if (ingredient instanceof ChicoryRoot) {
			return skill < 95 ? new FriedChicoryRoot() : new BakedChicoryWithHerbs();
		}
		//SPSEXPD: 其它 canBeCook 食材（如神秘肉）一律烤成烤肉排
		return new FireMeat();
	}

	public RangePan changeToRange(Hero hero) {
		RangePan replacement = new RangePan();
		copyState(this, replacement);
		hero.belongings.weapon = replacement;
		return replacement;
	}

	public static void copyState(Weapon source, Weapon replacement) {
		int level = source.trueLevel();
		if (level > 0) replacement.upgrade(level);
		else if (level < 0) replacement.degrade(-level);
		replacement.enchantment = source.enchantment;
		replacement.reinforced = source.reinforced;
		replacement.levelKnown = source.levelKnown;
		replacement.cursedKnown = source.cursedKnown;
		replacement.cursed = source.cursed;
	}

	@Override public int value() { return legacyValue(this); }

	public static int legacyValue(Weapon weapon) {
		int result = weapon.enchantment == null ? 50 : 75;
		if (weapon.cursed && weapon.cursedKnown) result /= 2;
		if (weapon.levelKnown) {
			if (weapon.trueLevel() > 0) result *= weapon.trueLevel() + 1;
			else if (weapon.trueLevel() < 0) result /= 1 - weapon.trueLevel();
		}
		return Math.max(1, result);
	}
}
