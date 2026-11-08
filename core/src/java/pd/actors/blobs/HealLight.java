/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.BlobEmitter;
import pd.effects.Speck;
import pd.effects.particles.ShaftParticle;
import pd.items.Heap;
import pd.messages.Messages;

public class HealLight extends Blob {
	@Override
	protected void evolve() {
		int width = Dungeon.level.width();
		int height = Dungeon.level.height();
		for (int y = Math.max(1, area.top - 1); y <= Math.min(height - 2, area.bottom); y++) {
			for (int x = Math.max(1, area.left - 1); x <= Math.min(width - 2, area.right); x++) {
				int cell = x + y * width;
				if (cur[cell] <= 0) {
					off[cell] = 0;
					continue;
				}
				Char ch = Actor.findChar(cell);
				if (ch != null && !ch.isImmune(getClass()) && ch.HP < ch.HT) {
					//SPSEXPD: 治疗伤害（伤害类型体系定稿）——不死族受治疗光不再恢复，
					//而是受到等量的「治疗伤害」（走特殊族路径，不吃格挡/法抗等常规减免）
					if (Char.hasProp(ch, Char.Property.UNDEAD)) {
						ch.damage(Math.max(1, ch.HT / 25), pd.actors.damagetype.DamageType.HEAL_DAMAGE);
					} else {
						ch.HP = Math.min(ch.HT, ch.HP + Math.max(1, ch.HT / 25));
						if (ch.sprite != null) ch.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
					}
				}
				Heap heap = Dungeon.level.heaps.get(cell);
				if (heap != null) heap.lighthit();
				off[cell] = cur[cell] - 1;
				volume += off[cell];
			}
		}
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(ShaftParticle.FACTORY, 1f, 0);
	}

	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
