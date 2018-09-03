/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.device;

import com.mongodb.client.MongoCursor;
import com.wpi.swarm.device.DeviceType.ValueDef.ValueType;
import com.wpi.swarm.mongo.MCon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.bson.Document;

/**
 *
 * @author jtste
 */
public class DeviceType {

    public static final String COLLECTION = "types";

    private final Map<String, ValueDef> valDefs = new HashMap<>();
    private final long type;
    private final String creator;

    public DeviceType(long type, String creator) {
        this.type = type;
        this.creator = creator;
    }

    public String getCreator(){
        return this.creator;
    }
    
    public long getType() {
        return type;
    }

    public Collection<ValueDef> getValueDefs() {
        return Collections.unmodifiableCollection(valDefs.values());
    }

    public ValueDef getDef(String field) {
        return valDefs.get(field);
    }

    public ValueDef getDef(int fieldId) {
        for (ValueDef d : valDefs.values()) {
            if (d.getFieldId() == fieldId) {
                return d;
            }
        }
        return null;
    }

    public DeviceType addDef(ValueDef def) {
        if (def != null) {
            this.valDefs.put(def.getName(), def);
        }
        return this;
    }

    public static DeviceType load(MCon con, long type) {
        MongoCursor<Document> it = con.getCollection(COLLECTION).find(new Document("_id", type)).iterator();
        if (it.hasNext()) {
            Document doc = it.next();
            if (doc != null) {
                try {
                    long t = doc.getLong("_id");
                    String c= doc.getString("creator");
                    DeviceType tp = new DeviceType(t,c);
                    List<Document> vs = (List<Document>) doc.get("vals");
                    if (vs != null) {
                        for (Document d : vs) {
                            try {
                                String n = d.getString("name");
                                String vt = d.getString("type");
                                int fid = d.getInteger("field", 0);
                                if (n != null && vt != null && fid != 0) {
                                    tp.addDef(new ValueDef(n, ValueType.valueOf(vt), fid));
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                    return tp;
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public static boolean create(MCon con, DeviceType tp) {
        Document d = new Document();
        d.put("_id", tp.type);
        d.put("creator", tp.creator);
        List<Document> docs = new ArrayList<>();
        for (ValueDef def : tp.valDefs.values()) {
            Document doc = new Document();
            doc.put("name", def.getName());
            doc.put("type", def.getType().name());
            doc.put("field", def.getFieldId());
        }
        d.put("vals", docs);
        try {
            con.getCollection(COLLECTION).insertOne(d);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean update(MCon con, DeviceType tp) {
        Document d = new Document();
        List<Document> docs = new ArrayList<>();
        for (ValueDef def : tp.valDefs.values()) {
            Document doc = new Document();
            doc.put("name", def.getName());
            doc.put("type", def.getType().name());
            doc.put("field", def.getFieldId());
        }
        d.put("vals", docs);
        try {
            return con.getCollection(COLLECTION).updateOne(new Document("_id", tp.getType()).append("creator", tp.getCreator()), d).getMatchedCount() != 0;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean delete(MCon con, long tp, String creator) {
        try {
            return con.getCollection(COLLECTION).deleteOne(new Document("_id", tp).append("creator", creator)).getDeletedCount() != 0;
        } catch (Exception e) {
        }
        return false;
    }

    public static final class ValueDef {

        public enum ValueType {
            NUMBER,
            STRING
        }

        private final String name;
        private final ValueType type;
        private final int fieldId;

        public ValueDef(String name, ValueType type, int fieldId) {
            this.name = name;
            this.type = type;
            this.fieldId = fieldId;
        }

        public String getName() {
            return name;
        }

        public ValueType getType() {
            return type;
        }

        public int getFieldId() {
            return fieldId;
        }
    }
    
    public static String toJson(DeviceType type) {
        JsonObjectBuilder obj = Json.createObjectBuilder();
        obj.add("type", type.getType());
        JsonObjectBuilder defs = Json.createObjectBuilder();
        for (ValueDef d : type.valDefs.values()){
            defs.add(d.getName(), Json.createObjectBuilder().add("fieldId", d.getFieldId()).add("type", d.getType().name()));
        }
        obj.add("defs", defs);
        return obj.build().toString();
    }
}
