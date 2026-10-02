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

import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.blobs.StormCloud;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Light;
import pd.actors.buffs.LockedFloor;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Sleep;
import pd.effects.CellEmitter;
import pd.effects.FloatingText;
import pd.effects.Speck;
import pd.effects.particles.LeafParticle;
import pd.items.equipment.armor.glyphs.Viscosity;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.equipment.weapon.melee.Sickle;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.GeyserTrap;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.FistSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public abstract class YogFist extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(YogFist.class)
			.t("invuln_warn", "拳头邻接古神之眼时无法受到伤害！")
			.t("rankings_desc", "被Yog-Dzewa吞噬")
			.t("desc", "这个拳头是古神力量的部分体现。这些拳头与古神的力量相互联系，并且当它们靠近古神之眼时会免疫所有受到的伤害。")
			.t("burningfist.name", "燃烧之拳")
			.t("burningfist.desc", "燃烧之拳是由混沌的炽热魔法形成的。它在移动时会不断地引燃周围的环境。它还可以发射火焰能量形成魔法冲击，使目标地陷入一片熊熊烈火。\n\n燃烧之拳引燃环境的能力在靠近水的时候会被抑制，但是其热量实在太大，以至于周围的水很快就会蒸发掉！")
			.t("soiledfist.name", "大地之拳")
			.t("soiledfist.desc", "大地之拳是由混沌的大地魔法形成的。当它移动时，它会不断地使周围生出植物。它还可以发射精准的大地冲击，将它的目标困在迅速生长的藤蔓和根系间！\n\n大地之拳能从植物的生命中获得力量，并能根据其周围的植被数量来抵挡伤害！")
			.t("rottingfist.name", "腐烂之拳")
			.t("rottingfist.desc", "腐烂之拳是由一团活的腐肉形成的。它是一团高塔般庞大的腐烂血肉，可以在近战中喷溅腐蚀淤泥，并喷射毒气作为远程攻击。更糟糕的是，腐烂之拳的巨大质量导致它受到攻击时只会进入流血状态，而不会直接受到伤害。\n\n腐蚀淤泥可以被水洗去，但腐烂之拳也可以通过吸收水来治愈自己！此外，在其流血时对腐烂之拳造成伤害只会刷新其流血状态而不会加重效果！")
			.t("rustedfist.name", "锈蚀之拳")
			.t("rustedfist.desc", "锈蚀之拳是由一块活性金属形成的。它是一块高塔般庞大的活性金属，具有强大的近战能力，并有在远距离使目标残废的能力。由于其巨大的质量，锈蚀之拳所受到的伤害都会被延缓，而非立即作用在其身上。\n\n尽管如此，锈蚀之拳那并不灵活的特性使得它无法通过较窄的走道。另外，虽然锈蚀之拳只会缓慢地受到伤害，但是延缓伤害还会在它受到多次攻击时继续叠加。")
			.t("brightfist.name", "辉耀之拳")
			.t("brightfist.teleport", "辉耀之拳在一道使你重度盲目的爆闪中传送到了别处！")
			.t("brightfist.desc", "辉耀之拳是由纯粹的光能量形成的。它能在每回合射出一道强烈的灼目光束！这些光束除了造成大量伤害外，还会暂时使你失明。辉耀之拳为了逃脱还能发出覆盖全场的刺眼爆闪！\n辉耀之拳在近战中并没有特殊能力。")
			.t("darkfist.name", "暗影之拳")
			.t("darkfist.teleport", "暗影之拳在一片使你光源熄灭的漆黑中传送到了别处！")
			.t("darkfist.desc", "暗影之拳是由纯粹的暗能量形成的。它能在每回合射出强大的暗黑法术！这些法术除了造成大量伤害外，还会削弱你的光源。暗影之拳为了逃脱还能瞬间使全场陷入一片漆黑！\n\n暗影之拳在近战中并没有特殊能力。");
	}


	{
		HP = HT = 300;
		defenseSkill = 20;

		viewDistance = Light.DISTANCE;

		//for doomed resistance
		EXP = 25;
		maxLvl = -2;

		state = HUNTING;

		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);
	}

	private float rangedCooldown;
	protected boolean canRangedInMelee = true;

	protected void incrementRangedCooldown(){
		rangedCooldown += Random.NormalFloat(8, 12);
	}

	@Override
	protected boolean act() {
		if (paralysed <= 0 && rangedCooldown > 0) rangedCooldown--;

		if (Dungeon.hero.invisible <= 0 && state == WANDERING){
			beckon(Dungeon.hero.pos);
			state = HUNTING;
			enemy = Dungeon.hero;
		}

		return super.act();
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if (rangedCooldown <= 0){
			return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		} else {
			return super.canAttack(enemy);
		}
	}

	private boolean invulnWarned = false;

	protected boolean isNearYog(){
		int yogPos = Dungeon.level.exit() + 3*Dungeon.level.width();
		return Dungeon.level.distance(pos, yogPos) <= 4;
	}

	@Override
	public boolean isInvulnerable(Class effect) {
		if (isNearYog() && !invulnWarned){
			invulnWarned = true;
			GLog.w(Messages.get(this, "invuln_warn"));
		}
		return isNearYog() || super.isInvulnerable(effect);
	}

	@Override
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos ) && (!canRangedInMelee || rangedCooldown > 0)) {

			return super.doAttack( enemy );

		} else {

			incrementRangedCooldown();
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}

	@Override
	public void damage(int dmg, Object src) {
		int preHP = HP;
		super.damage(dmg, src);
		int dmgTaken = preHP - HP;

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (dmgTaken > 0 && lock != null && !isImmune(src.getClass()) && !isInvulnerable(src.getClass())){
			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(dmgTaken/4f);
			else                                                    lock.addTime(dmgTaken/2f);
		}
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		for ( Char c : Actor.chars() ){
			if (c instanceof YogDzewa){
				((YogDzewa) c).processFistDeath();
			}
		}
	}

	protected abstract void zap();

	public void onZapComplete(){
		zap();
		next();
	}

	@Override
	public int attackSkill( Char target ) {
		return 36;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 18, 36 );
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 15);
	}

	{
		immunities.add( Sleep.class );
	}

	@Override
	public String description() {
		return Messages.get(YogFist.class, "desc") + "\n\n" + Messages.get(this, "desc");
	}

	public static final String RANGED_COOLDOWN = "ranged_cooldown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(RANGED_COOLDOWN, rangedCooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		rangedCooldown = bundle.getFloat(RANGED_COOLDOWN);
	}

	public static class BurningFist extends YogFist {

		{
			spriteClass = FistSprite.Burning.class;

			properties.add(Property.FIERY);
		}

		@Override
		public boolean act() {

			boolean result = super.act();

			if (Dungeon.level.map[pos] == Terrain.WATER){
				Level.set( pos, Terrain.EMPTY);
				GameScene.updateMap( pos );
				CellEmitter.get( pos ).burst( Speck.factory( Speck.STEAM ), 10 );
			}

			//1.67 evaporated tiles on average
			int evaporatedTiles = Random.chances(new float[]{0, 1, 2});

			for (int i = 0; i < evaporatedTiles; i++) {
				int cell = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
				if (Dungeon.level.map[cell] == Terrain.WATER){
					Level.set( cell, Terrain.EMPTY);
					GameScene.updateMap( cell );
					CellEmitter.get( cell ).burst( Speck.factory( Speck.STEAM ), 10 );
				}
			}

			for (int i : PathFinder.NEIGHBOURS9) {
				int vol = Fire.volumeAt(pos+i, Fire.class);
				if (vol < 4 && !Dungeon.level.water[pos + i] && !Dungeon.level.solid[pos + i]){
					GameScene.add( Blob.seed( pos + i, 4 - vol, Fire.class ) );
				}
			}

			return result;
		}

		@Override
		protected void zap() {
			spend( 1f );

			if (Dungeon.level.map[enemy.pos] == Terrain.WATER){
				Level.set( enemy.pos, Terrain.EMPTY);
				GameScene.updateMap( enemy.pos );
				CellEmitter.get( enemy.pos ).burst( Speck.factory( Speck.STEAM ), 10 );
			} else {
				Buff.affect( enemy, Burning.class ).reignite( enemy );
			}

			for (int i : PathFinder.NEIGHBOURS9){
				if (!Dungeon.level.water[enemy.pos+i] && !Dungeon.level.solid[enemy.pos+i]){
					int vol = Fire.volumeAt(enemy.pos+i, Fire.class);
					if (vol < 4){
						GameScene.add( Blob.seed( enemy.pos + i, 4 - vol, Fire.class ) );
					}
				}
			}

		}

		{
			immunities.add(Frost.class);

			resistances.add(StormCloud.class);
			resistances.add(GeyserTrap.class);
		}

	}

	public static class SoiledFist extends YogFist {

		{
			spriteClass = FistSprite.Soiled.class;
		}

		@Override
		public boolean act() {

			boolean result = super.act();

			//1.33 grass tiles on average
			int furrowedTiles = Random.chances(new float[]{0, 2, 1});

			for (int i = 0; i < furrowedTiles; i++) {
				int cell = pos + PathFinder.NEIGHBOURS9[Random.Int(9)];
				if (Dungeon.level.map[cell] == Terrain.GRASS) {
					Level.set(cell, Terrain.FURROWED_GRASS);
					GameScene.updateMap(cell);
					CellEmitter.get(cell).burst(LeafParticle.GENERAL, 10);
				}
			}

			Dungeon.observe();

			for (int i : PathFinder.NEIGHBOURS9) {
				int cell = pos + i;
				if (canSpreadGrass(cell)){
					Level.set(pos+i, Terrain.GRASS);
					GameScene.updateMap( pos + i );
				}
			}

			return result;
		}

		@Override
		public void damage(int dmg, Object src) {
			int grassCells = 0;
			for (int i : PathFinder.NEIGHBOURS9) {
				if (Dungeon.level.map[pos+i] == Terrain.FURROWED_GRASS
				|| Dungeon.level.map[pos+i] == Terrain.HIGH_GRASS){
					grassCells++;
				}
			}
			if (grassCells > 0) dmg = Math.round(dmg * (6-grassCells)/6f);

			//can be ignited, but takes no damage from burning
			if (src.getClass() == Burning.class){
				return;
			}

			super.damage(dmg, src);
		}

		@Override
		public int incomingDOT() {
			if (!needsIncomingDOTUpdate){
				return cachedIncomingDOT;
			}
			int total = super.incomingDOT();
			//we have special immunity rules to burning, so account for that here
			Burning b = buff(Burning.class);
			if (b != null){
				total -= b.totalIncomingDMG();
			}
			cachedIncomingDOT = total;
			return total;
		}

		@Override
		protected void zap() {
			spend( 1f );

			Invisibility.dispel(this);
			Char enemy = this.enemy;
			if (hit( this, enemy, true )) {

				Buff.affect( enemy, Roots.class, 3f );

			} else {

				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}

			for (int i : PathFinder.NEIGHBOURS9){
				int cell = enemy.pos + i;
				if (canSpreadGrass(cell)){
					if (Random.Int(5) == 0){
						Level.set(cell, Terrain.FURROWED_GRASS);
						GameScene.updateMap( cell );
					} else {
						Level.set(cell, Terrain.GRASS);
						GameScene.updateMap( cell );
					}
					CellEmitter.get( cell ).burst( LeafParticle.GENERAL, 10 );
				}
			}
			Dungeon.observe();

		}

		private boolean canSpreadGrass(int cell){
			int yogPos = Dungeon.level.exit() + Dungeon.level.width()*3;
			return Dungeon.level.distance(cell, yogPos) > 4 && !Dungeon.level.solid[cell]
					&& !(Dungeon.level.map[cell] == Terrain.FURROWED_GRASS || Dungeon.level.map[cell] == Terrain.HIGH_GRASS);
		}

	}

	public static class RottingFist extends YogFist {

		{
			spriteClass = FistSprite.Rotting.class;

			properties.add(Property.ACIDIC);
		}

		@Override
		protected boolean act() {
			//ensures toxic gas acts at the appropriate time when added
			GameScene.add(Blob.seed(pos, 0, ToxicGas.class));

			if (Dungeon.level.water[pos] && HP < HT) {
				sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(HT/50), FloatingText.HEALING);
				HP = Math.min(HT, HP + HT/50);
			}

			return super.act();
		}

		@Override
		public void damage(int dmg, Object src) {
			if (!isInvulnerable(src.getClass())
					&& !(src instanceof Bleeding)
					&& buff(Sickle.HarvestBleedTracker.class) == null){
				dmg = Math.round( dmg * resist( src.getClass() ));
				if (dmg < 0){
					return;
				}
				Bleeding b = buff(Bleeding.class);
				if (b == null){
					b = new Bleeding();
				}
				b.announced = false;
				b.attachTo(this);
				b.set(dmg*.6f);
				sprite.showStatus(CharSprite.WARNING, Messages.titleCase(b.name()) + " " + (int)b.level());
			} else{
				super.damage(dmg, src);
			}
		}

		@Override
		protected void zap() {
			spend( 1f );
			GameScene.add(Blob.seed(enemy.pos, 100, ToxicGas.class));
		}

		@Override
		public int attackProc( Char enemy, int damage ) {
			damage = super.attackProc( enemy, damage );

			if (Random.Int( 2 ) == 0) {
				Buff.affect( enemy, Ooze.class ).set( Ooze.DURATION );
				enemy.sprite.burst( 0xFF000000, 5 );
			}

			return damage;
		}

		{
			immunities.add(ToxicGas.class);
		}

	}

	public static class RustedFist extends YogFist {

		{
			spriteClass = FistSprite.Rusted.class;

			properties.add(Property.LARGE);
			properties.add(Property.INORGANIC);
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 22, 44 );
		}

		@Override
		public void damage(int dmg, Object src) {
			if (!isInvulnerable(src.getClass()) && !(src instanceof Viscosity.DeferedDamage)){
				dmg = Math.round( dmg * resist( src.getClass() ));
				if (dmg >= 0) {
					Buff.affect(this, Viscosity.DeferedDamage.class).extend(dmg);
					sprite.showStatus(CharSprite.WARNING, Messages.get(Viscosity.class, "deferred", dmg));
				}
			} else{
				super.damage(dmg, src);
			}
		}

		@Override
		protected void zap() {
			spend( 1f );
			Buff.affect(enemy, Cripple.class, 4f);
		}

	}

	public static class BrightFist extends YogFist {

		{
			spriteClass = FistSprite.Bright.class;

			properties.add(Property.ELECTRIC);

			canRangedInMelee = false;
		}

		@Override
		protected void incrementRangedCooldown() {
			//ranged attack has no cooldown
		}

		//used so resistances can differentiate between melee and magical attacks
		public static class LightBeam{}

		@Override
		protected void zap() {
			spend( 1f );

			Invisibility.dispel(this);
			Char enemy = this.enemy;
			if (hit( this, enemy, true )) {

				enemy.damage( Random.NormalIntRange(10, 20), new LightBeam() );
				Buff.prolong( enemy, Blindness.class, Blindness.DURATION/2f );

				if (!enemy.isAlive() && enemy == Dungeon.hero) {
					Badges.validateDeathFromEnemyMagic();
					Dungeon.fail( this );
					GLog.n( Messages.get(Char.class, "kill", name()) );
				}

			} else {

				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}

		}

		@Override
		public void damage(int dmg, Object src) {
			int beforeHP = HP;
			super.damage(dmg, src);
			if (isAlive() && beforeHP > HT/2 && HP <= HT/2){
				HP = HT/2;
				Buff.prolong( Dungeon.hero, Blindness.class, Blindness.DURATION*1.5f );
				int i;
				do {
					i = Random.Int(Dungeon.level.length());
				} while (Dungeon.level.heroFOV[i]
						|| Dungeon.level.solid[i]
						|| Actor.findChar(i) != null
						|| PathFinder.getStep(i, Dungeon.level.exit(), Dungeon.level.passable) == -1);
				ScrollOfTeleportation.appear(this, i);
				state = WANDERING;
				GameScene.flash(0x80FFFFFF);
				GLog.w( Messages.get( this, "teleport" ));
			} else if (!isAlive()){
				Buff.prolong( Dungeon.hero, Blindness.class, Blindness.DURATION*3f );
				GameScene.flash(0x80FFFFFF);
			}
		}

	}

	public static class DarkFist extends YogFist {

		{
			spriteClass = FistSprite.Dark.class;

			canRangedInMelee = false;
		}

		@Override
		protected void incrementRangedCooldown() {
			//ranged attack has no cooldown
		}

		//used so resistances can differentiate between melee and magical attacks
		public static class DarkBolt{}

		@Override
		protected void zap() {
			spend( 1f );

			Invisibility.dispel(this);
			Char enemy = this.enemy;
			if (hit( this, enemy, true )) {

				enemy.damage( Random.NormalIntRange(10, 20), new DarkBolt() );

				Light l = enemy.buff(Light.class);
				if (l != null){
					l.weaken(50);
				}

				if (!enemy.isAlive() && enemy == Dungeon.hero) {
					Badges.validateDeathFromEnemyMagic();
					Dungeon.fail( this );
					GLog.n( Messages.get(Char.class, "kill", name()) );
				}

			} else {

				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}

		}

		@Override
		public void damage(int dmg, Object src) {
			int beforeHP = HP;
			super.damage(dmg, src);
			if (isAlive() && beforeHP > HT/2 && HP <= HT/2){
				HP = HT/2;
				Light l = Dungeon.hero.buff(Light.class);
				if (l != null){
					l.detach();
				}
				int i;
				do {
					i = Random.Int(Dungeon.level.length());
				} while (Dungeon.level.heroFOV[i]
						|| Dungeon.level.solid[i]
						|| Actor.findChar(i) != null
						|| PathFinder.getStep(i, Dungeon.level.exit(), Dungeon.level.passable) == -1);
				ScrollOfTeleportation.appear(this, i);
				state = WANDERING;
				GameScene.flash(0, false);
				GLog.w( Messages.get( this, "teleport" ));
			} else if (!isAlive()){
				Light l = Dungeon.hero.buff(Light.class);
				if (l != null){
					l.detach();
				}
				GameScene.flash(0, false);
			}
		}

	}

}
