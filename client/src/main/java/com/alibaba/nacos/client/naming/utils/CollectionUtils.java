
package com.alibaba.nacos.client.naming.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CollectionUtils {

    private static final Integer INTEGER_ONE = 1;

    public CollectionUtils() {
    }

    public static Collection subtract(final Collection a, final Collection b) {
        ArrayList list = new ArrayList(a);
        for (Object aB : b) {
            list.remove(aB);
        }
        return list;
    }

    public static Map getCardinalityMap(final Collection coll) {
        Map count = new HashMap(coll.size());
        for (Object obj : coll) {
            Integer c = (Integer) (count.get(obj));
            if (c == null) {
                count.put(obj, INTEGER_ONE);
            } else {
                count.put(obj, c + 1);
            }
        }
        return count;
    }

    public static boolean isEqualCollection(final Collection a, final Collection b) {
        if (a.size() != b.size()) {
            return false;
        } else {
            Map mapa = getCardinalityMap(a);
            Map mapb = getCardinalityMap(b);
            if (mapa.size() != mapb.size()) {
                return false;
            } else {
                for (Object obj : mapa.keySet()) {
                    if (getFreq(obj, mapa) != getFreq(obj, mapb)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    //-----------------------------------------------------------------------

    public static boolean isEmpty(Collection coll) {
        return (coll == null || coll.isEmpty());
    }

    private static int getFreq(final Object obj, final Map freqMap) {
        Integer count = (Integer) freqMap.get(obj);
        if (count != null) {
            return count;
        }
        return 0;
    }
}
