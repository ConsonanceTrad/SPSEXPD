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

package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.ShieldBuff;
import pd.actors.buffs.Terror;
import pd.effects.FloatingText;
import pd.effects.SpellSprite;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Item;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.missiles.meleethrow.Tamahawk;
import pd.levels.features.Chasm;
import pd.messages.Messages;
import pd.sprites.BruteSprite;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Brute extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Brute.class)
			.t("name", "豺狼暴徒")
			.t("enraged", "激怒")
			.t("def_verb", "格挡")
			.t("desc", "暴徒是体型最大，力量最强且生命力最坚韧的一种豺狼人。受到致命伤时，他们会狂暴化，获得暂时的护盾和极高的伤害加成。")
			.t("$bruterage.name", "豺狼狂暴")
			.t("$bruterage.desc", "这个豺狼暴徒已经濒临死亡，但它想拉你作为陪葬品！\n\n时间的推移和外界的伤害都会消磨护盾，盾破便是暴徒的丧命之时。然而，一定要小心，在这一阶段暴徒会造成巨额伤害！\n\n剩余的护盾：%d");
	}



	@Override public Item SupercreateLoot() { return new Tamahawk(); }

	private static final String ENRAGED = "enraged";
	private boolean enraged;
	
	{
		spriteClass = BruteSprite.class;
		
		HP = HT = 120 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 2);
		defenseSkill = 10 + legacyDepthAdjustment(0);
		
		EXP = 8;
		maxLvl = 25;
		
		loot = Gold.class;
		lootChance = 0.5f;

		properties.add(Property.ORC);
	}

	public Brute() {
		if (usesLegacyBehavior()) {
			weaknesses.add(Wand.class);
			immunities.add(Terror.class);
		}
	}

	protected boolean usesLegacyBehavior() {
		return getClass() == Brute.class;
	}
	
	protected boolean hasRaged = false;
	
	@Override
	public int damageRoll() {
		if (usesLegacyBehavior()) {
			return enraged
					? Random.NormalIntRange(25 + legacyDepthAdjustment(0), 40 + legacyDepthAdjustment(0))
					: Random.NormalIntRange(5 + legacyDepthAdjustment(0), 25 + legacyDepthAdjustment(0));
		}
		return buff(BruteRage.class) != null ?
				Random.NormalIntRange( 15, 40 ) :
				Random.NormalIntRange( 5, 25 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return usesLegacyBehavior() ? 10 + legacyDepthAdjustment(1) : 20;
	}

	@Override
	public float attackDelay() {
		return usesLegacyBehavior() ? 1.5f : super.attackDelay();
	}
	
	@Override
	public int drRoll() {
		if (usesLegacyBehavior()) return enraged ? 0 : Random.NormalIntRange(0, 10);
		return super.drRoll() + Random.NormalIntRange(0, 8);
	}

	@Override
	public void damage(int damage, Object source) {
		super.damage(damage, source);
		if (usesLegacyBehavior() && isAlive() && !enraged && HP < HT / 4) {
			enraged = true;
			Buff.affect(this, DefenceUp.class, 3f).level(70);
			spend(TICK);
			if (Dungeon.level != null && Dungeon.level.heroFOV[pos]) {
				GLog.w(Messages.get(this, "enraged"));
				if (sprite != null) sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
			}
		}
	}

	@Override
	public void die(Object cause) {
		super.die(cause);

		if (cause == Chasm.class){
			hasRaged = true; //don't let enrage trigger for chasm deaths
		}
	}

	//cache this buff to prevent having to call buff(...) a bunch in isAlive
	private BruteRage rage;

	@Override
	public boolean isAlive() {
		if (usesLegacyBehavior()) return super.isAlive();
		if (super.isAlive()){
			return true;
		} else {
			if (!hasRaged){
				triggerEnrage();
			}
			if (rage == null){
				for (BruteRage b : buffs(BruteRage.class)){
					rage = b;
				}
			}
			return rage != null && rage.shielding() > 0;
		}
	}
	
	protected void triggerEnrage(){
		rage = Buff.affect(this, BruteRage.class);
		rage.setShield(HT/2 + 4);
		sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(HT/2), FloatingText.SHIELDING );
		if (Dungeon.level.heroFOV[pos]) {
			SpellSprite.show( this, SpellSprite.BERSERK);
		}
		spend( TICK );
		hasRaged = true;
	}
	
	private static final String HAS_RAGED = "has_raged";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HAS_RAGED, hasRaged);
		bundle.put(ENRAGED, enraged);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		hasRaged = bundle.getBoolean(HAS_RAGED);
		enraged = bundle.contains(ENRAGED) ? bundle.getBoolean(ENRAGED) : HP < HT / 4;
	}

	@Override
	public void rollToDropLoot() {
		super.rollToDropLoot();
		if (!usesLegacyBehavior() || Dungeon.hero == null || Dungeon.level == null
				|| !legacyLootLevelEligible() || Random.Float() >= legacySecondaryLootChance(0.5f)) return;
		Item ranged = Generator.randomUsingDefaults(Generator.Category.RANGEWEAPON);
		if (ranged != null) Dungeon.level.drop(ranged, pos).sprite.drop();
	}
	
	public static class BruteRage extends ShieldBuff {
		
		{
			type = buffType.POSITIVE;
		}
		
		@Override
		public boolean act() {
			
			if (target.HP > 0){
				detach();
				return true;
			}
			
			absorbDamage( Math.round(4*AscensionChallenge.statModifier(target)));
			
			if (shielding() <= 0){
				target.die(null);
			}
			
			spend( TICK );
			
			return true;
		}
		
		@Override
		public void detach() {
			super.detach();
			decShield(shielding()); //clear shielding to track that this was detached
		}

		@Override
		public int icon () {
			return BuffIndicator.FURY;
		}
		
		@Override
		public String desc () {
			return Messages.get(this, "desc", shielding());
		}

	}
}
