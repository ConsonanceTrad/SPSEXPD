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

import pd.atlas.items.SpecificPagesDict;

import pd.journal.Document;
import pd.messages.Messages;
import pd.messages.InlineText;

public class AlchemyPage extends DocumentPage {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AlchemyPage.class)
			.t("name", "被撕下的炼金指南书页")
			.t("desc", "从一本炼金指南书上撕下来的一页。\n\n在远处你只能看到一行行密密麻麻的小字，不过你仍然可以看清书页上的标题\n\n_\"%s\"_");
	}



	
	{
		image = SpecificPagesDict.ALCH_PAGE_0;
	}
	
	@Override
	public Document document() {
		return Document.ALCHEMY_GUIDE;
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", document().pageTitle(page()));
	}
}
