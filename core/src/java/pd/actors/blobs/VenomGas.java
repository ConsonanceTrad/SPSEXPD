package pd.actors.blobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Venom;
import pd.effects.BlobEmitter;
import pd.effects.Speck;
import pd.messages.Messages;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class VenomGas extends Blob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(VenomGas.class)
			.t("desc", "这里盘绕着一片鲜彩毒雾。它具有强烈的腐蚀性，会不断侵蚀其中的生物。");
	}

	private static final String STRENGTH = "strength";

	private int strength;

	@Override
	protected void evolve() {
		super.evolve();
		if (volume == 0) {
			strength = 0;
			return;
		}
		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * Dungeon.level.width();
				Char ch = cur[cell] > 0 ? Actor.findChar(cell) : null;
				if (ch != null && !ch.isImmune(getClass())) {
					Buff.affect(ch, Venom.class).set(2f, strength);
				}
			}
		}
	}

	public void setStrength(int strength) {
		this.strength = Math.max(this.strength, strength);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		strength = bundle.getInt(STRENGTH);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STRENGTH, strength);
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(Speck.factory(Speck.TOXIC), 0.6f);
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
