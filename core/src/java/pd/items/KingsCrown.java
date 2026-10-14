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

package pd.items;

import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import pd.Assets;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.ArmorAbility;
import pd.actors.hero.abilities.Ratmogrify;
import pd.effects.Speck;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.ClassArmor;
import pd.items.unique.UniqueFamily;
import pd.items.unique.UniqueItemId;
import pd.items.unique.UniqueItems;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.sprites.HeroSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import java.util.List;
import pd.messages.InlineText;

/**
 * SPSEXPD: 矮人国王的皇冠 —— 二次强化：沿天狗面具选定的路线，把一档专属物品再提升一档。
 *
 * <p>与面具不同，皇冠不弹任何选择：它读取当前一档物品的 (family, branch)，直接换成同路线的二档物品。
 * 一档物品缺任何一件都拒绝执行。原本「把护甲改造成英雄护甲」的功能已按用户裁决被本机制替换
 * （{@link #upgradeArmor} 保留仅供其它旧调用点，不再由 {@code AC_WEAR} 触发）。</p>
 */
public class KingsCrown extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(KingsCrown.class)
			.t("name", "矮人国王的皇冠")
			.t("ac_wear", "加冕")
			.t("naked", "非常遗憾，皇冠的魔力无法强化你的内衣。")
			.t("nothing_unique", "你身上没有可以二次强化的专属物品。")
			.t("missing", "你身上缺少 %s，无法强化。")
			.t("upgraded", "皇冠熔为曜日般的金光，你的专属物品在照耀下开始蜕变！")
			.t("ratgraded", "鼠王挥爪便召出了万丈光芒，而你的护甲在照耀下开始逐渐变形！")
			.t("desc", "末代矮人国王的皇冠，至强的魔法能量以光芒的形式从中辐射而出。\n\n如果你有决心将其戴上，皇冠的魔力会涌入你身上的_职业专属物品_，把它再提升一档——它会沿着你此前选定的路线继续蜕变。")
			.t("discover_hint", "你可从某种敌人的掉落物中获得该物品。");
	}

	private static final String AC_WEAR = "WEAR";

	{
		image = ConsumUsefulProcessEnhanceDict.CROWN_0;

		defaultAction = AC_WEAR;

		unique = true;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_WEAR );
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_WEAR)) {
			curUser = hero;
			upgradeUniqueTier2( hero );
		}
	}

	/**
	 * SPSEXPD: 二次强化 —— 沿当前一档物品所属的路线，换发同路线的二档物品（不选择）。
	 */
	public void upgradeUniqueTier2( Hero hero ) {
		if (hero == null || hero.belongings == null) return;

		UniqueFamily family = UniqueItems.familyOf( hero );
		if (family == null) {
			GLog.w( Messages.get(this, "nothing_unique") );
			return;
		}

		List<Item> tier1 = UniqueItems.held( hero, family, 1 );
		if (tier1.isEmpty()) {
			GLog.w( Messages.get(this, "nothing_unique") );
			return;
		}

		int branch = UniqueItems.idOf( tier1.get( 0 ) ).branch;

		List<UniqueItemId> miss = UniqueItems.missing( hero, family, 1, branch );
		if (!miss.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (UniqueItemId id : miss) {
				if (sb.length() > 0) sb.append( "、" );
				sb.append( UniqueItems.nameOf( id ) );
			}
			GLog.w( Messages.get(this, "missing", sb.toString()) );
			return;
		}

		List<Item> consumed = UniqueItems.consume( hero, family, 1, branch );
		List<Item> granted  = UniqueItems.grant( hero, family, 2, branch );
		UniqueItems.preserve( consumed, granted );

		detach( hero.belongings.backpack );
		Catalog.countUse( getClass() );

		if (hero.sprite != null) {
			hero.sprite.emitter().burst( Speck.factory( Speck.CROWN), 12 );
			hero.sprite.operate( hero.pos );
		}
		hero.spend(Actor.TICK);
		hero.busy();

		Sample.INSTANCE.play( Assets.Sounds.MASTERY );
		GLog.p( Messages.get(this, "upgraded") );
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	/**
	 * 原版行为：把护甲改造成英雄护甲。
	 * SPSEXPD: 皇冠已改为专属物品的二次强化，本方法不再由 AC_WEAR 调用；
	 * 保留是为了 WndChooseAbility / RatKing 等旧调用点仍能编译（是否彻底移除待后续裁决）。
	 */
	public void upgradeArmor(Hero hero, Armor armor, ArmorAbility ability) {

		detach(hero.belongings.backpack);
		Catalog.countUse( getClass() );

		hero.sprite.emitter().burst( Speck.factory( Speck.CROWN), 12 );
		hero.spend(Actor.TICK);
		hero.busy();

		if (armor != null){

			if (ability instanceof Ratmogrify){
				GLog.p(Messages.get(this, "ratgraded"));
			} else {
				GLog.p(Messages.get(this, "upgraded"));
			}

			ClassArmor classArmor = ClassArmor.upgrade(hero, armor);
			if (hero.belongings.armor == armor) {

				hero.belongings.armor = classArmor;
				((HeroSprite) hero.sprite).updateArmor();
				classArmor.activate(hero);

			} else {

				armor.detach(hero.belongings.backpack);
				classArmor.collect(hero.belongings.backpack);

			}
		}

		hero.armorAbility = ability;
		Talent.initArmorTalents(hero);

		hero.sprite.operate( hero.pos );
		Sample.INSTANCE.play( Assets.Sounds.MASTERY );
	}

}
