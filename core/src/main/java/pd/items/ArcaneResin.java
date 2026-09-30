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

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.effects.Speck;
import pd.items.bags.Bag;
import pd.items.bags.MagicalHolster;
import pd.items.wands.Wand;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import pd.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class ArcaneResin extends Item {

	{
		image = ItemSpriteSheet.ARCANE_RESIN;

		stackable = true;

		defaultAction = AC_APPLY;

		bones = true;
	}

	private static final String AC_APPLY = "APPLY";

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_APPLY );
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_APPLY)) {

			curUser = hero;
			GameScene.selectItem( itemSelector );

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
		return 30*quantity();
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(ArcaneResin.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return MagicalHolster.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Wand && item.isIdentified();
		}

		@Override
		public void onSelect( Item item ) {
			if (item != null && item instanceof Wand) {
				Wand w = (Wand)item;

				if (w.level() >= 3){
					GLog.w(Messages.get(ArcaneResin.class, "level_too_high"));
					return;
				}

				int resinToUse = w.level()+1;

				if (quantity() < resinToUse){
					GLog.w(Messages.get(ArcaneResin.class, "not_enough"));

				} else {

					Catalog.countUses(ArcaneResin.class, resinToUse);
					if (resinToUse < quantity()){
						quantity(quantity()-resinToUse);
					} else {
						detachAll(Dungeon.hero.belongings.backpack);
					}

					w.resinBonus++;
					w.curCharges++;
					w.updateLevel();
					Item.updateQuickslot();

					curUser.sprite.operate(curUser.pos);
					Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
					curUser.sprite.emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );

					curUser.spendAndNext(Actor.TICK);
					GLog.p(Messages.get(ArcaneResin.class, "apply"));
				}
			}
		}
	};

	public static class Recipe extends pd.items.Recipe {

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			return ingredients.size() == 1
					&& ingredients.get(0) instanceof Wand
					&& ingredients.get(0).cursedKnown
					&& !ingredients.get(0).cursed;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 5;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Item result = sampleOutput(ingredients);
			Wand w = (Wand)ingredients.get(0);

			if (!w.levelKnown){
				result.quantity(resinQuantity(w));
			}
			w.quantity(0);

			return result;
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			Wand w = (Wand)ingredients.get(0);

			if (w.levelKnown){
				return new ArcaneResin().quantity(resinQuantity(w));
			} else {
				return new ArcaneResin();
			}
		}

		private int resinQuantity(Wand w){
			int level = w.level() - w.resinBonus;
			int quantity = 2*(level+1);

			if (Dungeon.hero.heroClass != HeroClass.MAGE && Dungeon.hero.hasTalent(Talent.WAND_PRESERVATION)){
				quantity += Dungeon.hero.pointsInTalent(Talent.WAND_PRESERVATION);
			}
			return quantity;
		}
	}

}
