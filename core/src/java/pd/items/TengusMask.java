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
import pd.Badges;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Preparation;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.effects.Speck;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndChooseSubclass;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;

import java.util.ArrayList;
import pd.messages.InlineText;

public class TengusMask extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TengusMask.class)
			.t("name", "天狗的面具")
			.t("ac_wear", "佩戴")
			.t("used", "面具瓦解了，它所蕴含的能量涌入了你的身体。")
			.t("desc", "天狗死时，这幅面具像褪皮般从他的脸上失落地滑下。你能感受它在波动着一股神妙的魔力，仿佛在极力劝你成为它的新主人。难道天狗戴上它时也是被其力量吸引？\n\n如果你有勇气将其戴上，面具的庞大力量会转嫁到你体内，供你为英雄_选择一种职业专精_。\n\n你会选择哪条道路？")
			.t("discover_hint", "你可从某种敌人的掉落物中获得该物品。");
	}

	
	private static final String AC_WEAR	= "WEAR";
	
	{
		stackable = false;
		image = ConsumUsefulProcessEnhanceDict.MASK_0;

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

		if (action.equals( AC_WEAR )) {
			
			curUser = hero;

			GameScene.show( new WndChooseSubclass( this, hero ) );
			
		}
	}
	
	@Override
	public boolean doPickUp(Hero hero, int pos) {
		Badges.validateMastery();
		return super.doPickUp( hero, pos );
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	public void choose( HeroSubClass way ) {
		
		detach( curUser.belongings.backpack );
		Catalog.countUse( getClass() );
		
		curUser.spend( Actor.TICK );
		curUser.busy();
		
		curUser.subClass = way;
		Talent.initSubclassTalents(curUser);

		if (way == HeroSubClass.ASSASSIN && curUser.invisible > 0){
			Buff.affect(curUser, Preparation.class);
		}
		
		curUser.sprite.operate( curUser.pos );
		Sample.INSTANCE.play( Assets.Sounds.MASTERY );
		
		Emitter e = curUser.sprite.centerEmitter();
		e.pos(e.x-2, e.y-6, 4, 4);
		e.start(Speck.factory(Speck.MASK), 0.05f, 20);
		GLog.p( Messages.get(this, "used"));
		
	}
}
