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

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Light;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Sleep;
import pd.effects.Speck;
import pd.items.Item;
import pd.items.consum.food.MysteryMeat;
import pd.items.consum.scrolls.ScrollOfDummy;
import pd.items.consum.scrolls.ScrollOfLullaby;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.equipment.wands.WandOfCharm;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.SuccubusSprite;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Succubus extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Succubus.class)
			.t("name", "魅魔")
			.t("desc", "魅魔是一种善于操纵敌人的精神的变形恶魔。这一只化为了哥特风的苍白人形，或许是为了吸引矮人术士？\n\n在攻击时，魅魔能够暂时魅惑目标，使其无法直接攻击魅魔本身。魅魔在攻击被魅惑的目标时能吸取其生命精华。");
	}



	private static final int BLINK_DELAY = 5;

	private int blinkCooldown = 0;
	
	{
		spriteClass = SuccubusSprite.class;
		
		HP = HT = 110 + legacyDepthAdjustment(0) * Random.NormalIntRange(5, 7);
		defenseSkill = 25 + legacyDepthAdjustment(1);
		viewDistance = Light.DISTANCE;
		
		EXP = 12;
		maxLvl = 35;
		
		loot = ScrollOfLullaby.class;
		lootChance = 0.05f;

		properties.add(Property.DEMONIC);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(15, 25 + legacyDepthAdjustment(0));
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int(3) == 0) {
			Buff.affect(enemy, Charm.class, Random.IntRange(3, 7)).object = id();
			if (enemy.sprite != null && Dungeon.level.heroFOV[enemy.pos]) {
				enemy.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
				Sample.INSTANCE.play(Assets.Sounds.CHARMS);
			}
		}
		int healing = Math.min(damage, HT - HP);
		if (healing > 0 && buff(BeOld.class) == null) {
			HP += healing;
			if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
		}
		
		return damage;
	}
	
	@Override
	protected boolean getCloser( int target ) {
		if (fieldOfView[target] && Dungeon.level.distance(pos, target) > 2
				&& blinkCooldown <= 0 && !rooted && buff(Silent.class) == null) {
			
			if (blink( target )) {
				spend(-1 / speed());
				return true;
			} else {
				return false;
			}
			
		} else {

			blinkCooldown--;
			return super.getCloser( target );
			
		}
	}
	
	private boolean blink( int target ) {
		
		Ballistica route = new Ballistica( pos, target, Ballistica.PROJECTILE);
		int cell = route.collisionPos;

		//can't occupy the same cell as another char, so move back one.
		if (Actor.findChar( cell ) != null && cell != this.pos)
			cell = route.path.get(route.dist-1);

		if (Dungeon.level.avoid[ cell ] || (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[cell])){
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8) {
				cell = route.collisionPos + n;
				if (Dungeon.level.passable[cell]
						&& Actor.findChar( cell ) == null
						&& (!properties().contains(Property.LARGE) || Dungeon.level.openSpace[cell])) {
					candidates.add( cell );
				}
			}
			if (candidates.size() > 0)
				cell = Random.element(candidates);
			else {
				blinkCooldown = BLINK_DELAY;
				return false;
			}
		}
		
		ScrollOfTeleportation.appear( this, cell );

		blinkCooldown = BLINK_DELAY;
		return true;
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 40 + legacyDepthAdjustment(1);
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(5, 10);
	}

	@Override
	public Item createLoot() {
		return super.createLoot();
	}

	@Override
	public void rollToDropLoot() {
		super.rollToDropLoot();
		if (Dungeon.hero == null || Dungeon.level == null || !legacyLootLevelEligible()
				|| Random.Float() >= legacySecondaryLootChance(0.1f)) return;
		Dungeon.level.drop(new MysteryMeat(), pos).sprite.drop();
	}

	@Override public Item SupercreateLoot() { return Random.oneOf(new ScrollOfDummy(), new WandOfCharm()); }

	{
		immunities.add(Sleep.class);
	}

	private static final String BLINK_CD = "blink_cd";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BLINK_CD, blinkCooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		blinkCooldown = bundle.getInt(BLINK_CD);
	}
}
