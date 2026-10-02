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
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.Imp;
import pd.items.Item;
import pd.items.KindOfWeapon;
import pd.items.consum.food.staplefood.NormalRation;
import pd.items.consum.food.staplefood.OverpricedRation;
import pd.items.equipment.weapon.melee.normalweapon.FightGloves;
import pd.items.equipment.weapon.melee.normalweapon.Knuckles;
import pd.messages.Messages;
import pd.sprites.MonkSprite;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.Image;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Monk extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Monk.class)
			.t("name", "矮人武僧")
			.t("def_verb", "格挡")
			.t("parried", "招架")
			.t("disarm", "武僧将你手中的武器击飞了！")
			.t("desc", "这些僧侣皆是狂热的信徒，倾其所能从一切异族手中保护他们城市的秘密。他们既不披甲亦不执械，仅仅依靠他们的徒手格斗技巧退敌。")
			.t("focus.name", "凝神")
			.t("focus.desc", "这个武僧已经完全集中于目标的一举一动，似乎能预料到目标的下一步动作。\n\n处于凝神状态的角色必定会招架下一次物理攻击，哪怕是必中的攻击。招架攻击会消耗掉武僧的凝神，他们需要重新凝神以招架下次攻击。武僧在移动时可以更快地凝神。");
	}

	/** Kept for subclasses and save compatibility; SPS monks do not use focus. */
	protected float focusCooldown;
	
	{
		spriteClass = MonkSprite.class;
		
		HP = HT = 160 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 5);
		defenseSkill = 30 + legacyDepthAdjustment(1);
		
		EXP = 14;
		maxLvl = 30;
		
		loot = NormalRation.class;
		lootChance = 0.1f;

		properties.add(Property.DWARF);

		immunities.add(Amok.class);
		immunities.add(Terror.class);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(22, 36 + legacyDepthAdjustment(0));
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30 + legacyDepthAdjustment(1);
	}
	
	@Override
	public float attackDelay() {
		return 0.5f;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(2, 12);
	}
	
	@Override
	public void rollToDropLoot() {
		Imp.Quest.oldProcess( this );
		super.rollToDropLoot();
		if (Dungeon.hero != null && Dungeon.level != null && legacyLootLevelEligible()
				&& Random.Float() < legacySecondaryLootChance(0.4f)) {
			pd.items.Heap heap =
					Dungeon.level.drop(new OverpricedRation(), pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
	}

	@Override public Item SupercreateLoot() { return new FightGloves(); }

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(12) == 0 && enemy == Dungeon.hero) legacyDisarm(Dungeon.hero);
		return damage;
	}

	void legacyDisarm(Hero hero) {
		if (hero == null || Dungeon.level == null) return;
		KindOfWeapon weapon = hero.belongings.weapon;
		if (weapon != null && !(weapon instanceof Knuckles || weapon instanceof FightGloves)
				&& !weapon.cursed) {
			hero.belongings.weapon = null;
			Dungeon.quickslot.clearItem(weapon);
			weapon.updateQuickslot();
			pd.items.Heap heap = Dungeon.level.drop(weapon, hero.pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
			if (sprite != null) GLog.w(Messages.get(this, "disarm", weapon.name()));
		}
	}
	
	/** Retained so saves made before the SPS behavior restoration can still deserialize. */
	public static class Focus extends Buff {
		
		{
			type = buffType.POSITIVE;
			announced = true;
		}
		
		@Override
		public int icon() {
			return BuffIndicator.MIND_VISION;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.25f, 1.5f, 1f);
		}
	}
}
