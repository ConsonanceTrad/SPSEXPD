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
import pd.messages.InlineText;

public class RegionLorePage {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RegionLorePage.class)
			.t("$sewers.name", "磨旧的信笺")
			.t("$sewers.desc", "看起来像是一封信，写在一张沾满灰尘的纸上。你需要把它捡起来才能看清上面写着什么。")
			.t("$prison.name", "磨损的日志条目")
			.t("$prison.desc", "写在老旧纸张上的日记条目或其他记录。这似乎比你在下水道里找到的信看起来好一些，但你仍然需要捡起它才能阅读上面的内容。")
			.t("$caves.name", "老旧的日志条目")
			.t("$caves.desc", "看起来是探险家日志中的一个条目，尽管它肯定很旧，但保存得出奇得好。捡起它来阅读上面的内容。")
			.t("$city.name", "锈蚀的便签")
			.t("$city.desc", "一块生锈的小金属板，上面神奇地刻着一条信息。尽管它年代久远，可字母仍然清晰，但你必须捡起它才能阅读上面的内容。")
			.t("$halls.name", "发光的便签")
			.t("$halls.desc", "一块黑色的小板，上面刻有发光的绿色字母。发光的字母在远处模糊成一团薄雾，你必须捡起它才能阅读上面的内容。");
	}




	public static DocumentPage pageForDoc( Document doc ){
		switch (doc){
			case SEWERS_GUARD: default:     return new RegionLorePage.Sewers();
			case PRISON_WARDEN:             return new RegionLorePage.Prison();
			case CAVES_EXPLORER:            return new RegionLorePage.Caves();
			case CITY_WARLOCK:              return new RegionLorePage.City();
			case HALLS_KING:                return new RegionLorePage.Halls();
		}
	}

	public static class Sewers extends DocumentPage {
		{
			image = SpecificPagesDict.SEWER_PAGE_0;
		}

		@Override
		public Document document() {
			return Document.SEWERS_GUARD;
		}
	}

	public static class Prison extends DocumentPage {
		{
			image = SpecificPagesDict.PRISON_PAGE_0;
		}

		@Override
		public Document document() {
			return Document.PRISON_WARDEN;
		}
	}

	public static class Caves extends DocumentPage {
		{
			image = SpecificPagesDict.CAVES_PAGE_0;
		}

		@Override
		public Document document() {
			return Document.CAVES_EXPLORER;
		}
	}

	public static class City extends DocumentPage {
		{
			image = SpecificPagesDict.CITY_PAGE_0;
		}

		@Override
		public Document document() {
			return Document.CITY_WARLOCK;
		}
	}

	public static class Halls extends DocumentPage {
		{
			image = SpecificPagesDict.HALLS_PAGE_0;
		}

		@Override
		public Document document() {
			return Document.HALLS_KING;
		}
	}

}
