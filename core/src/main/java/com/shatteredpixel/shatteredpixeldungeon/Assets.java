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

package com.shatteredpixel.shatteredpixeldungeon;

public class Assets {

	public static class Effects {
		public static final String EFFECTS      = "effects/effects.png";
		public static final String FIREBALL     = "effects/fireball.png";
		public static final String SPECKS       = "effects/specks.png";
		public static final String SPS_SPECKS   = "effects/sps_specks.png";
		public static final String SPELL_ICONS  = "effects/spell_icons.png";
		public static final String TEXT_ICONS   = "effects/text_icons.png";
	}

	public static class Environment {
		public static final String TERRAIN_FEATURES = "environment/features/terrain_features.png";
		public static final String SPS_FEATURES = "environment/features/sps_features.png";
		public static final String SPS_TILES_TOWN = "environment/tiles/sps_tiles_town.png";
		public static final String SPS_TILES_SNOW_TOWN = "environment/tiles/sps_tiles_snow_town.png";
		public static final String SPS_TILES_FOREST = "environment/tiles/sps_tiles_forest.png";
		public static final String SPS_TILES_PUZZLE = "environment/tiles/sps_tiles_puzzle.png";
		public static final String SPS_TILES_SPRING = "environment/tiles/sps_tiles_spring.png";
		public static final String SPS_TILES_SEWERS_LEGACY = "environment/tiles/sps_tiles_sewers_legacy.png";
		public static final String SPS_TILES_PRISON_LEGACY = "environment/tiles/sps_tiles_prison_legacy.png";
		public static final String SPS_TILES_CAVES_LEGACY = "environment/tiles/sps_tiles_caves_legacy.png";
		public static final String SPS_TILES_CITY_LEGACY = "environment/tiles/sps_tiles_city_legacy.png";
		public static final String SPS_TILES_HALLS_LEGACY = "environment/tiles/sps_tiles_halls_legacy.png";
		public static final String SPS_TILES_MAGIC_CAVE = "environment/tiles/sps_tiles_magic_cave.png";
		public static final String SPS_WATER_PRISON = "environment/water/sps_water_prison.png";
		public static final String SPS_TILES_SKELETON = "environment/tiles/sps_tiles_skeleton.png";
		public static final String SPS_TILES_BEACH = "environment/tiles/sps_tiles_beach.png";
		public static final String SPS_TILES_VAULT = "environment/tiles/sps_tiles_vault.png";
		public static final String SPS_TILES_HONEY = "environment/tiles/sps_tiles_honey.png";
		public static final String SPS_WATER_SNOW = "environment/water/sps_water_snow.png";
		public static final String SPS_WATER_SEWERS = "environment/water/sps_water_sewers.png";
		public static final String SPS_WATER_CAVES = "environment/water/sps_water_caves.png";
		public static final String SPS_WATER_CITY = "environment/water/sps_water_city.png";
		public static final String SPS_WATER_HALLS = "environment/water/sps_water_halls.png";
		public static final String SPS_WATER_HONEY = "environment/water/sps_water_honey.png";
		//SPS: 水缝合边独立图集（每区域一张、256x16、帧 0-15 = 缝合 bit；水渲染不再取地形图集的 48-63 段）
		public static final String SPS_WATER_EDGES_SEWERS = "environment/water/sps_water_edges_sewers.png";
		public static final String SPS_WATER_EDGES_PRISON = "environment/water/sps_water_edges_prison.png";
		public static final String SPS_WATER_EDGES_CAVES  = "environment/water/sps_water_edges_caves.png";
		public static final String SPS_WATER_EDGES_CITY   = "environment/water/sps_water_edges_city.png";
		public static final String SPS_WATER_EDGES_HALLS  = "environment/water/sps_water_edges_halls.png";
		public static final String SPS_WATER_EDGES_SNOW   = "environment/water/sps_water_edges_snow.png";
		public static final String SPS_WATER_EDGES_HONEY  = "environment/water/sps_water_edges_honey.png";
		public static final String RAISED_TERRAIN = "environment/legacy-2.5d/raised_terrain.png";

		public static final String VISUAL_GRID          = "environment/legacy-2.5d/visual_grid.png";
		public static final String WALL_BLOCKING        = "environment/legacy-2.5d/wall_blocking.png";
		public static final String OCCLUSION_SHADOWS    = "environment/legacy-2.5d/occlusion_shadows.png";

		public static final String TILES_SEWERS = "environment/tiles/tiles_sewers.png";
		public static final String TILES_PRISON = "environment/tiles/tiles_prison.png";
		public static final String TILES_CAVES  = "environment/tiles/tiles_caves.png";
		public static final String TILES_CITY   = "environment/tiles/tiles_city.png";
		public static final String TILES_HALLS  = "environment/tiles/tiles_halls.png";

