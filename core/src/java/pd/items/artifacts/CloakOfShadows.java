/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package pd.items.artifacts;

import pd.Assets;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ForeverShadow;
import pd.actors.hero.Hero;
import pd.effects.particles.ElmoParticle;
import pd.items.Item;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.ItemSpriteSheet;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.noosa.tweeners.AlphaTweener;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/** SPS-PD 0.9.8's charge-based invisibility cloak. */
public class CloakOfShadows extends Artifact {

	public static final String AC_STEALTH = "STEALTH";
	public static final String AC_SHADOW = "SHADOW";

	private boolean stealthed;

	{
		image = ItemSpriteSheet.ARTIFACT_CLOAK;
		levelCap = 10;
		charge = Math.min(level() + 3, 10);
		partialCharge = 0;
		chargeCap = Math.min(level() + 3, 10);
		defaultAction = AC_STEALTH;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge > 1) actions.add(AC_STEALTH);
		if (isEquipped(hero) && level() > 3 && !cursed) actions.add(AC_SHADOW);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_STEALTH.equals(action) && !AC_SHADOW.equals(action)) {
			super.execute(hero, action);
			return;
		}

		if (AC_STEALTH.equals(action)) {
			if (!stealthed) {
				if (!isEquipped(hero)) {
					GLog.i(Messages.get(Artifact.class, "need_to_equip"));
				} else if (cooldown > 0) {
					GLog.i(Messages.get(this, "cooldown", cooldown));
				} else if (charge <= 1) {
					GLog.i(Messages.get(this, "no_charge"));
				} else {
					stealthed = true;
					hero.spend(1f);
					hero.busy();
					Sample.INSTANCE.play(Assets.Sounds.MELD);
					activeBuff = activeBuff();
					if (!activeBuff.attachTo(hero)) {
						activeBuff = null;
						stealthed = false;
						return;
					}
					if (hero.sprite != null) {
						if (hero.sprite.parent != null) {
							hero.sprite.parent.add(new AlphaTweener(hero.sprite, 0.4f, 0.4f));
						} else {
							hero.sprite.alpha(0.4f);
						}
						hero.sprite.operate(hero.pos);
					}
				}
			} else {
				Buff oldBuff = activeBuff;
				stealthed = false;
				if (oldBuff != null) oldBuff.detach();
				activeBuff = null;
				hero.spend(1f);
				if (hero.sprite != null) hero.sprite.operate(hero.pos);
			}
		} else if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
		} else if (level() > 3) {
			int duration = level() * 10;
			level(level() - 3);
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			if (hero.sprite != null) {
				hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
				hero.sprite.operate(hero.pos);
			}
			Buff.affect(hero, ForeverShadow.class, duration);
			hero.spend(1f);
			hero.busy();
			updateQuickslot();
		}
	}

	@Override
	public void activate(Char ch) {
		if (ch instanceof Hero && !isEquipped((Hero) ch)) return;
		super.activate(ch);
		if (stealthed) {
			activeBuff = activeBuff();
			if (!activeBuff.attachTo(ch)) {
				activeBuff = null;
				stealthed = false;
			}
		}
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (!super.doUnequip(hero, collect, single)) return false;
		if (activeBuff != null) {
			activeBuff.detach();
			activeBuff = null;
		}
		stealthed = false;
		return true;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new cloakRecharge();
	}

	@Override
	protected ArtifactBuff activeBuff() {
		return new cloakStealth();
	}

	@Override
	public Item upgrade() {
		chargeCap = Math.min(chargeCap + 1, 10);
		return super.upgrade();
	}

	/** Compatibility for retained Shattered-only rewards; normal SPS charging is time-based. */
	public void directCharge(int amount) {
		charge = Math.min(charge + Math.max(0, amount), chargeCap);
		updateQuickslot();
	}

	private static final String STEALTHED = "stealthed";
	private static final String OBSOLETE_BUFF = "buff";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STEALTHED, stealthed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		stealthed = bundle.getBoolean(STEALTHED) || bundle.contains(OBSOLETE_BUFF);
		if (bundle.contains("cooldown")) {
			exp = 0;
			level((int) Math.ceil(level() * 0.7f));
			charge = chargeCap = Math.min(3 + level(), 10);
		}
	}

	public class cloakRecharge extends ArtifactBuff {
		@Override
		public boolean act() {
			if (charge < chargeCap) {
				if (!stealthed) {
					float turnsToCharge = 50 - (chargeCap - charge);
					if (level() > 7) turnsToCharge -= 10 * (level() - 7) / 3f;
					partialCharge += 1f / turnsToCharge;
				}
				if (partialCharge >= 1) {
					charge++;
					partialCharge -= 1;
					if (charge == chargeCap) partialCharge = 0;
				}
			} else {
				partialCharge = 0;
			}
			if (cooldown > 0) cooldown--;
			updateQuickslot();
			spend(TICK);
			return true;
		}
	}

	public class cloakStealth extends ArtifactBuff {
		int turnsToCost;

		@Override
		public int icon() {
			return BuffIndicator.INVISIBLE;
		}

		@Override
		public boolean attachTo(Char target) {
			if (!super.attachTo(target)) return false;
			target.invisible++;
			return true;
		}

		@Override
		public boolean act() {
			turnsToCost--;
			if (turnsToCost <= 0) {
				charge--;
				if (charge < 0) {
					charge = 0;
					detach();
					GLog.w(Messages.get(this, "no_charge"));
					if (target instanceof Hero) ((Hero) target).interrupt();
				} else {
					int levelDifference = ((Hero) target).lvl - (1 + level() * 2);
					if (level() >= 7) levelDifference -= level() - 6;
					if (levelDifference >= 0) {
						exp += Math.round(10f * Math.pow(1.1f, levelDifference));
					} else {
						exp += Math.round(10f * Math.pow(0.75f, -levelDifference));
					}
					if (exp >= (level() + 1) * 50 && level() < levelCap) {
						upgrade();
						exp -= level() * 50;
						GLog.p(Messages.get(this, "levelup"));
					}
					turnsToCost = 5;
				}
				updateQuickslot();
			}
			spend(TICK);
			return true;
		}

		public void dispel() {
			act();
			if (target != null && target.buff(cloakStealth.class) == this) detach();
		}

		@Override
		public void fx(boolean on) {
			if (target == null || target.sprite == null) return;
			if (on) target.sprite.add(CharSprite.State.INVISIBLE);
			else if (target.invisible == 0) target.sprite.remove(CharSprite.State.INVISIBLE);
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}

		@Override
		public void detach() {
			if (target != null && target.invisible > 0) target.invisible--;
			stealthed = false;
			if (activeBuff == this) activeBuff = null;
			updateQuickslot();
			super.detach();
		}
	}
}
