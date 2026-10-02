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

package pd.actors.hero.spells;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.buffs.CounterBuff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Regeneration;
import pd.actors.buffs.Roots;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.PowerOfMany;
import pd.effects.BlobEmitter;
import pd.effects.CellEmitter;
import pd.effects.FloatingText;
import pd.effects.particles.LeafParticle;
import pd.effects.particles.ShaftParticle;
import pd.items.equipment.artifacts.HolyTome;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.BArray;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class HallowedGround extends TargetedClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HallowedGround.class)
			.t("name", "神圣领域")
			.t("prompt", "选择一个位置")
			.t("short_desc", "治疗盟友，减速敌人并在范围内扩散植被。")
			.t("desc", "祭司将其神圣魔法聚集在附近的地面上，创造一片%1$dx%1$d范围，持续20回合的神圣领域。\n\n施法时，法术会立即治疗所有盟友15点生命值(未受伤的的盟友和祭司会获得护盾)，短暂缠绕敌人并扩散矮草。\n\n神圣领域每回合会治疗盟友1点生命值(未受伤的的盟友和祭司会获得护盾)，残疾敌人并随机催生高草。\n\n神圣领域会被火焰摧毁。并且如果被动恢复效果被禁用或祭司长时间未获得经验值，神圣领域将只会产生枯草。")
			.t("hallowedterrain.desc", "此处已经化为神圣领域。神圣领域会减速敌人、治疗盟友并扩散植被。");
	}


	public static final HallowedGround INSTANCE = new HallowedGround();

	@Override
	public int icon() {
		return HeroIcon.HALLOWED_GROUND;
	}

	@Override
	public float chargeUse(Hero hero) {
		return 2;
	}

	@Override
	public int targetingFlags() {
		return Ballistica.STOP_TARGET;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.hasTalent(Talent.HALLOWED_GROUND);
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {

		if (target == null){
			return;
		}

		if (Dungeon.level.solid[target] || !Dungeon.level.heroFOV[target]){
			GLog.w(Messages.get(this, "invalid_target"));
			return;
		}

		ArrayList<Char> affected = new ArrayList<>();

		PathFinder.buildDistanceMap(target, BArray.not(Dungeon.level.solid, null), hero.pointsInTalent(Talent.HALLOWED_GROUND));
		for (int i = 0; i < Dungeon.level.length(); i++){
			if (PathFinder.distance[i] != Integer.MAX_VALUE){
				int c = Dungeon.level.map[i];
				if (c == Terrain.EMPTY || c == Terrain.EMBERS || c == Terrain.EMPTY_DECO) {
					Level.set( i, Terrain.GRASS);
					GameScene.updateMap( i );
					CellEmitter.get(i).burst(LeafParticle.LEVEL_SPECIFIC, 2);
				}
				GameScene.add(Blob.seed(i, 20, HallowedTerrain.class));
				CellEmitter.get(i).burst(ShaftParticle.FACTORY, 2);

				Char ch = Actor.findChar(i);
				if (ch != null){
					affected.add(ch);
				}
			}
		}

		Char ally = PowerOfMany.getPoweredAlly();
		if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null){
			if (affected.contains(hero) && !affected.contains(ally)){
				affected.add(ally);
			} else if (!affected.contains(hero) && affected.contains(ally)){
				affected.add(hero);
			}
		}

		for (Char ch : affected){
			affectChar(ch);
		}

		Sample.INSTANCE.play(Assets.Sounds.MELD);
		hero.sprite.zap(target);
		hero.spendAndNext( 1f );

		onSpellCast(tome, hero);

	}

	private void affectChar( Char ch ){
		if (ch.alignment == Char.Alignment.ALLY){

			if (ch == Dungeon.hero || ch.HP == ch.HT){
				int barrierToGive = Math.min(15, 30 - ch.shielding());
				Buff.affect(ch, Barrier.class).incShield(barrierToGive);
				ch.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(barrierToGive), FloatingText.SHIELDING );
			} else {
				int barrier = 15 - (ch.HT - ch.HP);
				barrier = Math.max(barrier, 0);
				ch.HP += 15 - barrier;
				ch.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(15-barrier), FloatingText.HEALING );
				if (barrier > 0){
					Buff.affect(ch, Barrier.class).incShield(barrier);
					ch.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(barrier), FloatingText.SHIELDING );
				}
			}
		} else if (!ch.flying) {
			Buff.affect(ch, GuidingLight.Illuminated.class);
			Buff.affect(ch, Roots.class, 2f);
		}
	}

	public String desc(){
		int area = 1 + 2*Dungeon.hero.pointsInTalent(Talent.HALLOWED_GROUND);
		return Messages.get(this, "desc", area) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	public static class HallowedTerrain extends Blob {

		@Override
		protected void evolve() {

			int cell;

			Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );

			ArrayList<Char> affected = new ArrayList<>();

			// on avg, hallowed ground produces 9/17/25 tiles of grass, 100/67/50% of total tiles
			int chance = 10 + 10*Dungeon.hero.pointsInTalent(Talent.HALLOWED_GROUND);

			for (int i = area.left-1; i <= area.right; i++) {
				for (int j = area.top-1; j <= area.bottom; j++) {
					cell = i + j*Dungeon.level.width();
					if (cur[cell] > 0) {

						//fire destroys hallowed terrain
						if (fire != null && fire.volume > 0 && fire.cur[cell] > 0){
							off[cell] = cur[cell] = 0;
							continue;
						}

						int c = Dungeon.level.map[cell];
						if (c == Terrain.GRASS && Dungeon.level.plants.get(c) == null) {
							if (Random.Int(chance) == 0) {
								if (!Regeneration.regenOn()
										|| (Dungeon.hero.buff(HallowedFurrowTracker.class) != null && Dungeon.hero.buff(HallowedFurrowTracker.class).count() > 100)){
									Level.set(cell, Terrain.FURROWED_GRASS);
								} else {
									Level.set(cell, Terrain.HIGH_GRASS);
								}
								GameScene.updateMap(cell);
								CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 5);
							}
						} else if (c == Terrain.EMPTY || c == Terrain.EMBERS || c == Terrain.EMPTY_DECO) {
							Level.set(cell, Terrain.GRASS);
							GameScene.updateMap(cell);
							CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 2);
						}

						Char ch = Actor.findChar(cell);
						if (ch != null){
							affected.add(ch);
						}

						off[cell] = cur[cell] - 1;
						volume += off[cell];
					} else {
						off[cell] = 0;
					}
				}
			}

			//max of 100 turns of grass per hero level before it starts to furrow
			if (volume > 0){
				Buff.count(Dungeon.hero, HallowedFurrowTracker.class, 1);
			}

			Char ally = PowerOfMany.getPoweredAlly();
			if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null){
				if (affected.contains(Dungeon.hero) && !affected.contains(ally)){
					affected.add(ally);
				} else if (!affected.contains(Dungeon.hero) && affected.contains(ally)){
					affected.add(Dungeon.hero);
				}
			}

			for (Char ch :affected){
				affectChar(ch);
			}

		}

		private void affectChar( Char ch ){
			if (ch.alignment == Char.Alignment.ALLY){
				if (ch == Dungeon.hero || ch.HP == ch.HT){
					Buff.affect(ch, Barrier.class).incShield(1);
					ch.sprite.showStatusWithIcon( CharSprite.POSITIVE, "1", FloatingText.SHIELDING );
				} else {
					ch.HP++;
					ch.sprite.showStatusWithIcon( CharSprite.POSITIVE, "1", FloatingText.HEALING );
				}
			} else if (!ch.flying && ch.buff(Roots.class) == null){
				Buff.prolong(ch, Cripple.class, 1f);
			}
		}

		@Override
		public void use(BlobEmitter emitter) {
			super.use( emitter );
			emitter.pour( ShaftParticle.FACTORY, 1f );
		}

		@Override
		public String tileDesc() {
			return Messages.get(this, "desc");
		}
	}

	public static class HallowedFurrowTracker extends CounterBuff{{revivePersists = true;}}

}
