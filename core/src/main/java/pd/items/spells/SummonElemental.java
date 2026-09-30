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

package pd.items.spells;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.AllyBuff;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.huntress.SpiritHawk;
import pd.actors.mobs.Elemental;
import pd.effects.MagicMissile;
import pd.effects.particles.FlameParticle;
import pd.effects.particles.RainbowParticle;
import pd.effects.particles.ShaftParticle;
import pd.items.Item;
import pd.items.potions.PotionOfFrost;
import pd.items.potions.PotionOfLiquidFlame;
import pd.items.quest.Embers;
import pd.items.scrolls.ScrollOfRecharging;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.items.scrolls.ScrollOfTransmutation;
import pd.journal.Catalog;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.noosa.audio.Sample;
import render.utils.Bundle;
import render.utils.Random;
import render.utils.Reflection;

import java.util.ArrayList;

public class SummonElemental extends Spell {

	public static final String AC_IMBUE = "IMBUE";

	{
		image = ItemSpriteSheet.SUMMON_ELE;

		talentChance = 1/(float)Recipe.OUT_QUANTITY;
	}

	private Class<? extends Elemental> summonClass = Elemental.AllyNewBornElemental.class;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_IMBUE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_IMBUE)){
			GameScene.selectItem(selector);
		}
	}

	@Override
	protected void onCast(Hero hero) {

		ArrayList<Integer> spawnPoints = new ArrayList<>();

		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = hero.pos + PathFinder.NEIGHBOURS8[i];
			if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
				spawnPoints.add( p );
			}
		}

		if (!spawnPoints.isEmpty()){

			for (Char ch : Actor.chars()){
				if (ch instanceof Elemental && ch.buff(InvisAlly.class) != null){
					ScrollOfTeleportation.appear( ch, Random.element(spawnPoints) );
					((Elemental) ch).state = ((Elemental) ch).HUNTING;
					curUser.spendAndNext(Actor.TICK);
					return;
				}
			}

			Elemental elemental = Reflection.newInstance(summonClass);
			GameScene.add( elemental );
			Buff.affect(elemental, InvisAlly.class);
			elemental.setSummonedALly();
			elemental.HP = elemental.HT;
			ScrollOfTeleportation.appear( elemental, Random.element(spawnPoints) );
			Invisibility.dispel(curUser);
			curUser.sprite.operate(curUser.pos);
			curUser.spendAndNext(Actor.TICK);

			detach(Dungeon.hero.belongings.backpack);
			Catalog.countUse(getClass());
			if (Random.Float() < talentChance){
				Talent.onScrollUsed(curUser, curUser.pos, talentFactor, getClass());
			}

		} else {
			GLog.w(Messages.get(SpiritHawk.class, "no_space"));
		}

	}

	@Override
	public ItemSprite.Glowing glowing() {
		if (summonClass == Elemental.FireElemental.class)   return new ItemSprite.Glowing(0xFFBB33);
		if (summonClass == Elemental.FrostElemental.class)  return new ItemSprite.Glowing(0x8EE3FF);
		if (summonClass == Elemental.ShockElemental.class)  return new ItemSprite.Glowing(0xFFFF85);
		if (summonClass == Elemental.ChaosElemental.class)  return new ItemSprite.Glowing(0xE3E3E3, 0.5f);
		return super.glowing();
	}

	@Override
	public String desc() {
		String desc = super.desc();

		desc += "\n\n";

		if (summonClass == Elemental.AllyNewBornElemental.class)    desc += Messages.get(this, "desc_newborn");
		if (summonClass == Elemental.FireElemental.class)           desc += Messages.get(this, "desc_fire");
		if (summonClass == Elemental.FrostElemental.class)          desc += Messages.get(this, "desc_frost");
		if (summonClass == Elemental.ShockElemental.class)          desc += Messages.get(this, "desc_shock");
		if (summonClass == Elemental.ChaosElemental.class)          desc += Messages.get(this, "desc_chaos");

		return desc;
	}

	private static final String SUMMON_CLASS = "summon_class";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SUMMON_CLASS, summonClass);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(SUMMON_CLASS)) summonClass = bundle.getClass(SUMMON_CLASS);

		if (summonClass == Elemental.AllyNewBornElemental.class)    image = ItemSpriteSheet.SUMMON_ELE;
		if (summonClass == Elemental.FireElemental.class)           image = ItemSpriteSheet.SUMMON_ELE_FIRE;
		if (summonClass == Elemental.FrostElemental.class)          image = ItemSpriteSheet.SUMMON_ELE_FROST;
		if (summonClass == Elemental.ShockElemental.class)          image = ItemSpriteSheet.SUMMON_ELE_SHOCK;
		if (summonClass == Elemental.ChaosElemental.class)          image = ItemSpriteSheet.SUMMON_ELE_CHAOS;
	}

	public WndBag.ItemSelector selector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(SummonElemental.class, "imbue_prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item.isIdentified() && (item instanceof PotionOfLiquidFlame
					|| item instanceof PotionOfFrost
					|| item instanceof ScrollOfRecharging
					|| item instanceof ScrollOfTransmutation);
		}

		@Override
		public void onSelect(Item item) {

			if (item == null){
				return;
			}

			item.detach(Dungeon.hero.belongings.backpack);
			if (item instanceof PotionOfLiquidFlame) {
				Sample.INSTANCE.play(Assets.Sounds.BURNING);
				curUser.sprite.emitter().burst( FlameParticle.FACTORY, 12 );
				summonClass = Elemental.FireElemental.class;
				image = ItemSpriteSheet.SUMMON_ELE_FIRE;

			} else if (item instanceof PotionOfFrost){
				Sample.INSTANCE.play(Assets.Sounds.SHATTER);
				curUser.sprite.emitter().burst( MagicMissile.MagicParticle.FACTORY, 12 );
				summonClass = Elemental.FrostElemental.class;
				image = ItemSpriteSheet.SUMMON_ELE_FROST;

			} else if (item instanceof ScrollOfRecharging){
				Sample.INSTANCE.play(Assets.Sounds.ZAP);
				curUser.sprite.emitter().burst( ShaftParticle.FACTORY, 12 );
				summonClass = Elemental.ShockElemental.class;
				image = ItemSpriteSheet.SUMMON_ELE_SHOCK;

			} else if (item instanceof ScrollOfTransmutation){
				Sample.INSTANCE.play(Assets.Sounds.READ);
				curUser.sprite.emitter().burst( RainbowParticle.BURST, 12 );
				summonClass = Elemental.ChaosElemental.class;
				image = ItemSpriteSheet.SUMMON_ELE_CHAOS;
			}

			curUser.sprite.operate(curUser.pos);

			updateQuickslot();
		}
	};

	public static class InvisAlly extends AllyBuff{

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.HEARTS);
			else    target.sprite.remove(CharSprite.State.HEARTS);
		}

	}

	public static class Recipe extends pd.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 6;

		{
			inputs =  new Class[]{Embers.class};
			inQuantity = new int[]{1};

			cost = 10;

			output = SummonElemental.class;
			outQuantity = OUT_QUANTITY;
		}

	}
}
