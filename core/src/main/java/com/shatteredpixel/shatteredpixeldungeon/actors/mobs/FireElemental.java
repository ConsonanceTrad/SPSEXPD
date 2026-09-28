/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.LuckyBadge;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFirebolt;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentFire;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentFire2;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FireElementalSprite;
import com.watabou.utils.Random;

/** Original SPS-PD runtime and save identity for the city fire elemental. */
public class FireElemental extends Mob {

	{
		spriteClass = FireElementalSprite.class;
		HP = HT = 120 + legacyDepthAdjustment(0) * Random.NormalIntRange(4, 7);
		defenseSkill = 20 + legacyDepthAdjustment(0);
		EXP = 15;
		maxLvl = 30;
		flying = true;
		loot = PotionOfLiquidFlame.class;
		lootChance = 0.1f;
		properties.add(Property.ELEMENT);
		properties.add(Property.MAGICER);
		immunities.add(Fire.class);
		immunities.add(DamageType.Fire.class);
		resistances.add(WandOfFirebolt.class);
		resistances.add(EnchantmentFire.class);
		resistances.add(EnchantmentFire2.class);
	}

	@Override
	public Item SupercreateLoot() {
		return new Firebloom.Seed();
	}

	public static Class<?> specialLootType() {
		return Firebloom.Seed.class;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(16, 20 + legacyDepthAdjustment(1));
	}

	@Override
	public int attackSkill(Char target) {
		return 25 + legacyDepthAdjustment(1);
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) Buff.affect(enemy, Burning.class).reignite(enemy, 3f);
		enemy.damage(damageRoll(), DamageType.FIRE_DAMAGE);
		return 0;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Dungeon.level != null && Dungeon.level.distance(pos, enemy.pos) <= 2;
	}

	@Override
	public boolean add(Buff buff) {
		if (buff instanceof Burning) {
			if (HP < HT && isAlive()) {
				HP += HT / 10;
				if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			}
			return false;
		}
		if (buff instanceof Frost || buff instanceof Chill) {
			boolean inWater = Dungeon.level != null && Dungeon.level.insideMap(pos)
					&& Dungeon.level.water[pos];
			damage(Random.NormalIntRange(inWater ? HT / 2 : 1,
					inWater ? HT : HT * 2 / 3), buff);
			return false;
		}
		return super.add(buff);
	}

	@Override
	public void rollToDropLoot() {
		if (Dungeon.hero == null || Dungeon.level == null || Dungeon.hero.lvl > maxLvl + 800) return;
		float bonus = 0.02f * LuckyBadge.luckBonus(Dungeon.hero);
		Item item = null;
		if (Random.Float() < 0.1f + bonus) item = new PotionOfLiquidFlame();
		else if (Random.Float() < 0.02f + bonus) item = new WandOfFirebolt();
		if (item != null) {
			Heap heap = Dungeon.level.drop(item, pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
		rollKnowledgeLoot();
	}
}
