/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 特质容器 —— 移植自 Darkest Pixel Dungeon 0.7.2。
 */

package pd.actors.hero.perks;

import java.util.ArrayList;

import pd.actors.hero.Hero;
import render.utils.serialize.Bundle;
import render.utils.serialize.Bundlable;

public class HeroPerk implements Bundlable {

	public static final String STR_PERKS = "perks";

	public final ArrayList<Perk> perks = new ArrayList<>();

	public ArrayList<Perk> getPerks() {
		return perks;
	}

	@SuppressWarnings("unchecked")
	public <T extends Perk> T get(Class<T> cls) {
		for (Perk p : perks) {
			if (cls.isInstance(p)) return (T) p;
		}
		return null;
	}

	public boolean has(Class<? extends Perk> cls) {
		return get(cls) != null;
	}

	/** 已拥有 -> 升级；未拥有 -> 加入并触发 onGain */
	public boolean add(Perk perk) {
		if (perk == null) return false;
		for (Perk p : perks) {
			if (p.getClass() == perk.getClass()) {
				p.upgrade();
				PerkGain.announceUpgrade(pd.Dungeon.hero, p);
				return true;
			}
		}
		if (!perks.add(perk)) return false;
		perk.onGain();
		return true;
	}

	/** 降级；降到 0 则移除 */
	public void downgrade(Perk perk) {
		if (perk == null) return;
		for (Perk p : new ArrayList<>(perks)) {
			if (p.getClass() == perk.getClass()) {
				if (p.level() > 0) {
					p.downgrade();
				} else {
					p.upgrade();
				}
				if (p.level() == 0) {
					perks.remove(p);
					p.onLose();
				}
				return;
			}
		}
	}

	public void remove(Perk perk) {
		if (perk == null) return;
		for (Perk p : new ArrayList<>(perks)) {
			if (p.getClass() == perk.getClass()) {
				perks.remove(p);
				p.onLose();
				return;
			}
		}
	}

	public void clear() {
		for (Perk p : new ArrayList<>(perks)) {
			perks.remove(p);
			p.onLose();
		}
	}

	/** 由等级/条件授予的表驱动检查（见 PerkGrants） */
	public void grantIfMissing(Perk perk, Hero hero) {
		if (perk == null) return;
		if (!has(perk.getClass())) {
			add(perk);
			PerkGain.announce(hero, perk);
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(STR_PERKS, perks);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		perks.clear();
		if (!bundle.contains(STR_PERKS)) return;
		for (Bundlable b : bundle.getCollection(STR_PERKS)) {
			if (b instanceof Perk) perks.add((Perk) b);
		}
	}
}
