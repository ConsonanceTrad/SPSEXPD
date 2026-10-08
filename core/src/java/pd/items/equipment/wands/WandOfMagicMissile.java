/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.MagicWeak;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** The original SPS-PD magic missile and magic-weakness wand. */
public class WandOfMagicMissile extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfMagicMissile.class)
			.t("name", "魔弹法杖")
			.t("staff_name", "魔弹魔杖")
			.t("desc", "这根十分普通的_无属性_法杖能发射由纯魔法能量构成的飞弹。")
			.t("stats_desc", "从法杖中发出的魔法飞弹会造成_%1$d~%2$d点伤害_，并施加魔法易伤。")
			.t("bmage_desc", "当_战斗法师_以魔弹魔杖近战攻击目标时，魔杖外的所有的法杖都会恢复一定的充能。")
			.t("eleblast_desc", "魔弹魔杖的元素风暴造成50%伤害，并使法师获得15回合的法杖充能效果。")
			.t("discover_hint", "某位英雄初始携带该物品。")
			.t("$magiccharge.name", "魔力强化")
			.t("$magiccharge.desc", "你的魔弹法杖向其他法杖回馈了一股能量，提升着下一次施法的有效等级。\n\n其他法杖被强化至：+%d\n\n剩余的魔力强化时长：%s回合");
	}




	{
		image = EquipmentWandBasicWandDict.WAND_SPS_MAGIC_MISSILE;
		collisionProperties = Ballistica.MAGIC_BOLT;
	}

	@Override public int min(int level) { return 2 + level; }
	@Override public int max(int level) { return 6 + 5 * level; }

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			target.damage((int)(damageRoll()
					* magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
			Buff.affect(target, MagicWeak.class, level());
			if (target.sprite != null) target.sprite.burst(0xFF99CCFF, 2);
		}
	}

	@Override public int initialCharges() { return 3; }

	//SPSEXPD: 魔弹法杖充能上限提到 21，且每级 +2 上限（升级时当前充能同样 +2）
	@Override public int chargeLimit() { return 21; }
	@Override public int chargesPerLevel() { return 2; }

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// The Shattered battlemage charge effect is retained below for save compatibility only.
	}

	/** Retained so saves made before the SPS restoration can still load and expire this buff. */
	public static class MagicCharge extends FlavourBuff {
		{ type = buffType.POSITIVE; announced = true; }

		public static final float DURATION = 4f;
		private int level;
		private Wand wandJustApplied;

		public void setup(Wand wand) {
			if (level < wand.buffedLvl()) {
				level = wand.buffedLvl();
				wandJustApplied = wand;
			}
		}

		@Override public void detach() { super.detach(); updateQuickslot(); }
		public int level() { return level; }
		public Wand wandJustApplied() {
			Wand result = wandJustApplied;
			wandJustApplied = null;
			return result;
		}
		@Override public int icon() { return BuffIndicator.UPGRADE; }
		@Override public void tintIcon(Image icon) { icon.hardlight(0.2f, 0.6f, 1f); }
		@Override public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}
		@Override public String desc() { return Messages.get(this, "desc", level(), dispTurns()); }

		private static final String LEVEL = "level";
		@Override public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(LEVEL, level);
		}
		@Override public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			level = bundle.getInt(LEVEL);
		}
	}
}
