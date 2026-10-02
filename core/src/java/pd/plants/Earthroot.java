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

package pd.plants;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.effects.CellEmitter;
import pd.effects.particles.EarthParticle;
import pd.items.equipment.weapon.missiles.arrows.RootFruit;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Earthroot extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Earthroot.class)
			.t("name", "地缚根")
			.t("desc", "碰到地缚根后，它的根系会在踩踏者周边形成某种无法移动的天然护甲。")
			.t("warden_desc", "_守望者_能将这种护甲转化为可移动的树肤护甲。")
			.t("$seed.name", "地缚根之种")
			.t("$armor.name", "植被护甲")
			.t("$armor.desc", "这是一种由层层树皮和藤蔓盘结而成的，不可移动的天然护甲。\n\n每当你受到物理攻击时，植被护甲都会吸收%d点伤害，直到其耐久被耗尽并瓦解。\n\n该护甲是不可移动的，所以一旦你离开原位护甲便会被破坏，消失不见。\n\n剩余护甲量：%d点")
			.t("$magicplantarmor.name", "植被护甲")
			.t("$magicplantarmor.desc", "一种不可移动的天然护甲正在保护你。这个护甲由层层树皮和藤蔓盘结而成，紧密地缠绕在你的身体周围。\n\n这种护甲能够吸收你受到的50%%物理伤害，直到其耗尽耐久而瓦解。护甲是不可移动的，这意味着如果你想重新移动的话必须将其彻底破坏。\n\n护盾量剩余：%d点。")
			.t("$exearthroot.name", "地缚根果丛")
			.t("$exearthroot.desc", "生长缠绕果的果丛。");
	}



	
	{
		image = 5;
		seedClass = Seed.class;
	}
	
	@Override
	public void activate( Char ch ) {
		if (Dungeon.level.heaps.get(pos) != null) Dungeon.level.heaps.get(pos).earthhit();

		if (ch != null){
			if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
				Barkskin.conditionallyAppend(Dungeon.hero, Dungeon.hero.lvl + 5, 5);
			} else {
				Buff.affect(ch, Armor.class).level(ch.HT);
			}
		}
		
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.bottom( pos ).start( EarthParticle.FACTORY, 0.05f, 8 );
			PixelScene.shake( 1, 0.4f );
		}
	}
	
	public static class Seed extends Plant.Seed {
		{
			image = ConsumPotionSeedSeedDict.SEED_EARTHROOT_0;

			plantClass = Earthroot.class;
			explantClass = ExEarthroot.class;

			bones = true;
		}
	}

	public static class ExEarthroot extends SpsFruitBush {
		{ image = 5; harvestCount = 3; harvestClass = RootFruit.class; }
	}
	
	public static class Armor extends Buff {
		
		private static final float STEP = 1f;
		
		private int pos;
		private int level;

		{
			type = buffType.POSITIVE;
			announced = true;
		}
		
		@Override
		public boolean act() {
			if (target.pos != pos) {
				detach();
			}
			spend( STEP );
			return true;
		}
		
		private static int blocking(){
			return (Dungeon.scalingDepth() + 5)/2;
		}
		
		public int absorb( int damage ) {
			if (pos != target.pos){
				detach();
				return damage;
			}
			int block = Math.min( damage, blocking());
			if (level <= block) {
				detach();
				return damage - block;
			} else {
				level -= block;
				return damage - block;
			}
		}
		
		public void level( int value ) {
			if (target != null) {
				if (level < value) {
					level = value;
				}
				pos = target.pos;
			}
		}
		
		@Override
		public int icon() {
			return BuffIndicator.ARMOR;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (target.HT - level) / (float) target.HT);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(level);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", blocking(), level);
		}

		private static final String POS		= "pos";
		private static final String LEVEL	= "level";
		
		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( POS, pos );
			bundle.put( LEVEL, level );
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			pos = bundle.getInt( POS );
			level = bundle.getInt( LEVEL );
		}
	}

	/** The separate 50% durability armor used by SPS-PD's Sandals of Nature. */
	public static class MagicPlantArmor extends Buff {

		private static final float STEP = 1f;
		private int pos;
		private int level;

		@Override
		public boolean attachTo(Char target) {
			pos = target.pos;
			return super.attachTo(target);
		}

		@Override
		public boolean act() {
			if (target.pos != pos) detach();
			spend(STEP);
			return true;
		}

		public int absorb(int damage) {
			int absorbed = damage - damage / 2;
			if (level <= absorbed) {
				int remainingDamage = damage - level;
				level = 0;
				detach();
				return remainingDamage;
			}
			level -= absorbed;
			return damage / 2;
		}

		public void level(int value) {
			if (level < value) level = value;
		}

		public int level() {
			return level;
		}

		@Override
		public int icon() {
			return BuffIndicator.ARMOR;
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(level);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", level);
		}

		private static final String POS = "pos";
		private static final String LEVEL = "level";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
			bundle.put(LEVEL, level);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
			level = bundle.getInt(LEVEL);
		}
	}
}
