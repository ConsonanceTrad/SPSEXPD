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

package pd.actors.mobs.npcs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.mobs.FetidRat;
import pd.actors.mobs.GnollTrickster;
import pd.actors.mobs.GreatCrab;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.artifacts.Artifact;
import pd.items.consum.eggs.Egg;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.weapon.Weapon;
import pd.journal.Notes;
import pd.levels.SewerLevel;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.GhostSprite;
import pd.utils.GLog;
import pd.windows.WndQuest;
import pd.windows.WndSadGhost;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;
import pd.messages.InlineText;

public class Ghost extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Ghost.class)
			.t("name", "悲伤幽灵")
			.t("rat_1", "你好，%s...曾几何时，我也如你这般——既强大又自信...直到被一只邪恶的野兽所害...我不能离开这个地方...除非完成复仇...杀死_腐臭老鼠_，就是它夺走了我的生命...\n\n它就在这一层游荡...四处散播污秽..._小心它的周边的恶臭云雾和腐蚀性的撕咬，它产生的酸性粘液可溶于水..._")
			.t("rat_2", "请帮助我...杀了那个令人憎恶的东西...\n\n_在水附近与它战斗...躲开它的臭气..._")
			.t("gnoll_1", "你好，%s...曾几何时，我也如你这般——既强大又自信...但我被一个狡猾的敌人杀死了...我不能离开这个地方...除非完成复仇...杀死_豺狼诡术师_，就是它夺走了我的生命...\n\n它与其他豺狼人不同...它会隐匿自身，还会使用投掷武器..._小心它的毒镖和火镖，尽量靠近它..._")
			.t("gnoll_2", "请帮助我...杀了那个诡诈的家伙...\n\n_别让它打到你...离它越近越好..._")
			.t("crab_1", "你好，%s...曾几何时，我也如你这般——既强大又自信...但我被一个古老的生物杀死了...我不能离开这个地方...除非完成复仇...杀死_巨钳螃蟹_，就是它夺走了我的生命...\n\n它经历了无数岁月的洗礼...有一个巨大的蟹钳和非常厚重的蟹壳..._小心它的蟹钳，你必须偷袭这只巨蟹，否则它会用钳子格挡你的攻击..._")
			.t("crab_2", "请帮助我...杀了那个甲壳类...\n\n_如果它发现你...就会挡住你所有的攻击..._")
			.t("find_me", "谢谢你...来找我吧...")
			.t("desc", "这个幽灵几乎不可见。它看起来像是由一片无定形的昏暗光斑和一张悲痛的面孔所组成的。");
	}




	{
		spriteClass = GhostSprite.class;
		
		flying = true;

		state = WANDERING;
		properties.add(Property.UNDEAD);
	}

	@Override
	public Notes.Landmark landmark() {
		return Notes.Landmark.GHOST;
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}
	
	@Override
	public float speed() {
		return 0.5f;
	}
	
	@Override
	protected Char chooseEnemy() {
		return null;
	}

	@Override
	public void damage( int dmg, Object src ) {
		//do nothing
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	public boolean interact(Char c) {
		sprite.turnTo( pos, c.pos );
		
		Sample.INSTANCE.play( Assets.Sounds.GHOST );

		if (c != Dungeon.hero){
			return super.interact(c);
		}
		
		if (Quest.given) {
			Quest.ensureLegacyRewards();
			if (Quest.artifact != null) {
				if (Quest.processed) {
					Game.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							GameScene.show(new WndSadGhost(Ghost.this, Quest.type));
						}
					});
				} else {
					Game.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							switch (Quest.type) {
								case 1:
								default:
									GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "rat_2")));
									break;
								case 2:
									GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "gnoll_2")));
									break;
								case 3:
									GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "crab_2")));
									break;
							}
						}
					});

					int newPos = -1;
					for (int i = 0; i < 10; i++) {
						newPos = Dungeon.level.randomRespawnCell(this);
						if (newPos != -1) break;
					}
					if (newPos != -1) {
						if (sprite != null && pos >= 0 && pos < Dungeon.level.length()) {
							CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
						}
						pos = newPos;
						if (sprite != null) {
							sprite.place(pos);
							sprite.visible = Dungeon.level.heroFOV == null || Dungeon.level.heroFOV[pos];
						}
					}

				}
			}
		} else {
			Mob questBoss;
			String txt_quest;

			switch (Quest.type){
				case 1: default:
					questBoss = new FetidRat();
					txt_quest = Messages.get(this, "rat_1", Messages.titleCase(Dungeon.hero.name())); break;
				case 2:
					questBoss = new GnollTrickster();
					txt_quest = Messages.get(this, "gnoll_1", Messages.titleCase(Dungeon.hero.name())); break;
				case 3:
					questBoss = new GreatCrab();
					txt_quest = Messages.get(this, "crab_1", Messages.titleCase(Dungeon.hero.name())); break;
			}

			questBoss.pos = Dungeon.level.randomRespawnCell( this );

			if (questBoss.pos != -1) {
				GameScene.add(questBoss);
				Quest.given = true;
				Notes.add(Notes.Landmark.GHOST);
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndQuest(Ghost.this, txt_quest));
					}
				});
			}

		}

		return true;
	}

	public static class Quest {
		
		private static boolean spawned;

		private static int type;

		private static boolean given;
		private static boolean processed;
		
		private static int depth;
		
		public static Artifact artifact;
		public static Ring ring;
		public static Egg pet;

		// Read-only migration holders for saves made before the SPS reward table was restored.
		private static Weapon legacyWeapon;
		private static Item legacyArmor;
		
		public static void reset() {
			spawned = false;
			
			artifact = null;
			ring = null;
			pet = null;
			legacyWeapon = null;
			legacyArmor = null;
		}
		
		private static final String NODE		= "sadGhost";
		
		private static final String SPAWNED		= "spawned";
		private static final String TYPE        = "type";
		private static final String GIVEN		= "given";
		private static final String PROCESSED	= "processed";
		private static final String DEPTH		= "depth";
		private static final String ARTIFACT		= "artifact";
		private static final String RING			= "ring";
		private static final String PET			= "pet";
		private static final String LEGACY_WEAPON = "weapon";
		private static final String LEGACY_ARMOR = "armor";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				
				node.put( TYPE, type );
				
				node.put( GIVEN, given );
				node.put( DEPTH, depth );
				node.put( PROCESSED, processed );
				
				node.put(ARTIFACT, artifact);
				node.put(RING, ring);
				node.put(PET, pet);
				if (artifact == null && legacyWeapon != null) node.put(LEGACY_WEAPON, legacyWeapon);
				if (artifact == null && legacyArmor != null) node.put(LEGACY_ARMOR, legacyArmor);
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {
			
			Bundle node = bundle.getBundle( NODE );

			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {

				type = node.getInt(TYPE);
				given	= node.getBoolean( GIVEN );
				processed = node.getBoolean( PROCESSED );

				depth	= node.getInt( DEPTH );
				
				artifact = node.contains(ARTIFACT) ? (Artifact) node.get(ARTIFACT) : null;
				ring = node.contains(RING) ? (Ring) node.get(RING) : null;
				pet = node.contains(PET) ? (Egg) node.get(PET) : null;
				legacyWeapon = node.contains(LEGACY_WEAPON) ? (Weapon) node.get(LEGACY_WEAPON) : null;
				legacyArmor = node.contains(LEGACY_ARMOR) ? (Item) node.get(LEGACY_ARMOR) : null;
			} else {
				reset();
			}
		}
		
		public static void spawn(SewerLevel level) {
			if (!spawned && Dungeon.depth > 1 && Random.Int( Math.max(1, 8 - Dungeon.depth) ) == 0) {
				
				Ghost ghost = new Ghost();
				int spawnPos = -1;
				for (int i = 0; i < 100 && spawnPos < 0; i++) {
					int candidate = level.randomRespawnCell(ghost);
					if (candidate >= 0) spawnPos = candidate;
				}
				if (spawnPos < 0) return;
				ghost.pos = spawnPos;
				level.mobs().add( ghost );
				
				spawned = true;
				//dungeon depth determines type of quest.
				//depth2=fetid rat, 3=gnoll trickster, 4=great crab
				type = Dungeon.depth-1;
				
				given = false;
				processed = false;
				depth = Dungeon.depth;

				generateRewards();

			}
		}

		private static void generateRewards() {
			for (int i = 0; i < 100 && (artifact == null || artifact.cursed); i++) {
				artifact = legacyChoice(Generator.Category.ARTIFACT, Artifact.class);
			}
			if (artifact != null) artifact.cursed = false;

			for (int i = 0; i < 100 && (ring == null || ring.cursed); i++) {
				ring = legacyChoice(Generator.Category.RING, Ring.class);
			}
			if (ring != null) ring.cursed = false;
			//SPSXPD: BASEPET 类别里混有「奖励包」(RandomPetEgg 家族, extends Item)。
			//若是包就从中随机取一颗魂石，保证 pet 始终是 Egg。
			Item petReward = Generator.random(Generator.Category.BASEPET);
			if (petReward instanceof pd.items.consum.eggs.randomone.RandomPetEgg) {
				Class<? extends Egg>[] pool = ((pd.items.consum.eggs.randomone.RandomPetEgg) petReward).possibleEggs();
				petReward = Reflection.newInstance(pool[Random.Int(pool.length)]);
			}
			pet = (petReward instanceof Egg) ? (Egg) petReward : null;
			if (artifact != null) artifact.identify(false);
			if (ring != null) ring.identify(false);
		}

		private static <T extends Item> T legacyChoice(Generator.Category category, Class<T> type) {
			int firstIndex = Random.chances(category.probs);
			int secondIndex = Random.chances(category.probs);
			if (firstIndex < 0 || secondIndex < 0) return null;
			T first = type.cast(Reflection.newInstance(category.classes[firstIndex]));
			T second = type.cast(Reflection.newInstance(category.classes[secondIndex]));
			if (first != null) first.random();
			if (second != null) second.random();
			return Random.Int(6) > 2 ? first : second;
		}

		private static void ensureLegacyRewards() {
			if (artifact == null && (legacyWeapon != null || legacyArmor != null)) {
				generateRewards();
				legacyWeapon = null;
				legacyArmor = null;
			}
		}
		
		public static void process() {
			if (spawned && given && !processed && (depth == Dungeon.depth)) {
				GLog.n( Messages.get(Ghost.class, "find_me") );
				for (Mob mob : Dungeon.level.mobs()) {
					if (mob instanceof Ghost) mob.beckon(Dungeon.hero.pos);
				}
				Sample.INSTANCE.play( Assets.Sounds.GHOST );
				processed = true;
			}
		}

		public static boolean active(){
			return spawned && given && !processed && depth == Dungeon.depth;
		}
		
		public static void complete() {
			artifact = null;
			ring = null;
			pet = null;
			legacyWeapon = null;
			legacyArmor = null;
			
			Notes.remove( Notes.Landmark.GHOST );
		}

		public static boolean processed(){
			return spawned && processed;
		}
		
		public static boolean completed(){
			return processed() && artifact == null && ring == null && pet == null;
		}
	}
}
