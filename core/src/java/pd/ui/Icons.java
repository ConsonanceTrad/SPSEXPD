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

package pd.ui;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.items.ConsumThrowsDict;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.HeroClass;
import pd.atlas.AtlasReader;
import pd.atlas.IconEntry;
import pd.atlas.interfaces.IconsDict;
import pd.levels.Level;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import render.noosa.Image;
import render.utils.geom.RectF;

public enum Icons {

	//slightly larger title screen icons, spacing for 17x16
	ENTER,
	GOLD,
	RANKINGS,
	BADGES,
	NEWS,
	CHANGES,
	PREFS,
	SHPX,
	JOURNAL,

	//grey icons, mainly used for buttons, spacing for 16x16
	EXIT,
	DISPLAY, //2 separate images, changes based on orientation
	DISPLAY_LAND,
	DISPLAY_PORT,
	DATA,
	AUDIO,
	LANGS,
	CONTROLLER,
	KEYBOARD,
	STATS,
	CHALLENGE_GREY,
	SCROLL_GREY,
	SEED,
	LEFTARROW,
	RIGHTARROW,
	CALENDAR,
	CHEVRON,
	SHUFFLE,

	//misc larger icons, mainly used for buttons, tabs, and journal, spacing for 16x16
	TARGET,
	INFO,
	WARNING,
	UNCHECKED,
	CHECKED,
	CLOSE,
	PLUS,
	REPEAT,
	ARROW,
	CHALLENGE_COLOR,
	SCROLL_COLOR,
	COPY,
	PASTE,

	BACKPACK_LRG,
	TALENT,
	MAGNIFY,
	SNAKE,
	BUFFS,
	CATALOG,
	ALCHEMY,
	GRASS,

	STAIRS,
	STAIRS_CHASM,
	STAIRS_WATER,
	STAIRS_GRASS,
	STAIRS_DARK,
	STAIRS_LARGE,
	STAIRS_TRAPS,
	STAIRS_SECRETS,
	WELL_HEALTH,
	WELL_AWARENESS,
	SACRIFICE_ALTAR,
	DISTANT_WELL,

	//smaller icons, variable spacing
	SKULL,
	BUSY,
	COMPASS,
	SLEEP,
	ALERT,
	LOST,
	INVESTIGATE,
	DEPTH,      //depth icons have three variants, for regular, seeded, daily, and daily replay runs
	DEPTH_CHASM,
	DEPTH_WATER,
	DEPTH_GRASS,
	DEPTH_DARK,
	DEPTH_LARGE,
	DEPTH_TRAPS,
	DEPTH_SECRETS,
	CHAL_COUNT,
	COIN_SML,
	ENERGY_SML,
	BACKPACK,
	SEED_POUCH,
	SCROLL_HOLDER,
	WAND_HOLSTER,
	POTION_BANDOLIER,
	//SPS: 0.9.8 独有的袋子图标（本基底图集缺失，从 SPS 图集按原像素补入）
	SHOP_CART,
	KEYRING,
	HOS,
	ARROW_C,
	//SPS: 恶魔刀锋（主菜单「继续游戏」图标，取自 SPS_DEMON_BLADE 物品图）
	DEMON_BLADE,
	//SPS: 幸运徽章（主菜单「加入交流群」图标，取自 LUCKY_BADGE 物品图）
	LUCKY_BADGE,

	//icons that appear in the about screen, variable spacing
	LIBGDX,
	ALEKS,
	WATA,
	PUMPKINVOLT,
	CELESTI,
	LUMINE,
	CUBE_CODE,
	PURIGRO,
	ARCNOR,
	ALASTAIR;

	//位置字典：图标 → interfaces/icons.png 中的位置（数据源自 tools/atlas-meta，由 gen-atlas-dict 生成）
	private static final Map<Icons, IconEntry> ENTRIES = new HashMap<>();
	//关于界面的大图需要额外缩放
	private static final Set<Icons> SCALED = EnumSet.noneOf(Icons.class);

