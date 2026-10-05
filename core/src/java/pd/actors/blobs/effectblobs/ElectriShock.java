package pd.actors.blobs.effectblobs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Shocked;
import pd.effects.BlobEmitter;
import pd.effects.particles.EnergyParticle;
import pd.items.Heap;
import pd.messages.Messages;
import pd.messages.InlineText;

public class ElectriShock extends SpsEffectBlob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ElectriShock.class)
			.t("desc", "SPS雷电场会在三回合内电击生物与物品。");
	}



	@Override protected void affect(Char target) {
		//SPSEXPD: 已经被电击的敌人会被雷电场麻痹 1 回合
		if (target.buff(Shocked.class) != null) {
			Buff.prolong(target, Paralysis.class, 1f);
		}
		Buff.affect(target, Shocked.class).set(10f);
	}
	@Override protected void affect(Heap heap) { heap.shockhit(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(EnergyParticle.FACTORY, 0.05f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
