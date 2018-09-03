/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.device;

import static com.wpi.swarm.device.DeviceInfo.makeDevInfo;
import com.wpi.swarm.mongo.MCon;
import java.util.HashMap;
import org.bson.Document;

/**
 *
 * @author jtste
 */
public class TypeLoader {

    private final MCon con;
    private final HashMap<Long, DeviceType> cache = new HashMap<>();

    public TypeLoader(MCon con) {
        this.con = con;
    }

    public DeviceType getType(long type) {
        try {
            DeviceType tp = cache.getOrDefault(type, DeviceType.load(con, type));
            if (tp != null) {
                cache.putIfAbsent(type, tp);
            }
            return tp;
        } catch (Exception e) {
        }
        return null;
    }

    public DeviceType getType(Document doc) {
        try {
            long ty = doc.getLong("type");
            return this.getType(ty);
        } catch (Exception e) {
        }
        return null;
    }
}
