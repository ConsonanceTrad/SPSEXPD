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

import pd.Dungeon;
import pd.items.Generator;
import pd.items.StrBottle;
import pd.items.Stylus;
import pd.items.Torch;
import pd.items.Weightstone;
import pd.items.misc.LuckyBadge;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.scrolls.ScrollOfMagicalInfusion;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.consum.stones.StoneOfEnchantment;
import pd.items.consum.stones.StoneOfIntuition;
import pd.items.equipment.trinkets.MossyClump;
import pd.items.equipment.trinkets.TrapMechanism;
import pd.items.equipment.trinkets.TrinketCatalyst;
import pd.journal.Notes;
import render.utils.math.Random;

/**
 * 每层的「感受」（feeling）抽取，以及随之排队的补给品与视野调整。
 *
 * 从 Level.create() 中独立出来：这是纯策略段——只读 Dungeon 的深度/挑战/限量掉落状态，
 * 写 level.feeling 与 level.viewDistance，并向 GroundItems 队列塞入本层额外补给
 * （食物、力量药剂、符文石、铆钉等；升级/魔力灌注卷轴只经幸运稀有奖励随机出现）。它不参与覆写。
 *
 * 两种感受表：普通层用 14 抽 1 的老式表（含 LARGE/TRAPS/SECRETS 与两个饰品的覆盖概率），
 * SPS 普通层在 2~24 层改用 10 抽 1 的简化表，并按 20 层前后调换 CHASM 与 DARK 的权重。
 */
public final class FloorFeeling {

	private FloorFeeling() { }

