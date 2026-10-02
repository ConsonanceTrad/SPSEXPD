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
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.effects.Pushing;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.sprites.SpectralNecromancerSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class SpectralNecromancer extends Necromancer {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpectralNecromancer.class)
			.t("name", "幽魂法师")
			.t("desc", "死灵法师们通常对召唤骷髅欲罢不能，但其中某些个体却偏爱使唤更为缥缈无形的造物。幽魂法师们便选择了怨灵作为他们的爪牙！\n\n怨灵的个体战斗力或许不如骷髅，但这帮幽魂法师无论召唤出多少手下都不嫌多！");
	}




	{
		spriteClass = SpectralNecromancerSprite.class;
	}

	private ArrayList<Integer> wraithIDs = new ArrayList<>();

	@Override
	protected boolean act() {
		if (summoning && state != HUNTING){
			summoning = false;
			if (sprite instanceof SpectralNecromancerSprite) {
				((SpectralNecromancerSprite) sprite).cancelSummoning();
			}
		}
		return super.act();
	}

	@Override
	public void rollToDropLoot() {
		if (Dungeon.hero.lvl > maxLvl + 2) return;

		super.rollToDropLoot();

		int ofs;
		do {
			ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs]);
		Dungeon.level.drop( new ScrollOfRemoveCurse(), pos + ofs ).sprite.drop( pos );
	}

	@Override
	public void die(Object cause) {
		for (int ID : wraithIDs){
			Actor a = Actor.findById(ID);
			if (a instanceof Wraith && ((Wraith) a).alignment == alignment){
				((Wraith) a).die(null);
			}
		}

		super.die(cause);
	}

	private static final String WRAITH_IDS = "wraith_ids";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		int[] wraithIDArr = new int[wraithIDs.size()];
		int i = 0; for (Integer val : wraithIDs){ wraithIDArr[i] = val; i++; }
		bundle.put(WRAITH_IDS, wraithIDArr);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		wraithIDs.clear();
		for (int i : bundle.getIntArray(WRAITH_IDS)){
			wraithIDs.add(i);
		}
	}

	@Override
	public void summonMinion() {
		if (Actor.findChar(summoningPos) != null) {

			int pushPos = pos;
			for (int c : PathFinder.NEIGHBOURS8) {
				if (Actor.findChar(summoningPos + c) == null
						&& Dungeon.level.passable[summoningPos + c]
						&& (Dungeon.level.openSpace[summoningPos + c] || !hasProp(Actor.findChar(summoningPos), Property.LARGE))
						&& Dungeon.level.trueDistance(pos, summoningPos + c) > Dungeon.level.trueDistance(pos, pushPos)) {
					pushPos = summoningPos + c;
				}
			}

			//no push if char is immovable
			if (Char.hasProp(Actor.findChar(summoningPos), Property.IMMOVABLE)){
				pushPos = pos;
			}

			//push enemy, or wait a turn if there is no valid pushing position
			if (pushPos != pos) {
				Char ch = Actor.findChar(summoningPos);
				Actor.add( new Pushing( ch, ch.pos, pushPos ) );

				ch.pos = pushPos;
				Dungeon.level.occupyCell(ch );

			} else {

				Char blocker = Actor.findChar(summoningPos);
				if (blocker.alignment != alignment){
					blocker.damage( Random.NormalIntRange(2, 10), new SummoningBlockDamage() );
					if (blocker == Dungeon.hero && !blocker.isAlive()){
						Badges.validateDeathFromEnemyMagic();
						Dungeon.fail(this);
						GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
					}
				}

				spend(TICK);
				return;
			}
		}

		summoning = firstSummon = false;

		Wraith wraith = Wraith.spawnAt(summoningPos, Wraith.class);
		if (wraith == null){
			spend(TICK);
			return;
		}
		wraith.adjustStats(4);
		Dungeon.level.occupyCell( wraith );
		((SpectralNecromancerSprite)sprite).finishSummoning();

		for (Buff b : buffs()){
			if (b.revivePersists) {
				Buff.affect(wraith, b.getClass());
			}
		}
		wraithIDs.add(wraith.id());
	}
}
