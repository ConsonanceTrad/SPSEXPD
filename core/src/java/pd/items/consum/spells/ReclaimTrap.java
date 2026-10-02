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

package pd.items.consum.spells;

import pd.atlas.items.ConsumScrollAmuletCrystalDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.quest.MetalShard;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.journal.Bestiary;
import pd.journal.Catalog;
import pd.levels.traps.Trap;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import pd.messages.InlineText;

public class ReclaimTrap extends TargetedSpell {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ReclaimTrap.class)
			.t("name", "陷阱晶柱")
			.t("no_trap", "这里没有陷阱。")
			.t("desc_trap", "施放晶柱将在目标位置产生一次_%s_的效果。")
			.t("desc", "这个晶柱蕴含着DM-300残余的机械能量。当对一个未触发的陷阱使用时，陷阱的力量会附着在你身上，使你能再次使用晶柱以释放这股力量并在你指定的任何位置触发此陷阱的效果。\n\n不过，有的陷阱并不能在任何位置都生效，而且晶柱一次只能储存一个陷阱。");
	}

	
	{
		image = ConsumScrollAmuletCrystalDict.RECLAIM_TRAP_0;

		talentChance = 1/(float)Recipe.OUT_QUANTITY;
	}

	@Override
	protected void affectTarget(Ballistica bolt, Hero hero) {
		Class<?extends Trap> storedTrap = null;

		if (hero.buff(ReclaimedTrap.class) != null){
			storedTrap = hero.buff(ReclaimedTrap.class).trap;
			hero.buff(ReclaimedTrap.class).detach();
		}

		if (storedTrap == null) {
			Trap t = Dungeon.level.traps.get(bolt.collisionPos);
			if (t != null && t.active && t.visible) {
				t.disarm(); //even disarms traps that normally wouldn't be
				
				Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
				ScrollOfRecharging.charge(hero);
				Buff.affect(hero, ReclaimedTrap.class).trap = t.getClass();
				Bestiary.setSeen(t.getClass());
				
			} else {
				GLog.w(Messages.get(this, "no_trap"));
			}

			//spell is not consumed, so doesn't count as a full use
			Invisibility.dispel();
			curUser.spendAndNext( timeToCast() );

		} else {
			
			Trap t = Reflection.newInstance(storedTrap);
			
			t.pos = bolt.collisionPos;
			t.reclaimed = true;
			Bestiary.countEncounter(t.getClass());
			t.activate();

			onSpellused();
			
		}
	}
	
	@Override
	public String desc() {
		String desc = super.desc();
		if (Dungeon.hero != null && Dungeon.hero.belongings.contains(this) && Dungeon.hero.buff(ReclaimedTrap.class) != null){
			desc += "\n\n" + Messages.get(this, "desc_trap", Messages.get(Dungeon.hero.buff(ReclaimedTrap.class).trap, "name"));
		}
		return desc;
	}
	
	private static final ItemSprite.Glowing[] COLORS = new ItemSprite.Glowing[]{
			new ItemSprite.Glowing( 0xFF0000 ),
			new ItemSprite.Glowing( 0xFF8000 ),
			new ItemSprite.Glowing( 0xFFFF00 ),
			new ItemSprite.Glowing( 0x00FF00 ),
			new ItemSprite.Glowing( 0x00FFFF ),
			new ItemSprite.Glowing( 0x8000FF ),
			new ItemSprite.Glowing( 0xFFFFFF ),
			new ItemSprite.Glowing( 0x808080 ),
			new ItemSprite.Glowing( 0x000000 )
	};
	
	@Override
	public ItemSprite.Glowing glowing() {
		if (Dungeon.hero != null && Dungeon.hero.belongings.contains(this) && Dungeon.hero.buff(ReclaimedTrap.class) != null){
			return COLORS[Reflection.newInstance(Dungeon.hero.buff(ReclaimedTrap.class).trap).color];
		}
		return null;
	}
	
	@Override
	public int value() {
		return (int)(60 * (quantity/(float)Recipe.OUT_QUANTITY));
	}

	@Override
	public int energyVal() {
		return (int)(12 * (quantity/(float)Recipe.OUT_QUANTITY));
	}
	
	public static class Recipe extends pd.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 5;
		
		{
			inputs =  new Class[]{ScrollOfMagicMapping.class, MetalShard.class};
			inQuantity = new int[]{1, 1};
			
			cost = 8;
			
			output = ReclaimTrap.class;
			outQuantity = OUT_QUANTITY;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Catalog.countUse(MetalShard.class);
			return super.brew(ingredients);
		}
	}

	public static class ReclaimedTrap extends Buff {

		{
			revivePersists = true;
		}

		private Class<?extends Trap> trap;

		private static final String TRAP = "trap";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TRAP, trap);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			trap = bundle.getClass(TRAP);
		}
	}
	
}
