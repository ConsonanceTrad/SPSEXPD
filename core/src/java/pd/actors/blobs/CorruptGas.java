/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.hero.Hero;
import pd.effects.BlobEmitter;
import pd.effects.particles.ShadowParticle;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import pd.messages.InlineText;

/** SPS corruption cloud: percentage damage, bleeding, and crippling. */
public class CorruptGas extends Blob implements Hero.Doom {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(CorruptGas.class)
			.t("name", "腐化气体")
			.t("desc", "一团黑紫色的腐化气体盘绕在这里。它会造成流血、减缓移动，并按生命上限造成伤害。")
			.t("rankings_desc", "被腐化气体吞噬")
			.t("ondeath", "你被腐化气体吞噬了……");
	}



	@Override
	protected void evolve() {
		super.evolve();
		int levelDamage = 5 + Dungeon.legacyDepth() / 2;
		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * Dungeon.level.width();
				Char ch = cur[cell] > 0 ? Actor.findChar(cell) : null;
				if (ch == null || ch.isImmune(getClass())) continue;
				Buff.affect(ch, Bleeding.class).set(levelDamage);
				Buff.prolong(ch, Cripple.class, Cripple.DURATION);
				int numerator = ch.HT / 2 + levelDamage;
				int damage = numerator / 40;
				if (Random.Int(40) < numerator % 40) damage++;
				ch.damage(damage, this);
			}
		}
	}
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(ShadowParticle.UP, 0.6f);
	}
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
	@Override public void onDeath() {
		Badges.validateDeathFromGas();
		Dungeon.fail(this);
		GLog.n(Messages.get(this, "ondeath"));
	}
}