	static {
		ENTRIES.put(ALASTAIR, IconsDict.ICON_081);
		ENTRIES.put(ALCHEMY, IconsDict.ICON_045);
		ENTRIES.put(ALEKS, IconsDict.ICON_075);
		ENTRIES.put(ALERT, IconsDict.ICON_061);
		ENTRIES.put(ARCNOR, IconsDict.ICON_078);
		ENTRIES.put(ARROW, IconsDict.ICON_033);
		ENTRIES.put(ARROW_C, IconsDict.ICON_087);
		ENTRIES.put(AUDIO, IconsDict.ICON_013);
		ENTRIES.put(BACKPACK, IconsDict.ICON_065);
		ENTRIES.put(BACKPACK_LRG, IconsDict.ICON_039);
		ENTRIES.put(BADGES, IconsDict.ICON_003);
		ENTRIES.put(BUFFS, IconsDict.ICON_043);
		ENTRIES.put(BUSY, IconsDict.ICON_060);
		ENTRIES.put(CALENDAR, IconsDict.ICON_023);
		ENTRIES.put(CATALOG, IconsDict.ICON_044);
		ENTRIES.put(CELESTI, IconsDict.ICON_076);
		ENTRIES.put(CHAL_COUNT, IconsDict.ICON_063);
		ENTRIES.put(CHALLENGE_COLOR, IconsDict.ICON_034);
		ENTRIES.put(CHALLENGE_GREY, IconsDict.ICON_018);
		ENTRIES.put(CHANGES, IconsDict.ICON_005);
		ENTRIES.put(CHECKED, IconsDict.ICON_029);
		ENTRIES.put(CHEVRON, IconsDict.ICON_024);
		ENTRIES.put(CLOSE, IconsDict.ICON_030);
		ENTRIES.put(COIN_SML, IconsDict.ICON_064);
		ENTRIES.put(COMPASS, IconsDict.ICON_070);
		ENTRIES.put(CONTROLLER, IconsDict.ICON_015);
		ENTRIES.put(COPY, IconsDict.ICON_036);
		ENTRIES.put(CUBE_CODE, IconsDict.ICON_080);
		ENTRIES.put(DATA, IconsDict.ICON_012);
		ENTRIES.put(DEMON_BLADE, IconsDict.ICON_088);
		ENTRIES.put(DISPLAY_LAND, IconsDict.ICON_011);
		ENTRIES.put(DISPLAY_PORT, IconsDict.ICON_010);
		ENTRIES.put(DISTANT_WELL, IconsDict.ICON_058);
		ENTRIES.put(ENERGY_SML, IconsDict.ICON_073);
		ENTRIES.put(ENTER, IconsDict.ICON_000);
		ENTRIES.put(EXIT, IconsDict.ICON_009);
		ENTRIES.put(GOLD, IconsDict.ICON_001);
		ENTRIES.put(GRASS, IconsDict.ICON_046);
		ENTRIES.put(HOS, IconsDict.ICON_086);
		ENTRIES.put(INFO, IconsDict.ICON_026);
		ENTRIES.put(INVESTIGATE, IconsDict.ICON_072);
		ENTRIES.put(JOURNAL, IconsDict.ICON_008);
		ENTRIES.put(KEYBOARD, IconsDict.ICON_016);
		ENTRIES.put(KEYRING, IconsDict.ICON_085);
		ENTRIES.put(LANGS, IconsDict.ICON_014);
		ENTRIES.put(LEFTARROW, IconsDict.ICON_021);
		ENTRIES.put(LIBGDX, IconsDict.ICON_074);
		ENTRIES.put(LOST, IconsDict.ICON_062);
		ENTRIES.put(LUCKY_BADGE, IconsDict.ICON_089);
		ENTRIES.put(LUMINE, IconsDict.ICON_077);
		ENTRIES.put(MAGNIFY, IconsDict.ICON_041);
		ENTRIES.put(NEWS, IconsDict.ICON_004);
		ENTRIES.put(PASTE, IconsDict.ICON_037);
		ENTRIES.put(PLUS, IconsDict.ICON_031);
		ENTRIES.put(POTION_BANDOLIER, IconsDict.ICON_069);
		ENTRIES.put(PREFS, IconsDict.ICON_006);
		ENTRIES.put(PUMPKINVOLT, IconsDict.ICON_083);
		ENTRIES.put(PURIGRO, IconsDict.ICON_079);
		ENTRIES.put(RANKINGS, IconsDict.ICON_002);
		ENTRIES.put(REPEAT, IconsDict.ICON_032);
		ENTRIES.put(RIGHTARROW, IconsDict.ICON_022);
		ENTRIES.put(SACRIFICE_ALTAR, IconsDict.ICON_057);
		ENTRIES.put(SCROLL_COLOR, IconsDict.ICON_035);
		ENTRIES.put(SCROLL_GREY, IconsDict.ICON_019);
		ENTRIES.put(SCROLL_HOLDER, IconsDict.ICON_066);
		ENTRIES.put(SEED, IconsDict.ICON_020);
		ENTRIES.put(SEED_POUCH, IconsDict.ICON_067);
		ENTRIES.put(SHOP_CART, IconsDict.ICON_084);
		ENTRIES.put(SHPX, IconsDict.ICON_007);
		ENTRIES.put(SHUFFLE, IconsDict.ICON_038);
		ENTRIES.put(SKULL, IconsDict.ICON_059);
		ENTRIES.put(SLEEP, IconsDict.ICON_071);
		ENTRIES.put(SNAKE, IconsDict.ICON_042);
		ENTRIES.put(STAIRS, IconsDict.ICON_047);
		ENTRIES.put(STAIRS_CHASM, IconsDict.ICON_048);
		ENTRIES.put(STAIRS_DARK, IconsDict.ICON_051);
		ENTRIES.put(STAIRS_GRASS, IconsDict.ICON_050);
		ENTRIES.put(STAIRS_LARGE, IconsDict.ICON_052);
		ENTRIES.put(STAIRS_SECRETS, IconsDict.ICON_054);
		ENTRIES.put(STAIRS_TRAPS, IconsDict.ICON_053);
		ENTRIES.put(STAIRS_WATER, IconsDict.ICON_049);
		ENTRIES.put(STATS, IconsDict.ICON_017);
		ENTRIES.put(TALENT, IconsDict.ICON_040);
		ENTRIES.put(TARGET, IconsDict.ICON_025);
		ENTRIES.put(UNCHECKED, IconsDict.ICON_028);
		ENTRIES.put(WAND_HOLSTER, IconsDict.ICON_068);
		ENTRIES.put(WARNING, IconsDict.ICON_027);
		ENTRIES.put(WATA, IconsDict.ICON_082);
		ENTRIES.put(WELL_AWARENESS, IconsDict.ICON_056);
		ENTRIES.put(WELL_HEALTH, IconsDict.ICON_055);
		SCALED.add(CELESTI);
		SCALED.add(LUMINE);
		SCALED.add(ARCNOR);
		SCALED.add(PURIGRO);
		SCALED.add(CUBE_CODE);
		SCALED.add(ALASTAIR);
	}

