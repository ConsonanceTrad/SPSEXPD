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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.MitBottle;
import com.shatteredpixel.shatteredpixeldungeon.items.StrBottle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes.Landmark;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class WaterOfTransmutation extends WellWater {

	private static final int MAX_REROLLS = 40;

	@Override
	protected boolean affectHero(Hero hero) {
		return false;
	}

	@Override
	protected Item affectItem(Item item, int pos) {
		if (item instanceof Weapon) {
			return changeWeapon((Weapon)item);
		} else if (item instanceof Armor) {
			return changeArmor((Armor)item);
		} else if (item instanceof Scroll) {
			return changeScroll((Scroll)item);
		} else if (item instanceof Potion) {
			return changePotion((Potion)item);
		} else if (item instanceof Ring) {
			return changeRing((Ring)item);
		} else if (item instanceof Wand) {
			return changeWand((Wand)item);
		} else if (item instanceof Artifact) {
			return changeArtifact((Artifact)item);
		} else if (item instanceof StrBottle) {
			return new MitBottle();
		}
		return null;
	}

	private Weapon changeWeapon(Weapon source) {
		for (int i = 0; i < MAX_REROLLS; i++) {
			Item generated = Generator.random(Generator.Category.MELEEWEAPON);
			if (generated instanceof Weapon && generated.getClass() != source.getClass()) {
				Weapon result = (Weapon)generated;
				copyLevelAndKnowledge(source, result);
				result.enchantment = source.enchantment;
				result.reinforced = source.reinforced;
				return result;
			}
		}
		return null;
	}

	private Armor changeArmor(Armor source) {
		for (int i = 0; i < MAX_REROLLS; i++) {
			Item generated = Generator.random(Generator.Category.ARMOR);
			if (generated instanceof Armor && generated.getClass() != source.getClass()) {
				Armor result = (Armor)generated;
				copyLevelAndKnowledge(source, result);
				result.glyph = source.glyph;
				result.reinforced = source.reinforced;
				return result;
			}
		}
		return null;
	}

	private Ring changeRing(Ring source) {
		for (int i = 0; i < MAX_REROLLS; i++) {
			Item generated = Generator.random(Generator.Category.RING);
			if (generated instanceof Ring && generated.getClass() != source.getClass()) {
				Ring result = (Ring)generated;
				copyLevelAndKnowledge(source, result);
				result.reinforced = source.reinforced;
				return result;
			}
		}
		return null;
	}

	private Wand changeWand(Wand source) {
		for (int i = 0; i < MAX_REROLLS; i++) {
			Item generated = Generator.random(Generator.Category.WAND);
			if (generated instanceof Wand && generated.getClass() != source.getClass()) {
				Wand result = (Wand)generated;
				copyLevelAndKnowledge(source, result);
				result.reinforced = source.reinforced;
				result.updateLevel();
				return result;
			}
		}
		return null;
	}

	private Artifact changeArtifact(Artifact source) {
		for (int i = 0; i < MAX_REROLLS; i++) {
			Artifact result = Generator.randomArtifact();
			if (result == null) return null;
			if (result.getClass() != source.getClass()) {
				result.cursedKnown = source.cursedKnown;
				result.cursed = source.cursed;
				result.levelKnown = source.levelKnown;
				result.transferUpgrade(source.visiblyUpgraded());
				return result;
			}
		}
		return null;
	}

	private Scroll changeScroll(Scroll source) {
		if (source instanceof ScrollOfUpgrade) return new ScrollOfMagicalInfusion();
		if (source instanceof ScrollOfMagicalInfusion) return new ScrollOfUpgrade();
		for (int i = 0; i < MAX_REROLLS; i++) {
			Item result = Generator.random(Generator.Category.SCROLL);
			if (result instanceof Scroll && result.getClass() != source.getClass()) return (Scroll)result;
		}
		return null;
	}

	private Potion changePotion(Potion source) {
		for (int i = 0; i < MAX_REROLLS; i++) {
			Item result = Generator.random(Generator.Category.POTION);
			if (result instanceof Potion && result.getClass() != source.getClass()) return (Potion)result;
		}
		return null;
	}

	private void copyLevelAndKnowledge(Item source, Item result) {
		result.level(0);
		if (source.trueLevel() > 0) result.upgrade(source.trueLevel());
		else if (source.trueLevel() < 0) result.degrade(-source.trueLevel());
		result.levelKnown = source.levelKnown;
		result.cursedKnown = source.cursedKnown;
		result.cursed = source.cursed;
	}

	@Override
	public Landmark landmark() {
		return Landmark.WELL_OF_TRANSMUTATION;
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(Speck.factory(Speck.CHANGE), 0.2f, 0);
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
