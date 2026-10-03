package pd.actors.blobs.effectblobs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShadowCurse;
import pd.effects.BlobEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.messages.Messages;
import pd.messages.InlineText;

public class ShadowGas extends SpsEffectBlob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ShadowGas.class)
			.t("desc", "SPS暗影场会在三回合内诅咒生物与物品。");
	}



	@Override protected void affect(Char target) {
		if (target.buff(ShadowCurse.class) == null) Buff.affect(target, ShadowCurse.class);
	}
	@Override protected void affect(Heap heap) { heap.darkhit(); }
	//SPSXPD: 暗影场呈现血色雾气 —— 毒气形态的红色粒子（Speck.BLOOD）
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.pour(Speck.factory(Speck.BLOOD), 0.4f); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
