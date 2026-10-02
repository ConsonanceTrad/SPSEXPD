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
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.HeroSprite;
import pd.utils.GLog;
import pd.windows.WndChooseAbility;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;

public class KingsCrown extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(KingsCrown.class)
			.t("name", "矮人国王的皇冠")
			.t("ac_wear", "加冕")
			.t("naked", "非常遗憾，皇冠的魔力无法强化你的内衣。")
			.t("upgraded", "皇冠熔为曜日般的金光，而你的护甲在照耀下开始逐渐变形！")
			.t("ratgraded", "鼠王挥爪便召出了万丈光芒，而你的护甲在照耀下开始逐渐变形！")
			.t("desc", "末代矮人国王的皇冠，至强的魔法能量以光芒的形式从中辐射而出。\n\n如果你有决心将其戴上，皇冠的魔力会涌入你正在装备的护甲，将其改造为_拥有特殊技能的独特英雄护甲_。新的护甲会保留原护甲的所有属性与刻印。")
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
			if (hero.belongings.armor() != null){
				GameScene.show( new WndChooseAbility(this, hero.belongings.armor(), hero));
			} else {
				GLog.w( Messages.get(this, "naked"));
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
