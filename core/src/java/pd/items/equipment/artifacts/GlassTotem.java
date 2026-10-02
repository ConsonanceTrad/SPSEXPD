/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

import pd.Assets;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.GlassShield;
import pd.actors.hero.Hero;
import pd.effects.particles.ElmoParticle;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/** SPS-PD 0.9.8's self-charging glass blessing artifact. */
public class GlassTotem extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GlassTotem.class)
			.t("name", "玻璃图腾")
			.t("ac_atk", "进攻祝福")
			.t("ac_def", "耗竭-保护祝福")
			.t("desc", "由玻璃女神的信徒所制作的一件……图腾？可以用它来和玻璃女神沟通并乞求她的祝福。");
	}


	public static final String AC_ATK = "ATK";
	public static final String AC_DEF = "DEF";
	public static final int FULL_CHARGE = 100;
	public static final int MAX_LEVEL = 10;

	{
		image = EquipmentJewelleryArtifactDict.GLASS_TOTEM;
		levelCap = MAX_LEVEL;
		chargeCap = FULL_CHARGE;
		defaultAction = AC_ATK;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && !cursed && charge == chargeCap) actions.add(AC_ATK);
		if (isEquipped(hero) && !cursed && level() > 2) actions.add(AC_DEF);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_ATK.equals(action)) {
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (cursed) {
				GLog.i(Messages.get(Artifact.class, "cursed"));
			} else if (charge != chargeCap) {
				GLog.i(Messages.get(Artifact.class, "no_charge"));
			} else {
				useAttackBlessing(hero);
			}
		} else if (AC_DEF.equals(action)) {
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (cursed) {
				GLog.i(Messages.get(Artifact.class, "cursed"));
			} else if (level() > 2) {
				useDefenceBlessing(hero);
			}
		}
	}

	void useAttackBlessing(Hero hero) {
		if (level() < levelCap) level(level() + 1);
		Buff.affect(hero, AttackUp.class, 200f).level(8 * level());
		Buff.affect(hero, ArmorBreak.class, 200f).level(8 * level());
		charge = 0;
		partialCharge = 0;
		finishUse(hero, 1f);
	}

	void useDefenceBlessing(Hero hero) {
		level(level() - 2);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
		if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		Buff.detach(hero, AttackUp.class);
		Buff.affect(hero, GlassShield.class).turns(2);
		finishUse(hero, 3f);
	}

	private void finishUse(Hero hero, float time) {
		hero.spend(time);
		hero.busy();
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		updateQuickslot();
	}

	void advanceCharge() {
		if (charge >= chargeCap) {
			partialCharge = 0;
			return;
		}
		partialCharge++;
		if (partialCharge >= 5f) {
			charge++;
			partialCharge = 0;
		}
	}

	void applyCursedBacklash(Hero hero) {
		Buff.affect(hero, ArmorBreak.class, 10f).level(100);
	}

	void applyFullChargeBlessing(Hero hero) {
		Buff.affect(hero, AttackUp.class, 5f).level(20);
		Buff.affect(hero, DefenceUp.class, 5f).level(20);
	}

	public int charge() { return charge; }

	@Override
	protected ArtifactBuff passiveBuff() {
		return new GlassRecharge();
	}

	public class GlassRecharge extends ArtifactBuff {
		@Override
		public boolean act() {
			if (charge < chargeCap && !cursed) {
				advanceCharge();
			} else if (cursed && target instanceof Hero && Random.Int(100) == 0) {
				applyCursedBacklash((Hero)target);
			} else if (!cursed && target instanceof Hero && Random.Int(1000 / (level() + 1)) == 0) {
				applyFullChargeBlessing((Hero)target);
			} else {
				partialCharge = 0;
			}
			updateQuickslot();
			spend(TICK);
			return true;
		}
	}
}
