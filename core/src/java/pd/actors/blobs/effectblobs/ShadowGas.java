package pd.actors.blobs.effectblobs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShadowCurse;
import pd.effects.BlobEmitter;
import pd.effects.particles.BlackFlameParticle;
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
	//SPSEXPD: 回滚为 0.9.8 原版表现 —— 暗影场是黑色火焰粒子（BlackFlameParticle），此前误改成了血红雾气
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(BlackFlameParticle.FACTORY, 0.1f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
