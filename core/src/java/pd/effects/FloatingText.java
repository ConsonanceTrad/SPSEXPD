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

package pd.effects;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Bless;
import pd.actors.buffs.ChampionEnemy;
import pd.actors.buffs.Daze;
import pd.actors.buffs.Hex;
import pd.actors.buffs.Momentum;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.actors.hero.spells.GuidingLight;
import pd.actors.mobs.ArmoredStatue;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Statue;
import pd.actors.mobs.npcs.MirrorImage;
import pd.actors.mobs.npcs.PrismaticImage;
import pd.items.KindOfWeapon;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.glyphs.Stone;
import pd.items.equipment.artifacts.DriedRose;
import pd.items.equipment.rings.RingOfAccuracy;
import pd.items.equipment.rings.RingOfEvasion;
import pd.items.equipment.trinkets.FerretTuft;
import pd.items.equipment.weapon.melee.Quarterstaff;
import pd.items.equipment.weapon.melee.Scimitar;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.tiles.DungeonTilemap;
import pd.ui.RenderedTextBlock;
import render.noosa.Camera;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.RenderedText;
import render.noosa.TextureFilm;
import render.utils.data.Callback;
import render.utils.data.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FloatingText extends RenderedTextBlock {

	private static final float LIFESPAN	= 1f;
	private static final float DISTANCE	= DungeonTilemap.SIZE;

	public static final int ICON_WIDTH = 7;
	public static final int ICON_HEIGHT = 8;
	public static TextureFilm iconFilm = new TextureFilm( Assets.Effects.TEXT_ICONS, ICON_WIDTH, ICON_HEIGHT );

	//extra/reason 帧表（与 tools/atlas-meta/effects/**/_atlas.json 顺序一致，勿手改，
	//由 gen-atlas-dict.ps1 生成字典；此表为运行时按帧序引用的固定排布）
	private static final pd.atlas.IconEntry[] EXTRA_TABLE = {
			pd.atlas.effects.TextIconsExtraDict.PIERCE,
			pd.atlas.effects.TextIconsExtraDict.BLUNT,
			pd.atlas.effects.TextIconsExtraDict.SLASH,
			pd.atlas.effects.TextIconsExtraDict.IMPACT,
			pd.atlas.effects.TextIconsExtraDict.CRUSH,
			pd.atlas.effects.TextIconsExtraDict.CURSE,
			pd.atlas.effects.TextIconsExtraDict.PURE,
			pd.atlas.effects.TextIconsExtraDict.ENERGY_DMG,
			pd.atlas.effects.TextIconsExtraDict.FIRE,
			pd.atlas.effects.TextIconsExtraDict.ICE,
			pd.atlas.effects.TextIconsExtraDict.SHOCK,
			pd.atlas.effects.TextIconsExtraDict.LIGHT,
			pd.atlas.effects.TextIconsExtraDict.POISON,
			pd.atlas.effects.TextIconsExtraDict.ACID,
			pd.atlas.effects.TextIconsExtraDict.HEAL_DAMAGE,
			pd.atlas.effects.TextIconsExtraDict.INTERFERE,
			pd.atlas.effects.TextIconsExtraDict.HUNGER_DAMAGE,
			pd.atlas.effects.TextIconsExtraDict.DISEASE,
			pd.atlas.effects.TextIconsExtraDict.BACKLASH,
			pd.atlas.effects.TextIconsExtraDict.HEALING,
			pd.atlas.effects.TextIconsExtraDict.SHIELDING,
			pd.atlas.effects.TextIconsExtraDict.EXPERIENCE,
			pd.atlas.effects.TextIconsExtraDict.STRENGTH,
			pd.atlas.effects.TextIconsExtraDict.GOLD,
			pd.atlas.effects.TextIconsExtraDict.ALCHEMY_ENERGY,
			pd.atlas.effects.TextIconsExtraDict.PERFECT_DODGE,
			pd.atlas.effects.TextIconsExtraDict.BLOCK
	};
	private static final pd.atlas.IconEntry[] REASON_TABLE = {
			pd.atlas.effects.TextIconsReasonDict.REASON_ARM,
			pd.atlas.effects.TextIconsReasonDict.REASON_BLS,
			pd.atlas.effects.TextIconsReasonDict.REASON_SUPR,
			pd.atlas.effects.TextIconsReasonDict.REASON_DAZE,
			pd.atlas.effects.TextIconsReasonDict.REASON_HIT_ACC,
			pd.atlas.effects.TextIconsReasonDict.REASON_MISS_ACC,
			pd.atlas.effects.TextIconsReasonDict.REASON_HEX,
			pd.atlas.effects.TextIconsReasonDict.REASON_DANCE,
			pd.atlas.effects.TextIconsReasonDict.REASON_EVA,
			pd.atlas.effects.TextIconsReasonDict.REASON_HIT_WEP,
			pd.atlas.effects.TextIconsReasonDict.REASON_MISS_WEP,
			pd.atlas.effects.TextIconsReasonDict.REASON_DEF,
			pd.atlas.effects.TextIconsReasonDict.REASON_TUFT,
			pd.atlas.effects.TextIconsReasonDict.REASON_RUN
	};

	//SPSEXPD: 新图标体系（伤害类型体系定稿）——
	//帧引用编码为 (set << ICON_SET_SHIFT) | index；set0=text_icons_extra、set1=text_icons_reason。
	//旧 text_icons.png 的伤害段帧全部退役（金/能量/正面/原因帧全部迁入新图集）。
	public static final int ICON_SET_EXTRA  = 0;
	public static final int ICON_SET_REASON = 1;
	public static final int ICON_SET_SHIFT  = 8;

	public static int NO_ICON = -1;

	//extra 帧序（与 tools/atlas-meta/effects/text_icons_extra/_atlas.json 一致）
	private static final int IDX_PIERCE = 0, IDX_BLUNT = 1, IDX_SLASH = 2, IDX_IMPACT = 3, IDX_CRUSH = 4;
	private static final int IDX_CURSE = 5, IDX_PURE = 6, IDX_ENERGY_DMG = 7;
	private static final int IDX_FIRE = 8, IDX_ICE = 9, IDX_SHOCK = 10, IDX_LIGHT = 11, IDX_POISON = 12, IDX_ACID = 13;
	private static final int IDX_HEAL_DMG = 14, IDX_INTERFERE = 15, IDX_HUNGER = 16, IDX_DISEASE = 17, IDX_BACKLASH = 18;
	private static final int IDX_HEALING = 19, IDX_SHIELDING = 20, IDX_EXPERIENCE = 21, IDX_STRENGTH = 22;
	private static final int IDX_GOLD = 23, IDX_ALCHEMY_ENERGY = 24, IDX_PERFECT_DODGE = 25, IDX_BLOCK = 26;

	//reason 帧序（与 tools/atlas-meta/effects/text_icons_reason/_atlas.json 一致）
	private static final int R_ARM = 0, R_BLS = 1, R_SUPR = 2, R_DAZE = 3, R_HIT_ACC = 4, R_MISS_ACC = 5;
	private static final int R_HEX = 6, R_DANCE = 7, R_EVA = 8, R_HIT_WEP = 9, R_MISS_WEP = 10;
	private static final int R_DEF = 11, R_TUFT = 12, R_RUN = 13;

	private static int extra( int idx ) { return (ICON_SET_EXTRA << ICON_SET_SHIFT) | idx; }
	private static int reason( int idx ) { return (ICON_SET_REASON << ICON_SET_SHIFT) | idx; }

	//combat damage icons（伤害类型仲裁链 F3 使用；名字保持旧引用兼容）
	//旧「普通物理/免甲物理」两档替换链在 F3 改为类型仲裁 + reason 拼接
	public static int PHYS_DMG         = extra( IDX_BLUNT );   //过渡 fallback：未细分物理按钝击帧
	public static int PHYS_DMG_NO_BLOCK = extra( IDX_PURE );   //纯粹/免甲档
	public static int MAGIC_DMG        = extra( IDX_ENERGY_DMG );
	public static int PICK_DMG         = extra( IDX_PIERCE );

	//伤害类型帧（新体系）
	public static int PUNCTURE_DMG   = extra( IDX_PIERCE );
	public static int BLUNT_DMG      = extra( IDX_BLUNT );
	public static int SLASH_DMG      = extra( IDX_SLASH );
	public static int IMPACT_DMG     = extra( IDX_IMPACT );
	public static int CRUSH_DMG      = extra( IDX_CRUSH );
	public static int CURSE_DMG      = extra( IDX_CURSE );
	public static int PURE_DMG       = extra( IDX_PURE );
	public static int ENERGY_DMG_I   = extra( IDX_ENERGY_DMG );
	public static int FIRE_DMG_I     = extra( IDX_FIRE );
	public static int ICE_DMG_I      = extra( IDX_ICE );
	public static int SHOCK_DMG_I    = extra( IDX_SHOCK );
	public static int LIGHT_DMG_I    = extra( IDX_LIGHT );
	public static int POISON_DMG_I   = extra( IDX_POISON );
	public static int ACID_DMG_I     = extra( IDX_ACID );
	public static int HEAL_DMG_I     = extra( IDX_HEAL_DMG );
	public static int INTERFERE_DMG  = extra( IDX_INTERFERE );
	public static int HUNGER_DMG_I   = extra( IDX_HUNGER );
	public static int DISEASE_DMG    = extra( IDX_DISEASE );
	public static int BACKLASH_DMG   = extra( IDX_BACKLASH );

	//debuff/dot damage icons（归并入类型令牌帧）
	public static int HUNGER    = extra( IDX_HUNGER );
	public static int BURNING   = extra( IDX_FIRE );
	public static int SHOCKING  = extra( IDX_SHOCK );
	public static int FROST     = extra( IDX_ICE );
	public static int WATER     = extra( IDX_IMPACT ); //间歇泉/风暴云 → 冲击
	public static int BLEEDING  = extra( IDX_DISEASE );
	public static int TOXIC     = extra( IDX_POISON );
	public static int CORROSION = extra( IDX_ACID );
	public static int POISON    = extra( IDX_POISON );
	public static int OOZE      = extra( IDX_POISON );
	public static int DEFERRED  = extra( IDX_PIERCE ); //F3 改为保留原伤害类型
	public static int CORRUPTION= extra( IDX_CURSE );
	public static int AMULET    = extra( IDX_BACKLASH );

	//positive icons
	public static int HEALING   = extra( IDX_HEALING );
	public static int SHIELDING = extra( IDX_SHIELDING );
	public static int EXPERIENCE= extra( IDX_EXPERIENCE );
	public static int STRENGTH  = extra( IDX_STRENGTH );

	//currency icons
	public static int GOLD      = extra( IDX_GOLD );
	public static int ENERGY    = extra( IDX_ALCHEMY_ENERGY );
	//SPSEXPD: 完全闪避/格挡（新帧位行5末尾）
	public static int PERFECT_DODGE = extra( IDX_PERFECT_DODGE );
	public static int BLOCK = extra( IDX_BLOCK );

	//hit reason icons（迁入 text_icons_reason；HIT/MISS 共用帧按合并语义）
	public static int HIT_WEP   = reason( R_HIT_WEP );
	public static int HIT_ARM   = reason( R_ARM );
	public static int HIT_BLS   = reason( R_BLS );
	public static int HIT_HEX   = reason( R_HEX );
	public static int HIT_DAZE  = reason( R_DAZE );
	public static int HIT_ACC   = reason( R_HIT_ACC );
	public static int HIT_EVA   = reason( R_EVA );
	public static int HIT_LIQ   = reason( R_EVA );       //未绘帧借 EVA
	public static int HIT_DANCE = reason( R_DANCE );
	public static int HIT_SUPR  = reason( R_SUPR );
	public static int HIT_PRES  = reason( R_DAZE );      //未绘帧借 DAZE
	public static int HIT_MOMEN = reason( R_BLS );       //未绘帧借 BLS

	//miss reason icons
	public static int MISS_WEP  = reason( R_MISS_WEP );
	public static int MISS_ARM  = reason( R_ARM );
	public static int MISS_BLS  = reason( R_BLS );
	public static int MISS_HEX  = reason( R_HEX );
	public static int MISS_DAZE = reason( R_DAZE );
	public static int MISS_ACC  = reason( R_MISS_ACC );
	public static int MISS_EVA  = reason( R_EVA );
	public static int MISS_LIQ  = reason( R_EVA );       //未绘帧借 EVA
	public static int MISS_DEF  = reason( R_DEF );
	public static int MISS_TUFT = reason( R_TUFT );
	public static int MISS_RUN  = reason( R_RUN );

	private Image icon;
	//SPSEXPD: 命中/闪避原因角标（拼接显示，跟主图标右侧）
	private Image reasonIcon;
	private boolean iconLeft;

	private float timeLeft;
	
	private int key = -1;

	private static final SparseArray<ArrayList<FloatingText>> stacks = new SparseArray<>();
	
	public FloatingText() {
		super(9*PixelScene.defaultZoom);
		setHightlighting(false);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (timeLeft >= 0) {
			if ((timeLeft -= Game.elapsed) <= 0) {
				kill();
			} else {
				float p = timeLeft / LIFESPAN;
				alpha( p > 0.5f ? 1 : p * 2 );
				
				float yMove = (DISTANCE / LIFESPAN) * Game.elapsed;
				y -= yMove;
				for (RenderedText t : words){
					t.y -= yMove;
				}

				if (icon != null){
					icon.alpha(p > 0.5f ? 1 : p * 2);
					icon.y -= yMove;
				}
				//SPSEXPD: 原因角标随主图标同步上浮/淡出
				if (reasonIcon != null){
					reasonIcon.alpha(p > 0.5f ? 1 : p * 2);
					reasonIcon.y -= yMove;
					reasonIcon.x = icon != null ? icon.x + icon.width() : reasonIcon.x;
				}
			}
		}
	}

	@Override
	protected synchronized void layout() {
		super.layout();
		if (icon != null){
			if (iconLeft){
				icon.x = left();
			} else {
				icon.x = left() + width() - icon.width() - (reasonIcon != null ? reasonIcon.width() : 0);
			}
			icon.x = PixelScene.align(Camera.main, icon.x);
			icon.y = PixelScene.align(Camera.main, top());
		}
		//SPSEXPD: 原因角标排在主图标右侧
		if (reasonIcon != null){
			reasonIcon.x = PixelScene.align(Camera.main, (icon != null ? icon.x + icon.width() : left()));
			reasonIcon.y = PixelScene.align(Camera.main, top());
		}
	}

	@Override
	public float width() {
		float width = super.width();
		if (icon != null){
			width += icon.width()-0.5f;
		}
		//SPSEXPD: 拼接原因角标占位
		if (reasonIcon != null){
			width += reasonIcon.width()-0.25f;
		}
		return width;
	}

	@Override
	public void kill() {
		if (key != -1) {
			synchronized (stacks) {
				stacks.get(key).remove(this);
			}
			key = -1;
		}
		super.kill();
	}
	
	@Override
	public void destroy() {
		kill();
		super.destroy();
	}
	
	public void reset( float x, float y, String text, int color, int iconIdx, boolean left ) {
		reset(x, y, text, color, iconIdx, NO_ICON, left);
	}

	public void reset( float x, float y, String text, int color, int iconIdx, int reasonIdx, boolean left ) {
		
		revive();
		
		zoom( 1 / (float)PixelScene.defaultZoom );

		text( text );
		hardlight( color );

		if (iconIdx != NO_ICON){
			//SPSEXPD: 新图标体系 —— 按 (set<<8)|index 从两新图集取帧；12x12 帧显示缩放到 7x8
			int set = (iconIdx >> ICON_SET_SHIFT) & 0x7F;
			int idx = iconIdx & 0xFF;
			pd.atlas.IconEntry entry = null;
			if (set == ICON_SET_EXTRA && idx < EXTRA_TABLE.length)      entry = EXTRA_TABLE[idx];
			else if (set == ICON_SET_REASON && idx < REASON_TABLE.length) entry = REASON_TABLE[idx];
			if (entry != null){
				icon = pd.atlas.AtlasReader.image(entry);
				float rawW = icon.width(), rawH = icon.height();
				if (rawW > ICON_WIDTH || rawH > ICON_HEIGHT){
					//SPSEXPD: 12x12 帧等比缩放到 7x7（用户定稿；7x8 帧原尺寸直用）
					icon.scale.set(7f / rawW, 7f / rawH);
				}
				add(icon);
				iconLeft = left;
				if (iconLeft){
					align(RIGHT_ALIGN);
				}
			} else {
				icon = null;
			}
		} else {
			icon = null;
		}

		//SPSEXPD: 命中/闪避原因角标拼接（伤害类型体系定稿）
		if (reasonIdx != NO_ICON){
			int rSet = (reasonIdx >> ICON_SET_SHIFT) & 0x7F;
			int rIdx = reasonIdx & 0xFF;
			pd.atlas.IconEntry rEntry = null;
			if (rSet == ICON_SET_EXTRA && rIdx < EXTRA_TABLE.length)      rEntry = EXTRA_TABLE[rIdx];
			else if (rSet == ICON_SET_REASON && rIdx < REASON_TABLE.length) rEntry = REASON_TABLE[rIdx];
			if (rEntry != null){
				reasonIcon = pd.atlas.AtlasReader.image(rEntry);
				float rawW = reasonIcon.width(), rawH = reasonIcon.height();
				if (rawW > ICON_WIDTH || rawH > ICON_HEIGHT){
					//SPSEXPD: 12x12 帧等比缩放到 7x7（用户定稿）
					reasonIcon.scale.set(7f / rawW, 7f / rawH);
				}
				add(reasonIcon);
			} else {
				reasonIcon = null;
			}
		} else {
			reasonIcon = null;
		}

		setPos(
			PixelScene.align( Camera.main, x - width() / 2),
			PixelScene.align( Camera.main, y - height())
		);
		
		timeLeft = LIFESPAN;
	}
	
	/* STATIC METHODS */

	/**
	 * SPSEXPD: 按伤害来源解析飘字图标——非物理伤害使用对应伤害类型的图标。
	 * 有贴图的类型（火 / 冰 / 雷 / 能量）用贴图；没有对应贴图的（地 / 光 / 暗 / 风 / 混乱）
	 * 用纯色小方块（{@link #SOLID_ICON_BASE}）。非元素/魔法来源返回 fallback，保持调用方原图标。
	 */
	public static int damageIconFor(Object src, int fallback) {
		pd.actors.damagetype.Element element = pd.actors.damagetype.Element.canonicalOf(src);
		if (element == null) return fallback;
		switch (element) {
			case FIRE:   return FIRE_DMG_I;
			case ICE:    return ICE_DMG_I;
			case SHOCK:  return SHOCK_DMG_I;
			case ENERGY: return ENERGY_DMG_I;
			case LIGHT:  return LIGHT_DMG_I;
			case POISON: return POISON_DMG_I;
			case ACID:   return ACID_DMG_I;
			//旧 EARTH/WIND 已归并到物理细分（钝/冲）——由物理族判定段另行出帧
			default:     return fallback;
		}
	}

	/**
	 * SPSEXPD: 类型令牌 → 飘字帧（伤害类型仲裁链核心，F3）。
	 * 物理五细分/能量/诅咒/特殊族/元素按令牌直出；旧地/风与暗/混乱按归并映射。
	 */
	public static int iconForToken(pd.actors.damagetype.DamageType token, int fallback) {
		if (token == null) return fallback;
		String n = token.getClass().getSimpleName();
		switch (n) {
			case "Puncture":  return PUNCTURE_DMG;
			case "Blunt":     return BLUNT_DMG;
			case "Slash":     return SLASH_DMG;
			case "Impact":    return IMPACT_DMG;
			case "Crush":     return CRUSH_DMG;
			case "Curse":     return CURSE_DMG;
			case "Energy":    return ENERGY_DMG_I;
			case "Dark": case "Chaos": return ENERGY_DMG_I; //归并：暗/混乱→能量
			case "Fire":      return FIRE_DMG_I;
			case "Ice":       return ICE_DMG_I;
			case "Shock":     return SHOCK_DMG_I;
			case "Light":     return LIGHT_DMG_I;
			case "Poison":    return POISON_DMG_I;
			case "Acid":      return ACID_DMG_I;
			case "Heal":      return HEAL_DMG_I;
			case "Interfere": return INTERFERE_DMG;
			case "Hunger":    return HUNGER_DMG_I;
			case "Disease":   return DISEASE_DMG;
			case "Backlash":  return BACKLASH_DMG;
			case "Earth":     return BLUNT_DMG;              //归并：旧地→钝
			case "Wind":      return IMPACT_DMG;             //归并：旧风→冲
			default:          return fallback;
		}
	}

	public static void show( float x, float y, String text, int color) {
		show(x, y, -1, text, color, -1, false);
	}
	
	public static void show( float x, float y, int key, String text, int color) {
		show(x, y, key, text, color, -1, false);
	}
	
	public static void show( float x, float y, int key, String text, int color, int iconIdx, boolean left ) {
		show(x, y, key, text, color, iconIdx, NO_ICON, left);
	}

	public static void show( float x, float y, int key, String text, int color, int iconIdx, int reasonIdx, boolean left ) {
		final int fReasonIdx = reasonIdx;
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				FloatingText txt = GameScene.status();
				if (txt != null){
					txt.reset(x, y, text, color, iconIdx, fReasonIdx, left);
					if (key != -1) push(txt, key);
				}
			}
		});
	}
	
	private static void push( FloatingText txt, int key ) {
		
		synchronized (stacks) {
			txt.key = key;
			
			ArrayList<FloatingText> stack = stacks.get(key);
			if (stack == null) {
				stack = new ArrayList<>();
				stacks.put(key, stack);
			}
			
			if (stack.size() > 0) {
				FloatingText below = txt;
				int aboveIndex = stack.size() - 1;
				int numBelow = 0;
				while (aboveIndex >= 0) {
					numBelow++;
					FloatingText above = stack.get(aboveIndex);
					if (above.bottom() + 4 > below.top()) {
						above.setPos(above.left(), below.top() - above.height() - 4);

						//reduce remaining time on texts being nudged up, to prevent spam
						above.timeLeft = Math.min(above.timeLeft, LIFESPAN-(numBelow/5f));
						above.timeLeft = Math.max(above.timeLeft, 0);
						
						below = above;
						aboveIndex--;
					} else {
						break;
					}
				}
			}
			
			stack.add(txt);
		}
	}

	// *** These are for custom icons that show impact of effects on top of base acc/eve ***
	// This logic is honestly a mess. Rather than tracking these modifiers as we
	// perform acc/eva calculation, we check and 'unwind' the modifiers from the final rolls.
	// This results in a lot of duplicated logic/numbers and awkward workarounds.
	// It does work though... mostly.

	public static int getHitReasonIcon(Char attacker, float accRoll, Char defender, float defRoll){
		HashMap<Integer, Float> hitReasons = new HashMap<>();

		//go through some garunteed hit interactions first
		if (defRoll == 0 && defender.buff(GuidingLight.Illuminated.class) != null){
			return HIT_BLS;
		}
		if (accRoll == Char.INFINITE_ACCURACY && attacker.invisible > 0){
			return HIT_SUPR;
		}
		if (defRoll == 0 && defender instanceof Mob && ((Mob) defender).surprisedBy(attacker)){
			return HIT_SUPR;
		}
		if (accRoll == Char.INFINITE_ACCURACY
				&& attacker.buff(Talent.PreciseAssaultTracker.class) != null){
			return HIT_PRES;
		}
		if (accRoll == Char.INFINITE_ACCURACY
				&& attacker.buff(Talent.LiquidAgilACCTracker.class) != null){
			return HIT_LIQ;
		}

		KindOfWeapon wep = null;
		if (attacker instanceof Hero) wep = ((Hero) attacker).belongings.attackingWeapon();
		if (attacker instanceof MirrorImage) wep = Dungeon.hero.belongings.weapon();
		if (attacker instanceof Statue) wep = ((Statue)attacker).weapon();
		if (attacker instanceof DriedRose.GhostHero) wep = ((DriedRose.GhostHero)attacker).weapon();

		Armor arm = null;
		if (defender instanceof Hero) arm = ((Hero) defender).belongings.armor();
		if (defender instanceof PrismaticImage) arm = Dungeon.hero.belongings.armor();
		if (defender instanceof ArmoredStatue) arm = ((ArmoredStatue)defender).armor();
		if (defender instanceof DriedRose.GhostHero) arm = ((DriedRose.GhostHero)defender).armor();

		//one last check
		if (defRoll == 0 && arm != null && arm.hasGlyph(Stone.class, defender)){
			return HIT_ARM;
		}

		//accuracy boosts (always > 1)
		if (wep != null && wep.accuracyFactor(attacker, defender) > 1){
			hitReasons.put( HIT_WEP, wep.accuracyFactor(attacker, defender));
		}
		float blessBoost = 1; //a few different sources contribute to this icon
		if (attacker.buff(ChampionEnemy.class) != null
			&& attacker.buff(ChampionEnemy.class).evasionAndAccuracyFactor() > 1){
			blessBoost *= attacker.buff(ChampionEnemy.class).evasionAndAccuracyFactor();
		}
		if (attacker.buff(Bless.class) != null) blessBoost *= 1.25f;
		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.BLESS)
				&& attacker.alignment == Char.Alignment.ALLY){
			// + 3%/5%
			blessBoost *= 1.01f + 0.02f*Dungeon.hero.pointsInTalent(Talent.BLESS);
		}
		if (blessBoost > 1f) hitReasons.put(HIT_BLS, blessBoost);
		if (RingOfAccuracy.accuracyMultiplier(attacker) > 1)    hitReasons.put(HIT_ACC, RingOfAccuracy.accuracyMultiplier(attacker));
		if (attacker.buff(Scimitar.SwordDance.class) != null)   hitReasons.put(HIT_DANCE, 1.5f);
		if (!(wep instanceof MissileWeapon)) {
			if (attacker instanceof Hero && ((Hero) attacker).hasTalent(Talent.PRECISE_ASSAULT) && ((Hero) attacker).heroClass != HeroClass.DUELIST){
				hitReasons.put(HIT_PRES, 0.1f * Dungeon.hero.pointsInTalent(Talent.PRECISE_ASSAULT));
			}
			if (attacker.buff(Talent.PreciseAssaultTracker.class) != null){
				hitReasons.put(HIT_PRES, Dungeon.hero.pointsInTalent(Talent.PRECISE_ASSAULT) == 2 ? 5f : 2f);
			} else if (attacker.buff(Talent.LiquidAgilACCTracker.class) != null) {
				hitReasons.put(HIT_LIQ, 3f);
			}
		} else {
			if (attacker.buff(Momentum.class) != null
					&& attacker.buff(Momentum.class).freerunning()
					&& ((Hero)attacker).hasTalent(Talent.PROJECTILE_MOMENTUM)) {
				hitReasons.put(HIT_MOMEN, 1f + ((Hero) attacker).pointsInTalent(Talent.PROJECTILE_MOMENTUM) / 2f);
			}
		}

		//evasion reductions (always < 1)
		if (defender.buff(Hex.class) != null)                   hitReasons.put(HIT_HEX, 0.8f);
		if (defender.buff(Daze.class) != null)                  hitReasons.put(HIT_DAZE, 0.5f);
		if (RingOfEvasion.evasionMultiplier(defender) < 1)      hitReasons.put(HIT_EVA, RingOfEvasion.evasionMultiplier(defender));
		if (arm != null && arm.evasionFactor(defender, 100) < 100) {
			//we express armor's normally flat evasion boost as a %, yes this is very awkward
			Armor.testingNoArmDefSkill = true;
			int baseDef = defender.defenseSkill(attacker);
			Armor.testingNoArmDefSkill = false;
			hitReasons.put(HIT_ARM, defender.defenseSkill(attacker)/(float)baseDef);
		}
		//hero specifically gets 1/2 eva when stunned, for mobs its a garunteed hit
		if (defender.paralysed > 0)  {
			if (defender instanceof Hero)   hitReasons.put(HIT_SUPR, 0.5f);
			else                            return HIT_SUPR;
		}

		//sort from largest modifier to smallest one
		ArrayList<Integer> sortedReasons = new ArrayList<>(hitReasons.keySet());
		Collections.sort(sortedReasons, new Comparator<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
				float a1 = hitReasons.get(a) >= 1f ? hitReasons.get(a) : 1 / hitReasons.get(a);
				float b1 = hitReasons.get(b) >= 1f ? hitReasons.get(b) : 1 / hitReasons.get(b);
				return (int)Math.signum(b1 - a1);
			}
		});

		for (Integer reason : sortedReasons){
			if (hitReasons.get(reason) >= 1f) {
				accRoll /= hitReasons.get(reason);
			} else {
				defRoll /= hitReasons.get(reason);
			}
			if (accRoll < defRoll){
				return reason;
			}
		}
		return -1;
	}

	public static int getMissReasonIcon(Char attacker, float accRoll, Char defender, float defRoll){
		HashMap<Integer, Float> missReasons = new HashMap<>();

		//some guaranteed dodged first
		//SPSEXPD: 无限闪避打空分帧 —— 液态敏捷走 EVA 帧，其余（武僧守备/完美闪避）用「完全闪避」帧
		if (defRoll == Char.INFINITE_EVASION && defender.buff(Talent.LiquidAgilEVATracker.class) != null){
			return MISS_LIQ;
		}
		if (defRoll == Char.INFINITE_EVASION){
			return PERFECT_DODGE;
		}

		KindOfWeapon wep = null;
		if (attacker instanceof Hero) wep = ((Hero) attacker).belongings.attackingWeapon();
		if (attacker instanceof MirrorImage) wep = Dungeon.hero.belongings.weapon();
		if (attacker instanceof Statue) wep = ((Statue)attacker).weapon();
		if (attacker instanceof DriedRose.GhostHero) wep = ((DriedRose.GhostHero)attacker).weapon();

		Armor arm = null;
		if (defender instanceof Hero) arm = ((Hero) defender).belongings.armor();
		if (defender instanceof PrismaticImage) arm = Dungeon.hero.belongings.armor();
		if (defender instanceof ArmoredStatue) arm = ((ArmoredStatue)defender).armor();
		if (defender instanceof DriedRose.GhostHero) arm = ((DriedRose.GhostHero)defender).armor();

		//evasion boosts (always > 1)
		float blessBoost = 1; //a few different sources contribute to this icon
		if (defender.buff(ChampionEnemy.class) != null
				&& defender.buff(ChampionEnemy.class).evasionAndAccuracyFactor() > 1){
			blessBoost *= defender.buff(ChampionEnemy.class).evasionAndAccuracyFactor();
		}
		if (defender.buff(Bless.class) != null) blessBoost *= 1.25f;
		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.BLESS)
				&& defender.alignment == Char.Alignment.ALLY){
			// + 3%/5%
			blessBoost *= 1.01f + 0.02f*Dungeon.hero.pointsInTalent(Talent.BLESS);
		}
		if (blessBoost > 1f)                                    missReasons.put(MISS_BLS, blessBoost);
		if (FerretTuft.evasionMultiplier() > 1)                 missReasons.put(MISS_TUFT, FerretTuft.evasionMultiplier());
		if (RingOfEvasion.evasionMultiplier(defender) > 1)      missReasons.put(MISS_EVA, RingOfEvasion.evasionMultiplier(defender));
		if (defender.buff(Quarterstaff.DefensiveStance.class) != null)  missReasons.put(MISS_DEF, 3f);
		if (arm != null && arm.evasionFactor(defender, 100) > 100) {
			//we express armor's normally flat evasion boost as a %, yes this is very awkward
			Armor.testingNoArmDefSkill = true;
			int baseDef = defender.defenseSkill(attacker);
			Armor.testingNoArmDefSkill = false;
			if (defender.buff(Momentum.class) != null){
				//this is cheating a little, as evasion aug gets wrapped into this too
				missReasons.put(MISS_RUN, defender.defenseSkill(attacker) / (float) baseDef);
			} else {
				missReasons.put(MISS_ARM, defender.defenseSkill(attacker) / (float) baseDef);
			}
		}
		if (defender.buff(Talent.LiquidAgilEVATracker.class) != null)   missReasons.put(MISS_LIQ, 3f);

		//accuracy reductions (always < 1)
		if (wep != null && wep.accuracyFactor(attacker, defender) < 1){
			missReasons.put( MISS_WEP, wep.accuracyFactor(attacker, defender));
		}
		if (attacker.buff(Hex.class) != null)                   missReasons.put(MISS_HEX, 0.8f);
		if (attacker.buff(Daze.class) != null)                  missReasons.put(MISS_DAZE, 0.5f);
		if (RingOfAccuracy.accuracyMultiplier(attacker) < 1)    missReasons.put(MISS_ACC, RingOfAccuracy.accuracyMultiplier(attacker));

		//sort from largest modifier to smallest one
		ArrayList<Integer> sortedReasons = new ArrayList<>(missReasons.keySet());
		Collections.sort(sortedReasons, new Comparator<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
				float a1 = missReasons.get(a) >= 1f ? missReasons.get(a) : 1 / missReasons.get(a);
				float b1 = missReasons.get(b) >= 1f ? missReasons.get(b) : 1 / missReasons.get(b);
				return (int)Math.signum(b1 - a1);
			}
		});

		for (Integer reason : sortedReasons){
			if (missReasons.get(reason) >= 1f) {
				defRoll /= missReasons.get(reason);
			} else {
				accRoll /= missReasons.get(reason);
			}
			if (defRoll < accRoll){
				return reason;
			}
		}
		return -1;
	}
}
