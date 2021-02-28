package com.Hzz;

import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapComparator {
    
    public static int ONLY_LEFT_MAP = 1;  // if a key only exists in the left map
    public static int ONLY_RIGHT_MAP = 2;  // if a key only exists in the right map
    public static int DIFFERENT = 3;  // if a key exists in both map, but their values are different
    public static int SAME = 4;  // if a key exists in both map with the same values
    
    public static <K, V> Map<K, Integer> compareMap(Map<K, V> left_map, Map<K, V> right_map){
        Map<K, Integer>  ret = new HashMap<>();
        
        Set<K> left_map_keys = left_map.keySet();
        Set<K> right_map_keys = right_map.keySet();
        Set<K> only_left = Sets.difference(left_map_keys, right_map_keys);
        Set<K> only_right = Sets.difference(right_map_keys, left_map_keys);
        Set<K> both = Sets.intersection(right_map_keys, left_map_keys);
        
        for (K obj: only_left) ret.put(obj, ONLY_LEFT_MAP);
        for (K obj: only_right) ret.put(obj, ONLY_RIGHT_MAP);
        for (K obj: both){
            if ( left_map.get(obj).equals(right_map.get(obj)) ){
                ret.put(obj, SAME);
            }else ret.put(obj, DIFFERENT);
        }
        
        return ret;
    }
    
    
}
