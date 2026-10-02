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

package pd.items.equipment.rings;

import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.CorruptGas;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.Electricity;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.ShockWeb;
import pd.actors.blobs.SlowGas;
import pd.actors.blobs.SlowWeb;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.VenomGas;
import pd.actors.blobs.Web;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.blobs.weather.WeatherOfRain;
import pd.actors.blobs.weather.WeatherOfSand;
import pd.actors.blobs.weather.WeatherOfSnow;
import pd.actors.blobs.weather.WeatherOfSun;
import pd.actors.buffs.AflyBless;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.Awareness;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.BloodImbue;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Disarm;
import pd.actors.buffs.EarthImbue;
import pd.actors.buffs.Feed;
import pd.actors.buffs.ForeverShadow;
import pd.actors.buffs.FrostImbue;
import pd.actors.buffs.GasesImmunity;
import pd.actors.buffs.GoldTouch;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Locked;
import pd.actors.buffs.MagicImmunity;
import pd.actors.buffs.Muscle;
import pd.actors.buffs.Needling;
import pd.actors.buffs.Notice;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Rhythm2;
import pd.actors.buffs.Rhythm;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Slow;
import pd.actors.buffs.TargetShoot;
import pd.actors.damagetype.DamageType;
import pd.actors.mobs.BrokenRobot;
import pd.actors.mobs.DM300;
import pd.actors.mobs.Eye;
import pd.actors.mobs.GnollShaman;
import pd.actors.mobs.LitTower;
import pd.actors.mobs.Otiluke;
import pd.actors.mobs.Shell;
import pd.actors.mobs.SpsDM300;
import pd.actors.mobs.Warlock;
import pd.actors.mobs.Yog;
import pd.actors.mobs.YogFist;
import pd.items.equipment.wands.Wand;
import pd.levels.traps.SpearTrap;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;

import java.util.HashSet;

/** SPS 0.9.8's duration-changing and magic-resistant ring. */
public class RingOfElements extends Ring {

	{
		icon = ItemIconSheet.RING_ELEMENTS;
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
