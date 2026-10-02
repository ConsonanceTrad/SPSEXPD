/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Balanced combat-style reinterpretations of SPS-PD skin mechanics.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.actors.hero;

import pd.messages.Messages;
import pd.messages.InlineText;

public enum CombatStyle {

	BALANCED(1f, 1f, 1f, 1f, 1f, false),
	ASSAULT(1.08f, 1f, 0.92f, 1f, 1f, false),
	GUARDIAN(0.92f, 1f, 1f, 1f, 1f, true),
	AGILE(1f, 1f, 1f, 1.08f, 0.94f, false),
	PRECISE(1f, 1.08f, 1f, 0.94f, 1f, false),
	ENDURING(1f, 0.94f, 1f, 1f, 1.08f, false),
	TACTICAL(0.95f, 1f, 1.06f, 1f, 1f, false),
	RECKLESS(1.05f, 1.05f, 1f, 1f, 0.90f, false);
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(CombatStyle.class)
			.t("balanced", "平衡")
			.t("balanced_summary", "无额外修正")
			.t("assault", "猛攻")
			.t("assault_summary", "伤害+8%，闪避-8%")
			.t("guardian", "守御")
			.t("guardian_summary", "获得少量护甲，伤害-8%")
			.t("agile", "灵动")
			.t("agile_summary", "速度+8%，生命-6%")
			.t("precise", "专注")
			.t("precise_summary", "命中+8%，速度-6%")
			.t("enduring", "坚韧")
			.t("enduring_summary", "生命+8%，命中-6%")
			.t("tactical", "战术")
			.t("tactical_summary", "闪避+6%，伤害-5%")
			.t("reckless", "冒险")
			.t("reckless_summary", "伤害和命中+5%，生命-10%");
	}




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
