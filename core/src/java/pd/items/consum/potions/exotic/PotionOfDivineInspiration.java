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

package pd.items.consum.potions.exotic;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.Flare;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.sprites.ItemIconSheet;
import pd.ui.StatusPane;
import pd.ui.TalentsPane;
import pd.utils.GLog;
import pd.windows.WndHero;
import pd.windows.WndOptions;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/**
 * SPSEXPD: 神意启发合剂（原版给「天赋点」，天赋体系已停用）——
 * 效果改为：每次饮用提供 3 次特质候选的重随机会，可反复饮用。
 */
public class PotionOfDivineInspiration extends ExoticPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfDivineInspiration.class)
			.t("name", "神意启发合剂")
			.t("no_more_points", "你无法再获得更多的额外天赋点了。")
			.t("select_tier", "选择一个天赋以获得两个额外点数。该天赋所在的层阶必须已被解锁。")
			.t("bonus", "重随机会 +3！")
			.t("desc", "这股神圣的力量会化作液态，灌注进饮用者的身体，让他在选择特质时获得 3 次额外的重随机会。\n\n这种药剂可以反复饮用，每次都会提供 3 次重随机会。");
	}

	/** 每次饮用提供的重随机会次数 */
	public static final int REROLL_BONUS = 3;

	{
		icon = ItemIconSheet.POTION_DIVINE;

		talentFactor = 2f;
	}

	protected static boolean identifiedByUse = false;

	/** 给英雄增加重随机会（UI 与无头校验共用） */
	public static void grantRerolls(Hero hero) {
		if (hero == null) return;
		hero.perkRerolls += REROLL_BONUS;
	}

	@Override
	//need to override drink so that time isn't spent right away
	protected void drink(final Hero hero) {

		if (!isKnown()) {
			identify();
			curItem = detach( hero.belongings.backpack );
			identifiedByUse = true;
		} else {
			identifiedByUse = false;
		}

		//SPSXPD: 效果改为「增加特质重随机会」（原版是选一个天赋阶 +2 天赋点）
		grantRerolls(hero);

		if (!identifiedByUse) {
			curItem.detach( curUser.belongings.backpack );
		}
		identifiedByUse = false;

		curUser.busy();
		curUser.sprite.operate(curUser.pos);
		curUser.spendAndNext(1f);

		Sample.INSTANCE.play( Assets.Sounds.DRINK );
		Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.3f, 0.7f, 1.2f);
		Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.6f, 0.7f, 1.2f);
		new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
		GLog.p(Messages.get(PotionOfDivineInspiration.class, "bonus"));

		if (!anonymous) {
			Catalog.countUse(PotionOfDivineInspiration.class);
			if (Random.Float() < talentChance) {
				Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
			}
		}
	}

	public static class DivineInspirationTracker extends Buff {

		{
			type = buffType.POSITIVE;
			revivePersists = true;
		}

		private boolean[] boostedTiers = new boolean[5];

		private static final String BOOSTED_TIERS = "boosted_tiers";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(BOOSTED_TIERS, boostedTiers);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			boostedTiers = bundle.getBooleanArray(BOOSTED_TIERS);
		}

		public void setBoosted( int tier ){
			boostedTiers[tier] = true;
		}

		public boolean isBoosted( int tier ){
			return boostedTiers[tier];
		}

	}
	
}
