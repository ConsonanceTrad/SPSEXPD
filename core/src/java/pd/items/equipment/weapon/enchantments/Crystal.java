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

package pd.items.equipment.weapon.enchantments;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Regeneration;
import pd.actors.hero.Hero;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.equipment.artifacts.DriedRose;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.messages.Messages;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.GameMath;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Crystal extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Crystal.class)
			.t("name", "魔晶%s")
			.t("alert_fixed", "你的魔晶武器崭新如初！")
			.t("alert_fixed_ghost", "幽灵的魔晶武器崭新如初！")
			.t("alert_noflaw", "你的魔晶武器上的缺损得到了修复。")
			.t("alert_noflaw_ghost", "幽灵的魔晶武器上的缺损得到了修复。")
			.t("alert_nocrack", "你的魔晶武器上的裂纹弥合了。")
			.t("alert_nocrack_ghost", "幽灵的魔晶武器上的裂纹弥合了。")
			.t("alert_flawed", "你的魔晶武器略有缺损。")
			.t("alert_flawed_ghost", "幽灵的魔晶武器略有缺损。")
			.t("alert_cracked", "你的魔晶武器已经布满裂纹，马上就要粉碎了！")
			.t("alert_cracked_ghost", "幽灵的魔晶武器已经布满裂纹，马上就要粉碎了！")
			.t("alert_shattered", "你的魔晶武器化为了齑粉！")
			.t("alert_shattered_ghost", "幽灵的魔晶武器化为了齑粉！")
			.t("desc", "这种强力的附魔可以造成额外的魔法伤害并提升武器的价值，但是，如果不加以维修或给其时间进行自我修复，魔力结晶化的武器就会彻底粉碎。")
			.t("desc_thrown", "魔晶投掷武器的耐久度相当低，但其可随时间流逝而自我修复。")
			.t("desc_perfect", "这把魔晶武器目前品相完美。")
			.t("desc_fine", "这把魔晶武器目前品相不错。")
			.t("desc_flawed", "这把魔晶武器已有缺损，但暂无粉碎的风险。")
			.t("desc_cracked", "这把魔晶武器上已经布满裂纹，有永久粉碎的风险！")
			.t("elestrike_desc", "武器拥有魔晶附魔时，元素打击会对范围内除主要目标外的每个敌人造成10~20点伤害并修复武器的碎裂。");
	}




	private static ItemSprite.Glowing LIGHT_BLUE = new ItemSprite.Glowing( 0x0088FF );
	private static ItemSprite.Glowing FLAW = new ItemSprite.Glowing( 0x0088FF, 0.5f );
	private static ItemSprite.Glowing CRACK = new ItemSprite.Glowing( 0x0088FF, 0.25f );

	private float durability = 100;

	//used for displaying durability to the player, prevents message spam
	//essentially it has a 'lag' of up to 10 points while self-repairing
	private float visualDurability = 100;

	private boolean thrownWeapon = false;

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		if (attacker == Dungeon.hero || attacker instanceof DriedRose.GhostHero) {
			if (weapon instanceof MissileWeapon) {
				setThrownWep(); //we piggyback on thrown weapon durability, don't change our own
			} else {
				thrownWeapon = false;
				//lasts for an average of ~33 attacks at normal speed
				durability -= Random.Float(2, 4) * weapon.delayFactor(attacker);

				float prevHeuristicDur = visualDurability;
				visualDurability = GameMath.gate(durability-10, visualDurability, durability);

				if (prevHeuristicDur > 50 && visualDurability <= 50) {
					if (attacker instanceof Hero) {
						GLog.w(Messages.get(this, "alert_flawed"));
					} else if (attacker instanceof DriedRose.GhostHero){
						GLog.w(Messages.get(this, "alert_flawed_ghost"));
					}
				} else if (prevHeuristicDur > 10 && visualDurability <= 10) {
					//cannot go from flawed to shattered
					durability = Math.max(durability, 1);
					visualDurability = Math.max(visualDurability, 1);

					Sample.INSTANCE.play( Assets.Sounds.SHATTER );
					if (attacker instanceof Hero) {
						GLog.n(Messages.get(this, "alert_cracked"));
					} else if (attacker instanceof DriedRose.GhostHero){
						GLog.n(Messages.get(this, "alert_cracked_ghost"));
					}
				} else if (visualDurability <= 0) {
					Sample.INSTANCE.play( Assets.Sounds.SHATTER );
					Splash.at(attacker.pos, 0x0088FF, 15);
					if (attacker instanceof Hero) {
						if (weapon.isEquipped((Hero) attacker)) {
							weapon.doUnequip((Hero) attacker, false);
						} else {
							weapon.detachAll(((Hero) attacker).belongings.backpack);
						}
						GLog.n(Messages.get(this, "alert_shattered"));
					} else if (attacker instanceof DriedRose.GhostHero){
						((DriedRose.GhostHero) attacker).clearWeapon();
						GLog.n(Messages.get(this, "alert_shattered_ghost"));
					}
				}
			}
		}

		if (attacker instanceof Hero){
			Buff.affect(attacker, CrystalRepair.class);
		} else if (attacker instanceof DriedRose.GhostHero){
			Buff.affect(Dungeon.hero, CrystalRepair.class);
		}

		int magicDmg = (int)Math.ceil(damage * 0.25f * genericProcChanceMultiplier(attacker));
		defender.damage(magicDmg, this);

		return damage;
	}

	public void setThrownWep(){
		thrownWeapon = true;
	}

	public void setDurability(float amount){
		durability = visualDurability = amount;
	}

	public void repair(Weapon w, boolean inRose, float amount){
		if (w instanceof MissileWeapon){
			amount /= 2; //crystal thrown weapons have more uses, they repair more slowly
			if (((MissileWeapon) w).durabilityLeft() < 100) {
				((MissileWeapon) w).repair(amount);
				if (((MissileWeapon) w).durabilityLeft() == 100){
					GLog.p(Messages.get(this, "alert_fixed"));
				}
			}
		} else {

			durability = Math.min(100, durability + amount);
			float prevHeuristicDur = visualDurability;
			visualDurability = Math.max(visualDurability, durability-10);

			if (durability == 100){
				if (visualDurability == 90){
					if (!inRose) {
						GLog.p(Messages.get(this, "alert_fixed"));
					} else {
						GLog.p(Messages.get(this, "alert_fixed_ghost"));
					}
				}
				visualDurability = 100;
			} else if (prevHeuristicDur < 50 && visualDurability >= 50){
				if (!inRose) {
					GLog.p(Messages.get(this, "alert_noflaw"));
				} else {
					GLog.p(Messages.get(this, "alert_noflaw_ghost"));
				}
			}else if (prevHeuristicDur < 10 && visualDurability >= 10){
				if (!inRose) {
					GLog.p(Messages.get(this, "alert_nocrack"));
				} else {
					GLog.p(Messages.get(this, "alert_nocrack_ghost"));
				}
			}

		}
		Item.updateQuickslot();
	}

	@Override
	public ItemSprite.Glowing glowing() {
		if (visualDurability > 50 || thrownWeapon){
			return LIGHT_BLUE;
		} else if (visualDurability > 10){
			return FLAW;
		} else {
			return CRACK;
		}
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (thrownWeapon){
			desc += " " + Messages.get(this, "desc_thrown");
		} else if (visualDurability == 100){
			desc += " " + Messages.get(this, "desc_perfect");
		}else if (visualDurability > 50){
			desc += " " + Messages.get(this, "desc_fine");
		} else if (visualDurability > 10){
			desc += " " + Messages.get(this, "desc_flawed");
		} else {
			desc += " _" + Messages.get(this, "desc_cracked") + "_";
		}
		return desc;
	}

	private static final String DURABILITY = "durability";
	private static final String VISUAL_DUR = "visual_dur";
	private static final String THROWN_WEP = "thrown_wep";

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		durability = bundle.getFloat(DURABILITY);
		visualDurability = bundle.getFloat(VISUAL_DUR);
		thrownWeapon = bundle.getBoolean(THROWN_WEP);
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put(DURABILITY, durability);
		bundle.put(VISUAL_DUR, visualDurability);
		bundle.put(THROWN_WEP, thrownWeapon);
	}

	public static class CrystalRepair extends Buff {

		{
			revivePersists = true;
		}

		@Override
		public boolean act() {
			if (Regeneration.regenOn()){
				if (target instanceof Hero) {
					for (Weapon w : ((Hero) target).belongings.getAllItems(Weapon.class)) {
						if (w.enchantment instanceof Crystal) {
							((Crystal) w.enchantment).repair(w, false, 0.2f);
						}
					}
					DriedRose rose = ((Hero) target).belongings.getItem(DriedRose.class);
					if (rose != null && rose.ghostWeapon() != null && rose.ghostWeapon().enchantment instanceof Crystal){
						((Crystal) rose.ghostWeapon().enchantment).repair(rose.ghostWeapon(), true, 0.2f);
					}
				}
			}

			spend(TICK);
			return true;
		}
	}
}