		public static final String TILES_CAVES_CRYSTAL  = "environment/tiles/tiles_caves_crystal.png";
		public static final String TILES_CAVES_GNOLL    = "environment/tiles/tiles_caves_gnoll.png";

		public static final String WATER_SEWERS = "environment/water/water0.png";
		public static final String WATER_PRISON = "environment/water/water1.png";
		public static final String WATER_CAVES  = "environment/water/water2.png";
		public static final String WATER_CITY   = "environment/water/water3.png";
		public static final String WATER_HALLS  = "environment/water/water4.png";

		public static final String CARPET           = "environment/custom_tiles/carpet.png";
		public static final String RAT_KING_ROOM    = "environment/custom_tiles/rat_king_room.png";
		public static final String WEAK_FLOOR       = "environment/custom_tiles/weak_floor.png";
		public static final String SEWER_BOSS       = "environment/custom_tiles/sewer_boss.png";
		public static final String PRISON_QUEST     = "environment/custom_tiles/prison_quest.png";
		public static final String PRISON_EXIT      = "environment/custom_tiles/prison_exit.png";
		public static final String CAVES_QUEST      = "environment/custom_tiles/caves_quest.png";
		public static final String CAVES_BOSS       = "environment/custom_tiles/caves_boss.png";
		public static final String CITY_QUEST       = "environment/custom_tiles/city_quest.png";
		public static final String CITY_BOSS        = "environment/custom_tiles/city_boss.png";
		public static final String HALLS_SP         = "environment/custom_tiles/halls_special.png";
	}
	
	//TODO include other font assets here? Some are platform specific though...
	public static class Fonts {
		public static final String PIXELFONT= "fonts/pixel_font.png";
	}

	public static class Interfaces {
		public static final String ARCS_BG  = "interfaces/arcs1.png";
		public static final String ARCS_FG  = "interfaces/arcs2.png";

		public static final String BANNERS  = "interfaces/banners.png";
		public static final String BADGES   = "interfaces/badges.png";
		public static final String LOCKED   = "interfaces/locked_badge.png";

		public static final String CHROME   = "interfaces/chrome.png";
		//SPS: 侧边标签底板（横向 2 帧：左=选中 | 右=未选，手动旋转后的标签贴图，每帧 TAB_W×TAB_H）
		public static final String SIDE_TABS= "interfaces/side_tabs.png";
		//SPS: 左右快捷栏整栏外框三段纹理（横向 3 帧：上帽/中段/下帽，
		//由 tools/make-side-toolbar.ps1 从 toolbar.png 下栏三段转置生成）
		public static final String SIDE_TOOLBAR = "interfaces/side_toolbar.png";
		public static final String ICONS    = "interfaces/icons.png";
		public static final String STATUS   = "interfaces/status_pane.png";
		public static final String MENU     = "interfaces/menu_pane.png";
		public static final String MENU_BTN = "interfaces/menu_button.png";
		public static final String TOOLBAR  = "interfaces/toolbar.png";
		public static final String SHADOW   = "interfaces/shadow.png";
		public static final String BOSSHP   = "interfaces/boss_hp.png";

		public static final String SURFACE  = "interfaces/surface.png";

		public static final String BUFFS_SMALL      = "interfaces/buffs.png";
		public static final String BUFFS_LARGE      = "interfaces/large_buffs.png";

		public static final String TALENT_ICONS     = "interfaces/talent_icons.png";
		public static final String TALENT_BUTTON    = "interfaces/talent_button.png";

		public static final String HERO_ICONS       = "interfaces/hero_icons.png";

		public static final String RADIAL_MENU      = "interfaces/radial_menu.png";

		public static final String CHANGE_ICONS    = "interfaces/change_icons.png";
	}

	//these points to resource bundles, not raw asset files
	public static class Messages {
		public static final String ACTORS   = "messages/actors/actors";
		public static final String ITEMS    = "messages/items/items";
		public static final String JOURNAL  = "messages/journal/journal";
		public static final String LEVELS   = "messages/levels/levels";
		public static final String MISC     = "messages/misc/misc";
		public static final String PLANTS   = "messages/plants/plants";
		public static final String SCENES   = "messages/scenes/scenes";
		public static final String UI       = "messages/ui/ui";
		public static final String WINDOWS  = "messages/windows/windows";
	}

	public static class Music {
		public static final String SPS_THEME            = "music/sps_theme.mp3";
		public static final String SPS_GAME             = "music/sps_game.mp3";
		public static final String SPS_SURFACE          = "music/sps_surface.mp3";

		public static final String THEME_1              = "music/theme_1.ogg";
		public static final String THEME_2              = "music/theme_2.ogg";
		public static final String THEME_FINALE         = "music/theme_finale.ogg";

		public static final String SEWERS_1             = "music/sewers_1.ogg";
		public static final String SEWERS_2             = "music/sewers_2.ogg";
		public static final String SEWERS_3             = "music/sewers_3.ogg";
		public static final String SEWERS_TENSE         = "music/sewers_tense.ogg";
		public static final String SEWERS_BOSS          = "music/sewers_boss.ogg";