	public Image get() {
		return get( this );
	}
	
	public static Image get( Icons type ) {
		//图标位置来自字典（pd/atlas），取图统一经 AtlasReader
		IconEntry entry = ENTRIES.get(type);
		if (entry != null) {
			Image icon = AtlasReader.image(entry);
			if (SCALED.contains(type)) {
				icon.scale.set(PixelScene.align(0.49f));
			}
			return icon;
		}
		//无固定位置的图标：委托给其它图标，或按运行状态选择
		Image icon = new Image( Assets.Interfaces.ICONS );
		switch (type) {


			case DISPLAY:
				if (!PixelScene.landscape()){
					return get(DISPLAY_PORT);
				} else {
					return get(DISPLAY_LAND);
				}




			case DEPTH:
				icon.frame( icon.texture.uvRectBySize( 32 + runTypeOfsX(), 80 + runTypeOfsY(), 6, 7 ) );
				break;
			case DEPTH_CHASM:
				icon.frame( icon.texture.uvRectBySize( 40 + runTypeOfsX(), 80 + runTypeOfsY(), 7, 7 ) );
				break;
			case DEPTH_WATER:
				icon.frame( icon.texture.uvRectBySize( 48 + runTypeOfsX(), 80 + runTypeOfsY(), 7, 7 ) );
				break;
			case DEPTH_GRASS:
				icon.frame( icon.texture.uvRectBySize( 56 + runTypeOfsX(), 80 + runTypeOfsY(), 7, 7 ) );
				break;
			case DEPTH_DARK:
				icon.frame( icon.texture.uvRectBySize( 64 + runTypeOfsX(), 80 + runTypeOfsY(), 7, 7 ) );
				break;
			case DEPTH_LARGE:
				icon.frame( icon.texture.uvRectBySize( 72 + runTypeOfsX(), 80 + runTypeOfsY(), 7, 7 ) );
				break;
			case DEPTH_TRAPS:
				icon.frame( icon.texture.uvRectBySize( 80 + runTypeOfsX(), 80 + runTypeOfsY(), 7, 7 ) );
				break;
			case DEPTH_SECRETS:
				icon.frame( icon.texture.uvRectBySize( 88 + runTypeOfsX(), 80 + runTypeOfsY(), 7, 7 ) );
				break;

			//SPS: 0.9.8 袋子图标（从 SPS 图集原像素补入本基底 icons.png 的空白区）


			//large icons are scaled down to match game's size

		}
		return icon;
	}

