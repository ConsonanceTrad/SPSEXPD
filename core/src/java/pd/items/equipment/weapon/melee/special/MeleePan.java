/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.items.consum.materials.Tinder;

import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.food.Food;
import pd.items.consum.food.MysteryMeat;
import pd.items.consum.food.cook.*;
import pd.items.consum.food.meatfood.FireMeat;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.MeleeWeapon;
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
			.t("ac_cook", "烹饪")
			.t("cook_prompt", "选择一份可烹制的食材")
			.t("cook_need_fire", "你需要一个火种生火才能烹饪！")
			.t("cook_done", "你用煎锅做好了%1$s。")
			.t("desc", "用于烹饪的煎锅。\n高级钝器\n\n使用「烹饪」可以把多份食材做成简餐，也可以煎单份食材（需要消耗 1 个火种生火），产物取决于你的烹饪技巧。");
	}




	public static final String AC_COOK = "COOK";
	//SPSEXPD: 烹饪耗时（参考血源的 2 回合）
	public static final float TIME_TO_COOK = 2f;

	{
		image = EquipmentEquipWeaponUniqueWeaponDict.MELEE_PAN;
		tier = 1;
		usesTargeting = true;
		//SPSEXPD: 视为重要道具（唯一物品，不再重复生成）
		unique = true;
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
		//SPSEXPD: 已去掉「切换」动作（不再切换成远程形态 RangePan）；
		//血源风烹饪保留，手持或背包里的煎锅都能用
		actions.add(AC_COOK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_COOK.equals(action)) pd.windows.WndCrafting.open(pd.windows.WndCrafting.MODE_COOK);
		else super.execute(hero, action);
	}

	//SPSEXPD: 烹饪改为打开平底锅窗口（多食材简餐 + 单食材煎制），选择器已不再需要

	/** SPSEXPD: 生火（消耗 1 个火种）并烹制一份食材。 */
	public static boolean cook(Hero hero, Food ingredient) {
		if (hero == null || ingredient == null) return false;
		if (!consumeTinder(hero)) return false;


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

	/** SPSEXPD: 消耗 1 个火种生火；没有火种时提示并返回 false。 */
	public static boolean consumeTinder(Hero hero) {
		if (hero == null) return false;
		Tinder tinder = hero.belongings.getItem(Tinder.class);
		if (tinder == null) {
			GLog.w(Messages.get(MeleePan.class, "cook_need_fire"));
			return false;
		}
		tinder.detach(hero.belongings.backpack);
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