		public static final String PRISON_1             = "music/prison_1.ogg";
		public static final String PRISON_2             = "music/prison_2.ogg";
		public static final String PRISON_3             = "music/prison_3.ogg";
		public static final String PRISON_TENSE         = "music/prison_tense.ogg";
		public static final String PRISON_BOSS          = "music/prison_boss.ogg";

		public static final String CAVES_1              = "music/caves_1.ogg";
		public static final String CAVES_2              = "music/caves_2.ogg";
		public static final String CAVES_3              = "music/caves_3.ogg";
		public static final String CAVES_TENSE          = "music/caves_tense.ogg";
		public static final String CAVES_BOSS           = "music/caves_boss.ogg";
		public static final String CAVES_BOSS_FINALE    = "music/caves_boss_finale.ogg";

		public static final String CITY_1               = "music/city_1.ogg";
		public static final String CITY_2               = "music/city_2.ogg";
		public static final String CITY_3               = "music/city_3.ogg";
		public static final String CITY_TENSE           = "music/city_tense.ogg";
		public static final String CITY_BOSS            = "music/city_boss.ogg";
		public static final String CITY_BOSS_FINALE     = "music/city_boss_finale.ogg";

		public static final String HALLS_1              = "music/halls_1.ogg";
		public static final String HALLS_2              = "music/halls_2.ogg";
		public static final String HALLS_3              = "music/halls_3.ogg";
		public static final String HALLS_TENSE          = "music/halls_tense.ogg";
		public static final String HALLS_BOSS           = "music/halls_boss.ogg";
		public static final String HALLS_BOSS_FINALE    = "music/halls_boss_finale.ogg";
	}

	public static class Sounds {
		public static final String CLICK    = "sounds/click.mp3";
		public static final String BADGE    = "sounds/badge.mp3";
		public static final String GOLD     = "sounds/gold.mp3";

		public static final String OPEN     = "sounds/door_open.mp3";
		public static final String UNLOCK   = "sounds/unlock.mp3";
		public static final String ITEM     = "sounds/item.mp3";
		public static final String DEWDROP  = "sounds/dewdrop.mp3";
		public static final String STEP     = "sounds/step.mp3";
		public static final String WATER    = "sounds/water.mp3";
		public static final String GRASS    = "sounds/grass.mp3";
		public static final String TRAMPLE  = "sounds/trample.mp3";
		public static final String STURDY   = "sounds/sturdy.mp3";

		public static final String HIT              = "sounds/hit.mp3";
		public static final String MISS             = "sounds/miss.mp3";
		public static final String HIT_SLASH        = "sounds/hit_slash.mp3";
		public static final String HIT_STAB         = "sounds/hit_stab.mp3";
		public static final String HIT_CRUSH        = "sounds/hit_crush.mp3";
		public static final String HIT_MAGIC        = "sounds/hit_magic.mp3";
		public static final String HIT_STRONG       = "sounds/hit_strong.mp3";
		public static final String HIT_PARRY        = "sounds/hit_parry.mp3";
		public static final String HIT_ARROW        = "sounds/hit_arrow.mp3";
		public static final String ATK_SPIRITBOW    = "sounds/atk_spiritbow.mp3";
		public static final String ATK_CROSSBOW     = "sounds/atk_crossbow.mp3";
		public static final String HEALTH_WARN      = "sounds/health_warn.mp3";
		public static final String HEALTH_CRITICAL  = "sounds/health_critical.mp3";

