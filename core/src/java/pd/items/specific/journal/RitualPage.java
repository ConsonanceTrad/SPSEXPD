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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items.specific.journal;

import java.util.ArrayList;

import pd.atlas.items.SpecificPagesDict;
import pd.journal.Document;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.rituals.Ritual;
import pd.rituals.Rituals;
import render.utils.math.Random;

/**
 * SPSEXPD: 仪式讲解残页（批次B）——拾取即登记对应仪式的讲解页（Document.findPage 流程）。
 * 未获得残页的仪式仍可激发，但布阵石无法布置该仪式（判定见 pd.rituals.RitualKnowledge）。
 * 随机页面工厂 random()：优先从「还没有讲解页的仪式」里抽，全都有则随机回一张。
 */
public class RitualPage extends DocumentPage {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(RitualPage.class)
			.t("name", "被撕下的仪式讲解书页")
			.t("desc", "从一本仪式讲解书上撕下来的一页。\n\n在远处你只能看到一行行密密麻麻的小字，不过你仍然可以看清书页上的标题\n\n_\"%s\"_");
	}

	{
		image = SpecificPagesDict.GUIDE_PAGE_0;
	}

	@Override
	public Document document() {
		return Document.RITUAL_GUIDE;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", document().pageTitle(page()));
	}

	/** 给一张讲解页：优先抽「还没有讲解页的仪式」；注册表为空时返回 null（命名避开 Item.random()） */
	public static RitualPage rollPage() {
		ArrayList<String> missing = new ArrayList<>();
		ArrayList<String> all = new ArrayList<>();
		for (Ritual ritual : Rituals.all()) {
			all.add( ritual.id() );
			if (!Document.RITUAL_GUIDE.isPageFound( ritual.id() )) {
				missing.add( ritual.id() );
			}
		}
		if (all.isEmpty()) return null;
		String page;
		if (missing.isEmpty()) {
			page = all.get( Random.Int( all.size() ) );
		} else {
			page = missing.get( Random.Int( missing.size() ) );
		}
		RitualPage p = new RitualPage();
		p.page( page );
		return p;
	}
}
