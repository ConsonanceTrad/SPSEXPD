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

import pd.atlas.items.SpecificTaskDict;

import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.scenes.AmuletScene;
import render.noosa.Game;

import java.io.IOException;
import java.util.ArrayList;
import pd.messages.InlineText;

public class Amulet extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Amulet.class)
			.t("name", "Yendor护符")
			.t("ac_end", "结束游戏")
			.t("desc", "Yendor护符是人类与矮人所知的最强大的神器。其上镶嵌的晶石奇光辉映、气象非常，蕴含着不可思议的神奇力量。")
			.t("desc_origins", "护符的起源与种种过去无人知晓。据历史记载，矮人国王曾夸口说他在矮人文明与外界切断一切联系之前不久就发现了这件神器。那么，他是如何找到的？古神又是怎样从他那里夺走了护符？也许这些问题不需要现在解答，真正重要的是：护符正属于你！")
			.t("desc_ascent", "护符的起源与种种过去无人知晓，但显然它已在古神的力量下受到了极大的侵蚀。你在地牢中踏经的每一寸土地似乎都已在古神意志的掌控之下，面前的敌人也变得众多强势、更甚以往！你也无力使用或抛下护符了——此时它与被诅咒已几无差别。")
			.t("ascent_title", "踏返登临")
			.t("ascent_desc", "你开始感受到古神强大可怖的力量自护符中泛溢而出。凭凡人的区区肉身自这地牢之底向上攀登至地面将远比你想象中的更难！\n\n如果你继续在持有护符的情况下向上返回，地牢将会变得更加险恶重重。跨层传送将会被抑制，而击杀沿途敌人返回地面则将成为你赢得这局游戏的唯一方式！\n\n如果你想要在不开始护符挑战的情况下返回上层，你可以把护符暂时留在这里，也可以选择在这里直接用护符以正常结束游戏。")
			.t("ascent_yes", "继续前进！")
			.t("ascent_no", "稍等片刻")
			.t("discover_hint", "你可在地牢底层找到该物品...");
	}

	
	private static final String AC_END = "END";
	
	{
		image = SpecificTaskDict.AMULET_0;
		
		unique = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (hero.buff(AscensionChallenge.class) != null){
			actions.clear();
		} else {
			actions.add(AC_END);
		}
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_END)) {
			showAmuletScene( false );
		}
	}
	
	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (super.doPickUp( hero, pos )) {
			
			if (!Statistics.amuletObtained) {
				Statistics.amuletObtained = true;
				hero.spend(-hero.cooldown());

				//delay with an actor here so pickup behaviour can fully process.
				Actor.add(new Actor(){

					{
						actPriority = VFX_PRIO;
					}

					@Override
					protected boolean act() {
						Actor.remove(this);
						showAmuletScene( true );
						return false;
					}
				});
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	private void showAmuletScene( boolean showText ) {
		AmuletScene.noText = !showText;
		Game.switchScene( AmuletScene.class, new Game.SceneChangeCallback() {
			@Override
			public void beforeCreate() {

			}

			@Override
			public void afterCreate() {
				Badges.validateVictory();
				Badges.validateChampion(Challenges.activeChallenges());
				try {
					Dungeon.saveAll();
					Badges.saveGlobal();
				} catch (IOException e) {
					ShatteredPixelDungeon.reportException(e);
				}
			}
		});
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public String desc() {
		String desc = super.desc();

		if (Dungeon.hero == null || Dungeon.hero.buff(AscensionChallenge.class) == null){
			desc += "\n\n" + Messages.get(this, "desc_origins");
		} else {
			desc += "\n\n" + Messages.get(this, "desc_ascent");
		}

		return desc;
	}
}
