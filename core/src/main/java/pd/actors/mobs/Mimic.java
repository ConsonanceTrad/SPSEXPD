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
import pd.Challenges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.effects.CellEmitter;
import pd.effects.Pushing;
import pd.effects.Speck;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.artifacts.TimekeepersHourglass;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.items.trinkets.MimicTooth;
import pd.messages.Messages;
import pd.plants.Swiftthistle;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.MimicSprite;
import pd.utils.GLog;
import watabou.noosa.audio.Sample;
import watabou.utils.Bundle;
import watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Mimic extends Mob {
	
	private int level;
	
	{
		spriteClass = MimicSprite.class;

		properties.add(Property.DEMONIC);
		properties.add(Property.UNKNOW);

		EXP = 0;
		
		//mimics are neutral when hidden
		alignment = Alignment.NEUTRAL;
		state = PASSIVE;
	}
	
	public ArrayList<Item> items;

	private boolean stealthy = false;

	public Mimic() {
		if (usesLegacyBehavior()) {
			properties.remove(Property.DEMONIC);
			alignment = Alignment.ENEMY;
			state = SLEEPING;
			immunities.add(ScrollOfPsionicBlast.class);
		}
	}

	private boolean usesLegacyBehavior() {
		return getClass() == Mimic.class;
	}
	
	private static final String LEVEL	= "level";
	private static final String ITEMS	= "items";
	private static final String STEALTHY= "stealthy";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		if (items != null) bundle.put( ITEMS, items );
		bundle.put( LEVEL, level );
		bundle.put( STEALTHY, stealthy );
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		if (bundle.contains( ITEMS )) {
			items = new ArrayList<>((Collection<Item>) ((Collection<?>) bundle.getCollection(ITEMS)));
		}
		level = bundle.getInt( LEVEL );
		adjustStats(level);
		stealthy = bundle.getBoolean(STEALTHY);
		super.restoreFromBundle(bundle);
		if (state != PASSIVE && alignment == Alignment.NEUTRAL){
			alignment = Alignment.ENEMY;
		}
	}

	@Override
	public boolean add(Buff buff) {
		if (super.add(buff)) {
			if (buff.type == Buff.buffType.NEGATIVE && alignment == Alignment.NEUTRAL) {
				alignment = Alignment.ENEMY;
				stopHiding();
				if (sprite != null) sprite.idle();
			}
			return true;
		}
		return false;
	}

	@Override
	public String name() {
		if (alignment == Alignment.NEUTRAL){
			return Messages.get(Heap.class, "chest");
		} else {
			return super.name();
		}
	}

	@Override
	public String description() {
		if (alignment == Alignment.NEUTRAL){
			if (MimicTooth.stealthyMimics()){
				return Messages.get(Heap.class, "chest_desc");
			} else {
				return Messages.get(Heap.class, "chest_desc") + "\n\n" + Messages.get(this, "hidden_hint");
			}
		} else {
			return super.description();
		}
	}

	@Override
	protected boolean act() {
		if (alignment == Alignment.NEUTRAL && state != PASSIVE){
			alignment = Alignment.ENEMY;
			if (sprite != null) sprite.idle();
			if (Dungeon.level.heroFOV[pos]) {
				GLog.w(Messages.get(this, "reveal") );
				CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10);
				Sample.INSTANCE.play(Assets.Sounds.MIMIC);
			}
		}
		return super.act();
	}

	@Override
	public CharSprite sprite() {
		MimicSprite sprite = (MimicSprite) super.sprite();
		if (alignment == Alignment.NEUTRAL) sprite.hideMimic(this);
		return sprite;
	}

	@Override
	public boolean interact(Char c) {
		if (alignment != Alignment.NEUTRAL || c != Dungeon.hero){
			return super.interact(c);
		}
		stopHiding();

		Dungeon.hero.busy();
		Dungeon.hero.sprite.operate(pos);
		if (Dungeon.hero.invisible <= 0
				&& Dungeon.hero.buff(Swiftthistle.TimeBubble.class) == null
				&& Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) == null){
			return doAttack(Dungeon.hero);
		} else {
			sprite.idle();
			alignment = Alignment.ENEMY;
			Dungeon.hero.spendAndNext(1f);
			return true;
		}
	}

	@Override
	public void onAttackComplete() {
		super.onAttackComplete();
		if (alignment == Alignment.NEUTRAL){
			alignment = Alignment.ENEMY;
			Dungeon.hero.spendAndNext(1f);
			enemySeen = true;
		}
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (state == PASSIVE){
			alignment = Alignment.ENEMY;
			stopHiding();
		}
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(int dmg, Object src) {
		if (state == PASSIVE){
			alignment = Alignment.ENEMY;
			stopHiding();
		}
		super.damage(dmg, src);
	}

	@Override
	public void die(Object cause) {
		if (state == PASSIVE){
			alignment = Alignment.ENEMY;
			stopHiding();
		}
		super.die(cause);
	}

	public void stopHiding(){
		state = HUNTING;
		if (sprite != null) sprite.idle();
		if (Actor.chars().contains(this) && Dungeon.level.heroFOV[pos]) {
			enemy = Dungeon.hero;
			target = Dungeon.hero.pos;
			GLog.w(Messages.get(this, "reveal") );
			CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10);
			Sample.INSTANCE.play(Assets.Sounds.MIMIC);
		}
	}

	//stealthy mimics have changes to visual behaviour that make them much harder to detect
	public boolean stealthy(){
		return stealthy;
	}

	@Override
	public int damageRoll() {
		if (usesLegacyBehavior()) return Random.NormalIntRange(HT / 20, HT / 10);
		if (alignment == Alignment.NEUTRAL){
			return Random.NormalIntRange( 2 + 2*level, 2 + 2*level);
		} else {
			return Random.NormalIntRange( 1 + level, 2 + 2*level);
		}
	}

	@Override
	public int drRoll() {
		if (usesLegacyBehavior()) return super.drRoll();
		return super.drRoll() + Random.NormalIntRange(0, 1 + level/2);
	}

	@Override
	public void beckon( int cell ) {
		if (alignment != Alignment.NEUTRAL) {
			super.beckon(cell);
		}
	}

	@Override
	public int attackSkill( Char target ) {
		if (usesLegacyBehavior()) return 9 + level;
		if (target != null && alignment == Alignment.NEUTRAL && target.invisible <= 0){
			return INFINITE_ACCURACY;
		} else {
			return 6 + level;
		}
	}

	public void setLevel( int level ){
		this.level = level;
		adjustStats(level);
	}
	
	public void adjustStats( int level ) {
		this.level = level;
		if (usesLegacyBehavior()) {
			HT = (30 + level) * 4;
			EXP = 2 + 2 * (level - 1) / 5;
			defenseSkill = attackSkill(null) / 2;
			enemySeen = true;
			return;
		}
		HP = HT = (1 + level) * 6;
		defenseSkill = 2 + level/2;
		
		enemySeen = true;
	}
	
	@Override
	public void rollToDropLoot(){
		
		if (items != null) {
			for (Item item : items) {
				Heap heap = Dungeon.level == null ? null : Dungeon.level.drop(item, pos);
				if (heap != null && heap.sprite != null) heap.sprite.drop();
			}
			items = null;
		}
		super.rollToDropLoot();
	}

	@Override
	public float spawningWeight() {
		return 0f;
	}

	@Override
	public boolean reset() {
		if (state != PASSIVE) state = WANDERING;
		return true;
	}

	public static Mimic spawnAt( int pos, Item... items){
		return spawnAt(pos, Mimic.class, items);
	}

	public static Mimic spawnAt( int pos, Class mimicType, Item... items){
		return spawnAt(pos, mimicType, true, items);
	}

	public static Mimic spawnAt( int pos, boolean useDecks, Item... items){
		return spawnAt(pos, Mimic.class, useDecks, items);
	}

	public static Mimic spawnAt( int pos, Class mimicType, boolean useDecks, Item... items){
		Mimic m;
		if (mimicType == GoldenMimic.class){
			m = new GoldenMimic();
		} else if (mimicType == CrystalMimic.class) {
			m = new CrystalMimic();
		} else if (mimicType == EbonyMimic.class) {
			m = new EbonyMimic();
		} else {
			m = new Mimic();
		}

		m.items = new ArrayList<>( Arrays.asList(items) );
		m.setLevel( m.usesLegacyBehavior() ? legacyDungeonDepth() : Dungeon.scalingDepth() );
		if (m.usesLegacyBehavior()) {
			m.HP = m.HT;
			m.state = m.HUNTING;
			m.alignment = Alignment.ENEMY;
		}
		m.pos = pos;

		//generate an extra reward for killing the mimic
		if (!m.usesLegacyBehavior()) m.generatePrize(useDecks);

		if (MimicTooth.stealthyMimics()){
			m.stealthy = true;
		}

		return m;
	}

	public static Mimic spawnAt(int pos, List<Item> items) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(pos)) return null;
		Char occupant = Actor.findChar(pos);
		if (occupant != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int offset : watabou.utils.PathFinder.NEIGHBOURS8) {
				int cell = pos + offset;
				if (Dungeon.level.insideMap(cell)
						&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])
						&& Actor.findChar(cell) == null) candidates.add(cell);
			}
			if (candidates.isEmpty()) return null;
			int destination = Random.element(candidates);
			Actor.addDelayed(new Pushing(occupant, occupant.pos, destination), -1);
			occupant.pos = destination;
			Dungeon.level.occupyCell(occupant);
		}

		Mimic mimic = new Mimic();
		mimic.items = new ArrayList<>(items);
		mimic.adjustStats(legacyDungeonDepth());
		mimic.HP = mimic.HT;
		mimic.pos = pos;
		mimic.state = mimic.HUNTING;
		GameScene.add(mimic, 1f);
		Dungeon.level.occupyCell(mimic);
		if (mimic.sprite != null) {
			if (Dungeon.hero != null) mimic.sprite.turnTo(pos, Dungeon.hero.pos);
			if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[pos]) {
				CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10);
				Sample.INSTANCE.play(Assets.Sounds.MIMIC);
			}
		}
		return mimic;
	}

	protected void generatePrize( boolean useDecks ){
		Item reward = null;
		do {
			switch (Random.Int(5)) {
				case 0:
					reward = new Gold().random();
					break;
				case 1:
					reward = Generator.randomMissile(!useDecks);
					break;
				case 2:
					reward = Generator.randomArmor();
					break;
				case 3:
					reward = Generator.randomWeapon(!useDecks);
					break;
				case 4:
					reward = useDecks ? Generator.random(Generator.Category.RING) : Generator.randomUsingDefaults(Generator.Category.RING);
					break;
			}
		} while (reward == null || Challenges.isItemBlocked(reward));
		items.add(reward);

		if (MimicTooth.stealthyMimics()){
			//add an extra random item if player has a mimic tooth
			items.add(Generator.randomUsingDefaults());
		}
	}

}
