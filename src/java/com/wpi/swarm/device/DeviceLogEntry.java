/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.device;

import com.mongodb.client.model.Indexes;
import com.wpi.swarm.mongo.MCon;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author jtste
 */
public class DeviceLogEntry {

    private final DeviceInfo inf;
    private final String msg;
    private long activity = System.currentTimeMillis();
    private final long id;

    public DeviceLogEntry(long id, DeviceInfo info, String message) {
        inf = info;
        this.id = id;
        msg = message;
    }

    public DeviceLogEntry(long id, DeviceInfo info) {
        this(id, info, null);
    }

    public DeviceLogEntry(long id, String message) {
        this(id, null, message);
    }

    private DeviceLogEntry(long id) {
        this(id, null, null);
    }

    public DeviceInfo getDeviceInfo() {
        return inf;
    }

    public String getMessage() {
        return msg;
    }

    public long getTimeStamp() {
        return this.activity;
    }
    
    public long getDeviceId(){
        return id;
    }

    public static Document makeInsertDoc(DeviceLogEntry log) {
        Document d;
        if (log.inf != null) {
            d = DeviceInfo.makeUpdate("devid", log.inf);
            if (d == null) {
                return null;
            }
            for (String s : d.keySet()) { // remove only live values
                if (s.startsWith("_")) {
                    d.remove(s);
                }
            }
        } else {
            d = new Document();
            d.append("activity", System.currentTimeMillis());
        }
        d.append("devid", log.id);
        if (log.msg != null && log.msg.length() > 0) {
            d.append("msg", log.msg);
        }
        return d;
    }

    public static Document makeSearchDoc(long id, long cutoffTime) {
        Document d = new Document("devid", id).append("activity", new Document("$gt", cutoffTime));
        return d;
    }

    public static Document makeSearchDoc(long id) {
        Document d = new Document("devid", id);
        return d;
    }

    public static DeviceLogEntry makeLogEntry(TypeLoader loader, Document doc) {
        if (doc == null) {
            return null;
        }
        DeviceLogEntry e = new DeviceLogEntry(doc.getLong("devid"),DeviceInfo.makeDevInfo("devid", loader, doc), doc.getString("msg"));
        try {
            e.activity = doc.getLong("activity");
        } catch (Exception ex) {
            return null;
        }
        return e;
    }

    public static void enforceIndex(MCon m) {
        m.getCollection(DeviceController.DEV_LOG_COLLECTION).createIndex(Indexes.hashed("devid"));
        m.getCollection(DeviceController.DEV_LOG_COLLECTION).createIndex(Indexes.descending("activity"));
    }

}
