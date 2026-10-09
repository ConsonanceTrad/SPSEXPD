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
import pd.scenes.GameScene;
import pd.effects.Speck;
import pd.items.consum.potions.elixirs.WishPotion;
import pd.items.consum.potions.wish.SimplifiedWish;
import pd.utils.GLog;
import pd.windows.WndTextInput;
import render.noosa.Game;
import render.utils.serialize.Bundle;

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
			.t("wish", "许愿")
			.t("wish_title", "护符许愿")
			.t("wish_body", "写下你要许下的愿望——写出一件物品的名字即可，护符会直接把它交给你。\n\n护符还能为你实现 %d 个愿望，用尽之后它将化作粉尘消散。")
			.t("wish_confirm", "许愿")
			.t("wish_cancel", "放弃")
			.t("wish_granted", "护符实现了你的愿望：%s")
			.t("wish_no_match", "护符听不懂这个愿望——没有找到对应的物品。这次许愿未被消耗。")
			.t("wish_failed", "护符的力量没能凝成实物。这次许愿未被消耗。")
			.t("wish_left", "剩余许愿次数：%d")
			.t("wish_dust", "护符耗尽了最后一丝力量，在你手中化作一捧粉尘消散了……")
			.t("discover_hint", "你可在地牢底层找到该物品...");
	}



	
	private static final String AC_END = "END";
	private static final String AC_WISH = "WISH";

	/** SPSEXPD: 真护符可以实现的许愿次数上限。 */
	public static final int MAX_WISHES = 21;
	private static final String WISH_USES = "wish_uses";
	/** SPSEXPD: 剩余许愿次数（旧档没有该字段时按上限补齐）。 */
	private int wishUses = MAX_WISHES;
	
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
			//SPSEXPD: 真护符可以许愿（简化判定），也可以直接结束游戏
			actions.add(AC_WISH);
			actions.add(AC_END);
		}
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_END)) {
			showAmuletScene( false );
		} else if (action.equals(AC_WISH)) {
			promptWish( hero );
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
	
	/** SPSEXPD: 简化许愿入口——文本输入窗（不做幸运/描述评分）。 */
	private void promptWish( final Hero hero ) {
		if (hero == null || wishUses <= 0) return;
		GameScene.show(new WndTextInput(
				Messages.get(Amulet.class, "wish_title"),
				Messages.get(Amulet.class, "wish_body", wishUses),
				"",
				WishPotion.MAX_WISH_LENGTH,
				false,
				Messages.get(Amulet.class, "wish_confirm"),
				Messages.get(Amulet.class, "wish_cancel")) {
			@Override
			public void onSelect(boolean positive, String text) {
				if (positive) wishFor(hero, text);
			}
		});
	}

	/** SPSEXPD: 命中即给物品并扣一次许愿；用尽后护符化作粉尘。 */
	private void wishFor( Hero hero, String text ) {
		SimplifiedWish.Outcome outcome = SimplifiedWish.grant(hero, text);
		if (outcome == SimplifiedWish.Outcome.GRANTED) {
			wishUses--;
			GLog.p(Messages.get(Amulet.class, "wish_granted", text == null ? "" : text.trim()));
			if (wishUses <= 0) dissolve(hero);
		} else if (outcome == SimplifiedWish.Outcome.NO_MATCH) {
			GLog.w(Messages.get(Amulet.class, "wish_no_match"));
		} else {
			GLog.w(Messages.get(Amulet.class, "wish_failed"));
		}
	}

	/** SPSEXPD: 许愿用尽——护符散去。 */
	private void dissolve( Hero hero ) {
		GLog.p(Messages.get(Amulet.class, "wish_dust"));
		if (hero.sprite != null) {
			hero.sprite.emitter().start(Speck.factory(Speck.DUST), 0.2f, 12);
		}
		if (hero.belongings != null) {
			detach(hero.belongings.backpack);
		}
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}

	/** SPSEXPD: 物品格右下角显示剩余许愿次数。 */
	@Override
	public String status() {
		return Integer.toString(Math.max(0, wishUses));
	}

	@Override
	public String desc() {
		String desc = super.desc();

		if (Dungeon.hero == null || Dungeon.hero.buff(AscensionChallenge.class) == null){
			desc += "\n\n" + Messages.get(this, "desc_origins");
		} else {
			desc += "\n\n" + Messages.get(this, "desc_ascent");
		}

		desc += "\n\n" + Messages.get(this, "wish_left", Math.max(0, wishUses));

		return desc;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( WISH_USES, wishUses );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		wishUses = bundle.contains( WISH_USES ) ? bundle.getInt( WISH_USES ) : MAX_WISHES;
	}
}
