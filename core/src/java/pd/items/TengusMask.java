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
import pd.items.equipment.armor.Armor;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 天狗的面具 —— 现在的作用是把英雄身上的「职业专属物品」整体强化一档，
 * 例如 破损纹章 / 纹章碎片 -> 完整纹章。
 *
 * 佩戴后自动强化背包与装备里的所有可强化物品；一件都强化不了时只提示、不消耗面具。
 * 原本的「选择职业专精」功能已按用户裁决移除（WndChooseSubclass 与 choose() 保留但不再被调用）。
 */
public class TengusMask extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TengusMask.class)
			.t("name", "天狗的面具")
			.t("ac_wear", "佩戴")
			.t("nothing", "你身上没有能被面具强化的专属物品。")
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
	 * SPSEXPD: 强化英雄的全部职业专属物品。返回实际强化成功的件数。
	 * 护甲上贴附的纹章会先摘下、换新后再贴回去，等级与刻印都原样保留。
	 */
	public int upgradeUniqueItems( Hero hero ) {
		if (hero == null || hero.belongings == null) return 0;

		int upgraded = 0;

		//护甲上贴附的纹章
		Armor armor = hero.belongings.armor;
		if (armor != null && isUpgradableUnique( armor.checkSeal() )) {
			BrokenSeal old = armor.detachSeal();
			Item replacement = upgradedCopy( old );
			if (replacement != null) {
				armor.affixSeal( (BrokenSeal)replacement );
				upgraded++;
			} else if (old != null) {
				//理论上不会发生；兜底把原物贴回去，避免凭空丢失
				armor.affixSeal( old );
			}
		}

		//背包里的独立物品
		pd.items.equipment.bags.Bag backpack = hero.belongings.backpack;
		for (Item item : new ArrayList<>(backpack.items)) {
			if (!isUpgradableUnique( item )) continue;
			Item replacement = upgradedCopy( item );
			if (replacement == null) continue;
			item.detachAll( backpack );
			if (!replacement.collect( backpack ) && Dungeon.level != null) {
				Dungeon.level.drop( replacement, hero.pos );
			}
			upgraded++;
		}

		if (upgraded <= 0) {
			//SPSEXPD: 没有可强化的目标就不消耗面具
			GLog.w( Messages.get(this, "nothing") );
			return 0;
		}

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
		GLog.p( Messages.get(this, "upgraded", upgraded) );
		return upgraded;
	}

	/** SPSEXPD: 该物品是否有对应的强化形态（完整纹章已是成品，不再强化）。 */
	public static boolean isUpgradableUnique( Item item ) {
		return item instanceof BrokenSeal && !(item instanceof SealComplete);
	}

	/** SPSEXPD: 造出强化形态，并把携带的等级与刻印原样搬过去；没有对应形态时返回 null。 */
	public static Item upgradedCopy( Item item ) {
		if (item instanceof BrokenSeal && !(item instanceof SealComplete)) {
			SealComplete result = new SealComplete();
			result.level( item.level() );
			result.setGlyph( ((BrokenSeal)item).getGlyph() );
			return result;
		}
		return null;
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
