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

package pd.items.artifacts;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.MagicImmune;
import pd.actors.buffs.Regeneration;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.Chains;
import pd.effects.Effects;
import pd.effects.Pushing;
import pd.effects.particles.ElmoParticle;
import pd.items.rings.RingOfEnergy;
import pd.journal.Catalog;
import pd.levels.MiningLevel;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSpriteSheet;
import pd.tiles.DungeonTilemap;
import render.utils.BArray;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.Callback;
import render.utils.PathFinder;
import render.utils.Random;

import java.util.ArrayList;

public class EtherealChains extends Artifact {

	public static final String AC_CAST       = "CAST";
	public static final String AC_LOCKED     = "LOCKED";

	{
		image = ItemSpriteSheet.ARTIFACT_CHAINS;

		levelCap = 5;
		exp = 0;

		charge = 5;

		defaultAction = AC_CAST;
		usesTargeting = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped(hero) && charge > 0 && !cursed) {
			actions.add(AC_CAST);
		}
		if (isEquipped(hero) && level() > 1 && !cursed) actions.add(AC_LOCKED);
		return actions;
	}

	public int targetingPos( Hero user, int dst ){
		return dst;
	}

	@Override
	public void execute(Hero hero, String action) {

		if (!AC_CAST.equals(action) && !AC_LOCKED.equals(action)) {
			super.execute(hero, action);
			return;
		}

		if (action.equals(AC_CAST)){

			curUser = hero;

			if (!isEquipped( hero )) {
				GLog.i( Messages.get(Artifact.class, "need_to_equip") );
				usesTargeting = false;

			} else if (charge < 1) {
				GLog.i( Messages.get(this, "no_charge") );
				usesTargeting = false;

			} else if (cursed) {
				GLog.w( Messages.get(this, "cursed") );
				usesTargeting = false;

			} else {
				usesTargeting = true;
				GameScene.selectCell(caster);
			}

		} else if (action.equals(AC_LOCKED)) {
			curUser = hero;
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (charge < 1) {
				GLog.i(Messages.get(this, "no_charge"));
			} else if (cursed) {
				GLog.w(Messages.get(this, "cursed"));
			} else {
				usesTargeting = true;
				GameScene.selectCell(locker);
			}
		}
	}

	final CellSelector.Listener locker = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null || Dungeon.level == null || !Dungeon.level.insideMap(target)
					|| (!Dungeon.level.visited[target] && !Dungeon.level.mapped[target])) return;
			Char affected = Actor.findChar(target);
			if (affected == null) {
				GLog.i(Messages.get(EtherealChains.class, "nothing_to_grab"));
				return;
			}
			applyLegacyLock(affected);
		}

		@Override
		public String prompt() {
			return Messages.get(EtherealChains.class, "prompt");
		}
	};

	void applyLegacyLock(Char affected) {
		if (affected == null || curUser == null || level() <= 1) return;
		float duration = level() * 4f;
		Buff.affect(affected, Locked.class, duration);
		Buff.affect(affected, Silent.class, duration);
		Buff.affect(affected, AttackDown.class, duration).level(90);
		Buff.affect(affected, Slow.class, duration);
		level(level() - 1);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
		if (curUser.sprite != null) curUser.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		curUser.spendAndNext(1f);
		updateQuickslot();
	}

	@Override
	public void resetForTrinity(int visibleLevel) {
		super.resetForTrinity(visibleLevel);
		charge = 5+(level()*2); //sets charge to soft cap
	}

	public CellSelector.Listener caster = new CellSelector.Listener(){

		@Override
		public void onSelect(Integer target) {
			if (target != null && (Dungeon.level.visited[target] || Dungeon.level.mapped[target])){

				//chains cannot be used to go where it is impossible to walk to
				PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
				if (!(Dungeon.level instanceof MiningLevel) && PathFinder.distance[curUser.pos] == Integer.MAX_VALUE){
					GLog.w( Messages.get(EtherealChains.class, "cant_reach") );
					return;
				}
				
				final Ballistica chain = new Ballistica(curUser.pos, target, Ballistica.STOP_TARGET);
				
				if (Actor.findChar( chain.collisionPos ) != null){
					chainEnemy( chain, curUser, Actor.findChar( chain.collisionPos ));
				} else {
					chainLocation( chain, curUser );
				}

			}

		}

		@Override
		public String prompt() {
			return Messages.get(EtherealChains.class, "prompt");
		}
	};
	
	//pulls an enemy to a position along the chain's path, as close to the hero as possible
	private void chainEnemy( Ballistica chain, final Hero hero, final Char enemy ){
		
		if (enemy.properties().contains(Char.Property.IMMOVABLE)) {
			GLog.w( Messages.get(this, "cant_pull") );
			return;
		}
		
		int bestPos = -1;
		for (int i : chain.subPath(1, chain.dist)){
			//prefer to the earliest point on the path
			if (!Dungeon.level.solid[i]
					&& Actor.findChar(i) == null
					&& (!Char.hasProp(enemy, Char.Property.LARGE) || Dungeon.level.openSpace[i])){
				bestPos = i;
				break;
			}
		}
		
		if (bestPos == -1) {
			GLog.i(Messages.get(this, "does_nothing"));
			return;
		}
		
		final int pulledPos = bestPos;
		
		int chargeUse = Dungeon.level.distance(enemy.pos, pulledPos);
		if (chargeUse > charge) {
			GLog.w( Messages.get(this, "no_charge") );
			return;
		}
		
		hero.busy();
		throwSound();
		Sample.INSTANCE.play( Assets.Sounds.CHAINS );
		hero.sprite.parent.add(new Chains(hero.sprite.center(),
				enemy.sprite.center(),
				Effects.Type.ETHEREAL_CHAIN,
				new Callback() {
			public void call() {
				Actor.add(new Pushing(enemy, enemy.pos, pulledPos, new Callback() {
					public void call() {
						enemy.pos = pulledPos;

						charge -= chargeUse;
						Invisibility.dispel(hero);
						Talent.onArtifactUsed(hero);
						updateQuickslot();

						Dungeon.level.occupyCell(enemy);
						Dungeon.observe();
						GameScene.updateFog();
						hero.spendAndNext(1f);

						artifactProc(enemy, visiblyUpgraded(), chargeUse);
					}
				}));
				hero.next();
			}
		}));
	}
	
	//pulls the hero along the chain to the collisionPos, if possible.
	private void chainLocation( Ballistica chain, final Hero hero ){

		//don't pull if rooted
		if (hero.rooted){
			PixelScene.shake( 1, 1f );
			GLog.w( Messages.get(EtherealChains.class, "rooted") );
			return;
		}

		//don't pull if the collision spot is in a wall
		if (Dungeon.level.solid[chain.collisionPos]
			|| !(Dungeon.level.passable[chain.collisionPos] || Dungeon.level.avoid[chain.collisionPos])){
			GLog.i( Messages.get(this, "inside_wall"));
			return;
		}
		
		//don't pull if there are no solid objects next to the pull location
		boolean solidFound = false;
		for (int i : PathFinder.NEIGHBOURS8){
			if (Dungeon.level.solid[chain.collisionPos + i]){
				solidFound = true;
				break;
			}
		}
		if (!solidFound){
			GLog.i( Messages.get(EtherealChains.class, "nothing_to_grab") );
			return;
		}
		
		final int newHeroPos = chain.collisionPos;
		
		int chargeUse = Dungeon.level.distance(hero.pos, newHeroPos);
		if (chargeUse > charge){
			GLog.w( Messages.get(EtherealChains.class, "no_charge") );
			return;
		}
		
		hero.busy();
		throwSound();
		Sample.INSTANCE.play( Assets.Sounds.CHAINS );
		hero.sprite.parent.add(new Chains(hero.sprite.center(),
				DungeonTilemap.raisedTileCenterToWorld(newHeroPos),
				Effects.Type.ETHEREAL_CHAIN,
				new Callback() {
			public void call() {
				Actor.add(new Pushing(hero, hero.pos, newHeroPos, new Callback() {
					public void call() {
						hero.pos = newHeroPos;

						charge -= chargeUse;
						Invisibility.dispel(hero);
						Talent.onArtifactUsed(hero);
						updateQuickslot();

						Dungeon.level.occupyCell(hero);
						hero.spendAndNext(1f);
						Dungeon.observe();
						GameScene.updateFog();
					}
				}));
				hero.next();
			}
		}));
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new chainsRecharge();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		// SPS-PD 0.9.8 charges only through time and defeated-enemy experience.
	}
	
	@Override
	public String desc() {
		String desc = super.desc();

		if (isEquipped( Dungeon.hero )){
			desc += "\n\n";
			if (cursed)
				desc += Messages.get(this, "desc_cursed");
			else
				desc += Messages.get(this, "desc_equipped");
		}
		return desc;
	}

	public class chainsRecharge extends ArtifactBuff{

		@Override
		public boolean act() {
			int chargeTarget = 5+(level()*2);
			if (charge < chargeTarget && !cursed) {
				//gains a charge in 40 - 2*missingCharge turns
				float chargeGain = (1 / (40f - (chargeTarget - charge)*2f));
				partialCharge += chargeGain;
			} else if (cursed && Random.Int(100) == 0){
				Buff.prolong( target, Cripple.class, 10f);
			}

			if (partialCharge >= 1) {
				partialCharge --;
				charge ++;
			}

			updateQuickslot();

			spend( TICK );

			return true;
		}

		public void gainExp( float levelPortion ) {
			if (cursed) return;

			exp += Math.round(levelPortion*100);

			//past the soft charge cap, gaining  charge from leveling is slowed.
			if (charge > 5+(level()*2)){
				levelPortion *= (5+((float)level()*2))/charge;
			}
			partialCharge += levelPortion*10f;

			if (exp > 100+level()*50 && level() < levelCap){
				exp -= 100+level()*50;
				GLog.p( Messages.get(this, "levelup") );
				upgrade();
			}

		}
	}
}
