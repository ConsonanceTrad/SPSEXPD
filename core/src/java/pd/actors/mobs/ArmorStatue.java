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

package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Silent;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import pd.journal.Notes;
import pd.messages.Messages;
import pd.sprites.StatueSprite;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** SPS-PD's armor-only statue, paired with a weapon statue in StatueRoom. */
public class ArmorStatue extends Mob {

	private Armor armor;

	{
		spriteClass = StatueSprite.class;
		EXP = 50 + legacyDepthAdjustment(0) * 2;
		state = PASSIVE;
		properties.add(Property.ELEMENT);
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
	}

	public ArmorStatue() {
		super();
		for (int i = 0; i < 100; i++) {
			Armor candidate = Generator.randomArmor();
			if (candidate instanceof NormalArmor && candidate.trueLevel() >= 0) {
				armor = candidate;
				break;
			}
		}
		if (armor == null) {
			armor = Generator.randomArmor();
			if (armor.trueLevel() < 0) armor.level(0);
		}
		armor.identify();
		armor.inscribe(Armor.Glyph.random());
		HP = HT = 15 + legacyDepthAdjustment(0) * 5;
	}

	public Armor armor() {
		return armor;
	}

	private static final String ARMOR = "armor";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ARMOR, armor);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		armor = (Armor)bundle.get(ARMOR);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(legacyDepthAdjustment(0), legacyDepthAdjustment(0) * 2);
	}

	@Override
	public int attackSkill(Char target) {
		return 9 + legacyDepthAdjustment(0) * 3;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return Math.round(armor.evasionFactor(this, 4 + legacyDepthAdjustment(0)));
	}

	@Override
	public float attackDelay() {
		return 1f;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(armor.DRMin(), armor.DRMax());
	}

	@Override
	public void damage(int damage, Object source) {
		if (state == PASSIVE) state = HUNTING;
		super.damage(damage, source);
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		return armor.proc(this, enemy, damage);
	}

	@Override
	public boolean add(Buff buff) {
		if (buff instanceof Locked || buff instanceof Silent) {
			damage(Random.NormalIntRange(1, HT * 2 / 3), buff);
			return false;
		}
		return super.add(buff);
	}

	@Override
	public void beckon(int cell) {
		// The source statue ignores beckoning until directly disturbed.
	}

	@Override
	public void die(Object cause) {
		Heap heap = Dungeon.level == null ? null : Dungeon.level.drop(armor, pos);
		if (heap != null && heap.sprite != null) heap.sprite.drop();
		super.die(cause);
	}

	@Override
	public Notes.Landmark landmark() {
		return Notes.Landmark.STATUE;
	}

	@Override
	public void destroy() {
		Notes.remove(Notes.Landmark.STATUE);
		super.destroy();
	}

	@Override
	public boolean reset() {
		state = PASSIVE;
		return true;
	}

	@Override
	public float spawningWeight() {
		return 0f;
	}

	@Override
	public String description() {
		return Messages.get(this, "desc", armor.name());
	}
}
