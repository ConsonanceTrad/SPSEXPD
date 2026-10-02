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

package pd.items.equipment.rings;


import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class RingOfMight extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RingOfMight.class)
			.t("name", "根骨之戒")
			.t("stats", "佩戴这枚戒指时，你的力量会增加_%1$d_，你的最大生命值会增加_%2$s%%_。")
			.t("typical_stats", "佩戴这枚戒指时，你的力量通常会提升_%1$d_点，并且最大生命值通常会增加_%2$s%%_。")
			.t("combined_stats", "你已装备的戒指正联结它们的力量，一共提高了你_%1$d_点力量与_%2$s%%_点最大生命值。")
			.t("upgrade_stat_name_1", "力量加成")
			.t("upgrade_stat_name_2", "生命上限加成")
			.t("desc", "这枚戒指会增强佩戴者的体质，使其拥有更强的力量与体格。被诅咒的戒指则会使人虚弱。");
	}




	{
		icon = ItemIconSheet.RING_MIGHT;
		buffClass = Might.class;
	}

	@Override
	public boolean doEquip(Hero hero) {
		if (super.doEquip(hero)){
			hero.updateHT( false );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			hero.updateHT( false );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Item upgrade() {
		super.upgrade();
		updateTargetHT();
		return this;
	}

	@Override
	public Item level(int value) {
		super.level(value);
		updateTargetHT();
		return this;
	}
	
	private void updateTargetHT(){
		if (buff != null && buff.target instanceof Hero){
			((Hero) buff.target).updateHT( false );
		}
	}
	
	public String statsInfo() {
		if (isIdentified()){
			String info = Messages.get(this, "stats",
					soloBonus(), Messages.decimalFormat("#.##", 100f * (Math.pow(1.035, soloBuffedBonus()) - 1f)));
			if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
				info += "\n\n" + Messages.get(this, "combined_stats",
						getBonus(Dungeon.hero, Might.class), Messages.decimalFormat("#.##", 100f * (Math.pow(1.035, combinedBuffedBonus(Dungeon.hero)) - 1f)));
			}
			return info;
		} else {
			return Messages.get(this, "typical_stats", 1, Messages.decimalFormat("#.##", 3.5f));
		}
	}

	@Override
	public String upgradeStat1(int level) {
		if (cursed && cursedKnown) level = Math.min(-1, level-3);
		return Integer.toString(level+1);
	}

	@Override
	public String upgradeStat2(int level) {
		if (cursed && cursedKnown) level = Math.min(-1, level-3);
		return Messages.decimalFormat("#.##", 100f * (Math.pow(1.035, level+1)-1f)) + "%";
	}

	@Override
	protected RingBuff buff( ) {
		return new Might();
	}
	
	public static int strengthBonus( Char target ){
		return getBonus( target, Might.class );
	}
	
	public static float HTMultiplier( Char target ){
		return (float)Math.pow(1.035, getBuffedBonus(target, Might.class));
	}

	public class Might extends RingBuff {
	}
}

