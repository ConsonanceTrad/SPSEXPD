/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Balanced combat-style reinterpretations of SPS-PD skin mechanics.
 * Distributed under the GNU General Public License v3 or later.
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public enum CombatStyle {

	BALANCED(1f, 1f, 1f, 1f, 1f, false),
	ASSAULT(1.08f, 1f, 0.92f, 1f, 1f, false),
	GUARDIAN(0.92f, 1f, 1f, 1f, 1f, true),
	AGILE(1f, 1f, 1f, 1.08f, 0.94f, false),
	PRECISE(1f, 1.08f, 1f, 0.94f, 1f, false),
	ENDURING(1f, 0.94f, 1f, 1f, 1.08f, false),
	TACTICAL(0.95f, 1f, 1.06f, 1f, 1f, false),
	RECKLESS(1.05f, 1.05f, 1f, 1f, 0.90f, false);

	private final float damage;
	private final float accuracy;
	private final float evasion;
	private final float speed;
	private final float health;
	private final boolean bonusArmor;

	CombatStyle(float damage, float accuracy, float evasion, float speed, float health,
			boolean bonusArmor) {
		this.damage = damage;
		this.accuracy = accuracy;
		this.evasion = evasion;
		this.speed = speed;
		this.health = health;
		this.bonusArmor = bonusArmor;
	}

	public float damageMultiplier() {
		return damage;
	}

	public float accuracyMultiplier() {
		return accuracy;
	}

	public float evasionMultiplier() {
		return evasion;
	}

	public float speedMultiplier() {
		return speed;
	}

	public float healthMultiplier() {
		return health;
	}

	public int bonusArmor(int heroLevel) {
		return bonusArmor ? 1 + heroLevel / 12 : 0;
	}

	public String title() {
		return Messages.get(this, name().toLowerCase());
	}

	public String summary() {
		return Messages.get(this, name().toLowerCase() + "_summary");
	}
}
