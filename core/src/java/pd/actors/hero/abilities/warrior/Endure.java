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

package pd.actors.hero.abilities.warrior;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Combo;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.ArmorAbility;
import pd.effects.Speck;
import pd.effects.SpellSprite;
import pd.items.equipment.armor.ClassArmor;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import render.noosa.Image;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Endure extends ArmorAbility {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Endure.class)
			.t("name", "苦痛坚忍")
			.t("$enduretracker.name", "苦痛之怒")
			.t("$enduretracker.desc", "战士将忍受的原始伤害织成愤怒，将大量的额外伤害倾泻给敌人。\n\n额外伤害：%1$d\n剩余攻击次数：%2$d")
			.t("short_desc", "战士_苦痛坚忍_，跳过若干回合并大幅抵抗伤害。接下来战士会根据忍受的原始伤害造成额外伤害。")
			.t("desc", "战士首先忍耐3回合，将任何受到的伤害减少一半。此减伤效果优先于其它减伤计算。\n\n在此之后，战士在10回合内的下一次攻击将造成额外伤害。额外伤害等同于在忍耐期间所有受到的原始伤害的一半。\n\n如果战士处于连击状态，此能力将补偿3回合连击时间。");
	}




	{
		baseChargeUse = 50f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		if (hero.buff(EndureTracker.class) != null){
			hero.buff(EndureTracker.class).detach();
		}
		Buff.prolong(hero, EndureTracker.class, 12f);

		Combo combo = hero.buff(Combo.class);
		if (combo != null){
			combo.addTime(3f);
		}
		hero.sprite.operate(hero.pos);

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(3f);
	}

	public static class EndureTracker extends FlavourBuff {

		{
			type = buffType.POSITIVE;
		}

		public boolean enduring = true;

		public int damageBonus = 0;
		public int hitsLeft = 0;

		@Override
		public int icon() {
			return enduring ? BuffIndicator.NONE : BuffIndicator.ARMOR;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1, 0, 0);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (10f - visualcooldown()) / 10f);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", damageBonus, hitsLeft);
		}

		public float adjustDamageTaken(float damage){
			if (enduring) {
				damageBonus += damage/2;

				float damageMulti = 0.5f;
				if (Dungeon.hero.hasTalent(Talent.SHRUG_IT_OFF)){
					//total damage reduction is 60%/68%/74%/80%, based on points in talent
					damageMulti *= Math.pow(0.8f, Dungeon.hero.pointsInTalent(Talent.SHRUG_IT_OFF));
				}

				return damage*damageMulti;
			}
			return damage;
		}

		public void endEnduring(){
			if (!enduring){
				return;
			}

			enduring = false;
			damageBonus *= 1f + 0.15f*Dungeon.hero.pointsInTalent(Talent.SUSTAINED_RETRIBUTION);

			int nearby = 0;
			for (Char ch : Actor.chars()){
				if (ch.alignment == Char.Alignment.ENEMY && Dungeon.level.distance(target.pos, ch.pos) <= 2){
					nearby ++;
				}
			}
			damageBonus *= 1f + (nearby*0.05f*Dungeon.hero.pointsInTalent(Talent.EVEN_THE_ODDS));

			hitsLeft = 1+Dungeon.hero.pointsInTalent(Talent.SUSTAINED_RETRIBUTION);
			damageBonus /= hitsLeft;

			if (damageBonus > 0) {
				target.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
				Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
				SpellSprite.show(target, SpellSprite.BERSERK);
			} else {
				detach();
			}
		}

		public float damageFactor(float damage){
			if (enduring){
				return damage;
			} else {
				int bonusDamage = damageBonus;
				hitsLeft--;

				if (hitsLeft <= 0){
					detach();
				}
				return damage + bonusDamage;
			}
		}

		public static String ENDURING       = "enduring";
		public static String DAMAGE_BONUS   = "damage_bonus";
		public static String HITS_LEFT      = "hits_left";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(ENDURING, enduring);
			bundle.put(DAMAGE_BONUS, damageBonus);
			bundle.put(HITS_LEFT, hitsLeft);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			enduring = bundle.getBoolean(ENDURING);
			damageBonus = bundle.getInt(DAMAGE_BONUS);
			hitsLeft = bundle.getInt(HITS_LEFT);
		}
	};

	@Override
	public int icon() {
		return HeroIcon.ENDURE;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.SUSTAINED_RETRIBUTION, Talent.SHRUG_IT_OFF, Talent.EVEN_THE_ODDS, Talent.HEROIC_ENERGY};
	}
}