		public static final String DESCEND  = "sounds/descend.mp3";
		public static final String EAT      = "sounds/eat.mp3";
		public static final String READ     = "sounds/read.mp3";
		public static final String LULLABY  = "sounds/lullaby.mp3";
		public static final String DRINK    = "sounds/drink.mp3";
		public static final String SHATTER  = "sounds/shatter.mp3";
		public static final String ZAP      = "sounds/zap.mp3";
		public static final String LIGHTNING= "sounds/lightning.mp3";
		public static final String LEVELUP  = "sounds/levelup.mp3";
		public static final String DEATH    = "sounds/death.mp3";
		public static final String CHALLENGE= "sounds/challenge.mp3";
		public static final String CURSED   = "sounds/cursed.mp3";
		public static final String TRAP     = "sounds/trap.mp3";
		public static final String EVOKE    = "sounds/evoke.mp3";
		public static final String TOMB     = "sounds/tomb.mp3";
		public static final String ALERT    = "sounds/alert.mp3";
		public static final String MELD     = "sounds/meld.mp3";
		public static final String BOSS     = "sounds/boss.mp3";
		public static final String BLAST    = "sounds/blast.mp3";
		public static final String PLANT    = "sounds/plant.mp3";
		public static final String RAY      = "sounds/ray.mp3";
		public static final String BEACON   = "sounds/beacon.mp3";
		public static final String TELEPORT = "sounds/teleport.mp3";
		public static final String CHARMS   = "sounds/charms.mp3";
		public static final String MASTERY  = "sounds/mastery.mp3";
		public static final String PUFF     = "sounds/puff.mp3";
		public static final String ROCKS    = "sounds/rocks.mp3";
		public static final String BURNING  = "sounds/burning.mp3";
		public static final String FALLING  = "sounds/falling.mp3";
		public static final String GHOST    = "sounds/ghost.mp3";
		public static final String SECRET   = "sounds/secret.mp3";
		public static final String BONES    = "sounds/bones.mp3";
		public static final String BEE      = "sounds/bee.mp3";
		public static final String DEGRADE  = "sounds/degrade.mp3";
		public static final String MIMIC    = "sounds/mimic.mp3";
		public static final String DEBUFF   = "sounds/debuff.mp3";
		public static final String CHARGEUP = "sounds/chargeup.mp3";
		public static final String GAS      = "sounds/gas.mp3";
		public static final String CHAINS   = "sounds/chains.mp3";
		public static final String SCAN     = "sounds/scan.mp3";
		public static final String SHEEP    = "sounds/sheep.mp3";
		public static final String MINE    = "sounds/mine.mp3";

		public static final String[] all = new String[]{
				CLICK, BADGE, GOLD,

				OPEN, UNLOCK, ITEM, DEWDROP, STEP, WATER, GRASS, TRAMPLE, STURDY,

				HIT, MISS, HIT_SLASH, HIT_STAB, HIT_CRUSH, HIT_MAGIC, HIT_STRONG, HIT_PARRY,
				HIT_ARROW, ATK_SPIRITBOW, ATK_CROSSBOW, HEALTH_WARN, HEALTH_CRITICAL,

				DESCEND, EAT, READ, LULLABY, DRINK, SHATTER, ZAP, LIGHTNING, LEVELUP, DEATH,
				CHALLENGE, CURSED, TRAP, EVOKE, TOMB, ALERT, MELD, BOSS, BLAST, PLANT, RAY, BEACON,
				TELEPORT, CHARMS, MASTERY, PUFF, ROCKS, BURNING, FALLING, GHOST, SECRET, BONES,
				BEE, DEGRADE, MIMIC, DEBUFF, CHARGEUP, GAS, CHAINS, SCAN, SHEEP, MINE
		};
	}

	public static class Splashes {
		public static final String WARRIOR  = "splashes/warrior.jpg";
		public static final String MAGE     = "splashes/mage.jpg";
		public static final String ROGUE    = "splashes/rogue.jpg";
		public static final String HUNTRESS = "splashes/huntress.jpg";
		public static final String DUELIST  = "splashes/duelist.png";
		public static final String CLERIC   = "splashes/cleric.jpg";
		public static final String SPELLSWORD = "splashes/fusion_spellsword.jpg";
		public static final String PERFORMER  = "splashes/fusion_performer.jpg";
		public static final String SOLDIER    = "splashes/fusion_soldier.jpg";
		public static final String FOLLOWER   = "splashes/fusion_follower.jpg";
		public static final String ASCETIC    = "splashes/fusion_ascetic.jpg";

		public static final String SEWERS   = "splashes/sewers.jpg";
		public static final String PRISON   = "splashes/prison.jpg";
		public static final String CAVES    = "splashes/caves.jpg";
		public static final String CITY     = "splashes/city.jpg";
		public static final String HALLS    = "splashes/halls.jpg";

		public static class Title {
			public static final String ARCHS         = "splashes/title/archs.png";
			public static final String BACK_CLUSTERS = "splashes/title/back_clusters.png";
			public static final String MID_MIXED     = "splashes/title/mid_mixed.png";
			public static final String FRONT_SMALL   = "splashes/title/front_small.png";
		}
	}

	public static class Sprites {
		public static final String ITEMS        = "sprites/items/items.png";
		public static final String ITEM_ICONS   = "sprites/items/item_icons.png";

		public static final String WARRIOR  = "sprites/heroes/warrior.png";
		public static final String MAGE     = "sprites/heroes/mage.png";
		public static final String ROGUE    = "sprites/heroes/rogue.png";
		public static final String HUNTRESS = "sprites/heroes/huntress.png";
		public static final String DUELIST  = "sprites/heroes/duelist.png";
		public static final String CLERIC   = "sprites/heroes/cleric.png";
		public static final String SPELLSWORD = "sprites/heroes/fusion_spellsword.png";
		public static final String SPS_WARRIOR  = "sprites/mobs/sps_warrior.png";
		public static final String SPS_MAGE     = "sprites/mobs/sps_mage.png";
		public static final String SPS_ROGUE    = "sprites/mobs/sps_rogue.png";
		public static final String SPS_HUNTRESS = "sprites/mobs/sps_huntress.png";
		public static final String PERFORMER = "sprites/heroes/fusion_performer.png";
		public static final String SOLDIER   = "sprites/heroes/fusion_soldier.png";
		public static final String FOLLOWER  = "sprites/heroes/fusion_follower.png";
		public static final String ASCETIC   = "sprites/heroes/fusion_ascetic.png";
		public static final String AVATARS  = "sprites/heroes/avatars.png";
		public static final String PET      = "sprites/pets/pet.png";
		public static final String AMULET   = "sprites/items/amulet.png";
		public static final String PUDDING_CUP = "sprites/mobs/pudding_cup.png";

