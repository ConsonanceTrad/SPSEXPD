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
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Preparation;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.effects.Speck;
import pd.items.misc.PotionOfMage;
import pd.items.unique.UniqueFamily;
import pd.items.unique.UniqueItemId;
import pd.items.unique.UniqueItems;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;

import java.util.ArrayList;
import java.util.List;
import pd.messages.InlineText;

/**
 * SPSEXPD: 天狗的面具 —— 一档强化：把英雄开局的两件「职业专属物品」整体换成一档新物品。
 *
 * <p>无论选择哪条路线，族内全部初始物品都会被消耗；缺任何一件都会拒绝执行
 * （物品可以丢弃后捡回，所以「身上没有」不能当作永久丢失）。有两条路线时会弹选择框。</p>
 *
 * 原本的「选择职业专精」功能已按用户裁决移除（WndChooseSubclass 与 choose() 保留但不再被调用）。
 */
public class TengusMask extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TengusMask.class)
			.t("name", "天狗的面具")
			.t("ac_wear", "佩戴")
			.t("nothing", "你身上没有能被面具强化的专属物品。")
			.t("missing", "你身上缺少 %s，无法强化。")
			.t("choose_route", "选择一条强化路线。")
			.t("route", "强化为 %s")
			.t("used", "面具瓦解了，它所蕴含的能量涌入了你的身体。")
			.t("upgraded", "面具强化了你的 %1$d 件专属物品。")
			.t("desc", "天狗死时，这幅面具像褪皮般从他的脸上失落地滑下。你能感受它在波动着一股神妙的魔力，仿佛在极力劝你成为它的新主人。难道天狗戴上它时也是被其力量吸引？\n\n如果你有勇气将其戴上，面具的庞大力量会转嫁到你体内，并_重塑你的专属物品_——破损之物会被补全、残缺的符咒会重新明亮起来。\n\n你会成为它的新主人吗？")
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
			upgradeUniqueItems( hero );
		}
	}

	/**
	 * SPSEXPD: 一档强化 —— 把英雄开局的两件专属物品整体换成一档新物品。
	 * 族内物品缺任何一件都拒绝执行（不消耗面具）。
	 */
	public void upgradeUniqueItems( Hero hero ) {
		if (hero == null || hero.belongings == null) return;

		UniqueFamily family = UniqueItems.familyOf( hero );
		if (family == null) {
			GLog.w( Messages.get(this, "nothing") );
			return;
		}

		List<UniqueItemId> miss = UniqueItems.missing( hero, family, 0, 0 );
		if (!miss.isEmpty()) {
			GLog.w( Messages.get(this, "missing", names( miss )) );
			return;
		}

		List<Integer> routes = UniqueItems.branches( family, 1 );
		if (routes.size() <= 1) {
			applyTier1( hero, family, routes.isEmpty() ? 0 : routes.get( 0 ) );
			return;
		}

		final List<Integer> branches = routes;
		String[] opts = new String[routes.size()];
		for (int i = 0; i < routes.size(); i++) {
			opts[i] = Messages.get(this, "route", UniqueItems.routeLabel( family, 1, branches.get( i ) ));
		}
		GameScene.show( new WndOptions( new ItemSprite(this), Messages.titleCase( name() ),
				Messages.get(this, "choose_route"), opts ) {
			@Override
			protected void onSelect( int index ) {
				applyTier1( hero, family, branches.get( index ) );
			}
		} );
	}

	/** SPSEXPD: 执行一档转换（消耗族内全部初始物品 → 发放所选路线的新物品）。 */
	private void applyTier1( Hero hero, UniqueFamily family, int branch ) {
		List<Item> consumed = UniqueItems.consume( hero, family, 0, 0 );
		//SPSEXPD: 法师的奇迹烧瓶一并回收——功能已被轴芯取代，直接移除不再返还
		if (family == UniqueFamily.MAGE) {
			UniqueItems.discard( hero, PotionOfMage.class );
		}
		List<Item> granted  = UniqueItems.grant( hero, family, 1, branch );
		UniqueItems.preserve( consumed, granted );

		detach( hero.belongings.backpack );
		Catalog.countUse( getClass() );

		hero.spend( Actor.TICK );
		hero.busy();

		if (hero.sprite != null) {
			hero.sprite.operate( hero.pos );
			Emitter e = hero.sprite.centerEmitter();
			e.pos(e.x-2, e.y-6, 4, 4);
			e.start(Speck.factory(Speck.MASK), 0.05f, 20);
		}
		Sample.INSTANCE.play( Assets.Sounds.MASTERY );

		GLog.p( Messages.get(this, "used") );
		GLog.p( Messages.get(this, "upgraded", granted.size()) );
	}

	private static String names( List<UniqueItemId> ids ) {
		StringBuilder sb = new StringBuilder();
		for (UniqueItemId id : ids) {
			if (sb.length() > 0) sb.append( "、" );
			sb.append( UniqueItems.nameOf( id ) );
		}
		return sb.toString();
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

	/**
	 * 原版行为：挑选职业专精。
	 * SPSEXPD: 面具已改为强化专属物品，本方法不再被调用；保留是为了 WndChooseSubclass 仍能编译。
	 */
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
