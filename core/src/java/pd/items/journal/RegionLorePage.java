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

package pd.items.journal;

import pd.atlas.items.SpecificPagesDict;

import pd.journal.Document;

public class RegionLorePage {

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
