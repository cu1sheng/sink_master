package com.sink.mybatis.comm.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class OgnlUtil {
	/**
	 * 可以用于判断String,Map,Collection,Array是否为空
	 *
	 * @param o
	 * @return
	 */
	public static boolean isEmpty(Object o) throws IllegalArgumentException {
		if (o == null)
			return true;

		if (o instanceof String) {
			if (((String) o).length() == 0) {
				return true;
			}
		} else if (o instanceof Collection) {
			if (((Collection) o).isEmpty()) {
				return true;
			}
		} else if (o.getClass().isArray()) {
			if (Array.getLength(o) == 0) {
				return true;
			}
		} else if (o instanceof Map) {
			if (((Map) o).isEmpty()) {
				return true;
			}
		} else {
			return false;
			// throw new
			// IllegalArgumentException("Illegal argument type,must be : Map,Collection,Array,String. but was:"+o.getClass());
		}

		return false;
	}

	/**
	 * 判断数组长度是否大于1
	 *
	 * @param o
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static boolean iflenthMoreThanOne(Object o, boolean flag) throws IllegalArgumentException {
		if (o == null)
			return false;

		if (o instanceof String) {
			if (flag) {
				if (((String) o).length() > 1) {
					return true;
				}
			} else {
				if (((String) o).length() == 1) {
					return true;
				}
			}
		} else if (o instanceof Collection) {
			if (flag) {
				if (((Collection) o).size() > 1) {
					return true;
				}
			} else {
				if (((Collection) o).size() == 1) {
					return true;
				}
			}
		} else if (o.getClass().isArray()) {
			if (flag) {
				if (Array.getLength(o) > 1) {
					return true;
				}
			} else {
				if (Array.getLength(o) == 1) {
					return true;
				}
			}
		} else if (o instanceof Map) {
			if (flag) {
				if (((Map) o).size() > 1) {
					return true;
				}
			} else {
				if (((Map) o).size() == 1) {
					return true;
				}
			}
		} else {
			return false;
			// throw new
			// IllegalArgumentException("Illegal argument type,must be : Map,Collection,Array,String. but was:"+o.getClass());
		}

		return false;
	}

	/**
	 * 判断数组长度是否等于于1
	 *
	 * @param o
	 * @return
	 * @throws IllegalArgumentException
	 */
	/*
	 * public static boolean iflenthMoreThanOne(Object o) throws IllegalArgumentException { if(o == null) return false;
	 *
	 * if(o instanceof String) { if(((String)o).length() > 1){ return true; } } else if(o instanceof Collection) { if(((Collection)o).size() >1){ return true; } } else if(o.getClass().isArray()) { if(Array.getLength(o) > 1){ return true; } } else if(o instanceof Map) {
	 * if(((Map)o).size() >1){ return true; } }else { return false; // throw new IllegalArgumentException ("Illegal argument type,must be : Map,Collection,Array,String. but was:" +o.getClass()); }
	 *
	 * return false; }
	 */

	/**
	 * 可以用于判断 Map,Collection,String,Array是否不为空
	 *
	 * @param c
	 * @return
	 */
	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	public static boolean isNotBlank(Object o) {
		return !isBlank(o);
	}

	public static boolean isNumber(Object o) {
		if (o == null)
			return false;
		if (o instanceof Number) {
			return true;
		}
		if (o instanceof String) {
			String str = (String) o;
			if (str.length() == 0)
				return false;
			if (str.trim().length() == 0)
				return false;
			return org.apache.commons.lang3.StringUtils.isNumeric(str);
		}
		return false;
	}

	public static boolean isBlank(Object o) {
		if (o == null)
			return true;
		if (o instanceof String) {
			String str = (String) o;
			return isBlank(str);
		}
		return false;
	}

	public static boolean isBlank(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 用于验证那些列可以排序
	 *
	 * ibatis示列使用 &lt;if test="@Ognl@checkOrderBy(orderBy,'username,password')"> ORDER BY ${orderBy} &lt;/if>
	 *
	 * <pre>
	 * 返回示例:
	 * 返回false相关验证:
	 * checkOrderBy(null,"user,pwd")
	 * checkOrderBy(" ","user,pwd")
	 * checkOrderBy("user asc,pwd desc","user") pwd不能排序
	 *
	 * 返回true相关验证:
	 * checkOrderBy("user asc,pwd desc",null)
	 * checkOrderBy("user asc,pwd desc","")
	 * checkOrderBy("user asc,pwd desc","user,pwd")
	 * </pre>
	 *
	 * @param orderBy 需要验证的order by字符串
	 * @param validSortColumns 可以排序的列
	 * @throws DataAccessException
	 */
	/*
	 * public static boolean checkOrderBy(String orderby,String validSortColumns) throws DataAccessException{ if(isBlank(orderby)) return false; if(orderby.indexOf("'") >= 0 || orderby.indexOf("\\") >= 0) { throw new
	 * IllegalArgumentException("orderBy:"+orderby+" has SQL Injection risk"); } if(orderby != null && orderby.length() > 100) { throw new IllegalArgumentException("orderby.length() <= 100 must be true"); } if(validSortColumns == null) return true; List<SortInfo> infos =
	 * SortInfo.parseSortColumns(orderby); String[] passColumns = validSortColumns.split(","); for(SortInfo info : infos) { String columnName = info.getColumnName(); if(!isPass(passColumns, info, columnName)) { throw new InvalidDataAccessApiUsageException("orderby:["+orderby
	 * +"] is invalid, only can orderby:"+validSortColumns); } } return true; }
	 *
	 * private static boolean isPass(String[] passColumns, SortInfo info, String columnName) { for(String column : passColumns) { if(column.equalsIgnoreCase(info.getColumnName())) { return true; } } return false; }
	 */
}
