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

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Challenges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Light;
import pd.effects.Beam;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.effects.particles.RainbowParticle;
import pd.effects.particles.ShadowParticle;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.levels.CellFlags;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.tiles.DungeonTilemap;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.geom.PointF;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentWandBasicWandDict;

public class WandOfPrismaticLight extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfPrismaticLight.class)
			.t("name", "棱光法杖")
			.t("staff_name", "棱光魔杖")
			.t("desc", "这根法杖由一块实心半透明水晶构成，就像一块厚长光滑的玻璃。一粒粒彩色的光点在法杖顶端跳跃，随时准备迸射而出。")
			.t("stats_desc", "这根法杖射出的光线能刺破地牢的黑暗，揭露隐藏的区域和陷阱。光线能致盲敌人并造成_%1$d~%2$d点伤害_。恶魔与亡灵生物会在法杖的强光下被灼烧而受到额外伤害。")
			.t("upgrade_stat_name_2", "致盲概率")
			.t("upgrade_stat_name_3", "照明持续时间")
			.t("bmage_desc", "当_战斗法师_以棱光魔杖近战攻击目标时，能使目标陷入随魔杖等级提升而延长的残废效果。")
			.t("eleblast_desc", "棱光魔杖的元素风暴造成67%伤害，揭示区域内所有地形，致盲所有目标5回合。");
	}




	{
		image = EquipmentWandBasicWandDict.WAND_LIGHT;

		collisionProperties = Ballistica.MAGIC_BOLT;
	}

	public int min(int lvl){
		return 1+lvl;
	}

	public int max(int lvl){
		return 5+3*lvl;
	}

	@Override
	public void onZap(Ballistica beam) {
		affectMap(beam);
		
		if (Dungeon.level.viewDistance < 6 ){
			if (Dungeon.isChallenged(Challenges.DARKNESS)){
				Buff.prolong( curUser, Light.class, 2f + buffedLvl());
			} else {
				Buff.prolong( curUser, Light.class, 10f+buffedLvl()*5);
			}
		}
		
		Char ch = Actor.findChar(beam.collisionPos);
		if (ch != null){
			wandProc(ch, chargesPerCast());
			affectTarget(ch);
		}
	}

	private void affectTarget(Char ch){
		int dmg = damageRoll();

		//three in (5+lvl) chance of failing
		if (Random.Int(5+buffedLvl()) >= 3) {
			Buff.prolong(ch, Blindness.class, 2f + (buffedLvl() * 0.333f));
			ch.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6 );
		}

		if (ch.properties().contains(Char.Property.DEMONIC) || ch.properties().contains(Char.Property.UNDEAD)){
			ch.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10+buffedLvl() );
			Sample.INSTANCE.play(Assets.Sounds.BURNING);

			ch.damage(Math.round(dmg*1.333f), this);
		} else {
			ch.sprite.centerEmitter().burst( RainbowParticle.BURST, 10+buffedLvl() );

			ch.damage(dmg, this);
		}

	}

	private void affectMap(Ballistica beam){
		boolean noticed = false;
		for (int c : beam.subPath(0, beam.dist)){
			if (!Dungeon.level.insideMap(c)){
				continue;
			}
			for (int n : PathFinder.NEIGHBOURS9){
				int cell = c+n;

				if (Dungeon.level.discoverable[cell])
					Dungeon.level.mapped[cell] = true;

				int terr = Dungeon.level.map[cell];
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

					CellFlags.discover( Dungeon.level,  cell );

					GameScene.discoverTile( cell, terr );
					ScrollOfMagicMapping.discover(cell);

					noticed = true;
				}
			}

			CellEmitter.center(c).burst( RainbowParticle.BURST, Random.IntRange( 1, 2 ) );
		}
		if (noticed)
			Sample.INSTANCE.play( Assets.Sounds.SECRET );

		GameScene.updateFog();
	}

	@Override
	public String upgradeStat2(int level) {
		return Messages.decimalFormat("#", 100*(1-(3/(float)(5+level)))) + "%";
	}

	@Override
	public String upgradeStat3(int level) {
		if (Dungeon.isChallenged(Challenges.DARKNESS)){
			return Integer.toString(2 + level);
		} else {
			return Integer.toString(10 + 5*level);
		}
	}

	@Override
	public void fx(Ballistica beam, Callback callback) {
		curUser.sprite.parent.add(
				new Beam.LightRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
		Sample.INSTANCE.play( Assets.Sounds.RAY );
		callback.call();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		//cripples enemy
		Buff.prolong( defender, Cripple.class, Math.round((1+staff.buffedLvl())*procChanceMultiplier(attacker)));
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( Random.Int( 0x1000000 ) );
		particle.am = 0.5f;
		particle.setLifespan(1f);
		particle.speed.polar(Random.Float(PointF.PI2), 2f);
		particle.setSize( 1f, 2f);
		particle.radiateXY( 0.5f);
	}

}
