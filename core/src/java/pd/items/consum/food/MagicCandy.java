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
 * SPSEXPD: 魔法硬糖——魔法果汁经「蒸馏压片机」蒸馏后的浓缩形态。
 *
 * <p>与果汁一一对应：名字由果汁名派生（去掉末尾「瓶」/「汁」再拼「硬糖」），
 * 所以「星辰精华瓶 → 星辰精华硬糖」「萝卜汁 → 萝卜硬糖」。效果同样跟着来源走。
 */
public class MagicCandy extends Food {

	static {
		InlineText.of(MagicCandy.class)
				.t("name", "魔法硬糖")
				.t("desc", "浓缩到几乎发亮的果蔬硬糖，含在嘴里能持续补充体力。")
				.t("desc_source", "用%s浓缩成的硬糖，含在嘴里能持续补充体力，也会重现那份效果。");
	}

	/** 来源类（蔬菜或某个旧瓶子类）；null 表示旧存档里的通用硬糖。 */
	public Class<? extends Item> vegetable = null;

	private static final String VEGETABLE = "vegetable";

	{
		//SPSEXPD: 图标先占位，等所有批次完成后统一绘制
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 30f;
		hornValue = 3;
	}

	public static MagicCandy of(Class<? extends Item> vegetable) {
		MagicCandy candy = new MagicCandy();
		candy.vegetable = vegetable;
		return candy;
	}

	@Override
	public String name() {
		if (vegetable == null) return Messages.get(this, "name");
		return MagicJuice.candyNameOf(vegetable);
	}

	@Override
	public String desc() {
		if (vegetable == null) return Messages.get(this, "desc");
		String juice = MagicJuice.juiceNameOf(vegetable);
		return Messages.get(this, "desc_source", juice == null ? Messages.get(this, "name") : juice);
	}

	@Override
	public boolean isSimilar(Item item) {
		return item instanceof MagicCandy && ((MagicCandy) item).vegetable == this.vegetable;
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
		if (name != null && !name.isEmpty()) {
			Class<?> cls = Reflection.forName(name);
			if (cls != null && Item.class.isAssignableFrom(cls)) {
				//noinspection unchecked
				vegetable = (Class<? extends Item>) cls;
			}
		}
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
		return 30 * quantity;
	}
}