	private static int runTypeOfsX(){
		return Dungeon.daily ? 64 : 0;
	}

	private static int runTypeOfsY(){
		if ((Dungeon.daily && Dungeon.dailyReplay)
				|| (!Dungeon.daily && !Dungeon.customSeedText.isEmpty())){
			return 8;
		} else {
			return 0;
		}
	}
	
	public static Image get( HeroClass cl ) {
		switch (cl) {
			case WARRIOR:
				return new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0);
			case MAGE:
				//mage's staff normally has 2 pixels extra at the top for particle effects, we chop that off here
				Image result = new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0);
				RectF frame = result.frame();
				frame.top += frame.height()/8f;
				result.frame(frame);
				return result;
			case ROGUE:
				return new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0);
			case HUNTRESS:
				return new ItemSprite(EquipmentEquipWeaponUniqueWeaponDict.SPIRIT_BOW_0);
			case DUELIST:
				return new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.RAPIER_0);
			case CLERIC:
				return new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0);
			case SPELLSWORD:
				return new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0);
			case PERFORMER:
				return new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0);
			case SOLDIER:
				return new ItemSprite(ConsumThrowsDict.THROWING_SPIKE_0);
			case FOLLOWER:
				return new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0);
			case ASCETIC:
				return new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.GLOVES);
			default:
				return null;
		}
	}

	public static Image get(Level.Feeling feeling){
		switch (feeling){
			case NONE: default:
				return get(DEPTH);
			case CHASM:
				return get(DEPTH_CHASM);
			case WATER:
				return get(DEPTH_WATER);
			case GRASS:
				return get(DEPTH_GRASS);
			case DARK:
				return get(DEPTH_DARK);
			case LARGE:
				return get(DEPTH_LARGE);
			case TRAPS:
				return get(DEPTH_TRAPS);
			case SECRETS:
				return get(DEPTH_SECRETS);
		}
	}

	public static Image getLarge(Level.Feeling feeling){
		switch (feeling){
			case NONE: default:
				return get(STAIRS);
			case CHASM:
				return get(STAIRS_CHASM);
			case WATER:
				return get(STAIRS_WATER);
			case GRASS:
				return get(STAIRS_GRASS);
			case DARK:
				return get(STAIRS_DARK);
			case LARGE:
				return get(STAIRS_LARGE);
			case TRAPS:
				return get(STAIRS_TRAPS);
			case SECRETS:
				return get(STAIRS_SECRETS);
		}
	}
}
