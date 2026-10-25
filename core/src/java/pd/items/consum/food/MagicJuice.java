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

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.messages.InlineText;
import pd.messages.Messages;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 魔法果汁——每种蔬菜各产一种（docs/alchemy-rework.md §4）。
 *
 * <p>名称、描述与食用效果都跟着「是哪一种蔬菜」走（如辣椒果汁会让人着火并获得火焰免疫），
 * 不同蔬菜榨出的果汁不会堆叠。硬糖（{@link MagicCandy}）是它的浓缩形态，同样按蔬菜分。
 */
public class MagicJuice extends Food {

	static {
		InlineText.of(MagicJuice.class)
				.t("name", "魔法果汁")
				.t("name_pattern", "%s果汁")
				.t("desc", "把蔬菜与果实一起榨出的浓稠汁液，带着一点草木的清香。")
				.t("desc_pattern", "用%s榨出的浓稠汁液，颜色和气味都跟它很像。喝下去会重现那份效果。")
				.t("desc_suffix", "可以直接喝，也能拿去蒸馏成硬糖。");
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
		juice.image = vegetable == null ? SpecificPlaceHolderDict.SOMETHING_0 : produceImage(vegetable);
		return juice;
	}

	/** 借用对应蔬菜的图标（等统一绘制后再换正式图）。 */
	private static pd.atlas.IconEntry produceImage(Class<? extends Item> vegetable) {
		Item veg = Reflection.newInstance(vegetable);
		return veg == null ? SpecificPlaceHolderDict.SOMETHING_0 : veg.image;
	}

	@Override
	public String name() {
		if (vegetable == null) return Messages.get(this, "name");
		return Messages.get(this, "name_pattern", Messages.titleCase(Messages.get(vegetable, "name")));
	}

	@Override
	public String desc() {
		if (vegetable == null) {
			return Messages.get(this, "desc") + "\n\n" + Messages.get(this, "desc_suffix");
		}
		return Messages.get(this, "desc_pattern", Messages.titleCase(Messages.get(vegetable, "name")))
				+ "\n\n" + Messages.get(this, "desc_suffix");
	}

	/** 只有同一种蔬菜榨出的果汁才堆叠。 */
	@Override
	public boolean isSimilar(Item item) {
		return item instanceof MagicJuice && ((MagicJuice) item).vegetable == this.vegetable;
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		FoodEffects.applyVegetableEffect(vegetable, hero);
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
