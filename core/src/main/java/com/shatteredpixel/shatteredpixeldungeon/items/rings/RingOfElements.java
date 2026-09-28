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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorruptGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.DarkGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ShockWeb;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SlowGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SlowWeb;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.VenomGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs.ElectriShock;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfRain;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSand;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSnow;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSun;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AflyBless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Arcane;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BloodImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Disarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EarthImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Feed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ForeverShadow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GasesImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GoldTouch;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Locked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Muscle;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Needling;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Notice;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Rhythm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Rhythm2;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TargetShoot;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.LitTower;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Otiluke;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollShaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpsDM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BrokenRobot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Yog;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SpearTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.HashSet;

/** SPS 0.9.8's duration-changing and magic-resistant ring. */
public class RingOfElements extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_ELEMENTS;
		buffClass = RingElements.class;
	}

	@Override
	public String statsInfo() {
		if (!isIdentified()) return "???";
		int bonus = level();
		return Messages.get(this, "stats",
				Messages.decimalFormat("#.##", 100f * improveFactor(bonus)),
				Messages.decimalFormat("#.##", 100f * reduceFactor(bonus)),
				Messages.decimalFormat("#.##", 100f * (1f - damageMultiplier(bonus))));
	}

	@Override
	public String upgradeStat1(int level) {
		return Messages.decimalFormat("#.##", 100f * (1f - damageMultiplier(level))) + "%";
	}

	@Override
	protected RingBuff buff() {
		return new RingElements();
	}

	public static final HashSet<Class<?>> REDUCE = new HashSet<>();
	public static final HashSet<Class<?>> IMPROVE = new HashSet<>();

	static {
		REDUCE.add(Burning.class);
		REDUCE.add(Slow.class);
		REDUCE.add(ToxicGas.class);
		REDUCE.add(VenomGas.class);
		REDUCE.add(SpearTrap.class);
		REDUCE.add(ParalyticGas.class);
		REDUCE.add(CorruptGas.class);
		REDUCE.add(DarkGas.class);
		REDUCE.add(ElectriShock.class);
		REDUCE.add(SlowGas.class);
		REDUCE.add(ConfusionGas.class);
		REDUCE.add(ShockWeb.class);
		REDUCE.add(SlowWeb.class);
		REDUCE.add(Web.class);
		REDUCE.add(Blindness.class);
		REDUCE.add(Disarm.class);
		REDUCE.add(Locked.class);
		REDUCE.add(Silent.class);
		REDUCE.add(WeatherOfRain.class);
		REDUCE.add(WeatherOfSand.class);
		REDUCE.add(WeatherOfSnow.class);
		REDUCE.add(WeatherOfSun.class);
		REDUCE.add(Poison.class);
		REDUCE.add(Electricity.class);
		REDUCE.add(Warlock.class);
		REDUCE.add(GnollShaman.class);
		REDUCE.add(BrokenRobot.class);
		REDUCE.add(SpsDM300.class);
		REDUCE.add(Eye.class);
		REDUCE.add(Otiluke.class);
		REDUCE.add(LitTower.class);
		REDUCE.add(Shell.class);
		REDUCE.add(Yog.BurningFist.class);
		REDUCE.add(Yog.PinningFist.class);

		// Retained Shattered counterparts remain covered while hidden from SPS runs.
		REDUCE.add(DM300.class);
		REDUCE.add(YogFist.BurningFist.class);

		IMPROVE.add(AflyBless.class);
		IMPROVE.add(Arcane.class);
		IMPROVE.add(Awareness.class);
		IMPROVE.add(BloodImbue.class);
		IMPROVE.add(EarthImbue.class);
		IMPROVE.add(Feed.class);
		IMPROVE.add(ForeverShadow.class);
		IMPROVE.add(FrostImbue.class);
		IMPROVE.add(GasesImmunity.class);
		IMPROVE.add(GoldTouch.class);
		IMPROVE.add(HasteBuff.class);
		IMPROVE.add(Invisibility.class);
		IMPROVE.add(Levitation.class);
		IMPROVE.add(MagicImmunity.class);
		IMPROVE.add(Muscle.class);
		IMPROVE.add(Needling.class);
		IMPROVE.add(Notice.class);
		IMPROVE.add(Recharging.class);
		IMPROVE.add(Rhythm.class);
		IMPROVE.add(Rhythm2.class);
		IMPROVE.add(TargetShoot.class);
	}

	public static float fintime(Char target, Class<?> effect) {
		int bonus = getBonus(target, RingElements.class);
		if (bonus == 0) return 1f;
		if (matches(REDUCE, effect)) return reduceFactor(bonus);
		if (matches(IMPROVE, effect)) return improveFactor(bonus);
		return 1f;
	}

	public static double damageMultiplier(Char target, Object source) {
		if (!isMagicDamage(source)) return 1f;
		return damageMultiplier(getBonus(target, RingElements.class));
	}

	private static boolean isMagicDamage(Object source) {
		return !(source instanceof Hunger) && (source instanceof Wand || source instanceof DamageType
				|| source instanceof Blob || source instanceof Buff);
	}

	private static boolean matches(HashSet<Class<?>> effects, Class<?> effect) {
		for (Class<?> candidate : effects) {
			if (candidate.isAssignableFrom(effect)) return true;
		}
		return false;
	}

	private static float reduceFactor(int bonus) {
		return Math.max(0.40f, (100f - bonus * 2f) / 100f);
	}

	private static float improveFactor(int bonus) {
		return Math.min(3f, (15f + bonus) / 15f);
	}

	private static double damageMultiplier(int bonus) {
		return Math.max(0.60, 1.0 - bonus / 75.0);
	}

	public class RingElements extends RingBuff {
		@Override public int level() { return RingOfElements.this.level(); }
		@Override public int buffedLvl() { return level(); }
	}

	/** Kept so saves and retained Shattered code can still resolve the former buff name. */
	@Deprecated
	public class Resistance extends RingElements { }
}
