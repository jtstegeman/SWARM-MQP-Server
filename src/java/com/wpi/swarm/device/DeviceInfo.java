/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.device;

import com.mongodb.client.model.Indexes;
import com.wpi.swarm.device.DeviceType.ValueDef;
import com.wpi.swarm.device.DeviceType.ValueDef.ValueType;
import com.wpi.swarm.mongo.MCon;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.bson.Document;

/**
 *
 * @author jtste
 */
public class DeviceInfo {

    public static final int DEFAULT_KEY_LEN = 8;
    private static final SecureRandom rnd = new SecureRandom();

    private long id = 0;
    private long type = 0;
    private byte[] key = new byte[0];
    private String owner = "";
    private String name = "";
    private double latitude = 0;
    private double longitude = 0;
    private long lastActivity = System.currentTimeMillis();
    private final Map<String, Double> numbers = new HashMap<>();
    private final Map<String, String> strings = new HashMap<>();

    private boolean idChange = false;
    private boolean nameChange = false;
    private boolean typeChange = false;
    private boolean latLngChange = false;
    private boolean valChange = false;
    private boolean keyChange = false;
    private boolean ownerChanged = false;

    public long getId() {
        return id;
    }

    public DeviceInfo setId(long id) {
        if (this.id != id) {
            this.id = id;
            this.idChange = true;
        }
        return this;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public long getType() {
        return type;
    }

    public DeviceInfo setType(long type) {
        if (this.type != type) {
            this.type = type;
            this.typeChange = true;
        }
        return this;
    }

    public byte[] getKey() {
        return key;
    }

    public String getKeyString() {
        if (this.key == null) {
            this.key = getRandomKey();
        }
        return Base64.getUrlEncoder().encodeToString(key);
    }

    public DeviceInfo setKey(byte[] key) {
        if (!Arrays.equals(key, key)) {
            this.key = key;
            this.keyChange = true;
        }
        return this;
    }

    public DeviceInfo setKey(String key) {
        if (key == null) {
            return this;
        }
        try {
            return this.setKey(Base64.getUrlDecoder().decode(key));
        } catch (Exception e) {
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public DeviceInfo setName(String name) {
        if (name == null) {
            return this;
        }
        if (!Objects.equals(this.name, name)) {
            this.name = name;
            if (this.name == null) {
                this.name = "";
            }
            this.nameChange = true;
        }
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public DeviceInfo setOwner(String owner) {
        if (owner != null) {
            if (!Objects.equals(this.owner, owner)) {
                this.owner = owner;
                this.ownerChanged = true;
            }
        }
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public DeviceInfo setLatitude(double latitude) {
        if (this.latitude != latitude) {
            this.latitude = latitude;
            this.latLngChange = true;
        }
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public DeviceInfo setLongitude(double longitude) {
        if (this.longitude != longitude) {
            this.longitude = longitude;
            this.latLngChange = true;
        }
        return this;
    }

    public Map<String, Double> getNumbers() {
        return Collections.unmodifiableMap(numbers);
    }

    public Map<String, String> getStrings() {
        return Collections.unmodifiableMap(strings);
    }

    public DeviceInfo setNumber(String s, Double val) {
        if (s == null || val == null) {
            return this;
        }
        if (this.numbers.containsKey(s)) {
            if (!Objects.equals(this.numbers.get(s), val)) {
                this.numbers.put(s, val);
                this.valChange = true;
            }
        } else {
            this.numbers.put(s, val);
            this.valChange = true;
        }
        return this;
    }

    public DeviceInfo setString(String s, String val) {
        if (s == null || val == null) {
            return this;
        }
        if (this.strings.containsKey(s)) {
            if (!Objects.equals(this.strings.get(s), val)) {
                this.strings.put(s, val);
                this.valChange = true;
            }
        } else {
            this.strings.put(s, val);
            this.valChange = true;
        }
        return this;
    }

    public DeviceInfo clearChangeLog() {
        this.keyChange = false;
        this.valChange = false;
        this.nameChange = false;
        this.latLngChange = false;
        this.typeChange = false;
        this.ownerChanged = false;
        this.idChange = false;
        return this;
    }

    public boolean changed() {
        return this.keyChange
                || this.valChange
                || this.nameChange
                || this.latLngChange
                || this.typeChange
                || this.ownerChanged
                || this.idChange;
    }

    public boolean isNameChanged() {
        return nameChange;
    }

    public boolean isTypeChanged() {
        return typeChange;
    }

    public boolean isLatLngChanged() {
        return latLngChange;
    }

    public boolean isValChanged() {
        return valChange;
    }

    public boolean isKeyChanged() {
        return keyChange;
    }

    public static DeviceInfo makeDevInfo(TypeLoader loader, Document doc) {
        return makeDevInfo("_id", loader.getType(doc), doc);
    }

    static DeviceInfo makeDevInfo(String devIdName, TypeLoader loader, Document doc) {
        return makeDevInfo(devIdName, loader.getType(doc), doc);
    }

    public static DeviceInfo makeDevInfo(DeviceType type, Document doc) {
        return makeDevInfo("_id", type, doc);
    }

    static DeviceInfo makeDevInfo(String devIdName, DeviceType type, Document doc) {
        if (type == null || doc == null) {
            return null;
        }
        DeviceInfo info = new DeviceInfo();

        try {
            info.setId(doc.getLong(devIdName));
        } catch (Exception e) {
            return null;
        }
        try {
            info.setType(doc.getLong("type"));
        } catch (Exception e) {
        }
        try {
            info.lastActivity = doc.getLong("activity");
        } catch (Exception e) {
        }
        try {
            info.setLatitude(doc.getDouble("lat"));
        } catch (Exception e) {
        }
        try {
            info.setLongitude(doc.getDouble("lng"));
        } catch (Exception e) {
        }
        try {
            info.setName(doc.getString("name"));
        } catch (Exception e) {
        }
        try {
            info.setOwner(doc.getString("owner"));
        } catch (Exception e) {
        }
        try {
            info.setKey(doc.getString("key"));
        } catch (Exception e) {
        }

        Document vals = (Document) doc.get("nums");

        if (vals != null) {
            for (String s : vals.keySet()) {
                info.setNumber(s, vals.getDouble(s));
            }
        }

        vals = (Document) doc.get("strs");

        if (vals != null) {
            for (String s : vals.keySet()) {
                ValueDef def = type.getDef(s);
                if (def != null) {
                    if (def.getType() == ValueType.NUMBER) {
                        try {
                            info.setNumber(s, vals.getDouble(s));
                        } catch (Exception e) {
                            info.setNumber(s, 0.0);
                        }
                    } else if (def.getType() == ValueType.STRING) {
                        try {
                            info.setString(s, vals.getString(s));
                        } catch (Exception e) {
                            info.setString(s, "");
                        }
                    }

                }

            }
        }
        return info;
    }

    public static Document makeCreate(DeviceInfo info) {
        return DeviceInfo.makeCreate("_id", info);
    }

    public static Document makeFind(DeviceInfo info) {
        return DeviceInfo.makeFind("_id", info);
    }

    public static Document makeUpdate(DeviceInfo info) {
        return DeviceInfo.makeUpdate("_id", info);
    }

    static Document makeFind(String devIdName, DeviceInfo info) {
        if (info.key == null || info.id == 0) {
            return null;
        }
        Document doc = new Document();
        if (info.idChange) {
            doc.put(devIdName, info.id);
        }
        if (info.ownerChanged && info.owner != null && info.owner.length() > 0) {
            doc.put("owner", info.owner);
        }
        if (info.typeChange && info.type != 0) {
            doc.put("type", info.type);
        }
        if (info.nameChange && info.name != null) {
            doc.put("name", info.name);
        }
        if (info.latLngChange && info.latitude != 0) {
            doc.put("lat", info.latitude);
        }
        if (info.latLngChange && info.longitude != 0) {
            doc.put("lng", info.longitude);
        }
        if (info.keyChange) {
            doc.put("key", Base64.getUrlEncoder().encodeToString(info.key));
        }
        if (info.valChange) {
            if (!info.numbers.isEmpty()) {
                Document vals = new Document();
                for (Entry<String, Double> e : info.numbers.entrySet()) {
                    doc.put(e.getKey(), e.getValue());
                }
                doc.put("nums", vals);
            }
            if (!info.strings.isEmpty()) {
                Document vals = new Document();
                for (Entry<String, String> e : info.strings.entrySet()) {
                    doc.put(e.getKey(), e.getValue());
                }
                doc.put("strs", vals);
            }
        }
        return doc;
    }

    static Document makeUpdate(String devIdName, DeviceInfo info) {
        if (info.key == null || info.id == 0) {
            return null;
        }
        Document doc = new Document();
        if (info.idChange) {
            doc.put(devIdName, info.id);
        }
        if (info.ownerChanged && info.owner != null && info.owner.length() > 0) {
            doc.put("owner", info.owner);
        }
        if (info.typeChange && info.type != 0) {
            doc.put("type", info.type);
        }
        if (info.nameChange && info.name != null) {
            doc.put("name", info.name);
        }
        if (info.latLngChange && info.latitude != 0) {
            doc.put("lat", info.latitude);
        }
        if (info.latLngChange && info.longitude != 0) {
            doc.put("lng", info.longitude);
        }
        if (info.keyChange) {
            doc.put("key", Base64.getUrlEncoder().encodeToString(info.key));
        }
        if (info.valChange) {
            if (!info.numbers.isEmpty()) {
                Document vals = new Document();
                for (Entry<String, Double> e : info.numbers.entrySet()) {
                    doc.put(e.getKey(), e.getValue());
                }
                doc.put("nums", vals);
            }
            if (!info.strings.isEmpty()) {
                Document vals = new Document();
                for (Entry<String, String> e : info.strings.entrySet()) {
                    doc.put(e.getKey(), e.getValue());
                }
                doc.put("strs", vals);
            }
        }
        info.lastActivity = System.currentTimeMillis();
        doc.put("activity", info.lastActivity);
        return doc;
    }

    static Document makeCreate(String devIdName, DeviceInfo info) {
        if (info.key == null || info.id == 0) {
            return null;
        }
        Document doc = new Document();
        doc.put(devIdName, info.id);

        if (info.owner != null && info.owner.length() > 0) {
            doc.put("owner", info.owner);
        }
        if (info.type != 0) {
            doc.put("type", info.type);
        }
        if (info.name != null) {
            doc.put("name", info.name);
        }
        if (info.latitude != 0) {
            doc.put("lat", info.latitude);
        }
        if (info.longitude != 0) {
            doc.put("lng", info.longitude);
        }
        doc.put("key", Base64.getUrlEncoder().encodeToString(info.key));
        if (!info.numbers.isEmpty()) {
            Document vals = new Document();
            for (Entry<String, Double> e : info.numbers.entrySet()) {
                doc.put(e.getKey(), e.getValue());
            }
            doc.put("nums", vals);
        }
        if (!info.strings.isEmpty()) {
            Document vals = new Document();
            for (Entry<String, String> e : info.strings.entrySet()) {
                doc.put(e.getKey(), e.getValue());
            }
            doc.put("strs", vals);
        }
        info.lastActivity = System.currentTimeMillis();
        doc.put("activity", info.lastActivity);
        return doc;
    }

    public static final byte[] getRandomKey(int length) {
        byte[] next = new byte[length];
        rnd.nextBytes(next);
        return next;
    }

    public static final byte[] getRandomKey() {
        return getRandomKey(DEFAULT_KEY_LEN);
    }

    public static void enforceIndex(MCon m) {
        m.getCollection(DeviceController.COLLECTION).createIndex(Indexes.hashed("owner"));
        m.getCollection(DeviceController.COLLECTION).createIndex(Indexes.hashed("key"));
    }

    public static JsonObjectBuilder toJson(DeviceInfo info) {
        JsonObjectBuilder obj = Json.createObjectBuilder();
        obj.add("id", info.getId());
        obj.add("key", info.getKeyString());
        obj.add("owner", info.getOwner());
        obj.add("name", info.getName());
        obj.add("type", info.getType());
        obj.add("lat", info.getLatitude());
        obj.add("lng", info.getLongitude());
        JsonObjectBuilder nums = Json.createObjectBuilder();
        for (Entry<String, Double> e : info.getNumbers().entrySet()) {
            nums.add(e.getKey(), e.getValue());
        }
        obj.add("nums", nums);
        JsonObjectBuilder strs = Json.createObjectBuilder();
        for (Entry<String, String> e : info.getStrings().entrySet()) {
            strs.add(e.getKey(), e.getValue());
        }
        obj.add("nums", strs);
        return obj;
    }
}
