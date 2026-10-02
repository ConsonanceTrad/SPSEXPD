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
import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.effects.Effects;
import pd.effects.MagicMissile;
import pd.effects.Pushing;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.levels.Terrain;
import pd.levels.features.Door;
import pd.levels.traps.TenguDartTrap;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.tiles.DungeonTilemap;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.Group;
import render.noosa.Image;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.geom.PointF;
import render.utils.math.Random;
import pd.messages.InlineText;

public class WandOfBlastWave extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfBlastWave.class)
			.t("name", "冲击波法杖")
			.t("staff_name", "冲击波魔杖")
			.t("ondeath", "你用冲击波法杖炸碎了自己...")
			.t("knockback_ondeath", "你死于撞击...")
			.t("desc", "这根法杖由一种大理石石材制成，嵌有金饰并在顶部镶着一个浑圆的黑宝石。它在你手中的感觉非常沉重。")
			.t("typical_stats_desc", "这根法杖能射出在目标地点剧烈爆炸的魔法飞弹。爆炸的威力通常会造成_%1$d~%2$d点伤害_，并强到足以炸飞大多数敌人。")
			.t("stats_desc", "这根法杖能射出一股能量，在目标位置引发强烈的爆炸。爆炸的力量会造成_%1$d~%2$d点伤害_并强到足以击飞大多数敌人。")
			.t("upgrade_stat_name_2", "击退")
			.t("bmage_desc", "当_战斗法师_以冲击波魔杖近战攻击已经被麻痹的目标时，将消耗目标身上剩余的麻痹回合数造成大量魔法伤害。此效果对每个目标均有短暂的冷却。")
			.t("eleblast_desc", "冲击波魔杖的元素风暴造成67%伤害，并将所有目标弹出影响区域。");
	}


	{
		image = EquipmentWandBasicWandDict.WAND_BLAST_WAVE_0;

		collisionProperties = Ballistica.PROJECTILE;
	}

	public int min(int lvl){
		return 1+lvl;
	}

	public int max(int lvl){
		return 3+3*lvl;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Sample.INSTANCE.play( Assets.Sounds.BLAST );
		BlastWave.blast(bolt.collisionPos);

		//presses all tiles in the AOE first, with the exception of tengu dart traps
		for (int i : PathFinder.NEIGHBOURS9){
			if (!(Dungeon.level.traps.get(bolt.collisionPos+i) instanceof TenguDartTrap)) {
				Dungeon.level.pressCell(bolt.collisionPos + i);
			}
		}

		//throws other chars around the center.
		for (int i  : PathFinder.NEIGHBOURS8){
			Char ch = Actor.findChar(bolt.collisionPos + i);

			if (ch != null){
				wandProc(ch, chargesPerCast());
				if (ch.alignment != Char.Alignment.ALLY) ch.damage(damageRoll(), this);

				//do not push chars that are dieing over a pit, or that move due to the damage
				if ((ch.isAlive() || ch.flying || !Dungeon.level.pit[ch.pos])
						&& ch.pos == bolt.collisionPos + i) {
					Ballistica trajectory = new Ballistica(ch.pos, ch.pos + i, Ballistica.MAGIC_BOLT);
					int strength = Math.round(1.5f + buffedLvl() / 2f);
					throwChar(ch, trajectory, strength, false, true, this);
				}

			}
		}

		//throws the char at the center of the blast
		Char ch = Actor.findChar(bolt.collisionPos);
		if (ch != null){
			wandProc(ch, chargesPerCast());
			ch.damage(damageRoll(), this);

			//do not push chars that are dieing over a pit, or that move due to the damage
			if ((ch.isAlive() || ch.flying || !Dungeon.level.pit[ch.pos])
					&& bolt.path.size() > bolt.dist+1 && ch.pos == bolt.collisionPos) {
				Ballistica trajectory = new Ballistica(ch.pos, bolt.path.get(bolt.dist + 1), Ballistica.MAGIC_BOLT);
				int strength = buffedLvl() + 3;
				throwChar(ch, trajectory, strength, false, true, this);
			}
		}
		
	}

	public static void throwChar(final Char ch, final Ballistica trajectory, int power,
	                             boolean closeDoors, boolean collideDmg, Object cause){
		if (ch.properties().contains(Char.Property.BOSS)) {
			power = (power+1)/2;
		}

		int dist = Math.min(trajectory.dist, power);

		boolean collided = dist == trajectory.dist;

		if (dist <= 0
				|| ch.rooted
				|| ch.properties().contains(Char.Property.IMMOVABLE)) return;

		//large characters cannot be moved into non-open space
		if (Char.hasProp(ch, Char.Property.LARGE)) {
			for (int i = 1; i <= dist; i++) {
				if (!Dungeon.level.openSpace[trajectory.path.get(i)]){
					dist = i-1;
					collided = true;
					break;
				}
			}
		}

		if (Actor.findChar(trajectory.path.get(dist)) != null){
			dist--;
			collided = true;
		}

		if (dist < 0) return;

		final int newPos = trajectory.path.get(dist);

		if (newPos == ch.pos) return;

		final int finalDist = dist;
		final boolean finalCollided = collided && collideDmg;
		final int initialpos = ch.pos;

		Actor.add(new Pushing(ch, ch.pos, newPos, new Callback() {
			public void call() {
				if (initialpos != ch.pos || Actor.findChar(newPos) != null) {
					//something caused movement or added chars before pushing resolved, cancel to be safe.
					ch.sprite.place(ch.pos);
					return;
				}
				int oldPos = ch.pos;
				ch.pos = newPos;
				if (finalCollided && ch.isActive()) {
					ch.damage(Random.NormalIntRange(finalDist, 2*finalDist), new Knockback());
					if (ch.isActive()) {
						Paralysis.prolong(ch, Paralysis.class, 1 + finalDist/2f);
					} else if (ch == Dungeon.hero){
						if (cause instanceof Wand || cause instanceof Weapon.Enchantment){
							Badges.validateDeathFromFriendlyMagic();
						}
						GLog.n(Messages.get(WandOfBlastWave.class, "knockback_ondeath"));
						Dungeon.fail(cause);
					}
				}
				if (closeDoors && Dungeon.level.map[oldPos] == Terrain.OPEN_DOOR){
					Door.leave(oldPos);
				}
				Dungeon.level.occupyCell(ch);
				if (ch == Dungeon.hero){
					Dungeon.observe();
					GameScene.updateFog();
				} else if (Dungeon.level.heroFOV[initialpos] != Dungeon.level.heroFOV[newPos]){
					Dungeon.observe();
				}
			}
		}));
	}

	public static class Knockback{}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {

		if (defender.buff(Paralysis.class) != null && defender.buff(BWaveOnHitTracker.class) == null){
			defender.buff(Paralysis.class).detach();
			int dmg = Hero.heroDamageIntRange(8+2*buffedLvl(), 12+3*buffedLvl());
			defender.damage(Math.round(procChanceMultiplier(attacker) * dmg), this);
			BlastWave.blast(defender.pos);
			Sample.INSTANCE.play( Assets.Sounds.BLAST );

			//brief immunity, to prevent stacking absurd damage with it with things like para gas
			Buff.prolong(defender, BWaveOnHitTracker.class, 3f);
		}
	}

	public static class BWaveOnHitTracker extends FlavourBuff{}

	@Override
	public String upgradeStat2(int level) {
		return Integer.toString(3 + level);
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.FORCE,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0x664422 ); particle.am = 0.6f;
		particle.setLifespan(3f);
		particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
		particle.setSize( 1f, 2f);
		particle.radiateXY(2.5f);
	}

	public static class BlastWave extends Image {

		private static final float TIME_TO_FADE = 0.2f;

		private float time;
		private float size;

		public BlastWave(){
			super(Effects.get(Effects.Type.RIPPLE));
			origin.set(width / 2, height / 2);
		}

		public void reset(int pos, float size) {
			revive();

			x = (pos % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
			y = (pos / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

			resetColor();
			scale.set(0);

			time = TIME_TO_FADE;
			this.size = size;
		}

		@Override
		public void update() {
			super.update();

			if ((time -= Game.elapsed) <= 0) {
				kill();
			} else {
				float p = time / TIME_TO_FADE;
				alpha(p);
				scale.y = scale.x = (1-p)*size;
			}
		}

		public static void blast(int pos) {
			blast(pos, 3);
		}

		public static void blast(int pos, float radius) {
			blast(pos, radius, -1);
		}

		public static void blast(int pos, float radius, int hardLight){
			Group parent = Dungeon.hero.sprite.parent;
			BlastWave b = (BlastWave) parent.recycle(BlastWave.class);
			parent.bringToFront(b);
			b.reset(pos, radius);
			if (hardLight != -1){
				b.hardlight(hardLight);
			}
		}

	}
}