	public static void apply( Level level ) {
		if (!Dungeon.bossLevel() && Dungeon.branch == 0) {

			if (level instanceof SpsRegularLevel) {
				// SPS-PD queued these supplies on every ordinary floor before painting it.
				GroundItems.addItemToSpawn( level, Generator.random(Generator.Category.FOOD));
				GroundItems.addItemToSpawn( level, Generator.random(Generator.Category.FOOD));
				//SPSEXPD: 升级/魔力灌注卷轴不再每层固定刷新，改为高稀有刷新物（幸运稀有奖励三选一）
				if (Random.Int(2) == 0) {
					GroundItems.addItemToSpawn( level, new Stylus());
					GroundItems.addItemToSpawn( level, new Weightstone());
				}
				if (Dungeon.posNeeded() && !Dungeon.shopOnLevel()) {
					Dungeon.LimitedDrops.STRENGTH_POTIONS.count++;
					GroundItems.addItemToSpawn( level, new StrBottle());
				}
				if (Random.Float() < LuckyBadge.rareRewardChance(LuckyBadge.luckBonus(Dungeon.hero))) {
					//SPSEXPD: 稀有奖励改三选一，升级/魔力灌注卷轴作为高稀有刷新物
					GroundItems.addItemToSpawn( level, Random.oneOf(
							new ScrollOfUpgrade(),
							new ScrollOfMagicalInfusion(),
							new PotionOfHealing()) );
				}
			} else {
				GroundItems.addItemToSpawn( level, Generator.random(Generator.Category.FOOD));
				if (Random.Float() < LuckyBadge.rareRewardChance(LuckyBadge.luckBonus(Dungeon.hero))) {
					//SPSEXPD: 稀有奖励改三选一，升级/魔力灌注卷轴作为高稀有刷新物
					GroundItems.addItemToSpawn( level, Random.oneOf(
							new ScrollOfUpgrade(),
							new ScrollOfMagicalInfusion(),
							new PotionOfHealing()) );
				}

				//SPSEXPD: 永久力量一律来自力量之瓶（力量药剂已改为临时力量道具，走随机药水池）
				if (Dungeon.posNeeded() && !Dungeon.shopOnLevel()) {
					Dungeon.LimitedDrops.STRENGTH_POTIONS.count++;
					GroundItems.addItemToSpawn( level, new StrBottle() );
				}
				//SPSEXPD: 升级卷轴保底（souNeeded/UPGRADE_SCROLLS）已移除，升级/魔力灌注卷轴
				// 改为高稀有刷新物：SCROLL 池权重 3（与嬗变相同）随机刷新，商店走随机卷轴格；
				// 禁咒挑战改为随机抽取时半数替换（Generator.applyNoScrollsThinning）
				if (Dungeon.asNeeded()) {
					Dungeon.LimitedDrops.ARCANE_STYLI.count++;
					GroundItems.addItemToSpawn( level,  new Stylus() );
				}
				if ( Dungeon.enchStoneNeeded() ){
					Dungeon.LimitedDrops.ENCH_STONE.drop();
					GroundItems.addItemToSpawn( level,  new StoneOfEnchantment() );
				}
				if ( Dungeon.intStoneNeeded() ){
					Dungeon.LimitedDrops.INT_STONE.drop();
					GroundItems.addItemToSpawn( level,  new StoneOfIntuition() );
				}
				if ( Dungeon.trinketCataNeeded() ){
					Dungeon.LimitedDrops.TRINKET_CATA.drop();
					GroundItems.addItemToSpawn( level,  new TrinketCatalyst());
				}
			}
			
			if (level instanceof SpsRegularLevel && Dungeon.depth > 1 && Dungeon.depth < 39) {
				//SPSEXPD: SPS 普通层的氛围抽签。总出现率 60%（300 分之 180），
				//其中黑暗层只占 2%（6/300），其余 58% 由另外 6 种氛围均分（各 29/300 ≈ 9.67%），
				//剩下 120/300 即 40% 为无变异 NONE。
				//用 300 分格是为了让 2% 与「其余均分」都能整除，不必凑近似值。
				//（LARGE/TRAPS/SECRETS 的实现一直在，此前只是抽签表里没有它们。）
				final int rollRange    = 300;   // 分格总数
				final int darkRoll     = 6;     // 2%  -> DARK
				final int otherRoll    = 29;    // 每种其它氛围
				final int feelingRoll  = 180;   // 60% -> 出现氛围（= 6 + 6*29）
				int roll = Random.Int(rollRange);
				if (roll < darkRoll) {
					//SPSEXPD: 黑暗层给 3 根火把并把视野收窄到 1/3
					level.feeling = Level.Feeling.DARK;
					GroundItems.addItemToSpawn( level, new Torch());
					GroundItems.addItemToSpawn( level, new Torch());
					GroundItems.addItemToSpawn( level, new Torch());
					level.viewDistance = (int)Math.ceil(level.viewDistance / 3f);
				} else if (roll < feelingRoll) {
					switch ((roll - darkRoll) / otherRoll) {
						case 0: level.feeling = Level.Feeling.CHASM; break;
						case 1: level.feeling = Level.Feeling.WATER; break;
						case 2: level.feeling = Level.Feeling.GRASS; break;
						case 3:
							//SPSEXPD: 广阔层——房间数与刷怪量由 RegularLevel 的 Feeling.LARGE 分支放大
							level.feeling = Level.Feeling.LARGE;
							GroundItems.addItemToSpawn( level, Generator.random(Generator.Category.FOOD));
							break;
						case 4: level.feeling = Level.Feeling.TRAPS; break;
						default: level.feeling = Level.Feeling.SECRETS; break;
					}
				} else {
					level.feeling = Level.Feeling.NONE;
				}
			} else if (Dungeon.depth > 1) {
				//50% chance of getting a level level.feeling
				//~7.15% chance for each level.feeling
				switch (Random.Int( 14 )) {
					case 0:
						level.feeling = Level.Feeling.CHASM;
						break;
					case 1:
						level.feeling = Level.Feeling.WATER;
						break;
					case 2:
						level.feeling = Level.Feeling.GRASS;
						break;
					case 3:
						level.feeling = Level.Feeling.DARK;
						level.viewDistance = Math.round(5*level.viewDistance/8f);
						break;
					case 4:
						level.feeling = Level.Feeling.LARGE;
						GroundItems.addItemToSpawn( level, Generator.random(Generator.Category.FOOD));
						break;
					case 5:
						level.feeling = Level.Feeling.TRAPS;
						break;
					case 6:
						level.feeling = Level.Feeling.SECRETS;
						break;
					default:
						//if-else statements are fine here as only one chance can be above 0 at a time
						// we pre-generate the floats to ensure Random is called consistently
						float mossyChance = Random.Float();
						float trapMechChance = Random.Float();
						if (mossyChance < MossyClump.overrideNormalLevelChance()){
							level.feeling = MossyClump.getNextFeeling();
						} else if (trapMechChance < TrapMechanism.overrideNormalLevelChance()) {
							level.feeling = TrapMechanism.getNextFeeling();
						} else {
							level.feeling = Level.Feeling.NONE;
						}
				}
			}

			//SPSEXPD: 本层若有特殊氛围，写进探险手册（只记有氛围的层，NONE 不记）
			Notes.Landmark landmark = feelingLandmark( level.feeling );
			if (landmark != null) Notes.add( landmark );
		}
	}

	/** SPSEXPD: 氛围 -> 探险手册地标。无变异（NONE）返回 null，即不写入。 */
	private static Notes.Landmark feelingLandmark( Level.Feeling feeling ) {
		if (feeling == null) return null;
		switch (feeling) {
			case CHASM:   return Notes.Landmark.CHASM_FLOOR;
			case WATER:   return Notes.Landmark.WATER_FLOOR;
			case GRASS:   return Notes.Landmark.GRASS_FLOOR;
			case DARK:    return Notes.Landmark.DARK_FLOOR;
			case LARGE:   return Notes.Landmark.LARGE_FLOOR;
			case TRAPS:   return Notes.Landmark.TRAPS_FLOOR;
			case SECRETS: return Notes.Landmark.SECRETS_FLOOR;
			default:      return null;
		}
	}
}
