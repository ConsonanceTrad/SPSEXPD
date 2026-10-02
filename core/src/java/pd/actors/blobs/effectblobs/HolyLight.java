package pd.actors.blobs.effectblobs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.LightShootAttack;
import pd.effects.BlobEmitter;
import pd.effects.particles.ShaftParticle;
import pd.items.Heap;
import pd.messages.Messages;
import pd.messages.InlineText;

public class HolyLight extends SpsEffectBlob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HolyLight.class)
			.t("desc", "SPS圣光场会在三回合内标记生物与物品。");
	}



	@Override protected void affect(Char target) { Buff.affect(target, LightShootAttack.class).level(5); }
	@Override protected void affect(Heap heap) { heap.lighthit(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(ShaftParticle.FACTORY, 1f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
