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

package render.utils.serialize;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import render.noosa.Game;

//wrapper for libGDX reflection
public class Reflection {
	
	public static boolean isMemberClass( Class cls ){
		return ClassReflection.isMemberClass(cls);
	}
	
	public static boolean isStatic( Class cls ){
		return ClassReflection.isStaticClass(cls);
	}
	
	public static <T> T newInstance( Class<T> cls ){
		//SPSXPD: 非静态内部类（如 RangeWeapon$NormalArrow）依赖外部实例，没有无参构造器，
		//图鉴(Notes/Catalog)等按类反射构造时必然抛 NoSuchMethodException。
		//这里直接返回 null，避免每个引用它的条目都刷一条异常。
		if (cls != null && cls.getEnclosingClass() != null
				&& !java.lang.reflect.Modifier.isStatic(cls.getModifiers())) {
			return null;
		}
		try {
			return ClassReflection.newInstance(cls);
		} catch (Exception e) {
			Game.reportException(e);
			return null;
		}
	}
	
	public static <T> T newInstanceUnhandled( Class<T> cls ) throws Exception {
		return ClassReflection.newInstance(cls);
	}
	
	public static Class forName( String name ){
		try {
			return ClassReflection.forName( name );
		} catch (Exception e) {
			//SPSEXPD: 老存档里可能还存着重分组前的旧包名，试一次迁移
			Class migrated = forNameLegacy( name );
			if (migrated != null) {
				return migrated;
			}
			Game.reportException(e);
			return null;
		}
	}
	
	public static Class forNameUnhandled( String name ) throws Exception {
		try {
			return ClassReflection.forName( name );
		} catch (Exception e) {
			//SPSEXPD: 同上
			Class migrated = forNameLegacy( name );
			if (migrated != null) {
				return migrated;
			}
			throw e;
		}
	}
	
	//SPSEXPD: 把 pd/items 重分组前的旧类名映射到新包名
	private static Class forNameLegacy( String name ){
		String migrated = LegacyItemPackages.migrate( name );
		if (migrated == null) {
			return null;
		}
		try {
			return ClassReflection.forName( migrated );
		} catch (Exception e) {
			return null;
		}
	}
	
}
