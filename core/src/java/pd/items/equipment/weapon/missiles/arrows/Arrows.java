/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.equipment.weapon.missiles.arrows;

import pd.Assets;
import pd.items.equipment.weapon.missiles.MissileWeapon;

/** Base for SPS-PD's consumable special arrows. */
public class Arrows extends MissileWeapon {

	private final int baseMin;
	private final int baseMax;

	//SPSEXPD: 补无参构造——SpsHiddenShopRoom / GnollTrickster / LinkSword / Generator.Category.ARROWS
	// 都以反射无参实例化本类，缺它会让 Reflection.newInstance 返回 null 并紧接着 NPE
	//（隐藏商店生成、豺狼猎手生成、勇者剑击杀掉落全崩）。默认伤害与既有 new Arrows(2, 7) 一致。
	public Arrows() {
		this(2, 7);
	}

	public Arrows(int min, int max) {
		baseMin = min;
		baseMax = max;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		baseUses = 1;
		tier = 1;
		levelKnown = true;
		DLY = 0.2f;
		quantity(1);
	}

	@Override public int min(int lvl) { return baseMin; }
	@Override public int max(int lvl) { return baseMax; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public boolean isIdentified() { return true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public int value() { return quantity() * 2; }
}
