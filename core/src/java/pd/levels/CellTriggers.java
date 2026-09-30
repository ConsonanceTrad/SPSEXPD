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
 */

package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SacrificialFire;
import pd.actors.blobs.Web;
import pd.actors.blobs.WellWater;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Regeneration;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Piranha;
import pd.actors.mobs.npcs.Sheep;
import pd.effects.CellEmitter;
import pd.effects.particles.SacrificialParticle;
import pd.items.artifacts.TimekeepersHourglass;
import pd.levels.features.Chasm;
import pd.levels.features.DewBlessRoom;
import pd.levels.features.Door;
import pd.levels.features.HighGrass;
import pd.levels.features.OldHighGrass;
import pd.levels.traps.Trap;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.plants.Swiftthistle;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;

/**
 * 生物踏入手机格时的处理：沾上蛛网/圣火、飞行与否的水与深坑、踩踏高草、触发陷阱与植物、进门。
 *
 * 从 Level 中独立出来的是两块：occupy（进入格子）与 press（踩踏/触发），二者同源 ——
 * occupy 内部会按生物类型决定「软踩」还是「硬踩」。
 *
 * Level 上仍保留 occupyCell 与 pressCell(int) 两个薄钩子转发到本类：二者各被十几个子类覆写
 * （各种 BossLevel 会拦截自家地形的处理），且覆写都调 super。正因为钩子留在 Level，
 * 全部约 90 处外部调用点无需改动。
 *
 * 消息键保持 Messages.get(Level.class, ...)：按类定位的消息键换类会失效。
 */
public final class CellTriggers {

	private CellTriggers() { }
	public static void occupy( Level level, Char ch ){
		if (!ch.isImmune(Web.class) && Blob.volumeAt(ch.pos, Web.class) > 0){
			level.blobs.get(Web.class).clear(ch.pos);
			Web.affectChar( ch );
		}

		if (Blob.volumeAt(ch.pos, SacrificialFire.class) > 0 && ch.buff( SacrificialFire.Marked.class ) == null){
			if (Dungeon.level.heroFOV[ch.pos]) {
				CellEmitter.get(ch.pos).burst( SacrificialParticle.FACTORY, 5 );
			}
			Buff.prolong( ch, SacrificialFire.Marked.class, SacrificialFire.Marked.DURATION );
		}

		if (!ch.flying){

			//we call act here instead of detach in case the debuffs haven't managed to deal dmg once yet
			if (level.map[ch.pos] == Terrain.WATER){
				if (ch.buff(Burning.class) != null){
					ch.buff(Burning.class).act();
				}
				if (ch.buff(Ooze.class) != null){
					ch.buff(Ooze.class).act();
				}
			}

			if ( (level.map[ch.pos] == Terrain.GRASS || level.map[ch.pos] == Terrain.EMBERS)
					&& ch == Dungeon.hero && Dungeon.hero.hasTalent(Talent.REJUVENATING_STEPS)
					&& ch.buff(Talent.RejuvenatingStepsCooldown.class) == null){

				if (!Regeneration.regenOn()){
					Level.set(ch.pos, Terrain.FURROWED_GRASS);
				} else if (ch.buff(Talent.RejuvenatingStepsFurrow.class) != null && ch.buff(Talent.RejuvenatingStepsFurrow.class).count() >= 200) {
					Level.set(ch.pos, Terrain.FURROWED_GRASS);
				} else {
					Level.set(ch.pos, Terrain.HIGH_GRASS);
					Buff.count(ch, Talent.RejuvenatingStepsFurrow.class, 3 - Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
				}
				GameScene.updateMap(ch.pos);
				Buff.affect(ch, Talent.RejuvenatingStepsCooldown.class, 15f - 5f*Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
			}
			
			if (level.pit[ch.pos]){
				if (ch == Dungeon.hero) {
					Chasm.heroFall(ch.pos);
				} else if (ch instanceof Mob) {
					Chasm.mobFall( (Mob)ch );
				}
				return;
			}
			
			//characters which are not the hero or a sheep 'soft' press cells
			press( level, ch.pos, ch instanceof Hero || ch instanceof Sheep);
		} else {
			if (level.map[ch.pos] == Terrain.DOOR){
				Door.enter( ch.pos );
			}
		}

		if (ch.isAlive() && ch instanceof Piranha && !level.water[ch.pos]){
			((Piranha) ch).dieOnLand();
		}
	}

	public static void press( Level level, int cell, boolean hard ) {

		Trap trap = null;
		
		switch (level.map[cell]) {
		
		case Terrain.SECRET_TRAP:
			if (hard) {
				trap = level.traps.get( cell );
				GLog.i(Messages.get(Level.class, "hidden_trap", trap.name()));
			}
			break;
			
		case Terrain.TRAP:
			trap = level.traps.get( cell );
			break;
			
		case Terrain.HIGH_GRASS:
		case Terrain.FURROWED_GRASS:
			HighGrass.trample( level, cell);
			break;

		case Terrain.OLD_HIGH_GRASS:
			OldHighGrass.trample(level, cell, Actor.findChar(cell));
			break;
			
		case Terrain.WELL:
			WellWater.affectCell( cell );
			break;

		case Terrain.DEW_BLESS:
			DewBlessRoom.trample(level, cell, Actor.findChar(cell));
			break;
			
		case Terrain.DOOR:
			Door.enter( cell );
			break;
		}

		TimekeepersHourglass.timeFreeze timeFreeze =
				Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);

		Swiftthistle.TimeBubble bubble =
				Dungeon.hero.buff(Swiftthistle.TimeBubble.class);

		if (trap != null) {
			if (bubble != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAP);
				CellFlags.discover( level, cell );
				bubble.setDelayedPress(cell);
				
			} else if (timeFreeze != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAP);
				CellFlags.discover( level, cell );
				timeFreeze.setDelayedPress(cell);
				
			} else {
				if (Dungeon.hero.pos == cell) {
					Dungeon.hero.interrupt();
				}
				trap.trigger();

			}
		}
		
		Plant plant = level.plants.get( cell );
		if (plant != null) {
			if (bubble != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAMPLE, 1, Random.Float( 0.96f, 1.05f ) );
				bubble.setDelayedPress(cell);

			} else if (timeFreeze != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAMPLE, 1, Random.Float( 0.96f, 1.05f ) );
				timeFreeze.setDelayedPress(cell);

			} else {
				plant.trigger();

			}
		}

		if (hard && Blob.volumeAt(cell, Web.class) > 0){
			level.blobs.get(Web.class).clear(cell);
		}
	}
}
