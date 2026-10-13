/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee;

import pd.actors.hero.Hero;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.items.consum.materials.DryTwig;
import pd.items.equipment.weapon.guns.GunArchetype;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.messages.InlineText;

/**
 * SPSEXPD: 十字弩——已接入「特殊枪械」系统：以枯枝为弹药（弹匣容量 1，装填方式与马人长弓一致），
 * 一次装填消耗背包里的一根枯枝，命中等同普通枪械弹道。
 * 原有的「蓄势射击」武技与飞镖强化随飞镖/涂药系统一并移除。
 */
public class Crossbow extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Crossbow.class)
			.t("name", "十字弩")
			.t("desc", "这是一件看起来相当精密复杂的装置，能够把粗短的弩矢以极高的速度射出。"
					+ "它需要枯枝作为弹药，弹匣一次只装得下一发。")
			.t("no_twig", "你没有可以装填的枯枝。");
	}

	{
		image = EquipmentEquipWeaponBasicWeaponDict.CROSSBOW_0;
	}

	public Crossbow() {
		super(GunArchetype.CUSTOM);
		fullCharge = 1;      //SPSEXPD: 弹匣容量 1（等同马人长弓：一根枯枝一发）
		shotsPerFire = 1;
		perfectSTR = 16;
		effectiveRange = 5;
		noiseRadius = 8;     //SPSEXPD: 重型弩，枪声传播较远
		baseReloadTime = 2f;
	}

	//SPSEXPD: 伤害沿用原十字弩（tier 4 曲线：1+lvl ~ 20+4lvl）
	@Override public int min(int lvl) { return 1 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 20 + 4 * Math.max(0, lvl); }

	/** SPSEXPD: 装填耗材 = 背包里的一根枯枝（无限弹药袋生效期间无需消耗）。 */
	@Override
	protected boolean consumeReload(Hero hero) {
		if (hero == null) return false;
		DryTwig twig = hero.belongings.getItem(DryTwig.class);
		if (twig == null || !hero.belongings.backpack.contains(twig)) return false;
		if (pd.actors.buffs.EndlessAmmo.isActive(hero)) return true;
		return twig.detach(hero.belongings.backpack) != null;
	}

	@Override protected String reloadHintKey() { return "no_twig"; }
}
