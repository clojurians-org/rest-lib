package com.bumblebee.acquisition.core.model;

// import com.bumblebee.acquisition.util.CommonUtils;
import com.bumblebee.acquisition.util.Pair;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据采集上下文对象
 * Created by hua on 2017/5/29.
 */
public class AcquisitionParameter implements Serializable {
    private final Map<String, List<Pair<String, String>>> data;

    public AcquisitionParameter() {
        this.data = new HashMap<>();
    }

    /**
     * 获取stirng值
     *
     * @param key key
     * @return pairs
     */
    public List<Pair<String, String>> get(String key) {
        return data.get(key);
    }

    /**
     * put pairs
     *
     * @param key   pair key
     * @param pairs value
     */
    public void put(String key, List<Pair<String, String>> pairs) {
        data.put(key, pairs);
    }

    /**
     * 转换成Map
     *
     * @return map
     */
    public static String append(Object... objs){
        StringBuilder result = new StringBuilder() ;
        for(Object obj : objs) {
          result.append(obj) ;
        }
        return result.toString() ;
    }
    public Map<String, String> toMap() {
        Map<String, String> parameter = new HashMap<>();
        for (Map.Entry<String, List<Pair<String, String>>> entry : data.entrySet()) {
            String key = entry.getKey();
            for (Pair<String, String> pair : entry.getValue()) {
                parameter.put(append(key, "_", pair.getKey()), pair.getValue()); // append key + pairKey
            }
        }
        return parameter;
    }

    /**
     * 获取参数组的Map对象
     * (key相同会被后面的覆盖)
     *
     * @param groupName 参数组名称
     * @return map
     */
    public Map<String, String> getGroupParameter(String groupName) {
        Map<String, String> parameter = new HashMap<>();
        List<Pair<String, String>> pairs = data.get(groupName);
        if (CollectionUtils.isEmpty(pairs)) {
            return parameter;
        }

        for (Pair<String, String> pair : pairs) {
            parameter.put(pair.getKey(), pair.getValue()); // append key + pairKey
        }
        return parameter;
    }

}
