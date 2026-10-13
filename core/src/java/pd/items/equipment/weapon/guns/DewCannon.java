package pd.items.equipment.weapon.guns;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.items.Waterskin;
import pd.messages.InlineText;

/**
 * SPSEXPD: 特殊枪械·露珠炮——以露珠为弹药。
 * 一次装填消耗露珠瓶 15 点露珠，填充 3 发充能；对亡灵生物造成额外伤害。
 */
public class DewCannon extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DewCannon.class)
			.t("name", "露珠炮")
			.t("desc", "以露珠为弹药的奇特火炮，一次装填消耗露珠瓶 15 点露珠，填充 3 发。\n\n凝聚的生命能量对_亡灵_生物格外致命。")
			.t("no_dew", "露珠瓶里没有足够的露珠（需要 15 点）。");
	}

	/** SPSEXPD: 一次装填消耗的露珠数。 */
	public static final int DEW_COST = 15;
	/** SPSEXPD: 弹药（充能）数。 */
	public static final int CHARGES = 3;

	{ image = EquipmentEquipWeaponGunDict.DEW_CANNON; }
	public DewCannon() {
		super(GunArchetype.CUSTOM);
		fullCharge = CHARGES;
		shotsPerFire = 1;
		perfectSTR = 14;
		effectiveRange = 6;
		noiseRadius = 6;
		baseReloadTime = 2f;
	}

	/** SPSEXPD: 装填耗材 = 露珠瓶里的 15 点露珠。 */
	@Override
	protected boolean consumeReload(Hero hero) {
		if (hero == null) return false;
		if (pd.actors.buffs.EndlessAmmo.isActive(hero)) return true;
		Waterskin flask = hero.belongings.getItem(Waterskin.class);
		if (flask == null || !flask.hasDew(DEW_COST)) return false;
		return flask.spendDewStrict(DEW_COST);
	}

	@Override protected String reloadHintKey() { return "no_dew"; }

	/** SPSEXPD: 对亡灵额外造成一段伤害。 */
	@Override
	protected void onBulletHit(Char attacker, Char defender, int damage) {
		if (Char.hasProp(defender, Char.Property.UNDEAD)) {
			defender.damage(Math.max(1, damage / 2), this);
		}
	}
}