		public static final String RAT      = "sprites/mobs/rat.png";
		public static final String BRUTE    = "sprites/mobs/brute.png";
		public static final String SPINNER  = "sprites/mobs/spinner.png";
		public static final String DM300    = "sprites/mobs/dm300.png";
		public static final String WRAITH   = "sprites/mobs/wraith.png";
		public static final String UNDEAD   = "sprites/mobs/undead.png";
		public static final String KING     = "sprites/mobs/king.png";
		public static final String PIRANHA  = "sprites/mobs/piranha.png";
		public static final String EYE      = "sprites/mobs/eye.png";
		public static final String GNOLL    = "sprites/mobs/gnoll.png";
		public static final String CRAB     = "sprites/mobs/crab.png";
		public static final String GOO      = "sprites/mobs/goo.png";
		public static final String SWARM    = "sprites/mobs/swarm.png";
		public static final String SKELETON = "sprites/mobs/skeleton.png";
		public static final String SHAMAN   = "sprites/mobs/shaman.png";
		public static final String THIEF    = "sprites/mobs/thief.png";
		public static final String TENGU    = "sprites/mobs/tengu.png";
		public static final String SHEEP    = "sprites/mobs/sheep.png";
		public static final String T_CLOUD  = "sprites/mobs/tcloud.png";
		public static final String SPS_SOKOBAN_SHEEP = "sprites/mobs/sps_sokoban_sheep.png";
		public static final String SPS_SENTINEL = "sprites/mobs/sps_sentinel.png";
		public static final String SPS_MONSTER_BOX = "sprites/mobs/sps_monster_box.png";
		public static final String SPS_OTILUKE = "sprites/mobs/sps_otiluke.png";
		public static final String SPS_OTILUKE_STONE = "sprites/mobs/sps_otiluke_stone.png";
		public static final String SPS_BLUE_DRAGON = "sprites/mobs/sps_blue_dragon.png";
		public static final String SPS_BLUE_GIRL = "sprites/mobs/sps_blue_girl.png";
		public static final String SPS_BUG_DRAGON = "sprites/mobs/sps_bug_dragon.png";
		public static final String SPS_GOLD_DRAGON = "sprites/mobs/sps_gold_dragon.png";
		public static final String SPS_GREEN_DRAGON = "sprites/mobs/sps_green_dragon.png";
		public static final String SPS_LIGHT_DRAGON = "sprites/mobs/sps_light_dragon.png";
		public static final String SPS_RED_DRAGON = "sprites/mobs/sps_red_dragon.png";
		public static final String SPS_SHADOW_DRAGON = "sprites/mobs/sps_shadow_dragon.png";
		public static final String SPS_VIOLET_DRAGON = "sprites/mobs/sps_violet_dragon.png";
		public static final String SPS_ADULT_DRAGON = "sprites/mobs/sps_adult_dragon.png";
		public static final String SPS_BOSS_DRAGON = "sprites/mobs/sps_new_dragon_02.png";
		public static final String SPS_UGOO = "sprites/mobs/sps_ugoo.png";
		public static final String SPS_PLANT_KING = "sprites/mobs/sps_plant_king.png";
		public static final String SPS_GNOLL_ARCHER = "sprites/mobs/sps_gnoll_archer.png";
		public static final String SPS_GNOLL_KING = "sprites/mobs/sps_gnoll_king.png";
		public static final String SPS_ZOT = "sprites/mobs/sps_zot.png";
		public static final String SPS_ZOT_PHASE = "sprites/mobs/sps_zot_phase.png";
		public static final String SPS_MAGIC_EYE = "sprites/mobs/sps_magic_eye.png";
		public static final String SPS_ICE_RABBIT = "sprites/mobs/sps_ice_rabbit.png";
		public static final String SPS_ERROR = "sprites/mobs/sps_error.png";
		public static final String SPS_NEW_GHOST = "sprites/mobs/sps_newghost.png";
		public static final String SPS_WAR_TREE = "sprites/mobs/sps_wartree.png";
		public static final String SPS_SEEKING_BOMB = "sprites/mobs/sps_seeking_bomb.png";
		public static final String SPS_PRISON_WANDER = "sprites/mobs/sps_prison_wander.png";
		public static final String SPS_TANK = "sprites/mobs/sps_tank.png";
		public static final String SPS_TENGU = "sprites/mobs/sps_tengu.png";
		public static final String SPS_GOO = "sprites/mobs/sps_goo.png";
		public static final String SPS_POISON_GOO = "sprites/mobs/sps_poison_goo.png";
		public static final String SPS_HYBRID = "sprites/mobs/sps_hybrid.png";
		public static final String SPS_DM300 = "sprites/mobs/sps_dm300.png";
		public static final String SPS_TOWER = "sprites/mobs/sps_tower.png";
		public static final String SPS_BROKEN_ROBOT = "sprites/mobs/sps_broken_robot.png";
		public static final String SPS_SPIDER_QUEEN = "sprites/mobs/sps_spider_queen.png";
		public static final String SPS_SPIDER_EGG = "sprites/mobs/sps_spider_egg.png";
		public static final String SPS_SPIDER_WORKER = "sprites/mobs/sps_spider_worker.png";
		public static final String SPS_SPIDER_MIND = "sprites/mobs/sps_spider_mind.png";
		public static final String SPS_LICH_DANCER = "sprites/mobs/sps_lich_dancer.png";
		public static final String SPS_BATTERY_TOMB = "sprites/mobs/sps_battery_tomb.png";
		public static final String SPS_ELDER_AVATAR = "sprites/mobs/sps_elder_avatar.png";
		public static final String SPS_OBELISK = "sprites/mobs/sps_obelisk.png";
		public static final String SPS_KING = "sprites/mobs/sps_king.png";
		public static final String SPS_UNDEAD = "sprites/mobs/sps_undead.png";
		public static final String SPS_DWARF_KING_TOMB = "sprites/mobs/sps_dwarf_king_tomb.png";
		public static final String SPS_DWARF_LICH = "sprites/mobs/sps_dwarf_lich.png";
		public static final String SPS_YOG = "sprites/mobs/sps_yog.png";
		public static final String SPS_BURNING_FIST = "sprites/mobs/sps_burning_fist.png";
		public static final String SPS_ROTTING_FIST = "sprites/mobs/sps_rotting_fist.png";
		public static final String SPS_INFECTING_FIST = "sprites/mobs/sps_infecting_fist.png";
		public static final String SPS_PINNING_FIST = "sprites/mobs/sps_pinning_fist.png";
		public static final String SPS_YOG_LARVA = "sprites/mobs/sps_yog_larva.png";
		public static final String SPS_SEWER_BAT = "sprites/mobs/sps_sewer_bat.png";
		public static final String SPS_SEWER_ELEMENTAL = "sprites/mobs/sps_sewer_elemental.png";
		public static final String SPS_SEWER_RAT = "sprites/mobs/sps_sewer_rat.png";
		public static final String SPS_SHIT = "sprites/mobs/sps_shit.png";
		public static final String SPS_LIVE_MOSS = "sprites/mobs/sps_live_moss.png";
		public static final String SPS_PATROL_UAV = "sprites/mobs/sps_patrol_uav.png";
		public static final String SPS_VAGRANT = "sprites/mobs/sps_vagrant.png";
		public static final String SPS_LIVE_PHOTO = "sprites/mobs/sps_livephoto.png";
		public static final String SPS_ASSASSIN = "sprites/mobs/sps_assassin.png";
		public static final String SPS_TROLL_WARRIOR = "sprites/mobs/sps_trollwarrior.png";
		public static final String SPS_PRISON_FIRE_RABBIT = "sprites/mobs/sps_firerabbit.png";
		public static final String SPS_BAMBOO = "sprites/mobs/sps_bamboo.png";
		public static final String SPS_GOLD_COLLECTOR = "sprites/mobs/sps_goldcollector.png";
		public static final String SPS_ZOMBIE = "sprites/mobs/sps_zombie.png";
		public static final String SPS_BANDIT_KING = "sprites/mobs/sps_banditking.png";
		public static final String SPS_NORMAL_CELL = "sprites/mobs/sps_cellmob.png";
		public static final String SPS_SAND_MOB = "sprites/mobs/sps_sandmob.png";
		public static final String SPS_ICE_BUG = "sprites/mobs/sps_icebug.png";
		public static final String SPS_TIME_KEEPER = "sprites/mobs/sps_timekeeper.png";
		public static final String SPS_GNOLL_SHAMAN = "sprites/mobs/sps_gnoll_shaman.png";
		public static final String SPS_DRAGON_RIDER = "sprites/mobs/sps_dragonrider.png";
		public static final String SPS_SPIDER_BOT = "sprites/mobs/sps_spiderbot.png";
		public static final String SPS_MUSKETEER = "sprites/mobs/sps_musketeer.png";
		public static final String SPS_MANY_SKELETON = "sprites/mobs/sps_manyskeleton.png";
		public static final String SPS_LEVEL_CHECKER = "sprites/mobs/sps_levelchecker.png";
		public static final String SPS_GREAT_MOSS = "sprites/mobs/sps_greatmoss.png";
		public static final String SPS_FOREST_PROTECTOR = "sprites/mobs/sps_plantdoctor.png";
		public static final String SPS_MOSSY_SKELETON = "sprites/mobs/sps_mossyskeleton.png";
		public static final String SPS_GRAVE_PROTECTOR = "sprites/mobs/sps_singleeye.png";
		public static final String SPS_ALBINO_PIRANHA = "sprites/mobs/sps_albinopiranha.png";
		public static final String SPS_FISH_PROTECTOR = "sprites/mobs/sps_icebig.png";
		public static final String SPS_GOLD_THIEF = "sprites/mobs/sps_goldthief.png";
		public static final String SPS_VAULT_PROTECTOR = "sprites/mobs/sps_boundhunter.png";
		public static final String SPS_RED_WRAITH = "sprites/mobs/sps_redwraith.png";
		public static final String SPS_DEMON_GOO = "sprites/mobs/sps_demongoo.png";
		public static final String SPS_THIEF_IMP = "sprites/mobs/sps_thiefimp.png";
		public static final String SPS_DEMON_FLOWER = "sprites/mobs/sps_demonflower.png";
		public static final String SPS_SUFFERER = "sprites/mobs/sps_sufferer.png";
		public static final String SPS_DEMON_RABBIT = "sprites/mobs/sps_demonrabbit.png";
		public static final String SPS_SEWER_HEART = "sprites/mobs/sps_sewer_heart.png";
		public static final String SPS_SEWER_LASHER = "sprites/mobs/sps_sewer_lasher.png";
		public static final String SPS_PLAGUE_DOCTOR = "sprites/mobs/sps_plague_doctor.png";
		public static final String SPS_SHADOW_RAT = "sprites/mobs/sps_shadow_rat.png";
		public static final String SPS_SHADOW_YOG = "sprites/mobs/sps_shadow_yog.png";
		public static final String SPS_FIEND = "sprites/mobs/sps_fiend.png";
		public static final String SPS_GOLD_ORC = "sprites/mobs/sps_gold_orc.png";
		public static final String SPS_BLUE_WRAITH = "sprites/mobs/sps_bluewraith.png";
		public static final String SPS_ORC = "sprites/mobs/sps_orc.png";
		public static final String SPS_FLYING_PROTECTOR = "sprites/mobs/sps_flyingprotector.png";
		public static final String SPS_SKELETON_KING = "sprites/mobs/sps_skeleton_king.png";
		public static final String SPS_SKELETON_HAND = "sprites/mobs/sps_skeleton_hand.png";
		public static final String SPS_CRAB_KING = "sprites/mobs/sps_crab_king.png";
		public static final String SPS_LIGHTNING_SHELL = "sprites/mobs/sps_lightning_shell.png";
		public static final String SPS_HERMIT_CRAB = "sprites/mobs/sps_hermit_crab.png";
		public static final String SPS_THIEF_KING = "sprites/mobs/sps_thief_king.png";
		public static final String SPS_UDM300 = "sprites/mobs/sps_udm300.png";
		public static final String SPS_FIRE_RABBIT = "sprites/mobs/sps_fire_rabbit.png";
		public static final String SPS_BUNNY = "sprites/pets/sps_bunny.png";
		public static final String SPS_SCARECROW = "sprites/mobs/sps_scarecrow.png";
		public static final String SPS_YEAR_BEAST = "sprites/mobs/sps_year_beast.png";
		public static final String SPS_ABI = "sprites/mobs/sps_abi.png";
		public static final String SPS_HARO = "sprites/mobs/sps_haro.png";
		public static final String SPS_PIG_PET = "sprites/pets/sps_pig.png";
		public static final String SPS_VELOCIROOSTER = "sprites/mobs/sps_velocirooster.png";
		public static final String SPS_BUTTERFLY_PET = "sprites/mobs/sps_butterfly.png";
		public static final String SPS_CHOCOBO = "sprites/pets/sps_chocobo.png";
		public static final String SPS_DATURA = "sprites/mobs/sps_datura.png";
		public static final String SPS_DOG_PET = "sprites/pets/sps_dog.png";
		public static final String SPS_DWARF_BOY = "sprites/mobs/sps_dwarfboy.png";
		public static final String SPS_FOX_HELPER = "sprites/pets/sps_foxhelper.png";
		public static final String SPS_FROG_PET = "sprites/pets/sps_frogpet.png";
		public static final String SPS_GENTLE_CRAB = "sprites/mobs/sps_gentlecrab.png";
		public static final String SPS_KODORA = "sprites/mobs/sps_kodora.png";
		public static final String SPS_LIT_DEMON = "sprites/mobs/sps_litdemon.png";
		public static final String SPS_KLIKS = "sprites/mobs/sps_kliks.png";
		public static final String SPS_SNAKE_PET = "sprites/mobs/sps_newsnake.png";
		public static final String SPS_SPIDER_PET = "sprites/mobs/sps_newspinner.png";
		public static final String SPS_STAR_KID = "sprites/mobs/sps_starkid.png";
		public static final String SPS_FLY_PET = "sprites/mobs/sps_swarm.png";
		public static final String SPS_RIBBON_RAT = "sprites/mobs/sps_ribbon_rat.png";
		public static final String KEEPER   = "sprites/npcs/shopkeeper.png";
		public static final String BAT      = "sprites/mobs/bat.png";
		public static final String ELEMENTAL= "sprites/mobs/elemental.png";
		public static final String MONK     = "sprites/mobs/monk.png";
		public static final String WARLOCK  = "sprites/mobs/warlock.png";
		public static final String GOLEM    = "sprites/mobs/golem.png";
		public static final String STATUE   = "sprites/mobs/statue.png";
		public static final String SUCCUBUS = "sprites/mobs/succubus.png";
		public static final String SCORPIO  = "sprites/mobs/scorpio.png";
		public static final String FISTS    = "sprites/mobs/yog_fists.png";
		public static final String YOG      = "sprites/mobs/yog.png";
		public static final String LARVA    = "sprites/mobs/larva.png";
		public static final String GHOST    = "sprites/npcs/ghost.png";
		public static final String MAKER    = "sprites/npcs/wandmaker.png";
		public static final String TINKERER = "sprites/npcs/tinkerer.png";
		public static final String TROLL    = "sprites/npcs/blacksmith.png";
		public static final String IMP      = "sprites/npcs/imp.png";
		public static final String RATKING  = "sprites/npcs/ratking.png";
		public static final String BEE      = "sprites/pets/bee.png";
		public static final String MIMIC    = "sprites/mobs/mimic.png";
		public static final String ROT_LASH = "sprites/mobs/rot_lasher.png";
		public static final String ROT_HEART= "sprites/mobs/rot_heart.png";
		public static final String GUARD    = "sprites/mobs/guard.png";
		public static final String WARDS    = "sprites/mobs/wards.png";
		public static final String GUARDIAN = "sprites/mobs/guardian.png";
		public static final String SLIME    = "sprites/mobs/slime.png";
		public static final String SNAKE    = "sprites/mobs/snake.png";
		public static final String NECRO    = "sprites/mobs/necromancer.png";
		public static final String GHOUL    = "sprites/mobs/ghoul.png";
		public static final String RIPPER   = "sprites/mobs/ripper.png";
		public static final String SPAWNER  = "sprites/mobs/spawner.png";
		public static final String DM100    = "sprites/mobs/dm100.png";
		public static final String PYLON    = "sprites/mobs/pylon.png";
		public static final String DM200    = "sprites/mobs/dm200.png";
		public static final String LOTUS    = "sprites/mobs/lotus.png";
		public static final String NINJA_LOG        = "sprites/mobs/ninja_log.png";
		public static final String SPIRIT_HAWK      = "sprites/mobs/spirit_hawk.png";
		public static final String SENTRY           = "sprites/mobs/sentry.png";
		public static final String CRYSTAL_WISP     = "sprites/mobs/crystal_wisp.png";
		public static final String CRYSTAL_GUARDIAN = "sprites/mobs/crystal_guardian.png";
		public static final String CRYSTAL_SPIRE    = "sprites/mobs/crystal_spire.png";
		public static final String GNOLL_GUARD      = "sprites/mobs/gnoll_guard.png";
		public static final String GNOLL_SAPPER     = "sprites/mobs/gnoll_sapper.png";
		public static final String GNOLL_GEOMANCER  = "sprites/mobs/gnoll_geomancer.png";
		public static final String FUNGAL_SPINNER   = "sprites/mobs/fungal_spinner.png";
		public static final String FUNGAL_SENTRY    = "sprites/mobs/fungal_sentry.png";
		public static final String FUNGAL_CORE      = "sprites/mobs/fungal_core.png";
		public static final String LYNN             = "sprites/mobs/sps_lynn.png";
		public static final String MRDESTRUCTO      = "sprites/mobs/sps_mrdestructo.png";
		public static final String SPS_ORB_OF_ZOT   = "sprites/mobs/sps_orbofzot.png";
		public static final String FAIRY            = "sprites/mobs/sps_fairy.png";
		public static final String MOBILE           = "sprites/mobs/sps_mobile.png";
		public static final String VAULT_TOKENS_DOOR= "sprites/mobs/vault_tokens_door.png";
		public static final String VAULT_MIRROR     = "sprites/mobs/vault_mirror.png";
		public static final String VAULT_BOSS_ELEMENTAL= "sprites/mobs/vault_boss_elemental.png";
	}
}
