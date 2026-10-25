/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items.consum.food;

import java.util.LinkedHashMap;
import java.util.Map;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Item;
import pd.messages.InlineText;
import pd.messages.Messages;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 魔法果汁——每种蔬菜各产一种（docs/alchemy-rework.md §4）。
 *
 * <p>名字由 {@link #SOURCE_NAMES} 逐种指定（如「星花瓣 → 星辰精华瓶」「萝卜 → 萝卜汁」），
 * 未列出的蔬菜回退成「&lt;蔬菜名&gt;瓶」。食用效果跟蔬菜走，不同蔬菜的果汁不堆叠。
 *
 * <p>旧菌类改造而来的几种（蓝浆果汁瓶 / 露珠能量瓶 / 寄生孢子瓶 / 虚空能量瓶 /
 * 火焰磷菌瓶 / 小精灵伞瓶 / 死亡溶剂瓶）也继承本类，它们各自覆写 name() 保留原名。
 */
public class MagicJuice extends Food {

	static {
		InlineText.of(MagicJuice.class)
				.t("name", "魔法果汁")
				.t("desc", "把蔬菜与果实一起榨出的浓稠汁液，带着一点草木的清香。")
				.t("desc_source", "把%s与果实一起榨出的浓稠汁液，颜色和气味都跟它很像。喝下去会重现那份效果。")
				.t("desc_suffix", "可以直接喝，也能拿去蒸馏成硬糖。")
				.t("itself_juice", "这其实是一种果汁。");
	}

	/** 蔬菜 → 果汁名（逐种指定，不用统一后缀）。 */
	private static final Map<Class<? extends Item>, String> SOURCE_NAMES = new LinkedHashMap<>();

	static {
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.BattleFlower.class, "星辰精华瓶");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.IceMint.class, "清凉薄荷瓶");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.NutVegetable.class, "坚果果汁瓶");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.QuartzFlower.class, "辉耀水晶瓶");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.Radish.class, "萝卜汁");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.RainbowPansy.class, "混乱原浆瓶");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.Sorrel.class, "韵魔速行瓶");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.StarEaterFlower.class, "酸蚀恶水瓶");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.Sunflower.class, "阳春疗愈瓶");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.TransmuteCage.class, "韵魔原液瓶");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.Truffles.class, "松露汁");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.Lettuce.class, "莴苣汁");
		SOURCE_NAMES.put(pd.items.consum.food.vegetable.Apple.class, "苹果汁");
		SOURCE_NAMES.put(pd.items.consum.food.Berry.class, "浆果汁");
	}

	/** 榨汁用的蔬菜类；null 表示旧存档里的通用果汁。 */
	public Class<? extends Item> vegetable = null;

	private static final String VEGETABLE = "vegetable";

	{
		//SPSEXPD: 图标先占位，等所有批次完成后统一绘制
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 15f;
		hornValue = 2;
	}

	public static MagicJuice of(Class<? extends Item> vegetable) {
		MagicJuice juice = new MagicJuice();
		juice.vegetable = vegetable;
		juice.image = produceImage(vegetable);
		return juice;
	}

	/** 借用来源的图标（等统一绘制后再换正式图）。 */
	private static pd.atlas.IconEntry produceImage(Class<? extends Item> vegetable) {
		Item veg = Reflection.newInstance(vegetable);
		return veg == null ? SpecificPlaceHolderDict.SOMETHING_0 : veg.image;
	}

	/** 来源类 → 果汁名：先查专表，未列出则回退「&lt;名字&gt;瓶」；旧瓶子类返回自身名字。 */
	public static String juiceNameOf(Class<? extends Item> source) {
		if (source == null) return null;
		String mapped = SOURCE_NAMES.get(source);
		if (mapped != null) return mapped;
		Item src = Reflection.newInstance(source);
		if (src instanceof MagicJuice) return src.name();
		String base = src != null ? src.name() : Messages.titleCase(Messages.get(source, "name"));
		return base + "瓶";
	}

	/** 来源类 → 硬糖名：果汁名去掉末尾「瓶」或「汁」再拼「硬糖」。 */
	public static String candyNameOf(Class<? extends Item> source) {
		String juice = juiceNameOf(source);
		if (juice == null) return Messages.get(MagicCandy.class, "name");
		if (juice.endsWith("瓶")) {
			juice = juice.substring(0, juice.length() - 1);
		} else if (juice.endsWith("汁")) {
			juice = juice.substring(0, juice.length() - 1);
		}
		return juice + "硬糖";
	}

	@Override
	public String name() {
		if (getClass() != MagicJuice.class) {
			//旧菌类改造来的果汁瓶子类：保留自己的名字
			return Messages.get(getClass(), "name");
		}
		String mapped = juiceNameOf(vegetable);
		return mapped != null ? mapped : Messages.get(this, "name");
	}

	@Override
	public String desc() {
		if (getClass() != MagicJuice.class) {
			return Messages.get(getClass(), "desc") + "\n\n" + Messages.get(this, "itself_juice");
		}
		String source = vegetable == null ? null : Messages.get(vegetable, "name");
		if (source == null) {
			return Messages.get(this, "desc") + "\n\n" + Messages.get(this, "desc_suffix");
		}
		return Messages.get(this, "desc_source", source) + "\n\n" + Messages.get(this, "desc_suffix");
	}

	/** 只有同一种蔬菜榨出的果汁才堆叠。 */
	@Override
	public boolean isSimilar(Item item) {
		return item instanceof MagicJuice && item.getClass() == getClass()
				&& ((MagicJuice) item).vegetable == this.vegetable;
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		FoodEffects.applyVegetableEffect(vegetable, hero);
	}

	/** 周围所有生物（旧瓶效果要用）。 */
	protected Mob[] mobs() {
		if (pd.Dungeon.level == null) return new Mob[0];
		return pd.Dungeon.level.mobs().toArray(new Mob[0]);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(VEGETABLE, vegetable == null ? "" : vegetable.getName());
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		String name = bundle.getString(VEGETABLE);
		vegetable = name == null || name.isEmpty() ? null : resolve(name);
	}

	private static Class<? extends Item> resolve(String className) {
		Class<?> cls = Reflection.forName(className);
		if (cls != null && Item.class.isAssignableFrom(cls)) {
			//noinspection unchecked
			return (Class<? extends Item>) cls;
		}
		return null;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 20 * quantity;
	}
}
