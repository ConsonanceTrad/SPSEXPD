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

package pd.items.equipment.wands;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Chill;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Frost;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.levels.rooms.special.MagicalFireRoom;
import pd.mechanics.Ballistica;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.geom.PointF;
import render.utils.math.Random;
import pd.messages.InlineText;

public class WandOfFrost extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfFrost.class)
			.t("name", "冰霜法杖")
			.t("staff_name", "冰霜魔杖")
			.t("desc", "这根法杖似乎由某种魔法冰块制成。它的圆顶闪着亮光。握起来很冰，但不知为何你的手仍保持着温暖。")
			.t("stats_desc", "这根法杖能向你的敌人射击冰霜能量，造成_%1$d~%2$d点伤害_并冻伤目标，在水中的作用似乎更强。冻伤或冻结的敌人将受到更少该法杖的伤害。")
			.t("upgrade_stat_name_2", "冻伤持续时间")
			.t("bmage_desc", "当_战斗法师_以冰霜魔杖近战攻击目标时，有概率将其直接冻结。目标冻伤越严重，被冻结的概率就越高。")
			.t("eleblast_desc", "冰霜魔杖的元素风暴造成100%伤害，冻结敌人并熄灭火焰。");
	}


	{
		image = EquipmentWandBasicWandDict.WAND_FROST;
	}

	public int min(int lvl){
		return 2+lvl;
	}

	public int max(int lvl){
		return 8+5*lvl;
	}

	@Override
	public void onZap(Ballistica bolt) {

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) {
			heap.freeze();
		}

		Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
		if (fire != null && fire.volume > 0) {
			fire.clear( bolt.collisionPos );
		}

		MagicalFireRoom.EternalFire eternalFire = (MagicalFireRoom.EternalFire)Dungeon.level.blobs.get(MagicalFireRoom.EternalFire.class);
		if (eternalFire != null && eternalFire.volume > 0) {
			eternalFire.clear( bolt.collisionPos );
			//bolt ends 1 tile short of fire, so check next tile too
			if (bolt.path.size() > bolt.dist+1){
				eternalFire.clear( bolt.path.get(bolt.dist+1) );
			}

		}

		Char ch = Actor.findChar(bolt.collisionPos);
		if (ch != null){

			int damage = damageRoll();

			if (ch.buff(Frost.class) != null){
				return; //do nothing, can't affect a frozen target
			}
			if (ch.buff(Chill.class) != null){
				//6.67% less damage per turn of chill remaining, to a max of 10 turns (50% dmg)
				float chillturns = Math.min(10, ch.buff(Chill.class).cooldown());
				damage = (int)Math.round(damage * Math.pow(0.9333f, chillturns));
			} else {
				ch.sprite.burst( 0xFF99CCFF, buffedLvl() / 2 + 2 );
			}

			wandProc(ch, chargesPerCast());
			ch.damage(damage, this);
			Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, 1.1f * Random.Float(0.87f, 1.15f) );

			if (ch.isAlive()){
				if (Dungeon.level.water[ch.pos])
					Buff.affect(ch, Chill.class, 4+buffedLvl());
				else
					Buff.affect(ch, Chill.class, 2+buffedLvl());
			}
		} else {
			Dungeon.level.pressCell(bolt.collisionPos);
		}
	}

	@Override
	public String upgradeStat2(int level) {
		return Integer.toString(2 + level);
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent,
				MagicMissile.FROST,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		Chill chill = defender.buff(Chill.class);

		if (chill != null) {

			//1/9 at 2 turns of chill, scaling to 9/9 at 10 turns
			float procChance = ((int)Math.floor(chill.cooldown()) - 1)/9f;
			procChance *= procChanceMultiplier(attacker);

			if (Random.Float() < procChance) {

				float powerMulti = Math.max(1f, procChance);

				//need to delay this through an actor so that the freezing isn't broken by taking damage from the staff hit.
				new FlavourBuff() {
					{
						actPriority = VFX_PRIO;
					}

					public boolean act() {
						Buff.affect(target, Frost.class, Math.round(Frost.DURATION * powerMulti));
						return super.act();
					}
				}.attachTo(defender);
			}
		}
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color(0x88CCFF);
		particle.am = 0.6f;
		particle.setLifespan(2f);
		float angle = Random.Float(PointF.PI2);
		particle.speed.polar( angle, 2f);
		particle.acc.set( 0f, 1f);
		particle.setSize( 0f, 1.5f);
		particle.radiateXY(Random.Float(1f));
	}

}
