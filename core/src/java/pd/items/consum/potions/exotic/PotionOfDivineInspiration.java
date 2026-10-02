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

public class PotionOfDivineInspiration extends ExoticPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfDivineInspiration.class)
			.t("name", "神意启发合剂")
			.t("no_more_points", "你无法再获得更多的额外天赋点了。")
			.t("select_tier", "选择一个天赋以获得两个额外点数。该天赋所在的层阶必须已被解锁。")
			.t("bonus", "天赋点+2！")
			.t("desc", "这股神圣的力量会化作液态，灌注进饮用者的身体，赋予其钟意的天赋两个额外天赋点。\n\n这种药剂对每一层天赋只能生效一次。");
	}



	
	{
		icon = ItemIconSheet.POTION_DIVINE;

		talentFactor = 2f;
	}

	protected static boolean identifiedByUse = false;

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

		boolean[] enabled = new boolean[5];
		enabled[1] = enabled[2] = enabled[3] = enabled[4] = true;

		DivineInspirationTracker tracker = hero.buff(DivineInspirationTracker.class);

		if (tracker != null){
			boolean allBoosted = true;
			for (int i = 1; i <= 4; i++){
				if (tracker.isBoosted(i)){
					enabled[i] = false;
				} else {
					allBoosted = false;
				}
			}

			if (allBoosted){
				GLog.w(Messages.get(this, "no_more_points"));
				return;
			}
		}

		GameScene.show(new WndOptions(
				new ItemSprite(this),
				Messages.titleCase(trueName()),
				Messages.get(PotionOfDivineInspiration.class, "select_tier"),
				Messages.titleCase(Messages.get(TalentsPane.class, "tier", 1)),
				Messages.titleCase(Messages.get(TalentsPane.class, "tier", 2)),
				Messages.titleCase(Messages.get(TalentsPane.class, "tier", 3)),
				Messages.titleCase(Messages.get(TalentsPane.class, "tier", 4))
		){
			@Override
			protected boolean enabled(int index) {
				return enabled[index+1];
			}

			@Override
			protected void onSelect(int index) {
				super.onSelect(index);

				if (index != -1){
					Buff.affect(curUser, DivineInspirationTracker.class).setBoosted(index+1);

					if (!identifiedByUse) {
						curItem.detach(curUser.belongings.backpack);
					}
					identifiedByUse = false;

					curUser.busy();
					curUser.sprite.operate(curUser.pos);

					curUser.spendAndNext(1f);

					boolean unspentTalents = false;
					for (int i = 1; i <= Dungeon.hero.talents.size(); i++){
						if (Dungeon.hero.talentPointsAvailable(i) > 0){
							unspentTalents = true;
							break;
						}
					}
					if (unspentTalents){
						StatusPane.talentBlink = 10f;
						WndHero.lastIdx = 1;
					}

					GameScene.showlevelUpStars();

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
			}

			@Override
			public void onBackPressed() {
				//window can be closed if potion is already IDed
				if (!identifiedByUse){
					super.onBackPressed();
				}
			}
		});

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
