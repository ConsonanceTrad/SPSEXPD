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

package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class ItemSpriteSheet {

	public static final int SIZE = 16;

	private static final int TX_WIDTH = 256;
	private static final int TX_HEIGHT = 992;

	private static final int WIDTH = TX_WIDTH / SIZE;

	public static TextureFilm film = new TextureFilm( TX_WIDTH, TX_HEIGHT, SIZE, SIZE );

	private static int xy(int x, int y){
		x -= 1; y -= 1;
		return x + WIDTH*y;
	}

	private static void assignItemRect( int item, int width, int height ){
		int x = (item % WIDTH) * SIZE;
		int y = (item / WIDTH) * SIZE;
		film.add( item, x, y, x+width, y+height);
	}

	private static final int PLACEHOLDERS   =                               xy(1, 1);   //18 slots
	//SOMETHING is the default item sprite at position 0. May show up ingame if there are bugs.
	public static final int SOMETHING       = PLACEHOLDERS+0;
	public static final int WEAPON_HOLDER   = PLACEHOLDERS+1;
	public static final int ARMOR_HOLDER    = PLACEHOLDERS+2;
	public static final int MISSILE_HOLDER  = PLACEHOLDERS+3;
	public static final int WAND_HOLDER     = PLACEHOLDERS+4;
	public static final int RING_HOLDER     = PLACEHOLDERS+5;
	public static final int ARTIFACT_HOLDER = PLACEHOLDERS+6;
	public static final int TRINKET_HOLDER  = PLACEHOLDERS+7;
	public static final int FOOD_HOLDER     = PLACEHOLDERS+8;
	public static final int BOMB_HOLDER     = PLACEHOLDERS+9;
	public static final int POTION_HOLDER   = PLACEHOLDERS+10;
	public static final int SEED_HOLDER     = PLACEHOLDERS+11;
	public static final int SCROLL_HOLDER   = PLACEHOLDERS+12;
	public static final int STONE_HOLDER    = PLACEHOLDERS+13;
	public static final int ELIXIR_HOLDER   = PLACEHOLDERS+14;
	public static final int SPELL_HOLDER    = PLACEHOLDERS+15;
	public static final int MOB_HOLDER      = PLACEHOLDERS+16;
	public static final int DOCUMENT_HOLDER = PLACEHOLDERS+17;
	//SPS: 饰品格子统一占位图（问号）。专属槽位 xy(9,1) = 像素(144,16)，供美术重设计时直接替换
	public static final int ACCESSORY_HOLDER = xy(13, 6);
	static{
		assignItemRect(SOMETHING,       8,  13);
		assignItemRect(WEAPON_HOLDER,   14, 14);
		assignItemRect(ARMOR_HOLDER,    14, 12);
		assignItemRect(MISSILE_HOLDER,  15, 15);
		assignItemRect(WAND_HOLDER,     14, 14);
		assignItemRect(RING_HOLDER,     8,  10);
		assignItemRect(ARTIFACT_HOLDER, 15, 15);
		assignItemRect(TRINKET_HOLDER,  16, 11);
		assignItemRect(FOOD_HOLDER,     15, 11);
		assignItemRect(BOMB_HOLDER,     10, 13);
		assignItemRect(POTION_HOLDER,   12, 14);
		assignItemRect(SEED_HOLDER,     10, 10);
		assignItemRect(SCROLL_HOLDER,   15, 14);
		assignItemRect(STONE_HOLDER,    14, 12);
		assignItemRect(ELIXIR_HOLDER,   12, 14);
		assignItemRect(SPELL_HOLDER,    8,  16);
		assignItemRect(MOB_HOLDER,      15, 14);
		assignItemRect(DOCUMENT_HOLDER, 10, 11);
		assignItemRect(ACCESSORY_HOLDER, 16, 16);
	}

	private static final int UNCOLLECTIBLE  =                               xy(3, 2);   //14 slots
	public static final int GOLD            = UNCOLLECTIBLE+0;
	public static final int ENERGY          = UNCOLLECTIBLE+1;

	public static final int DEWDROP         = UNCOLLECTIBLE+3;
	public static final int PETAL           = UNCOLLECTIBLE+4;
	public static final int SANDBAG         = UNCOLLECTIBLE+5;
	public static final int SPIRIT_ARROW    = UNCOLLECTIBLE+6;
	
	public static final int TENGU_BOMB      = UNCOLLECTIBLE+8;
	public static final int TENGU_SHOCKER   = UNCOLLECTIBLE+9;
	public static final int GEO_BOULDER     = UNCOLLECTIBLE+10;
	static{
		assignItemRect(GOLD,        15, 13);
		assignItemRect(ENERGY,      16, 16);

		//SPS: 露珠改用 SPS 贴图（16x16 多变体系列，见 0.9.8 ItemSpriteSheet DEWDROP=ROW2+0）
		assignItemRect(DEWDROP,     16, 16);
		assignItemRect(PETAL,        8,  8);
		assignItemRect(SANDBAG,     10, 10);
		assignItemRect(SPIRIT_ARROW,11, 11);
		
		assignItemRect(TENGU_BOMB,      10, 10);
		assignItemRect(TENGU_SHOCKER,   10, 10);
		assignItemRect(GEO_BOULDER,     16, 15);
	}

	private static final int CONTAINERS     =                               xy(1, 3);   //16 slots
	public static final int BONES           = CONTAINERS+0;
	public static final int REMAINS         = CONTAINERS+1;
	public static final int TOMB            = CONTAINERS+2;
	public static final int GRAVE           = CONTAINERS+3;
	public static final int CHEST           = CONTAINERS+4;
	public static final int LOCKED_CHEST    = CONTAINERS+5;
	public static final int CRYSTAL_CHEST   = CONTAINERS+6;
	public static final int EBONY_CHEST     = CONTAINERS+7;
	static{
		assignItemRect(BONES,           14, 11);
		assignItemRect(REMAINS,         14, 11);
		assignItemRect(TOMB,            14, 15);
		assignItemRect(GRAVE,           14, 15);
		assignItemRect(CHEST,           16, 14);
		assignItemRect(LOCKED_CHEST,    16, 14);
		assignItemRect(CRYSTAL_CHEST,   16, 14);
		assignItemRect(EBONY_CHEST,     16, 14);
	}

	private static final int MISC_CONSUMABLE =                              xy(1, 4);   //32 slots
	public static final int ANKH            = MISC_CONSUMABLE +0;
	public static final int STYLUS          = MISC_CONSUMABLE +1;
	public static final int SEAL            = MISC_CONSUMABLE +2;
	public static final int TORCH           = MISC_CONSUMABLE +3;
	public static final int BEACON          = MISC_CONSUMABLE +4;
	public static final int HONEYPOT        = MISC_CONSUMABLE +5;
	public static final int SHATTPOT        = MISC_CONSUMABLE +6;
	public static final int IRON_KEY        = MISC_CONSUMABLE +7;
	public static final int GOLDEN_KEY      = MISC_CONSUMABLE +8;
	public static final int CRYSTAL_KEY     = MISC_CONSUMABLE +9;
	public static final int WORN_KEY        = MISC_CONSUMABLE +10;
	public static final int MASK            = MISC_CONSUMABLE +11;
	public static final int CROWN           = MISC_CONSUMABLE +12;
	public static final int AMULET          = MISC_CONSUMABLE +13;
	public static final int MASTERY         = MISC_CONSUMABLE +14;
	public static final int KIT             = MISC_CONSUMABLE +15;
	public static final int SEAL_SHARD      = MISC_CONSUMABLE +16;
	public static final int BROKEN_STAFF    = MISC_CONSUMABLE +17;
	public static final int CLOAK_SCRAP     = MISC_CONSUMABLE +18;
	public static final int BOW_FRAGMENT    = MISC_CONSUMABLE +19;
	public static final int BROKEN_HILT     = MISC_CONSUMABLE +20;
	public static final int TORN_PAGE       = MISC_CONSUMABLE +21;
	public static final int TRINKET_CATA    = MISC_CONSUMABLE +22;

	static{
		assignItemRect(ANKH,            10, 16);
		assignItemRect(STYLUS,          12, 13);
		
		assignItemRect(SEAL,            13, 13);
		assignItemRect(TORCH,           12, 15);
		assignItemRect(BEACON,          16, 15);
		
		assignItemRect(HONEYPOT,        14, 12);
		assignItemRect(SHATTPOT,        14, 12);
		assignItemRect(IRON_KEY,        8,  14);
		assignItemRect(GOLDEN_KEY,      8,  14);
		assignItemRect(CRYSTAL_KEY,     8,  14);
		assignItemRect(WORN_KEY,        8,  14);
		assignItemRect(MASK,            11,  9);
		assignItemRect(CROWN,           13,  7);
		assignItemRect(AMULET,          16, 16);
		assignItemRect(MASTERY,         13, 16);
		assignItemRect(KIT,             16, 15);

		assignItemRect(SEAL_SHARD,      12, 12);
		assignItemRect(BROKEN_STAFF,    14, 10);
		assignItemRect(CLOAK_SCRAP,      9,  9);
		assignItemRect(BOW_FRAGMENT,    12,  9);
		assignItemRect(BROKEN_HILT,      9,  9);
		assignItemRect(TORN_PAGE,       11, 13);

		assignItemRect(TRINKET_CATA,    12, 11);
	}
	
	private static final int BOMBS          =                               xy(1, 6);   //16 slots
	public static final int BOMB            = BOMBS+0;
	public static final int DBL_BOMB        = BOMBS+1;
	public static final int FIRE_BOMB       = BOMBS+2;
	public static final int FROST_BOMB      = BOMBS+3;
	public static final int REGROWTH_BOMB   = BOMBS+4;
	public static final int SMOKE_BOMB      = BOMBS+5;
	public static final int FLASHBANG       = BOMBS+6;
	public static final int HOLY_BOMB       = BOMBS+7;
	public static final int WOOLY_BOMB      = BOMBS+8;
	public static final int NOISEMAKER      = BOMBS+9;
	public static final int ARCANE_BOMB     = BOMBS+10;
	public static final int SHRAPNEL_BOMB   = BOMBS+11;
	
	static{
		assignItemRect(BOMB,            10, 13);
		assignItemRect(DBL_BOMB,        14, 13);
		assignItemRect(FIRE_BOMB,       13, 12);
		assignItemRect(FROST_BOMB,      13, 12);
		assignItemRect(REGROWTH_BOMB,   13, 12);
		assignItemRect(SMOKE_BOMB,      13, 12);
		assignItemRect(FLASHBANG,       10, 13);
		assignItemRect(HOLY_BOMB,       10, 13);
		assignItemRect(WOOLY_BOMB,      10, 13);
		assignItemRect(NOISEMAKER,      10, 13);
		assignItemRect(ARCANE_BOMB,     10, 13);
		assignItemRect(SHRAPNEL_BOMB,   10, 13);
	}

	private static final int WEP_TIER1      =                               xy(1, 7);   //8 slots
	public static final int WORN_SHORTSWORD = WEP_TIER1+0;
	public static final int CUDGEL          = WEP_TIER1+1;
	public static final int GLOVES          = WEP_TIER1+2;
	public static final int RAPIER          = WEP_TIER1+3;
	public static final int DAGGER          = WEP_TIER1+4;
	public static final int MAGES_STAFF     = WEP_TIER1+5;
	static{
		assignItemRect(WORN_SHORTSWORD, 13, 13);
		assignItemRect(CUDGEL,          15, 15);
		assignItemRect(GLOVES,          12, 16);
		assignItemRect(RAPIER,          13, 14);
		assignItemRect(DAGGER,          12, 13);
		assignItemRect(MAGES_STAFF,     15, 16);
	}

	private static final int WEP_TIER2      =                               xy(9, 7);   //8 slots
	public static final int SHORTSWORD      = WEP_TIER2+0;
	public static final int HAND_AXE        = WEP_TIER2+1;
	public static final int SPEAR           = WEP_TIER2+2;
	public static final int QUARTERSTAFF    = WEP_TIER2+3;
	public static final int DIRK            = WEP_TIER2+4;
	public static final int SICKLE          = WEP_TIER2+5;
	static{
		assignItemRect(SHORTSWORD,      13, 13);
		assignItemRect(HAND_AXE,        12, 14);
		assignItemRect(SPEAR,           16, 16);
		assignItemRect(QUARTERSTAFF,    16, 16);
		assignItemRect(DIRK,            13, 14);
		assignItemRect(SICKLE,          15, 15);
	}

	private static final int WEP_TIER3      =                               xy(1, 8);   //8 slots
	public static final int SWORD           = WEP_TIER3+0;
	public static final int MACE            = WEP_TIER3+1;
	public static final int SCIMITAR        = WEP_TIER3+2;
	public static final int ROUND_SHIELD    = WEP_TIER3+3;
	public static final int SAI             = WEP_TIER3+4;
	public static final int WHIP            = WEP_TIER3+5;
	static{
		assignItemRect(SWORD,           14, 14);
		assignItemRect(MACE,            15, 15);
		assignItemRect(SCIMITAR,        13, 16);
		assignItemRect(ROUND_SHIELD,    16, 16);
		assignItemRect(SAI,             16, 16);
		assignItemRect(WHIP,            14, 14);
	}

	private static final int WEP_TIER4      =                               xy(9, 8);   //8 slots
	public static final int LONGSWORD       = WEP_TIER4+0;
	public static final int BATTLE_AXE      = WEP_TIER4+1;
	public static final int FLAIL           = WEP_TIER4+2;
	public static final int RUNIC_BLADE     = WEP_TIER4+3;
	public static final int ASSASSINS_BLADE = WEP_TIER4+4;
	public static final int CROSSBOW        = WEP_TIER4+5;
	public static final int KATANA          = WEP_TIER4+6;
	static{
		assignItemRect(LONGSWORD,       15, 15);
		assignItemRect(BATTLE_AXE,      16, 16);
		assignItemRect(FLAIL,           14, 14);
		assignItemRect(RUNIC_BLADE,     14, 14);
		assignItemRect(ASSASSINS_BLADE, 14, 15);
		assignItemRect(CROSSBOW,        15, 15);
		assignItemRect(KATANA,          15, 16);
	}

	private static final int WEP_TIER5      =                               xy(1, 9);   //8 slots
	public static final int GREATSWORD      = WEP_TIER5+0;
	public static final int WAR_HAMMER      = WEP_TIER5+1;
	public static final int GLAIVE          = WEP_TIER5+2;
	public static final int GREATAXE        = WEP_TIER5+3;
	public static final int GREATSHIELD     = WEP_TIER5+4;
	public static final int GAUNTLETS       = WEP_TIER5+5;
	public static final int WAR_SCYTHE      = WEP_TIER5+6;
	static{
		assignItemRect(GREATSWORD,  16, 16);
		assignItemRect(WAR_HAMMER,  16, 16);
		assignItemRect(GLAIVE,      16, 16);
		assignItemRect(GREATAXE,    12, 16);
		assignItemRect(GREATSHIELD, 12, 16);
		assignItemRect(GAUNTLETS,   13, 15);
		assignItemRect(WAR_SCYTHE,  14, 15);
	}

	                                                                                    //8 free slots
	private static final int SPS_START_ITEMS =                              xy(9, 9);
	public static final int LEGACY_SHOVEL = SPS_START_ITEMS;
	public static final int LEGACY_SOLDIER_GUN = SPS_START_ITEMS+1;
	public static final int LEGACY_SOLDIER_AMMO = SPS_START_ITEMS+2;
	public static final int LEGACY_FIRE_BOMB = SPS_START_ITEMS+3;
	public static final int LEGACY_ICE_BOMB = SPS_START_ITEMS+4;
	public static final int LEGACY_STORM_BOMB = SPS_START_ITEMS+5;
	static {
		for (int i = SPS_START_ITEMS; i < SPS_START_ITEMS+6; i++) assignItemRect(i, 16, 16);
	}

	private static final int MISSILE_WEP    =                               xy(1, 10);  //16 slots. 3 per tier + bow
	public static final int SPIRIT_BOW      = MISSILE_WEP+0;
	
	public static final int THROWING_SPIKE  = MISSILE_WEP+1;
	public static final int THROWING_KNIFE  = MISSILE_WEP+2;
	public static final int THROWING_STONE  = MISSILE_WEP+3;
	
	public static final int FISHING_SPEAR   = MISSILE_WEP+4;
	public static final int SHURIKEN        = MISSILE_WEP+5;
	public static final int THROWING_CLUB   = MISSILE_WEP+6;
	
	public static final int THROWING_SPEAR  = MISSILE_WEP+7;
	public static final int BOLAS           = MISSILE_WEP+8;
	public static final int KUNAI           = MISSILE_WEP+9;
	
	public static final int JAVELIN         = MISSILE_WEP+10;
	public static final int TOMAHAWK        = MISSILE_WEP+11;
	public static final int BOOMERANG       = MISSILE_WEP+12;
	
	public static final int TRIDENT         = MISSILE_WEP+13;
	public static final int THROWING_HAMMER = MISSILE_WEP+14;
	public static final int FORCE_CUBE      = MISSILE_WEP+15;
	
	static{
		assignItemRect(SPIRIT_BOW,      16, 16);
		
		assignItemRect(THROWING_SPIKE,  11, 10);
		assignItemRect(THROWING_KNIFE,  12, 13);
		assignItemRect(THROWING_STONE,  12, 10);
		
		assignItemRect(FISHING_SPEAR,   11, 11);
		assignItemRect(SHURIKEN,        12, 12);
		assignItemRect(THROWING_CLUB,   12, 12);
		
		assignItemRect(THROWING_SPEAR,  13, 13);
		assignItemRect(BOLAS,           15, 14);
		assignItemRect(KUNAI,           15, 15);
		
		assignItemRect(JAVELIN,         16, 16);
		assignItemRect(TOMAHAWK,        13, 13);
		assignItemRect(BOOMERANG,       14, 14);
		
		assignItemRect(TRIDENT,         16, 16);
		assignItemRect(THROWING_HAMMER, 12, 12);
		assignItemRect(FORCE_CUBE,      11, 12);
	}
	
	public static final int DARTS    =                                      xy(1, 11);  //16 slots
	public static final int DART            = DARTS+0;
	public static final int ROT_DART        = DARTS+1;
	public static final int INCENDIARY_DART = DARTS+2;
	public static final int ADRENALINE_DART = DARTS+3;
	public static final int HEALING_DART    = DARTS+4;
	public static final int CHILLING_DART   = DARTS+5;
	public static final int SHOCKING_DART   = DARTS+6;
	public static final int POISON_DART     = DARTS+7;
	public static final int CLEANSING_DART  = DARTS+8;
	public static final int PARALYTIC_DART  = DARTS+9;
	public static final int HOLY_DART       = DARTS+10;
	public static final int DISPLACING_DART = DARTS+11;
	public static final int BLINDING_DART   = DARTS+12;
	static {
		for (int i = DARTS; i < DARTS+16; i++)
			assignItemRect(i, 15, 15);
	}
	
	private static final int ARMOR          =                               xy(1, 12);  //16 slots
	public static final int ARMOR_CLOTH     = ARMOR+0;
	public static final int ARMOR_LEATHER   = ARMOR+1;
	public static final int ARMOR_MAIL      = ARMOR+2;
	public static final int ARMOR_SCALE     = ARMOR+3;
	public static final int ARMOR_PLATE     = ARMOR+4;
	public static final int ARMOR_WARRIOR   = ARMOR+5;
	public static final int ARMOR_MAGE      = ARMOR+6;
	public static final int ARMOR_ROGUE     = ARMOR+7;
	public static final int ARMOR_HUNTRESS  = ARMOR+8;
	public static final int ARMOR_DUELIST   = ARMOR+9;
	public static final int ARMOR_CLERIC    = ARMOR+10;
	static{
		assignItemRect(ARMOR_CLOTH,     15, 12);
		assignItemRect(ARMOR_LEATHER,   14, 13);
		assignItemRect(ARMOR_MAIL,      14, 12);
		assignItemRect(ARMOR_SCALE,     14, 11);
		assignItemRect(ARMOR_PLATE,     12, 12);
		assignItemRect(ARMOR_WARRIOR,   12, 12);
		assignItemRect(ARMOR_MAGE,      15, 15);
		assignItemRect(ARMOR_ROGUE,     14, 12);
		assignItemRect(ARMOR_HUNTRESS,  13, 15);
		assignItemRect(ARMOR_DUELIST,   12, 13);
		assignItemRect(ARMOR_CLERIC,    13, 14);
	}

	                                                                                    //16 free slots
	private static final int SPS_FAITH_ITEMS =                             xy(1, 13);
	public static final int LEGACY_FAITH_SIGN = SPS_FAITH_ITEMS;
	public static final int LEGACY_BIG_BATTERY = SPS_FAITH_ITEMS+1;
	public static final int LEGACY_WOODEN_STAFF = SPS_FAITH_ITEMS+2;
	public static final int LEGACY_TRICK_SAND = SPS_FAITH_ITEMS+3;
	public static final int LEGACY_ATTACK_SHOES = SPS_FAITH_ITEMS+4;
	public static final int LEGACY_ATTACK_SHIELD = SPS_FAITH_ITEMS+5;
	public static final int LEGACY_CANNON_OF_MAGE = SPS_FAITH_ITEMS+6;
	public static final int LEGACY_LINK_SWORD = SPS_FAITH_ITEMS+7;
	public static final int LEGACY_B_SHOVEL = SPS_FAITH_ITEMS+8;
	public static final int LEGACY_MK_BOX = SPS_FAITH_ITEMS+9;
	public static final int LEGACY_DIAMOND_PICKAXE = SPS_FAITH_ITEMS+10;
	public static final int LEGACY_HOLY_MACE = SPS_FAITH_ITEMS+11;
	public static final int LEGACY_BRAVE_BOOK = SPS_FAITH_ITEMS+12;
	public static final int LEGACY_PIXEL_TORCH = SPS_FAITH_ITEMS+13;
	public static final int LEGACY_TIME_OCLOCK = SPS_FAITH_ITEMS+14;
	public static final int LEGACY_ALIEN_BAG = SPS_FAITH_ITEMS+15;
	static {
		for (int i = SPS_FAITH_ITEMS; i < SPS_FAITH_ITEMS+16; i++) assignItemRect(i, 16, 16);
	}

	private static final int WANDS              =                           xy(1, 14);  //16 slots
	public static final int WAND_MAGIC_MISSILE  = WANDS+0;
	public static final int WAND_FIREBOLT       = WANDS+1;
	public static final int WAND_FROST          = WANDS+2;
	public static final int WAND_LIGHTNING      = WANDS+3;
	public static final int WAND_DISINTEGRATION = WANDS+4;
	public static final int WAND_PRISMATIC_LIGHT= WANDS+5;
	public static final int WAND_CORROSION      = WANDS+6;
	public static final int WAND_LIVING_EARTH   = WANDS+7;
	public static final int WAND_BLAST_WAVE     = WANDS+8;
	public static final int WAND_CORRUPTION     = WANDS+9;
	public static final int WAND_WARDING        = WANDS+10;
	public static final int WAND_REGROWTH       = WANDS+11;
	public static final int WAND_TRANSFUSION    = WANDS+12;
	static {
		for (int i = WANDS; i < WANDS+16; i++)
			assignItemRect(i, 14, 14);
	}

	private static final int RINGS          =                               xy(1, 15);  //16 slots
	public static final int RING_GARNET     = RINGS+0;
	public static final int RING_RUBY       = RINGS+1;
	public static final int RING_TOPAZ      = RINGS+2;
	public static final int RING_EMERALD    = RINGS+3;
	public static final int RING_ONYX       = RINGS+4;
	public static final int RING_OPAL       = RINGS+5;
	public static final int RING_TOURMALINE = RINGS+6;
	public static final int RING_SAPPHIRE   = RINGS+7;
	public static final int RING_AMETHYST   = RINGS+8;
	public static final int RING_QUARTZ     = RINGS+9;
	public static final int RING_AGATE      = RINGS+10;
	public static final int RING_DIAMOND    = RINGS+11;
	static {
		for (int i = RINGS; i < RINGS+16; i++)
			assignItemRect(i, 8, 10);
	}

	private static final int ARTIFACTS          =                            xy(1, 16);  //32 slots
	public static final int ARTIFACT_CLOAK      = ARTIFACTS+0;
	public static final int ARTIFACT_ARMBAND    = ARTIFACTS+1;
	public static final int ARTIFACT_CAPE       = ARTIFACTS+2;
	public static final int ARTIFACT_TALISMAN   = ARTIFACTS+3;
	public static final int ARTIFACT_HOURGLASS  = ARTIFACTS+4;
	public static final int ARTIFACT_TOOLKIT    = ARTIFACTS+5;
	public static final int ARTIFACT_SPELLBOOK  = ARTIFACTS+6;
	public static final int ARTIFACT_BEACON     = ARTIFACTS+7;
	public static final int ARTIFACT_CHAINS     = ARTIFACTS+8;
	public static final int ARTIFACT_HORN1      = ARTIFACTS+9;
	public static final int ARTIFACT_HORN2      = ARTIFACTS+10;
	public static final int ARTIFACT_HORN3      = ARTIFACTS+11;
	public static final int ARTIFACT_HORN4      = ARTIFACTS+12;
	public static final int ARTIFACT_CHALICE1   = ARTIFACTS+13;
	public static final int ARTIFACT_CHALICE2   = ARTIFACTS+14;
	public static final int ARTIFACT_CHALICE3   = ARTIFACTS+15;
	public static final int ARTIFACT_SANDALS    = ARTIFACTS+16;
	public static final int ARTIFACT_SHOES      = ARTIFACTS+17;
	public static final int ARTIFACT_BOOTS      = ARTIFACTS+18;
	public static final int ARTIFACT_GREAVES    = ARTIFACTS+19;
	public static final int ARTIFACT_ROSE1      = ARTIFACTS+20;
	public static final int ARTIFACT_ROSE2      = ARTIFACTS+21;
	public static final int ARTIFACT_ROSE3      = ARTIFACTS+22;
	public static final int ARTIFACT_TOME       = ARTIFACTS+23;
	public static final int ARTIFACT_KEY        = ARTIFACTS+24;
	static{
		assignItemRect(ARTIFACT_CLOAK,      9,  15);
		assignItemRect(ARTIFACT_ARMBAND,    16, 13);
		assignItemRect(ARTIFACT_CAPE,       16, 14);
		assignItemRect(ARTIFACT_TALISMAN,   15, 13);
		assignItemRect(ARTIFACT_HOURGLASS,  13, 16);
		assignItemRect(ARTIFACT_TOOLKIT,    15, 13);
		assignItemRect(ARTIFACT_SPELLBOOK,  13, 16);
		assignItemRect(ARTIFACT_BEACON,     16, 16);
		assignItemRect(ARTIFACT_CHAINS,     16, 16);
		assignItemRect(ARTIFACT_HORN1,      15, 15);
		assignItemRect(ARTIFACT_HORN2,      15, 15);
		assignItemRect(ARTIFACT_HORN3,      15, 15);
		assignItemRect(ARTIFACT_HORN4,      15, 15);
		assignItemRect(ARTIFACT_CHALICE1,   12, 15);
		assignItemRect(ARTIFACT_CHALICE2,   12, 15);
		assignItemRect(ARTIFACT_CHALICE3,   12, 15);
		assignItemRect(ARTIFACT_SANDALS,    16, 6 );
		assignItemRect(ARTIFACT_SHOES,      16, 6 );
		assignItemRect(ARTIFACT_BOOTS,      16, 9 );
		assignItemRect(ARTIFACT_GREAVES,    16, 14);
		assignItemRect(ARTIFACT_ROSE1,      14, 14);
		assignItemRect(ARTIFACT_ROSE2,      14, 14);
		assignItemRect(ARTIFACT_ROSE3,      14, 14);
		assignItemRect(ARTIFACT_TOME,       14, 16);
		assignItemRect(ARTIFACT_KEY,        8,  16);
	}

	private static final int TRINKETS        =                               xy(1, 18);  //32 slots
	public static final int RAT_SKULL       = TRINKETS+0;
	public static final int PARCHMENT_SCRAP = TRINKETS+1;
	public static final int PETRIFIED_SEED  = TRINKETS+2;
	public static final int EXOTIC_CRYSTALS = TRINKETS+3;
	public static final int MOSSY_CLUMP     = TRINKETS+4;
	public static final int SUNDIAL         = TRINKETS+5;
	public static final int CLOVER          = TRINKETS+6;
	public static final int TRAP_MECHANISM  = TRINKETS+7;
	public static final int MIMIC_TOOTH     = TRINKETS+8;
	public static final int WONDROUS_RESIN  = TRINKETS+9;
	public static final int EYE_OF_NEWT     = TRINKETS+10;
	public static final int SALT_CUBE       = TRINKETS+11;
	public static final int BLOOD_VIAL      = TRINKETS+12;
	public static final int OBLIVION_SHARD  = TRINKETS+13;
	public static final int CHAOTIC_CENSER  = TRINKETS+14;
	public static final int FERRET_TUFT     = TRINKETS+15;
	public static final int SPYGLASS        = TRINKETS+16;
	static{
		assignItemRect(RAT_SKULL,       16, 11);
		assignItemRect(PARCHMENT_SCRAP, 10, 14);
		assignItemRect(PETRIFIED_SEED,   9,  9);
		assignItemRect(EXOTIC_CRYSTALS, 15, 13);
		assignItemRect(MOSSY_CLUMP,     12, 11);
		assignItemRect(SUNDIAL,         16, 12);
		assignItemRect(CLOVER,          11, 15);
		assignItemRect(TRAP_MECHANISM,  13, 15);
		assignItemRect(MIMIC_TOOTH,     8,  15);
		assignItemRect(WONDROUS_RESIN,  12, 11);
		assignItemRect(EYE_OF_NEWT,     12, 12);
		assignItemRect(SALT_CUBE,       12, 13);
		assignItemRect(BLOOD_VIAL,      6,  15);
		assignItemRect(OBLIVION_SHARD,  7,  14);
		assignItemRect(CHAOTIC_CENSER,  13, 15);
		assignItemRect(FERRET_TUFT,     16, 15);
		assignItemRect(SPYGLASS,        15, 15);
	}

	private static final int SCROLLS        =                               xy(1, 20);  //16 slots
	public static final int SCROLL_KAUNAN   = SCROLLS+0;
	public static final int SCROLL_SOWILO   = SCROLLS+1;
	public static final int SCROLL_LAGUZ    = SCROLLS+2;
	public static final int SCROLL_YNGVI    = SCROLLS+3;
	public static final int SCROLL_GYFU     = SCROLLS+4;
	public static final int SCROLL_RAIDO    = SCROLLS+5;
	public static final int SCROLL_ISAZ     = SCROLLS+6;
	public static final int SCROLL_MANNAZ   = SCROLLS+7;
	public static final int SCROLL_NAUDIZ   = SCROLLS+8;
	public static final int SCROLL_BERKANAN = SCROLLS+9;
	public static final int SCROLL_NCOSRANE = SCROLLS+10;
	public static final int SCROLL_ODAL     = SCROLL_NCOSRANE;
	public static final int SCROLL_TIWAZ    = SCROLLS+11;
	public static final int SCROLL_NENDIL   = SCROLLS+12;
	public static final int SCROLL_LIBRA    = SCROLLS+14;

	public static final int ARCANE_RESIN    = SCROLLS+13;
	static {
		for (int i = SCROLLS; i < SCROLLS+16; i++)
			assignItemRect(i, 15, 14);
		assignItemRect(ARCANE_RESIN   , 12, 11);
	}
	
	private static final int EXOTIC_SCROLLS =                               xy(1, 21);  //16 slots
	public static final int EXOTIC_KAUNAN   = EXOTIC_SCROLLS+0;
	public static final int EXOTIC_SOWILO   = EXOTIC_SCROLLS+1;
	public static final int EXOTIC_LAGUZ    = EXOTIC_SCROLLS+2;
	public static final int EXOTIC_YNGVI    = EXOTIC_SCROLLS+3;
	public static final int EXOTIC_GYFU     = EXOTIC_SCROLLS+4;
	public static final int EXOTIC_RAIDO    = EXOTIC_SCROLLS+5;
	public static final int EXOTIC_ISAZ     = EXOTIC_SCROLLS+6;
	public static final int EXOTIC_MANNAZ   = EXOTIC_SCROLLS+7;
	public static final int EXOTIC_NAUDIZ   = EXOTIC_SCROLLS+8;
	public static final int EXOTIC_BERKANAN = EXOTIC_SCROLLS+9;
	public static final int EXOTIC_ODAL     = EXOTIC_SCROLLS+10;
	public static final int EXOTIC_TIWAZ    = EXOTIC_SCROLLS+11;
	static {
		for (int i = EXOTIC_SCROLLS; i < EXOTIC_SCROLLS+16; i++)
			assignItemRect(i, 15, 14);
	}
	
	private static final int STONES             =                           xy(1, 22);  //16 slots
	public static final int STONE_AGGRESSION    = STONES+0;
	public static final int STONE_AUGMENTATION  = STONES+1;
	public static final int STONE_FEAR          = STONES+2;
	public static final int STONE_BLAST         = STONES+3;
	public static final int STONE_BLINK         = STONES+4;
	public static final int STONE_CLAIRVOYANCE  = STONES+5;
	public static final int STONE_SLEEP         = STONES+6;
	public static final int STONE_DETECT        = STONES+7;
	public static final int STONE_ENCHANT       = STONES+8;
	public static final int STONE_FLOCK         = STONES+9;
	public static final int STONE_INTUITION     = STONES+10;
	public static final int STONE_SHOCK         = STONES+11;
	static {
		for (int i = STONES; i < STONES+16; i++)
			assignItemRect(i, 14, 12);
	}

	private static final int POTIONS        =                               xy(1, 23);  //16 slots
	public static final int POTION_CRIMSON  = POTIONS+0;
	public static final int POTION_AMBER    = POTIONS+1;
	public static final int POTION_GOLDEN   = POTIONS+2;
	public static final int POTION_JADE     = POTIONS+3;
	public static final int POTION_TURQUOISE= POTIONS+4;
	public static final int POTION_AZURE    = POTIONS+5;
	public static final int POTION_INDIGO   = POTIONS+6;
	public static final int POTION_MAGENTA  = POTIONS+7;
	public static final int POTION_BISTRE   = POTIONS+8;
	public static final int POTION_CHARCOAL = POTIONS+9;
	public static final int POTION_SILVER   = POTIONS+10;
	public static final int POTION_IVORY    = POTIONS+11;

	public static final int LIQUID_METAL    = POTIONS+13;
	static {
		for (int i = POTIONS; i < POTIONS+16; i++)
			assignItemRect(i, 12, 14);
		assignItemRect(LIQUID_METAL,    8, 15);
	}
	
	private static final int EXOTIC_POTIONS =                               xy(1, 24);  //16 slots
	public static final int EXOTIC_CRIMSON  = EXOTIC_POTIONS+0;
	public static final int EXOTIC_AMBER    = EXOTIC_POTIONS+1;
	public static final int EXOTIC_GOLDEN   = EXOTIC_POTIONS+2;
	public static final int EXOTIC_JADE     = EXOTIC_POTIONS+3;
	public static final int EXOTIC_TURQUOISE= EXOTIC_POTIONS+4;
	public static final int EXOTIC_AZURE    = EXOTIC_POTIONS+5;
	public static final int EXOTIC_INDIGO   = EXOTIC_POTIONS+6;
	public static final int EXOTIC_MAGENTA  = EXOTIC_POTIONS+7;
	public static final int EXOTIC_BISTRE   = EXOTIC_POTIONS+8;
	public static final int EXOTIC_CHARCOAL = EXOTIC_POTIONS+9;
	public static final int EXOTIC_SILVER   = EXOTIC_POTIONS+10;
	public static final int EXOTIC_IVORY    = EXOTIC_POTIONS+11;
	static {
		for (int i = EXOTIC_POTIONS; i < EXOTIC_POTIONS+16; i++)
			assignItemRect(i, 12, 13);
	}

	private static final int SEEDS              =                           xy(1, 25);  //16 slots
	public static final int SEED_ROTBERRY       = SEEDS+0;
	public static final int SEED_FIREBLOOM      = SEEDS+1;
	public static final int SEED_SWIFTTHISTLE   = SEEDS+2;
	public static final int SEED_SUNGRASS       = SEEDS+3;
	public static final int SEED_ICECAP         = SEEDS+4;
	public static final int SEED_STORMVINE      = SEEDS+5;
	public static final int SEED_SORROWMOSS     = SEEDS+6;
	public static final int SEED_MAGEROYAL = SEEDS+7;
	public static final int SEED_EARTHROOT      = SEEDS+8;
	public static final int SEED_STARFLOWER     = SEEDS+9;
	public static final int SEED_FADELEAF       = SEEDS+10;
	public static final int SEED_BLINDWEED      = SEEDS+11;
	static{
		for (int i = SEEDS; i < SEEDS+16; i++)
			assignItemRect(i, 10, 10);
	}
	
	private static final int BREWS          =                               xy(1, 26);  //8 slots
	public static final int BREW_INFERNAL   = BREWS+0;
	public static final int BREW_BLIZZARD   = BREWS+1;
	public static final int BREW_SHOCKING   = BREWS+2;
	public static final int BREW_CAUSTIC    = BREWS+3;
	public static final int BREW_AQUA       = BREWS+4;
	public static final int BREW_UNSTABLE   = BREWS+5;
	
	private static final int ELIXIRS        =                               xy(9, 26);  //8 slots
	public static final int ELIXIR_HONEY    = ELIXIRS+0;
	public static final int ELIXIR_AQUA     = ELIXIRS+1;
	public static final int ELIXIR_MIGHT    = ELIXIRS+2;
	public static final int ELIXIR_DRAGON   = ELIXIRS+3;
	public static final int ELIXIR_TOXIC    = ELIXIRS+4;
	public static final int ELIXIR_ICY      = ELIXIRS+5;
	public static final int ELIXIR_ARCANE   = ELIXIRS+6;
	public static final int ELIXIR_FEATHER  = ELIXIRS+7;
	static{
		for (int i = BREWS; i < BREWS+16; i++)
			assignItemRect(i, 12, 14);

		assignItemRect(BREW_INFERNAL,   11, 13);
		assignItemRect(BREW_BLIZZARD,   11, 13);
		assignItemRect(BREW_UNSTABLE,   11, 13);
		assignItemRect(BREW_AQUA,        9, 11);
	}
	
	private static final int SPELLS         =                               xy(1, 27);  //16 slots
	public static final int WILD_ENERGY     = SPELLS+0;
	public static final int PHASE_SHIFT     = SPELLS+1;
	public static final int TELE_GRAB       = SPELLS+2;
	public static final int UNSTABLE_SPELL  = SPELLS+3;
	public static final int CURSE_INFUSE    = SPELLS+4;
	public static final int MAGIC_INFUSE    = SPELLS+5;
	public static final int ALCHEMIZE       = SPELLS+6;
	public static final int RECYCLE         = SPELLS+7;
	public static final int RECLAIM_TRAP    = SPELLS+8;
	public static final int RETURN_BEACON   = SPELLS+9;
	public static final int SUMMON_ELE      = SPELLS+10;
	public static final int SUMMON_ELE_FIRE = SPELLS+11;
	public static final int SUMMON_ELE_FROST= SPELLS+12;
	public static final int SUMMON_ELE_SHOCK= SPELLS+13;
	public static final int SUMMON_ELE_CHAOS= SPELLS+14;

	static{
		assignItemRect(WILD_ENERGY,     6, 15);
		assignItemRect(PHASE_SHIFT,     12, 10);
		assignItemRect(TELE_GRAB,       10, 10);
		assignItemRect(UNSTABLE_SPELL,  12, 13);

		assignItemRect(CURSE_INFUSE,    10, 16);
		assignItemRect(MAGIC_INFUSE,    10, 14);
		assignItemRect(ALCHEMIZE,       12, 12);
		assignItemRect(RECYCLE,         12, 13);

		assignItemRect(RECLAIM_TRAP,    14, 11);
		assignItemRect(RETURN_BEACON,    8, 16);
		assignItemRect(SUMMON_ELE,       8, 16);
		assignItemRect(SUMMON_ELE_FIRE,  8, 16);
		assignItemRect(SUMMON_ELE_FROST, 8, 16);
		assignItemRect(SUMMON_ELE_SHOCK, 8, 16);
		assignItemRect(SUMMON_ELE_CHAOS, 8, 16);
	}
	
	private static final int FOOD       =                                   xy(1, 28);  //16 slots
	public static final int MEAT            = FOOD+0;
	public static final int STEAK           = FOOD+1;
	public static final int STEWED          = FOOD+2;
	public static final int OVERPRICED      = FOOD+3;
	public static final int CARPACCIO       = FOOD+4;
	public static final int RATION          = FOOD+5;
	public static final int PASTY           = FOOD+6;
	public static final int MEAT_PIE        = FOOD+7;
	public static final int BLANDFRUIT      = FOOD+8;
	public static final int BLAND_CHUNKS    = FOOD+9;
	public static final int BERRY           = FOOD+10;
	public static final int PHANTOM_MEAT    = FOOD+11;
	public static final int SUPPLY_RATION   = FOOD+12;
	public static final int BLACKBERRY      = FOOD+13;
	public static final int CLOUDBERRY      = FOOD+14;
	public static final int BLUEBERRY       = FOOD+15;
	static{
		assignItemRect(MEAT,            15, 11);
		assignItemRect(STEAK,           15, 11);
		assignItemRect(STEWED,          15, 11);
		assignItemRect(OVERPRICED,      14, 11);
		assignItemRect(CARPACCIO,       15, 11);
		assignItemRect(RATION,          16, 12);
		assignItemRect(PASTY,           16, 11);
		assignItemRect(MEAT_PIE,        16, 12);
		assignItemRect(BLANDFRUIT,      9,  12);
		assignItemRect(BLAND_CHUNKS,    14,  6);
		assignItemRect(BERRY,           9,  11);
		assignItemRect(PHANTOM_MEAT,    15, 11);
		assignItemRect(SUPPLY_RATION,   16, 12);
		assignItemRect(BLACKBERRY,       16, 16);
		assignItemRect(CLOUDBERRY,       16, 16);
		assignItemRect(BLUEBERRY,        16, 16);
	}

	private static final int HOLIDAY_FOOD   =                               xy(1, 29);  //16 slots
	public static final int STEAMED_FISH    = HOLIDAY_FOOD+0;
	public static final int FISH_LEFTOVER   = HOLIDAY_FOOD+1;
	public static final int CHOC_AMULET     = HOLIDAY_FOOD+2;
	public static final int EASTER_EGG      = HOLIDAY_FOOD+3;
	public static final int RAINBOW_POTION  = HOLIDAY_FOOD+4;
	public static final int SHATTERED_CAKE  = HOLIDAY_FOOD+5;
	public static final int PUMPKIN_PIE     = HOLIDAY_FOOD+6;
	public static final int VANILLA_CAKE    = HOLIDAY_FOOD+7;
	public static final int CANDY_CANE      = HOLIDAY_FOOD+8;
	public static final int SPARKLING_POTION= HOLIDAY_FOOD+9;
	public static final int MOONBERRY       = HOLIDAY_FOOD+10;
	public static final int FULLMOONBERRY   = HOLIDAY_FOOD+11;
	public static final int STRAWBERRY      = HOLIDAY_FOOD+12;
	public static final int CHERRY          = HOLIDAY_FOOD+13;
	public static final int DURIAN          = HOLIDAY_FOOD+14;
	public static final int STAR_FLOWER     = HOLIDAY_FOOD+15;
	static{
		assignItemRect(STEAMED_FISH,    16, 12);
		assignItemRect(FISH_LEFTOVER,   16, 12);
		assignItemRect(CHOC_AMULET,     16, 16);
		assignItemRect(EASTER_EGG,      12, 14);
		assignItemRect(RAINBOW_POTION,  12, 14);
		assignItemRect(SHATTERED_CAKE,  14, 13);
		assignItemRect(PUMPKIN_PIE,     16, 12);
		assignItemRect(VANILLA_CAKE,    14, 13);
		assignItemRect(CANDY_CANE,      13, 16);
		assignItemRect(SPARKLING_POTION, 7, 16);
		assignItemRect(MOONBERRY,       16, 16);
		assignItemRect(FULLMOONBERRY,   16, 16);
		assignItemRect(STRAWBERRY,      16, 16);
		assignItemRect(CHERRY,          16, 16);
		assignItemRect(DURIAN,          16, 16);
		assignItemRect(STAR_FLOWER,     16, 16);
	}

	private static final int QUEST  =                                       xy(1, 30);  //16 slots
	public static final int DREAM_LEAF = QUEST+0;
	public static final int DUST    = QUEST+1;
	public static final int CANDLE  = QUEST+2;
	public static final int EMBER   = QUEST+3;
	public static final int PICKAXE = QUEST+4;
	public static final int ORE     = QUEST+5;
	public static final int TOKEN   = QUEST+6;
	public static final int BLOB    = QUEST+7;
	public static final int SHARD   = QUEST+8;
	public static final int ESCAPE  = QUEST+9;
	public static final int STATUE  = QUEST+10;
	public static final int HEAL_LEAF = QUEST+11;
	public static final int NUT_VEGETABLE = QUEST+12;
	public static final int TRUFFLES = QUEST+13;
	public static final int BREW_LEFT = QUEST+14;
	public static final int MUSHROOM = QUEST+15;
	static{
		assignItemRect(DREAM_LEAF, 16, 16);
		assignItemRect(DUST,    12, 11);
		assignItemRect(CANDLE,  12, 12);
		assignItemRect(EMBER,   12, 11);
		assignItemRect(PICKAXE, 14, 14);
		assignItemRect(ORE,     15, 15);
		assignItemRect(TOKEN,   12, 12);
		assignItemRect(BLOB,    10,  9);
		assignItemRect(SHARD,    8, 10);
		assignItemRect(ESCAPE,   8, 16);
		assignItemRect(STATUE,  10, 16);
		assignItemRect(HEAL_LEAF, 16, 16);
		assignItemRect(NUT_VEGETABLE, 16, 16);
		assignItemRect(TRUFFLES, 16, 16);
		assignItemRect(BREW_LEFT, 16, 16);
		assignItemRect(MUSHROOM, 16, 16);
	}

	private static final int BAGS       =                                   xy(1, 31);  //16 slots
	public static final int WATERSKIN   = BAGS+0;
	public static final int BACKPACK    = BAGS+1;
	public static final int POUCH       = BAGS+2;
	public static final int HOLDER      = BAGS+3;
	public static final int BANDOLIER   = BAGS+4;
	public static final int HOLSTER     = BAGS+5;
	public static final int VIAL        = BAGS+6;
	public static final int YELLOW_DEWDROP = BAGS+7;
	public static final int RED_DEWDROP = BAGS+8;
	public static final int VIOLET_DEWDROP = BAGS+9;
	public static final int UPGRADE_GOO_YELLOW = BAGS+10;
	public static final int UPGRADE_GOO_RED = BAGS+11;
	public static final int UPGRADE_GOO_VIOLET = BAGS+12;
	public static final int UPGRADE_EATER = BAGS+13;
	public static final int TRANSMUTATION_BALL = BAGS+14;
	public static final int SPS_POTION_MENDING = BAGS+15;
	static{
		assignItemRect(WATERSKIN,   16, 14);
		assignItemRect(BACKPACK,    16, 16);
		assignItemRect(POUCH,       14, 15);
		assignItemRect(HOLDER,      16, 16);
		assignItemRect(BANDOLIER,   15, 16);
		assignItemRect(HOLSTER,     15, 16);
		assignItemRect(VIAL,        12, 12);
		assignItemRect(YELLOW_DEWDROP, 16, 16);
		assignItemRect(RED_DEWDROP, 16, 16);
		assignItemRect(VIOLET_DEWDROP, 16, 16);
		assignItemRect(UPGRADE_GOO_YELLOW, 16, 16);
		assignItemRect(UPGRADE_GOO_RED, 16, 16);
		assignItemRect(UPGRADE_GOO_VIOLET, 16, 16);
		assignItemRect(UPGRADE_EATER, 16, 16);
		assignItemRect(TRANSMUTATION_BALL, 16, 16);
		assignItemRect(SPS_POTION_MENDING, 16, 16);
	}

	private static final int DOCUMENTS  =                                   xy(1, 32);  //16 slots
	public static final int GUIDE_PAGE  = DOCUMENTS+0;
	public static final int ALCH_PAGE   = DOCUMENTS+1;
	public static final int SEWER_PAGE  = DOCUMENTS+2;
	public static final int PRISON_PAGE = DOCUMENTS+3;
	public static final int CAVES_PAGE  = DOCUMENTS+4;
	public static final int CITY_PAGE   = DOCUMENTS+5;
	public static final int HALLS_PAGE  = DOCUMENTS+6;
	public static final int SPS_POTION_MIGHT = DOCUMENTS+7;
	public static final int SPS_POTION_SHIELD = DOCUMENTS+8;
	public static final int SPS_POTION_OVERHEALING = DOCUMENTS+9;
	public static final int SPS_POTION_MIXING = DOCUMENTS+10;
	public static final int PUDDING_CUP = DOCUMENTS+11;
	public static final int ELEVATOR = DOCUMENTS+12;
	static{
		assignItemRect(GUIDE_PAGE,  10, 11);
		assignItemRect(ALCH_PAGE,   10, 11);
		assignItemRect(SEWER_PAGE,  10, 11);
		assignItemRect(PRISON_PAGE, 10, 11);
		assignItemRect(CAVES_PAGE,  10, 11);
		assignItemRect(CITY_PAGE,   10, 11);
		assignItemRect(HALLS_PAGE,  10, 11);
		assignItemRect(SPS_POTION_MIGHT, 16, 16);
		assignItemRect(SPS_POTION_SHIELD, 16, 16);
		assignItemRect(SPS_POTION_OVERHEALING, 16, 16);
		assignItemRect(SPS_POTION_MIXING, 16, 16);
		assignItemRect(PUDDING_CUP, 16, 16);
		assignItemRect(ELEVATOR, 16, 16);
	}

	private static final int SPS_SUMMON =                                  xy(1, 33);
	public static final int FAIRY_CARD = SPS_SUMMON;
	public static final int MOBILE = SPS_SUMMON+1;
	public static final int ACTIVE_MR_DESTRUCTO = SPS_SUMMON+2;
	static {
		assignItemRect(FAIRY_CARD, 16, 16);
		assignItemRect(MOBILE, 16, 16);
		assignItemRect(ACTIVE_MR_DESTRUCTO, 16, 16);
	}

	private static final int SPS_CRAFT =                                   xy(1, 34);
	public static final int BUILD_BOMB = SPS_CRAFT;
	public static final int HUGE_BOMB = SPS_CRAFT+1;
	public static final int SP_AMMO = SPS_CRAFT+2;
	public static final int E_DUST = SPS_CRAFT+3;
	public static final int M_WEB = SPS_CRAFT+4;
	public static final int TOWEL = SPS_CRAFT+5;
	public static final int SPECTACLES = SPS_CRAFT+6;
	public static final int AUTO_POTION = SPS_CRAFT+7;
	public static final int KNOWLEDGE_BOOK = SPS_CRAFT+8;
	public static final int SPS_PET_EGG = SPS_CRAFT+9;
	public static final int PALANTIR = SPS_CRAFT+10;
	public static final int DOOR_BLOCK = SPS_CRAFT+11;
	public static final int PLANT_POT_BLOCK = SPS_CRAFT+12;
	public static final int SHOPPING_CART = SPS_CRAFT+13;
	public static final int ORB_OF_ZOT = SPS_CRAFT+14;
	static {
		assignItemRect(BUILD_BOMB, 16, 16);
		assignItemRect(HUGE_BOMB, 16, 16);
		assignItemRect(SP_AMMO, 16, 16);
		assignItemRect(E_DUST, 16, 16);
		assignItemRect(M_WEB, 16, 16);
		assignItemRect(TOWEL, 16, 16);
		assignItemRect(SPECTACLES, 16, 16);
		assignItemRect(AUTO_POTION, 16, 16);
		assignItemRect(KNOWLEDGE_BOOK, 16, 16);
		assignItemRect(SPS_PET_EGG, 16, 16);
		assignItemRect(PALANTIR, 16, 16);
		assignItemRect(DOOR_BLOCK, 16, 16);
		assignItemRect(PLANT_POT_BLOCK, 16, 16);
		assignItemRect(SHOPPING_CART, 16, 16);
		assignItemRect(ORB_OF_ZOT, 16, 16);
	}

	private static final int SPS_BATTLE =                                  xy(1, 35);
	public static final int HEART_OF_SCARECROW = SPS_BATTLE;
	public static final int FIRE_CRACKER = SPS_BATTLE+1;
	public static final int BOSS_RUSH = SPS_BATTLE+2;
	public static final int MONEY_PACK = SPS_BATTLE+3;
	public static final int YEAR_PET_EGG = SPS_BATTLE+4;
	public static final int CURSE_BLOOD = SPS_BATTLE+5;
	public static final int AFLY_FOOD = SPS_BATTLE+6;
	public static final int AFLY_EGG = SPS_BATTLE+7;
	public static final int ADAMANT_WEAPON = SPS_BATTLE+8;
	public static final int ADAMANT_ARMOR = SPS_BATTLE+9;
	public static final int ADAMANT_WAND = SPS_BATTLE+10;
	public static final int ADAMANT_RING = SPS_BATTLE+11;
	public static final int PET_FOOD = SPS_BATTLE+12;
	public static final int SKILL_ATK = SPS_BATTLE+13;
	public static final int SKILL_DEF = SPS_BATTLE+14;
	public static final int SKILL_MIG = SPS_BATTLE+15;
	static {
		assignItemRect(HEART_OF_SCARECROW, 16, 16);
		assignItemRect(FIRE_CRACKER, 16, 16);
		assignItemRect(BOSS_RUSH, 16, 16);
		assignItemRect(MONEY_PACK, 16, 16);
		assignItemRect(YEAR_PET_EGG, 16, 16);
		assignItemRect(CURSE_BLOOD, 16, 16);
		assignItemRect(AFLY_FOOD, 16, 16);
		assignItemRect(AFLY_EGG, 16, 16);
		assignItemRect(ADAMANT_WEAPON, 16, 16);
		assignItemRect(ADAMANT_ARMOR, 16, 16);
		assignItemRect(ADAMANT_WAND, 16, 16);
		assignItemRect(ADAMANT_RING, 16, 16);
		assignItemRect(PET_FOOD, 16, 16);
		assignItemRect(SKILL_ATK, 16, 16);
		assignItemRect(SKILL_DEF, 16, 16);
		assignItemRect(SKILL_MIG, 16, 16);
	}

	private static final int SPS_SHOP =                                    xy(1, 36);
	public static final int POCKET_BALL = SPS_SHOP;
	public static final int NORN_GREEN = SPS_SHOP+1;
	public static final int NORN_BLUE = SPS_SHOP+2;
	public static final int NORN_ORANGE = SPS_SHOP+3;
	public static final int NORN_PURPLE = SPS_SHOP+4;
	public static final int NORN_YELLOW = SPS_SHOP+5;
	public static final int NEPTUNUS_TRIDENT = SPS_SHOP+6;
	public static final int ARES_SWORD = SPS_SHOP+7;
	public static final int JUPITERS_WRAITH = SPS_SHOP+8;
	public static final int CROM_CRUACH_AXE = SPS_SHOP+9;
	public static final int LOKIS_FLAIL = SPS_SHOP+10;
	public static final int GNOLL_CLOTHES = SPS_SHOP+11;
	public static final int SOUL_COLLECT = SPS_SHOP+12;
	static {
		assignItemRect(POCKET_BALL, 16, 16);
		assignItemRect(NORN_GREEN, 16, 16);
		assignItemRect(NORN_BLUE, 16, 16);
		assignItemRect(NORN_ORANGE, 16, 16);
		assignItemRect(NORN_PURPLE, 16, 16);
		assignItemRect(NORN_YELLOW, 16, 16);
		assignItemRect(NEPTUNUS_TRIDENT, 16, 16);
		assignItemRect(ARES_SWORD, 16, 16);
		assignItemRect(JUPITERS_WRAITH, 16, 16);
		assignItemRect(CROM_CRUACH_AXE, 16, 16);
		assignItemRect(LOKIS_FLAIL, 16, 16);
		assignItemRect(GNOLL_CLOTHES, 16, 16);
		assignItemRect(SOUL_COLLECT, 16, 16);
	}

	private static final int SPS_BOSS_REWARDS =                            xy(1, 37);
	public static final int PLAYER_ICON = SPS_BOSS_REWARDS;
	public static final int POWER_HAND = SPS_BOSS_REWARDS+1;
	public static final int TENGU_SWORD = SPS_BOSS_REWARDS+2;
	public static final int HAND_CANNON = SPS_BOSS_REWARDS+3;
	public static final int FOUR_CLOVER = SPS_BOSS_REWARDS+4;
	public static final int SPS_EASTER_EGG = SPS_BOSS_REWARDS+5;
	public static final int SHADOW_DRAGON_EGG = xy(15, 38);
	public static final int TREASURE_MAP = SPS_BOSS_REWARDS+6;
	public static final int SHIT_BALL = SPS_BOSS_REWARDS+7;
	public static final int SAVE_YOUR_LIFE = SPS_BOSS_REWARDS+8;
	public static final int SJRB_MUSIC = SPS_BOSS_REWARDS+9;
	public static final int SPORK = SPS_BOSS_REWARDS+10;
	static {
		assignItemRect(PLAYER_ICON, 16, 16);
		assignItemRect(POWER_HAND, 16, 16);
		assignItemRect(TENGU_SWORD, 16, 16);
		assignItemRect(HAND_CANNON, 16, 16);
		assignItemRect(FOUR_CLOVER, 16, 16);
		assignItemRect(SPS_EASTER_EGG, 16, 16);
		assignItemRect(SHADOW_DRAGON_EGG, 16, 16);
		assignItemRect(TREASURE_MAP, 16, 16);
		assignItemRect(SHIT_BALL, 16, 16);
		assignItemRect(SAVE_YOUR_LIFE, 16, 16);
		assignItemRect(SJRB_MUSIC, 16, 16);
		assignItemRect(SPORK, 16, 16);
	}

	public static final int SPS_RUNIC_BLADE =                              xy(5, 61);
	static {
		assignItemRect(SPS_RUNIC_BLADE, 16, 16);
	}

	private static final int SPS_CITY_ITEMS =                              xy(1, 38);
	public static final int BUG_MEAT = SPS_CITY_ITEMS;
	public static final int LIGHT_BOMB = SPS_CITY_ITEMS+1;
	public static final int FISHING_BOMB = SPS_CITY_ITEMS+2;
	static {
		assignItemRect(BUG_MEAT, 16, 16);
		assignItemRect(LIGHT_BOMB, 16, 16);
		assignItemRect(FISHING_BOMB, 16, 16);
	}

	private static final int SPS_LINK_DROPS =                              xy(1, 48);
	public static final int LEGACY_WAVE = SPS_LINK_DROPS;
	public static final int LEGACY_SKULL = SPS_LINK_DROPS+1;
	public static final int EARTH_BOMB = SPS_LINK_DROPS+2;
	public static final int DARK_BOMB = SPS_LINK_DROPS+3;
	static {
		for (int i = SPS_LINK_DROPS; i < SPS_LINK_DROPS+4; i++) assignItemRect(i, 16, 16);
	}

	// Original SPS-PD high-food sprites. Existing KEBAB and HOTDOG slots are reused.
	private static final int SPS_HIGH_FOODS = SPS_LINK_DROPS+4;
	public static final int CHICKENNUGGET = SPS_HIGH_FOODS;
	public static final int FOAMED = SPS_HIGH_FOODS+1;
	public static final int FRUIT_SALAD = SPS_HIGH_FOODS+2;
	public static final int HAMBURGER = SPS_HIGH_FOODS+3;
	public static final int HERB_MEAT = SPS_HIGH_FOODS+4;
	public static final int HONEY_MEAT = SPS_HIGH_FOODS+5;
	public static final int HONEY_RICE = SPS_HIGH_FOODS+6;
	public static final int ICECREAM = SPS_HIGH_FOODS+7;
	public static final int PERFECT_FOOD = SPS_HIGH_FOODS+8;
	public static final int RICE_FOOD = SPS_HIGH_FOODS+9;
	public static final int VEGETABLE_SOUP = SPS_HIGH_FOODS+10;
	public static final int HONEY_GEL = SPS_HIGH_FOODS+11;
	public static final int GEL = SPS_HIGH_FOODS+12;
	public static final int HONEY_WATER = SPS_HIGH_FOODS+13;
	public static final int CHOCOLATE = SPS_HIGH_FOODS+14;
	public static final int FOOD_FANS = SPS_HIGH_FOODS+15;
	public static final int FRENCH_FRIES = SPS_HIGH_FOODS+16;
	static {
		for (int i = SPS_HIGH_FOODS; i < SPS_HIGH_FOODS+17; i++) assignItemRect(i, 16, 16);
	}

	private static final int SPS_SKIN_TWO_WARRIOR = SPS_HIGH_FOODS+17;
	public static final int SPS_STR_BOTTLE = SPS_SKIN_TWO_WARRIOR;
	public static final int SPS_DEMON_PAPER = SPS_SKIN_TWO_WARRIOR+1;
	public static final int SPS_BASE_ARMOR = SPS_SKIN_TWO_WARRIOR+2;
	public static final int SPS_GNOLL_MARK = SPS_SKIN_TWO_WARRIOR+3;
	public static final int SPS_UNDEAD_BOOK = SPS_SKIN_TWO_WARRIOR+4;
	public static final int SPS_TAURCEN_BOW = SPS_SKIN_TWO_WARRIOR+5;
	public static final int SPS_HOLY_WATER = SPS_SKIN_TWO_WARRIOR+6;
	public static final int SPS_MECH_POCKET = SPS_SKIN_TWO_WARRIOR+7;
	public static final int SPS_GRASS_BOOK = SPS_SKIN_TWO_WARRIOR+8;
	public static final int SPS_LIFE_ARMOR = SPS_SKIN_TWO_WARRIOR+9;
	static {
		for (int i = SPS_SKIN_TWO_WARRIOR; i < SPS_SKIN_TWO_WARRIOR+10; i++) assignItemRect(i, 16, 16);
	}

	private static final int SPS_SKIN_THREE =                             xy(1, 50);
	public static final int SPS_HEAL_BAG = SPS_SKIN_THREE;
	public static final int SPS_RANGE_BAG = SPS_SKIN_THREE+1;
	public static final int SPS_DANCE_LION = SPS_SKIN_THREE+2;
	public static final int SPS_WHISK = SPS_SKIN_THREE+3;
	public static final int SPS_SAVAGE_HELMET = SPS_SKIN_THREE+4;
	public static final int SPS_HORSE_TOTEM = SPS_SKIN_THREE+5;
	static {
		for (int i = SPS_SKIN_THREE; i < SPS_SKIN_THREE+6; i++) assignItemRect(i, 16, 16);
	}

	private static final int SPS_SKIN_FOUR =                              xy(1, 51);
	public static final int SPS_TEST_WEAPON = SPS_SKIN_FOUR;
	public static final int SPS_TEST_ARMOR = SPS_SKIN_FOUR+1;
	public static final int SPS_TEST_WAND = SPS_SKIN_FOUR+2;
	public static final int SPS_REWARD_PAPER = SPS_SKIN_FOUR+3;
	public static final int SPS_ELF_BOW = SPS_SKIN_FOUR+4;
	public static final int SPS_DEMON_BLADE = SPS_SKIN_FOUR+5;
	public static final int SPS_NEED_PAPER = SPS_SKIN_FOUR+6;
	public static final int SPS_PPC = SPS_SKIN_FOUR+7;
	public static final int SPS_MIND_ARROW = SPS_SKIN_FOUR+8;
	public static final int SPS_LEADER_FLAG = SPS_SKIN_FOUR+9;
	public static final int SPS_NM_HEAL_BAG = SPS_SKIN_FOUR+10;
	public static final int SPS_DICE_TOWER = SPS_SKIN_FOUR+11;
	public static final int SPS_WATER_BLOCK = SPS_SKIN_FOUR+12;
	public static final int BOTTLE_FIRE = SPS_SKIN_FOUR+13;
	public static final int HONEY_ARROW = SPS_SKIN_FOUR+14;
	public static final int LYNN_DOLL = SPS_SKIN_FOUR+15;
	static {
		for (int i = SPS_SKIN_FOUR; i < SPS_SKIN_FOUR+16; i++) assignItemRect(i, 16, 16);
	}

	private static final int SPS_SKIN_SIX =                               xy(1, 52);
	public static final int SPS_SERIOUS_PUNCH = SPS_SKIN_SIX;
	public static final int SPS_ANKH_SHIELD = SPS_SKIN_SIX+1;
	public static final int SPS_PPC2 = SPS_SKIN_SIX+2;
	public static final int SPS_ELE_KATANA = SPS_SKIN_SIX+3;
	public static final int SPS_SHOOT_GUN = SPS_SKIN_SIX+4;
	public static final int SPS_WEIGHTSTONE = SPS_SKIN_SIX+5;
	public static final int SPS_SHOOT_AMMO = SPS_SKIN_SIX+6;
	public static final int SPS_SHOOT_END_AMMO = SPS_SKIN_SIX+7;
	public static final int SPS_EQUIP_CHANGE = SPS_SKIN_SIX+8;
	static {
		for (int i = SPS_SKIN_SIX; i < SPS_SKIN_SIX+9; i++) assignItemRect(i, 16, 16);
	}
	private static final int SPS_TOWN_MISC =                              xy(10, 52);
	public static final int FISH_BONE = SPS_TOWN_MISC;
	public static final int GHOST_GIRL_ROSE = SPS_TOWN_MISC+1;
	public static final int RAIN_SHIELD = SPS_TOWN_MISC+2;
	public static final int CURSE_PHONE = SPS_TOWN_MISC+3;
	public static final int AFLY_SOCK = SPS_TOWN_MISC+4;
	public static final int MELEE_PAN = SPS_TOWN_MISC+5;
	public static final int XIXI_BOX = SPS_TOWN_MISC+6;
	static {
		for (int i = SPS_TOWN_MISC; i < SPS_TOWN_MISC+7; i++) assignItemRect(i, 16, 16);
	}

	private static final int SPS_SKIN_SEVEN =                              xy(1, 53);
	public static final int SPS_MEGA_CANNON = SPS_SKIN_SEVEN;
	public static final int SPS_MEGA_AMMO_SMALL = SPS_SKIN_SEVEN+1;
	public static final int SPS_MEGA_AMMO_MEDIUM = SPS_SKIN_SEVEN+2;
	public static final int SPS_MEGA_AMMO_LARGE = SPS_SKIN_SEVEN+3;
	public static final int SPS_ROCKMAN_JUMP = SPS_SKIN_SEVEN+4;
	public static final int SPS_BUNNY_ARMOR = SPS_SKIN_SEVEN+5;
	public static final int SPS_BUNNY_DAGGER = SPS_SKIN_SEVEN+6;
	public static final int SPS_NINJA_FAN = SPS_SKIN_SEVEN+7;
	public static final int SPS_MINI_BOMB = SPS_SKIN_SEVEN+8;
	public static final int SPS_FISH_PET_FOOD = SPS_SKIN_SEVEN+9;
	public static final int SPS_FISH_FOOD = SPS_SKIN_SEVEN+10;
	public static final int SPS_HONEY = SPS_SKIN_SEVEN+11;
	public static final int SPS_ZONGZI = SPS_SKIN_SEVEN+12;
	public static final int SPS_NUT_CAKE = SPS_SKIN_SEVEN+13;
	public static final int SPS_YEAR_FOOD = SPS_SKIN_SEVEN+14;
	public static final int DOLYA_SLATE = SPS_SKIN_SEVEN+15;
	static {
		for (int i = SPS_SKIN_SEVEN; i < SPS_SKIN_SEVEN+16; i++) assignItemRect(i, 16, 16);
	}

	private static final int SPS_HOLIDAY_STAPLES =                         xy(1, 54);
	public static final int SPS_PASTY = SPS_HOLIDAY_STAPLES;
	public static final int SPS_SPRING_ASSORTED = SPS_HOLIDAY_STAPLES+1;
	public static final int SPS_KNOWLEDGE_FOOD = SPS_HOLIDAY_STAPLES+2;
	public static final int SPS_PASTY_EASTER_EGG = SPS_HOLIDAY_STAPLES+3;
	public static final int SPS_PUMPKIN_PIE = SPS_HOLIDAY_STAPLES+4;
	public static final int SPS_TURKEY_MEAT = SPS_HOLIDAY_STAPLES+5;
	public static final int SPS_CANDY_CANE = SPS_HOLIDAY_STAPLES+6;
	public static final int SPS_BRICK_FOOD = SPS_HOLIDAY_STAPLES+7;
	public static final int SPS_JELLY_SWORD = SPS_HOLIDAY_STAPLES+8;
	public static final int SPS_GARBAGE = SPS_HOLIDAY_STAPLES+9;
	static {
		for (int i = SPS_HOLIDAY_STAPLES; i < SPS_HOLIDAY_STAPLES+10; i++) assignItemRect(i, 16, 16);
	}

	private static final int SPS_SEWER_REWARDS =                           xy(1, 39);
	public static final int POCKET_BALL_EMPTY = SPS_SEWER_REWARDS;
	public static final int WOODEN_SHIELD = SPS_SEWER_REWARDS+1;
	public static final int MIX_BOTTLE = SPS_SEWER_REWARDS+2;
	public static final int SLIME_BALL = SPS_SEWER_REWARDS+3;
	public static final int RUSTY_CAT = SPS_SEWER_REWARDS+4;
	public static final int CHAOS_PACK = SPS_SEWER_REWARDS+5;
	public static final int EMPTY_BODY = SPS_SEWER_REWARDS+6;
	public static final int SHADOW_EATER = SPS_SEWER_REWARDS+7;
	public static final int POT_KEY = SPS_SEWER_REWARDS+8;
	public static final int LUCKY_BADGE = SPS_SEWER_REWARDS+9;
	public static final int LEGACY_BOOMERANG = SPS_SEWER_REWARDS+10;
	static {
		assignItemRect(POCKET_BALL_EMPTY, 16, 16);
		assignItemRect(WOODEN_SHIELD, 16, 16);
		assignItemRect(MIX_BOTTLE, 16, 16);
		assignItemRect(SLIME_BALL, 16, 16);
		assignItemRect(RUSTY_CAT, 16, 16);
		assignItemRect(CHAOS_PACK, 16, 16);
		assignItemRect(EMPTY_BODY, 16, 16);
		assignItemRect(SHADOW_EATER, 16, 16);
		assignItemRect(POT_KEY, 16, 16);
		assignItemRect(LUCKY_BADGE, 16, 16);
		assignItemRect(LEGACY_BOOMERANG, 16, 16);
	}

	private static final int SPS_ERROR_ITEMS =                             xy(3, 56);
	public static final int SPS_ERROR_WEAPON = SPS_ERROR_ITEMS;
	public static final int SPS_ERROR_ARMOR = SPS_ERROR_ITEMS+1;
	public static final int SPS_ERROR_AMMO = SPS_ERROR_ITEMS+2;
	public static final int SPS_ROBOT_HEART = SPS_ERROR_ITEMS+3;
	public static final int TEST_CLOAK = SPS_ERROR_ITEMS+4;
	static {
		for (int i = SPS_ERROR_ITEMS; i < SPS_ERROR_ITEMS+5; i++) assignItemRect(i, 16, 16);
	}

	private static final int SPS_GIFT_PET_EGGS =                          xy(1, 57);
	public static final int COCO_CAT_EGG = SPS_GIFT_PET_EGGS;
	public static final int VELOCIROOSTER_EGG = SPS_GIFT_PET_EGGS+1;
	public static final int HARO_EGG = SPS_GIFT_PET_EGGS+2;
	public static final int PIG_PET_EGG = SPS_GIFT_PET_EGGS+3;
	public static final int RABBIT_PET_EGG = SPS_GIFT_PET_EGGS+4;
	public static final int LING_HEART = SPS_GIFT_PET_EGGS+5;
	public static final int SPS_PUMPKIN = SPS_GIFT_PET_EGGS+6;
	public static final int BUTTERFLY_EGG = SPS_GIFT_PET_EGGS+7;
	public static final int CHOCOBO_EGG = SPS_GIFT_PET_EGGS+8;
	public static final int DATURA_EGG = SPS_GIFT_PET_EGGS+9;
	public static final int DOG_PET_EGG = SPS_GIFT_PET_EGGS+10;
	public static final int DWARF_BOY_EGG = SPS_GIFT_PET_EGGS+11;
	public static final int FLY_EGG = SPS_GIFT_PET_EGGS+12;
	public static final int FOX_HELPER_EGG = SPS_GIFT_PET_EGGS+13;
	public static final int FROG_PET_EGG = SPS_GIFT_PET_EGGS+14;
	public static final int GENTLE_CRAB_EGG = SPS_GIFT_PET_EGGS+15;
	private static final int SPS_BASE_PET_EGGS =                         xy(1, 58);
	public static final int KODORA_EGG = SPS_BASE_PET_EGGS;
	public static final int LIT_DEMON_EGG = SPS_BASE_PET_EGGS+1;
	public static final int MONKEY_EGG = SPS_BASE_PET_EGGS+2;
	public static final int RIBBON_RAT_EGG = SPS_BASE_PET_EGGS+3;
	public static final int SNAKE_PET_EGG = SPS_BASE_PET_EGGS+4;
	public static final int SPIDER_PET_EGG = SPS_BASE_PET_EGGS+5;
	public static final int STAR_KID_EGG = SPS_BASE_PET_EGGS+6;
	public static final int STONE_PET_EGG = SPS_BASE_PET_EGGS+7;
	public static final int BLUE_DRAGON_EGG = SPS_BASE_PET_EGGS+8;
	public static final int BLUE_GIRL_EGG = SPS_BASE_PET_EGGS+9;
	public static final int BUG_DRAGON_EGG = SPS_BASE_PET_EGGS+10;
	public static final int GOLD_DRAGON_EGG = SPS_BASE_PET_EGGS+11;
	public static final int GREEN_DRAGON_EGG = SPS_BASE_PET_EGGS+12;
	public static final int LERY_FIRE_EGG = SPS_BASE_PET_EGGS+13;
	public static final int LIGHT_DRAGON_EGG = SPS_BASE_PET_EGGS+14;
	public static final int RED_DRAGON_EGG = SPS_BASE_PET_EGGS+15;
	private static final int SPS_BASE_PET_EGGS_2 =                       xy(1, 59);
	public static final int SCORPION_EGG = SPS_BASE_PET_EGGS_2;
	public static final int VIOLET_DRAGON_EGG = SPS_BASE_PET_EGGS_2+1;
	public static final int VIP_CARD = SPS_BASE_PET_EGGS_2+2;
	public static final int SPS_KEY_RING = SPS_BASE_PET_EGGS_2+3;
	public static final int SPS_ARROW_COLLECTER = SPS_BASE_PET_EGGS_2+4;
	public static final int SPS_GLASS_TOTEM = SPS_BASE_PET_EGGS_2+5;
	public static final int SPS_GREEN_DEWDROP = SPS_BASE_PET_EGGS_2+6;
	public static final int SPS_GOLD_BAG = SPS_BASE_PET_EGGS_2+7;
	public static final int SPS_SPECIAL_COIN = SPS_BASE_PET_EGGS_2+8;
	public static final int SPS_MIT_BOTTLE = SPS_BASE_PET_EGGS_2+9;
	public static final int SPS_UNBLESS_ANKH = SPS_BASE_PET_EGGS_2+10;
	public static final int SPS_CALL_COCONUT = SPS_BASE_PET_EGGS_2+11;
	public static final int SPS_POCKET_BALL_FULL = SPS_BASE_PET_EGGS_2+12;
	public static final int SPS_HOOK_HAM = SPS_BASE_PET_EGGS_2+13;
	public static final int SPS_LOLLIPOP = SPS_BASE_PET_EGGS_2+14;
	public static final int SPS_EASTER_TREE = SPS_BASE_PET_EGGS_2+15;
	private static final int SPS_EASTER_WEAPONS_2 =                      xy(1, 60);
	public static final int SPS_PAPER_FAN = SPS_EASTER_WEAPONS_2;
	public static final int SPS_MINI_MOAI = SPS_EASTER_WEAPONS_2+1;
	public static final int SPS_DRAGON_BOAT = SPS_EASTER_WEAPONS_2+2;
	public static final int SPS_GOEI = SPS_EASTER_WEAPONS_2+3;
	public static final int SPS_TEKKO_KAGI = SPS_EASTER_WEAPONS_2+4;
	public static final int SPS_WRAITH_BREATH = SPS_EASTER_WEAPONS_2+5;
	public static final int SPS_STONE_CROSS = SPS_EASTER_WEAPONS_2+6;
	public static final int SPS_MIRROR_DOLL = SPS_EASTER_WEAPONS_2+7;
	public static final int SPS_HAND_LIGHT = SPS_EASTER_WEAPONS_2+8;
	public static final int SPS_CURSE_BOX = SPS_EASTER_WEAPONS_2+9;
	public static final int SPS_SMALL_CHAKRAM = SPS_EASTER_WEAPONS_2+10;
	public static final int SPS_HUGE_SHURIKEN = SPS_EASTER_WEAPONS_2+11;
	public static final int SPS_TAMAHAWK = SPS_EASTER_WEAPONS_2+12;
	public static final int SPS_GOBLIN_SHIELD = SPS_EASTER_WEAPONS_2+13;
	public static final int SPS_SP_KNUCKLES = SPS_EASTER_WEAPONS_2+14;
	public static final int SPS_FLAG = SPS_EASTER_WEAPONS_2+15;
	// These source icons already occur pixel-for-pixel in occupied atlas slots.
	public static final int SPS_EASTER_BRICK =                            xy(8, 54);
	public static final int SPS_KEY_WEAPON = SPS_CALL_COCONUT;
	static {
		for (int i = SPS_GIFT_PET_EGGS; i < SPS_GIFT_PET_EGGS+16; i++) assignItemRect(i, 16, 16);
		for (int i = SPS_BASE_PET_EGGS; i < SPS_BASE_PET_EGGS+16; i++) assignItemRect(i, 16, 16);
		for (int i = SPS_BASE_PET_EGGS_2; i < SPS_BASE_PET_EGGS_2+16; i++) assignItemRect(i, 16, 16);
		for (int i = SPS_EASTER_WEAPONS_2; i < SPS_EASTER_WEAPONS_2+16; i++) assignItemRect(i, 16, 16);
	}

	private static final int SPS_SELL_ITEMS =                              xy(8, 56);
	public static final int APK_931 = SPS_SELL_ITEMS;
	public static final int CRYSTAL_VIAL = APK_931;
	public static final int BOTTLE_FLOWER = SPS_SELL_ITEMS+1;
	public static final int BROKEN_HAMMER = SPS_SELL_ITEMS+2;
	public static final int CROSS_PHOTO = SPS_SELL_ITEMS+3;
	public static final int DWARF_HAMMER = SPS_SELL_ITEMS+4;
	public static final int HUMMING_TOOL = SPS_SELL_ITEMS+5;
	public static final int MIRROR_2 = SPS_SELL_ITEMS+6;
	public static final int NOUTH_SOUTH = SPS_SELL_ITEMS+7;
	public static final int SELL_PERMIT = SPS_SELL_ITEMS+8;
	private static final int SPS_SELL_ITEMS_2 =                            xy(11, 54);
	public static final int SHEEP_FUR = SPS_SELL_ITEMS_2;
	public static final int SIMPLE_360 = SPS_SELL_ITEMS_2+1;
	public static final int TISSUE = SPS_SELL_ITEMS_2+2;
	public static final int UNCLE_DUMBBELL = SPS_SELL_ITEMS_2+3;
	public static final int APOSTLE_BOX = SPS_SELL_ITEMS_2+4;
	public static final int SPS_JOURNAL_PAGE = SPS_SELL_ITEMS_2+5;
	public static final int MONEY_BOOK = SPS_JOURNAL_PAGE;
	static {
		for (int i = SPS_SELL_ITEMS; i < SPS_SELL_ITEMS+9; i++) assignItemRect(i, 16, 16);
		for (int i = SPS_SELL_ITEMS_2; i < SPS_SELL_ITEMS_2+6; i++) assignItemRect(i, 16, 16);
	}

	private static final int SPS_GUNS =                                    xy(1, 40);
	public static final int LEGACY_BULLET = SPS_GUNS;
	public static final int SLING = SPS_GUNS+1;
	public static final int GUN_A = SPS_GUNS+2;
	public static final int GUN_B = SPS_GUNS+3;
	public static final int GUN_C = SPS_GUNS+4;
	public static final int GUN_D = SPS_GUNS+5;
	public static final int GUN_E = SPS_GUNS+6;
	public static final int TOY_GUN = SPS_GUNS+7;
	public static final int LEGACY_ARROW = SPS_GUNS+8;
	public static final int WOODEN_BOW = SPS_GUNS+9;
	public static final int STONE_BOW = SPS_GUNS+10;
	public static final int METAL_BOW = SPS_GUNS+11;
	public static final int ALLOY_BOW = SPS_GUNS+12;
	public static final int PVC_BOW = SPS_GUNS+13;
	public static final int MANY_KNIVE = SPS_GUNS+14;
	public static final int LEGACY_KNIFE = SPS_GUNS+15;
	static {
		assignItemRect(LEGACY_BULLET, 16, 16);
		assignItemRect(SLING, 16, 16);
		assignItemRect(GUN_A, 16, 16);
		assignItemRect(GUN_B, 16, 16);
		assignItemRect(GUN_C, 16, 16);
		assignItemRect(GUN_D, 16, 16);
		assignItemRect(GUN_E, 16, 16);
		assignItemRect(TOY_GUN, 16, 16);
		assignItemRect(LEGACY_ARROW, 16, 16);
		assignItemRect(WOODEN_BOW, 16, 16);
		assignItemRect(STONE_BOW, 16, 16);
		assignItemRect(METAL_BOW, 16, 16);
		assignItemRect(ALLOY_BOW, 16, 16);
		assignItemRect(PVC_BOW, 16, 16);
		assignItemRect(MANY_KNIVE, 16, 16);
		assignItemRect(LEGACY_KNIFE, 16, 16);
	}

	private static final int SPS_CHALLENGE_REWARDS =                       xy(1, 41);
	public static final int KING_BONE = SPS_CHALLENGE_REWARDS;
	public static final int CAVE_SHELL = SPS_CHALLENGE_REWARDS+1;
	public static final int ANCIENT_COIN = SPS_CHALLENGE_REWARDS+2;
	public static final int SACRIFICE_BOOK = SPS_CHALLENGE_REWARDS+3;
	public static final int GOLDEN_NUT = SPS_CHALLENGE_REWARDS+4;
	public static final int CHALLENGE_REWARD_BAG = SPS_CHALLENGE_REWARDS+5;
	public static final int CRYSTAL_NUCLEUS = SPS_CHALLENGE_REWARDS+6;
	public static final int GOLDEN_SKELETON_KEY = SPS_CHALLENGE_REWARDS+7;
	static {
		assignItemRect(KING_BONE, 16, 16);
		assignItemRect(CAVE_SHELL, 16, 16);
		assignItemRect(ANCIENT_COIN, 16, 16);
		assignItemRect(SACRIFICE_BOOK, 16, 16);
		assignItemRect(GOLDEN_NUT, 16, 16);
		assignItemRect(CHALLENGE_REWARD_BAG, 16, 16);
		assignItemRect(CRYSTAL_NUCLEUS, 16, 16);
		assignItemRect(GOLDEN_SKELETON_KEY, 16, 16);
	}

	private static final int SPS_CHALLENGE_PAGES =                         xy(1, 42);
	public static final int CHALLENGE_BOOK = SPS_CHALLENGE_PAGES;
	public static final int ICE_CHALLENGE = SPS_CHALLENGE_PAGES+1;
	public static final int SEWER_CHALLENGE = SPS_CHALLENGE_PAGES+2;
	public static final int PRISON_CHALLENGE = SPS_CHALLENGE_PAGES+3;
	public static final int CAVE_CHALLENGE = SPS_CHALLENGE_PAGES+4;
	public static final int CITY_CHALLENGE = SPS_CHALLENGE_PAGES+5;
	public static final int COURAGE_CHALLENGE = SPS_CHALLENGE_PAGES+6;
	public static final int POWER_CHALLENGE = SPS_CHALLENGE_PAGES+7;
	public static final int WISDOM_CHALLENGE = SPS_CHALLENGE_PAGES+8;
	public static final int SPS_TRIFORCE = SPS_CHALLENGE_PAGES+9;
	public static final int VIAL_UPDATER = SPS_CHALLENGE_PAGES+10;
	static {
		assignItemRect(CHALLENGE_BOOK, 16, 16);
		assignItemRect(ICE_CHALLENGE, 16, 16);
		assignItemRect(SEWER_CHALLENGE, 16, 16);
		assignItemRect(PRISON_CHALLENGE, 16, 16);
		assignItemRect(CAVE_CHALLENGE, 16, 16);
		assignItemRect(CITY_CHALLENGE, 16, 16);
		assignItemRect(COURAGE_CHALLENGE, 16, 16);
		assignItemRect(POWER_CHALLENGE, 16, 16);
		assignItemRect(WISDOM_CHALLENGE, 16, 16);
		assignItemRect(SPS_TRIFORCE, 16, 16);
		assignItemRect(VIAL_UPDATER, 16, 16);
	}

	// Original SPS-PD row 17, preserved at its native 16x16 pixel size.
	private static final int SPS_LEGACY_WANDS =                            xy(1, 43);
	public static final int WAND_SPS_MAGIC_MISSILE = SPS_LEGACY_WANDS;
	public static final int WAND_POISON = SPS_LEGACY_WANDS+1;
	public static final int WAND_METEORITE = SPS_LEGACY_WANDS+2;
	public static final int WAND_FLOW = SPS_LEGACY_WANDS+3;
	public static final int WAND_CHARM = SPS_LEGACY_WANDS+4;
	public static final int WAND_SPS_DISINTEGRATION = SPS_LEGACY_WANDS+5;
	public static final int WAND_SPS_FIREBOLT = SPS_LEGACY_WANDS+6;
	public static final int WAND_BLOOD = SPS_LEGACY_WANDS+7;
	public static final int WAND_FREEZE = SPS_LEGACY_WANDS+8;
	public static final int WAND_SPS_LIGHTNING = SPS_LEGACY_WANDS+9;
	public static final int WAND_ACID = SPS_LEGACY_WANDS+10;
	public static final int WAND_LIGHT = SPS_LEGACY_WANDS+11;
	public static final int WAND_FLOCK = SPS_LEGACY_WANDS+12;
	public static final int WAND_TCLOUD = SPS_LEGACY_WANDS+13;
	public static final int WAND_ERROR = SPS_LEGACY_WANDS+14;
	static {
		for (int i = SPS_LEGACY_WANDS; i < SPS_LEGACY_WANDS+16; i++) {
			assignItemRect(i, 16, 16);
		}
	}

	// Original SPS-PD normal armor deck, in generation order.
	private static final int SPS_NORMAL_ARMORS = xy(1, 44);
	public static final int SPS_ARMOR_CLOTH = SPS_NORMAL_ARMORS;
	public static final int SPS_WOODEN_ARMOR = SPS_NORMAL_ARMORS+1;
	public static final int SPS_VEST_ARMOR = SPS_NORMAL_ARMORS+2;
	public static final int SPS_ARMOR_LEATHER = SPS_NORMAL_ARMORS+3;
	public static final int SPS_CERAMICS_ARMOR = SPS_NORMAL_ARMORS+4;
	public static final int SPS_RUBBER_ARMOR = SPS_NORMAL_ARMORS+5;
	public static final int SPS_ARMOR_DISC = SPS_NORMAL_ARMORS+6;
	public static final int SPS_STONE_ARMOR = SPS_NORMAL_ARMORS+7;
	public static final int SPS_CD_ARMOR = SPS_NORMAL_ARMORS+8;
	public static final int SPS_ARMOR_MAIL = SPS_NORMAL_ARMORS+9;
	public static final int SPS_MULTIPLE_ARMOR = SPS_NORMAL_ARMORS+10;
	public static final int SPS_STYROFOAM_ARMOR = SPS_NORMAL_ARMORS+11;
	public static final int SPS_ARMOR_SCALE = SPS_NORMAL_ARMORS+12;
	public static final int SPS_BULLET_ARMOR = SPS_NORMAL_ARMORS+13;
	public static final int SPS_PROTECTIVE_ARMOR = SPS_NORMAL_ARMORS+14;
	public static final int SPS_ARMOR_PLATE = SPS_NORMAL_ARMORS+15;
	public static final int SPS_MACHINE_ARMOR = SPS_NORMAL_ARMORS+16;
	public static final int SPS_PHANTOM_ARMOR = SPS_NORMAL_ARMORS+17;
	public static final int SPS_ARMOR_WARRIOR = SPS_NORMAL_ARMORS+18;
	public static final int SPS_ARMOR_MAGE = SPS_NORMAL_ARMORS+19;
	public static final int SPS_ARMOR_ROGUE = SPS_NORMAL_ARMORS+20;
	public static final int SPS_ARMOR_HUNTRESS = SPS_NORMAL_ARMORS+21;
	public static final int SPS_ARMOR_PERFORMER = SPS_NORMAL_ARMORS+22;
	public static final int SPS_ARMOR_SOLDIER = SPS_NORMAL_ARMORS+23;
	public static final int SPS_ARMOR_FOLLOWER = SPS_NORMAL_ARMORS+24;
	public static final int SPS_ARMOR_ASCETIC = SPS_NORMAL_ARMORS+25;
	static {
		for (int i = SPS_NORMAL_ARMORS; i < SPS_NORMAL_ARMORS+26; i++) {
			assignItemRect(i, 16, 16);
		}
	}

	// Original SPS-PD ordinary melee deck, ordered by tier and source generator order.
	private static final int SPS_NORMAL_WEAPONS = xy(1, 46);
	public static final int SPS_WEP_DAGGER = SPS_NORMAL_WEAPONS;
	public static final int SPS_WEP_KNUCKLES = SPS_NORMAL_WEAPONS+1;
	public static final int SPS_WEP_SHORT_SWORD = SPS_NORMAL_WEAPONS+2;
	public static final int SPS_WEP_MAGE_BOOK = SPS_NORMAL_WEAPONS+3;
	public static final int SPS_WEP_HANDAXE = SPS_NORMAL_WEAPONS+4;
	public static final int SPS_WEP_SPEAR = SPS_NORMAL_WEAPONS+5;
	public static final int SPS_WEP_DUAL_KNIVE = SPS_NORMAL_WEAPONS+6;
	public static final int SPS_WEP_FIGHT_GLOVES = SPS_NORMAL_WEAPONS+7;
	public static final int SPS_WEP_NUNCHAKUS = SPS_NORMAL_WEAPONS+8;
	public static final int SPS_WEP_SCIMITAR = SPS_NORMAL_WEAPONS+9;
	public static final int SPS_WEP_WHIP = SPS_NORMAL_WEAPONS+10;
	public static final int SPS_WEP_RAPIER = SPS_NORMAL_WEAPONS+11;
	public static final int SPS_WEP_ASSASSINS_BLADE = SPS_NORMAL_WEAPONS+12;
	public static final int SPS_WEP_BATTLE_AXE = SPS_NORMAL_WEAPONS+13;
	public static final int SPS_WEP_GLAIVE = SPS_NORMAL_WEAPONS+14;
	public static final int SPS_WEP_CLUB = SPS_NORMAL_WEAPONS+15;
	public static final int SPS_WEP_GSWORD = SPS_NORMAL_WEAPONS+16;
	public static final int SPS_WEP_HALBERD = SPS_NORMAL_WEAPONS+17;
	public static final int SPS_WEP_WAR_HAMMER = SPS_NORMAL_WEAPONS+18;
	public static final int SPS_WEP_LANCE = SPS_NORMAL_WEAPONS+19;
	public static final int MEAT_SOUP = SPS_NORMAL_WEAPONS+20;
	public static final int HOTDOG = SPS_NORMAL_WEAPONS+21;
	public static final int KEBAB = SPS_NORMAL_WEAPONS+22;
	public static final int NUT_COOKIE = SPS_NORMAL_WEAPONS+23;
	public static final int MIX_PIZZA = SPS_NORMAL_WEAPONS+24;
	public static final int RICE_GRUEL = SPS_NORMAL_WEAPONS+25;
	public static final int FRUIT_CANDY = SPS_NORMAL_WEAPONS+26;
	public static final int MOON_CAKE = SPS_NORMAL_WEAPONS+27;
	public static final int LEGACY_BLIND_FRUIT = SPS_NORMAL_WEAPONS+28;
	public static final int LEGACY_EMP_BOLA = SPS_NORMAL_WEAPONS+29;
	static {
		for (int i = SPS_NORMAL_WEAPONS; i < SPS_NORMAL_WEAPONS+30; i++) {
			assignItemRect(i, 16, 16);
		}
	}

	// SPS-PD's 18 distinct seed sprites. Freshberry shares Rotberry's sprite.
	private static final int SPS_LEGACY_SEEDS = xy(1, 55);
	public static final int SPS_SEED_ROTBERRY = SPS_LEGACY_SEEDS;
	public static final int SPS_SEED_FIREBLOOM = SPS_LEGACY_SEEDS+1;
	public static final int SPS_SEED_BLINDWEED = SPS_LEGACY_SEEDS+2;
	public static final int SPS_SEED_SUNGRASS = SPS_LEGACY_SEEDS+3;
	public static final int SPS_SEED_ICECAP = SPS_LEGACY_SEEDS+4;
	public static final int SPS_SEED_STORMVINE = SPS_LEGACY_SEEDS+5;
	public static final int SPS_SEED_SORROWMOSS = SPS_LEGACY_SEEDS+6;
	public static final int SPS_SEED_DREAMFOIL = SPS_LEGACY_SEEDS+7;
	public static final int SPS_SEED_EARTHROOT = SPS_LEGACY_SEEDS+8;
	public static final int SPS_SEED_FADELEAF = SPS_LEGACY_SEEDS+9;
	public static final int SPS_SEED_BLANDFRUIT = SPS_LEGACY_SEEDS+10;
	public static final int SPS_SEED_DUNGEONNUT = SPS_LEGACY_SEEDS+11;
	public static final int SPS_SEED_STARFLOWER = SPS_LEGACY_SEEDS+12;
	public static final int SPS_SEED_SEEDPOD = SPS_LEGACY_SEEDS+13;
	public static final int SPS_SEED_SIOFLOWER = SPS_LEGACY_SEEDS+14;
	public static final int SPS_SEED_STAREATER = SPS_LEGACY_SEEDS+15;
	public static final int SPS_SEED_DEWCATCHER = SPS_LEGACY_SEEDS+16;
	public static final int SPS_SEED_RENEPENTH = SPS_LEGACY_SEEDS+17;
	static {
		for (int i = SPS_LEGACY_SEEDS; i < SPS_LEGACY_SEEDS+18; i++) {
			assignItemRect(i, 16, 16);
		}
	}

	private static final int SPS_PORTAL_KEYS = xy(1, 61);
	public static final int TENGU_KEY = SPS_PORTAL_KEYS;
	public static final int MAGIC_HAND = SPS_PORTAL_KEYS+1;
	public static final int RICE_BALL = SPS_PORTAL_KEYS+2;
	public static final int SPS_LING_POTION = SPS_PORTAL_KEYS+3;
	public static final int ARTIFACT_ICE_EYE = SPS_PORTAL_KEYS+5;
	public static final int NOOMLIN_CROWN = SPS_PORTAL_KEYS+6;
	static {
		assignItemRect(TENGU_KEY, 16, 16);
		assignItemRect(MAGIC_HAND, 16, 16);
		assignItemRect(RICE_BALL, 16, 16);
		assignItemRect(SPS_LING_POTION, 16, 16);
		assignItemRect(ARTIFACT_ICE_EYE, 16, 16);
		assignItemRect(NOOMLIN_CROWN, 16, 16);
	}

	//SPS: 0.9.9 礼物商店奖励物品（原 20 列图集导入，ImportChinaMechSprites）
	public static final int SPS_CHINA_MECH =                             xy(8, 61);
	public static final int SPS_JUMPER_DANCER =                          xy(9, 61);
	static {
		assignItemRect(SPS_CHINA_MECH, 16, 16);
		assignItemRect(SPS_JUMPER_DANCER, 16, 16);
	}

	// Original SPS-PD medicine sprites from the legacy 20-column atlas.
	private static final int SPS_LEGACY_MEDICINES = xy(1, 62);
	public static final int MUSHROOM_LANTERN = SPS_LEGACY_MEDICINES;
	public static final int MUSHROOM_EARTHSTAR = SPS_LEGACY_MEDICINES + 1;
	public static final int MUSHROOM_GREEN_SPORE = SPS_LEGACY_MEDICINES + 2;
	public static final int MUSHROOM_DEATHCAP = SPS_LEGACY_MEDICINES + 3;
	public static final int MUSHROOM_PIXIEPARASOL = SPS_LEGACY_MEDICINES + 4;
	public static final int MUSHROOM_GOLDENJELLY = SPS_LEGACY_MEDICINES + 5;
	public static final int MUSHROOM_BLUEMILK = SPS_LEGACY_MEDICINES + 6;
	public static final int GREAT_PILL = SPS_LEGACY_MEDICINES + 7;
	public static final int WINE = SPS_LEGACY_MEDICINES + 8;
	static {
		for (int i = SPS_LEGACY_MEDICINES; i < SPS_LEGACY_MEDICINES + 9; i++) {
			assignItemRect(i, 16, 16);
		}
	}

	//for smaller 8x8 icons that often accompany an item sprite
	public static class Icons {

		private static final int WIDTH = 16;
		public static final int SIZE = 8;

		public static TextureFilm film = new TextureFilm( Assets.Sprites.ITEM_ICONS, SIZE, SIZE );

		private static int xy(int x, int y){
			x -= 1; y -= 1;
			return x + WIDTH*y;
		}

		private static void assignIconRect( int item, int width, int height ){
			int x = (item % WIDTH) * SIZE;
			int y = (item / WIDTH) * SIZE;
			film.add( item, x, y, x+width, y+height);
		}

		private static final int RINGS          =                            xy(1, 1);  //16 slots
		public static final int RING_ACCURACY   = RINGS+0;
		public static final int RING_ARCANA     = RINGS+1;
		public static final int RING_ELEMENTS   = RINGS+2;
		public static final int RING_ENERGY     = RINGS+3;
		public static final int RING_EVASION    = RINGS+4;
		public static final int RING_FORCE      = RINGS+5;
		public static final int RING_FUROR      = RINGS+6;
		public static final int RING_HASTE      = RINGS+7;
		public static final int RING_MIGHT      = RINGS+8;
		public static final int RING_SHARPSHOOT = RINGS+9;
		public static final int RING_TENACITY   = RINGS+10;
		public static final int RING_WEALTH     = RINGS+11;
		static {
			assignIconRect( RING_ACCURACY,      7, 7 );
			assignIconRect( RING_ARCANA,        7, 7 );
			assignIconRect( RING_ELEMENTS,      7, 7 );
			assignIconRect( RING_ENERGY,        7, 5 );
			assignIconRect( RING_EVASION,       7, 7 );
			assignIconRect( RING_FORCE,         5, 6 );
			assignIconRect( RING_FUROR,         7, 6 );
			assignIconRect( RING_HASTE,         6, 6 );
			assignIconRect( RING_MIGHT,         7, 7 );
			assignIconRect( RING_SHARPSHOOT,    7, 7 );
			assignIconRect( RING_TENACITY,      6, 6 );
			assignIconRect( RING_WEALTH,        7, 6 );
		}

		                                                                                //16 free slots

		private static final int SCROLLS        =                            xy(1, 3);  //16 slots
		public static final int SCROLL_UPGRADE  = SCROLLS+0;
		public static final int SCROLL_IDENTIFY = SCROLLS+1;
		public static final int SCROLL_REMCURSE = SCROLLS+2;
		public static final int SCROLL_MIRRORIMG= SCROLLS+3;
		public static final int SCROLL_RECHARGE = SCROLLS+4;
		public static final int SCROLL_TELEPORT = SCROLLS+5;
		public static final int SCROLL_LULLABY  = SCROLLS+6;
		public static final int SCROLL_MAGICMAP = SCROLLS+7;
		public static final int SCROLL_RAGE     = SCROLLS+8;
		public static final int SCROLL_RETRIB   = SCROLLS+9;
		public static final int SCROLL_TERROR   = SCROLLS+10;
		public static final int SCROLL_TRANSMUTE= SCROLLS+11;
		static {
			assignIconRect( SCROLL_UPGRADE,     7, 7 );
			assignIconRect( SCROLL_IDENTIFY,    4, 7 );
			assignIconRect( SCROLL_REMCURSE,    7, 7 );
			assignIconRect( SCROLL_MIRRORIMG,   7, 5 );
			assignIconRect( SCROLL_RECHARGE,    7, 5 );
			assignIconRect( SCROLL_TELEPORT,    7, 7 );
			assignIconRect( SCROLL_LULLABY,     7, 6 );
			assignIconRect( SCROLL_MAGICMAP,    7, 7 );
			assignIconRect( SCROLL_RAGE,        6, 6 );
			assignIconRect( SCROLL_RETRIB,      5, 6 );
			assignIconRect( SCROLL_TERROR,      5, 7 );
			assignIconRect( SCROLL_TRANSMUTE,   7, 7 );
		}

		private static final int EXOTIC_SCROLLS =                            xy(1, 4);  //16 slots
		public static final int SCROLL_ENCHANT  = EXOTIC_SCROLLS+0;
		public static final int SCROLL_DIVINATE = EXOTIC_SCROLLS+1;
		public static final int SCROLL_ANTIMAGIC= EXOTIC_SCROLLS+2;
		public static final int SCROLL_PRISIMG  = EXOTIC_SCROLLS+3;
		public static final int SCROLL_MYSTENRG = EXOTIC_SCROLLS+4;
		public static final int SCROLL_PASSAGE  = EXOTIC_SCROLLS+5;
		public static final int SCROLL_SIREN    = EXOTIC_SCROLLS+6;
		public static final int SCROLL_FORESIGHT= EXOTIC_SCROLLS+7;
		public static final int SCROLL_CHALLENGE= EXOTIC_SCROLLS+8;
		public static final int SCROLL_PSIBLAST = EXOTIC_SCROLLS+9;
		public static final int SCROLL_DREAD    = EXOTIC_SCROLLS+10;
		public static final int SCROLL_METAMORPH= EXOTIC_SCROLLS+11;
		static {
			assignIconRect( SCROLL_ENCHANT,     7, 7 );
			assignIconRect( SCROLL_DIVINATE,    7, 6 );
			assignIconRect( SCROLL_ANTIMAGIC,   7, 7 );
			assignIconRect( SCROLL_PRISIMG,     5, 7 );
			assignIconRect( SCROLL_MYSTENRG,    7, 5 );
			assignIconRect( SCROLL_PASSAGE,     5, 7 );
			assignIconRect( SCROLL_SIREN,       7, 6 );
			assignIconRect( SCROLL_FORESIGHT,   7, 5 );
			assignIconRect( SCROLL_CHALLENGE,   7, 7 );
			assignIconRect( SCROLL_PSIBLAST,    5, 6 );
			assignIconRect( SCROLL_DREAD,       5, 7 );
			assignIconRect( SCROLL_METAMORPH,   7, 7 );
		}

		                                                                                //16 free slots

		private static final int POTIONS        =                            xy(1, 6);  //16 slots
		public static final int POTION_STRENGTH = POTIONS+0;
		public static final int POTION_HEALING  = POTIONS+1;
		public static final int POTION_MINDVIS  = POTIONS+2;
		public static final int POTION_FROST    = POTIONS+3;
		public static final int POTION_LIQFLAME = POTIONS+4;
		public static final int POTION_TOXICGAS = POTIONS+5;
		public static final int POTION_HASTE    = POTIONS+6;
		public static final int POTION_INVIS    = POTIONS+7;
		public static final int POTION_LEVITATE = POTIONS+8;
		public static final int POTION_PARAGAS  = POTIONS+9;
		public static final int POTION_PURITY   = POTIONS+10;
		public static final int POTION_EXP      = POTIONS+11;
		static {
			assignIconRect( POTION_STRENGTH,    7, 7 );
			assignIconRect( POTION_HEALING,     6, 7 );
			assignIconRect( POTION_MINDVIS,     7, 5 );
			assignIconRect( POTION_FROST,       7, 7 );
			assignIconRect( POTION_LIQFLAME,    5, 7 );
			assignIconRect( POTION_TOXICGAS,    7, 7 );
			assignIconRect( POTION_HASTE,       6, 6 );
			assignIconRect( POTION_INVIS,       5, 7 );
			assignIconRect( POTION_LEVITATE,    6, 7 );
			assignIconRect( POTION_PARAGAS,     7, 7 );
			assignIconRect( POTION_PURITY,      5, 7 );
			assignIconRect( POTION_EXP,         7, 7 );
		}

		private static final int EXOTIC_POTIONS =                            xy(1, 7);  //16 slots
		public static final int POTION_MASTERY  = EXOTIC_POTIONS+0;
		public static final int POTION_SHIELDING= EXOTIC_POTIONS+1;
		public static final int POTION_MAGISIGHT= EXOTIC_POTIONS+2;
		public static final int POTION_SNAPFREEZ= EXOTIC_POTIONS+3;
		public static final int POTION_DRGBREATH= EXOTIC_POTIONS+4;
		public static final int POTION_CORROGAS = EXOTIC_POTIONS+5;
		public static final int POTION_STAMINA  = EXOTIC_POTIONS+6;
		public static final int POTION_SHROUDFOG= EXOTIC_POTIONS+7;
		public static final int POTION_STRMCLOUD= EXOTIC_POTIONS+8;
		public static final int POTION_EARTHARMR= EXOTIC_POTIONS+9;
		public static final int POTION_CLEANSE  = EXOTIC_POTIONS+10;
		public static final int POTION_DIVINE   = EXOTIC_POTIONS+11;
		static {
			assignIconRect( POTION_MASTERY,     7, 7 );
			assignIconRect( POTION_SHIELDING,   6, 6 );
			assignIconRect( POTION_MAGISIGHT,   7, 5 );
			assignIconRect( POTION_SNAPFREEZ,   7, 7 );
			assignIconRect( POTION_DRGBREATH,   7, 7 );
			assignIconRect( POTION_CORROGAS,    7, 7 );
			assignIconRect( POTION_STAMINA,     6, 6 );
			assignIconRect( POTION_SHROUDFOG,   7, 7 );
			assignIconRect( POTION_STRMCLOUD,   7, 7 );
			assignIconRect( POTION_EARTHARMR,   6, 6 );
			assignIconRect( POTION_CLEANSE,     7, 7 );
			assignIconRect( POTION_DIVINE,      7, 7 );
		}

		                                                                                //16 free slots

	}

}
