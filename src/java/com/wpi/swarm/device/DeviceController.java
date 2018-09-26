/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.device;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.wpi.swarm.mongo.MCon;
import com.wpi.swarm.mongo.MCounter;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author jtste
 */
public class DeviceController {

    public static final String COUNTER_NAME = "deviceCounter";
    public static final String COLLECTION = "devs";
    public static final String DEV_LOG_COLLECTION = "devLog";

    protected final MCon con;

    public DeviceController(MCon con) {
        this.con = con;
    }

    public DeviceController() {
        this(new MCon());
    }

    public MCon getConnection() {
        return con;
    }

    public DeviceInfo createDevice(String owner, String name, long type, byte[] key, double lat, double lng) {
        try {
            MCounter dCounter = new MCounter(COUNTER_NAME);
            long id = dCounter.nextCount();
            DeviceInfo inf = new DeviceInfo();
            inf.setId(id).setOwner(owner).setName(name).setType(type).setKey(key).setLatitude(lat).setLongitude(lng);
            Document d = DeviceInfo.makeCreate(inf);
            if (d != null) {
                DeviceInfo.enforceIndex(con);
                con.getCollection(COLLECTION).insertOne(d);
                DeviceLogEntry.enforceIndex(con);
                Document lg = DeviceLogEntry.makeInsertDoc(new DeviceLogEntry(id, inf, "Device created"));
                if (lg != null) {
                    con.getCollection(DEV_LOG_COLLECTION).insertOne(lg);
                }
                return inf;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public DeviceInfo createDevice(String owner, String name, long type, String key, double lat, double lng) {
        try {
            MCounter dCounter = new MCounter(COUNTER_NAME);
            long id = dCounter.nextCount();
            DeviceInfo inf = new DeviceInfo();
            inf.setId(id).setOwner(owner).setName(name).setType(type).setKey(key).setLatitude(lat).setLongitude(lng);
            Document d = DeviceInfo.makeCreate(inf);
            if (d != null) {
                DeviceInfo.enforceIndex(con);
                con.getCollection(COLLECTION).insertOne(d);
                DeviceLogEntry.enforceIndex(con);
                Document lg = DeviceLogEntry.makeInsertDoc(new DeviceLogEntry(id, "Device created"));
                if (lg != null) {
                    con.getCollection(DEV_LOG_COLLECTION).insertOne(lg);
                }
                return inf;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean deleteDevice(long id, String key) {
        DeviceInfo inf = new DeviceInfo();
        inf.setId(id).setKey(key);
        Document d = DeviceInfo.makeFind(inf);
        if (d != null) {
            try {
                DeviceInfo.enforceIndex(con);
                DeleteResult deleteOne = con.getCollection(COLLECTION).deleteOne(d);
                if (deleteOne.getDeletedCount() != 0) {
                    DeviceLogEntry.enforceIndex(con);
                    Document lg = DeviceLogEntry.makeInsertDoc(new DeviceLogEntry(id, "Device deleted"));
                    if (lg != null) {
                        con.getCollection(DEV_LOG_COLLECTION).insertOne(lg);
                    }
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public boolean deleteDevice(long id, byte[] key) {
        DeviceInfo inf = new DeviceInfo();
        inf.setId(id).setKey(key);
        Document d = DeviceInfo.makeFind(inf);
        if (d != null) {
            try {
                DeviceInfo.enforceIndex(con);
                DeleteResult deleteOne = con.getCollection(COLLECTION).deleteOne(d);
                if (deleteOne.getDeletedCount() != 0) {
                    DeviceLogEntry.enforceIndex(con);
                    Document lg = DeviceLogEntry.makeInsertDoc(new DeviceLogEntry(id, "Device deleted"));
                    if (lg != null) {
                        con.getCollection(DEV_LOG_COLLECTION).insertOne(lg);
                    }
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public boolean deleteDevice(DeviceInfo inf) {
        return this.deleteDevice(inf.getId(), inf.getKey());
    }

    public DeviceInfo getLatestDevice(long id, String key) {
        DeviceInfo inf = new DeviceInfo();
        inf.setId(id).setKey(key);
        Document d = DeviceInfo.makeFind(inf);
        if (d != null) {
            try {
                DeviceInfo.enforceIndex(con);
                MongoCursor<Document> it = con.getCollection(COLLECTION).find(d).iterator();
                TypeLoader loader = new TypeLoader(con);
                if (it.hasNext()) {
                    return DeviceInfo.makeDevInfo(loader, it.next());
                }
            } catch (Exception e) {
                return null;
            }
            return inf;
        }
        return null;
    }

    public DeviceInfo getLatestDevice(long id, byte[] key) {
        DeviceInfo inf = new DeviceInfo();
        inf.setId(id).setKey(key);
        Document d = DeviceInfo.makeFind(inf);
        if (d != null) {
            try {
                DeviceInfo.enforceIndex(con);
                MongoCursor<Document> it = con.getCollection(COLLECTION).find(d).iterator();
                TypeLoader loader = new TypeLoader(con);
                if (it.hasNext()) {
                    return DeviceInfo.makeDevInfo(loader, it.next());
                }
            } catch (Exception e) {
                return null;
            }
            return inf;
        }
        return null;
    }

    public DeviceInfo getLatestDevice(DeviceInfo inf) {
        return this.getLatestDevice(inf.getId(), inf.getKey());
    }

    public boolean updateDevice(long id, byte[] key, DeviceInfo inf) {
        DeviceInfo q = new DeviceInfo().setId(id).setKey(key);
        Document d = DeviceInfo.makeFind(q);
        Document u = DeviceInfo.makeUpdate(inf);
        if (d != null && u != null) {
            try {
                DeviceInfo.enforceIndex(con);
                if (con.getCollection(COLLECTION).updateOne(d, new Document("$set", u), new UpdateOptions().upsert(true)).getMatchedCount() != 0) {
                    DeviceLogEntry.enforceIndex(con);
                    Document lg = DeviceLogEntry.makeInsertDoc(new DeviceLogEntry(id, inf));
                    if (lg != null) {
                        con.getCollection(DEV_LOG_COLLECTION).insertOne(lg);
                    }
                    return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean updateDevice(long id, String key, DeviceInfo inf) {
        DeviceInfo q = new DeviceInfo().setId(id).setKey(key);
        Document d = DeviceInfo.makeFind(q);
        Document u = DeviceInfo.makeUpdate(inf);
        if (d != null && u != null) {
            try {
                DeviceInfo.enforceIndex(con);
                if (con.getCollection(COLLECTION).updateOne(d, new Document("$set", u), new UpdateOptions().upsert(true)).getMatchedCount() != 0) {
                    DeviceLogEntry.enforceIndex(con);
                    Document lg = DeviceLogEntry.makeInsertDoc(new DeviceLogEntry(id, inf));
                    if (lg != null) {
                        con.getCollection(DEV_LOG_COLLECTION).insertOne(lg);
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean updateDevice(DeviceInfo inf) {
        if (inf.getId() == 0) {
            return false;
        }
        Document d = DeviceInfo.makeFind(inf);
        Document u = DeviceInfo.makeUpdate(inf);
        if (d != null && u != null) {
            try {
                DeviceInfo.enforceIndex(con);
                if (con.getCollection(COLLECTION).updateOne(d, new Document("$set", u), new UpdateOptions().upsert(true)).getMatchedCount() != 0) {
                    DeviceLogEntry.enforceIndex(con);
                    Document lg = DeviceLogEntry.makeInsertDoc(new DeviceLogEntry(inf.getId(), inf));
                    if (lg != null) {
                        con.getCollection(DEV_LOG_COLLECTION).insertOne(lg);
                    }
                    return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    public List<DeviceInfo> getOwnerDevices(String owner) {
        return getOwnerDevicesOfType(owner, 0);
    }

    public List<DeviceInfo> getOwnerDevicesOfType(String owner, long type) {
        if (owner == null) {
            return null;
        }
        List<DeviceInfo> dil = new ArrayList<>();
        Document d = new Document("owner", owner);
        if (type != 0) {
            d.append("type", type);
        }
        if (d != null) {
            try {
                DeviceInfo.enforceIndex(con);
                MongoCursor<Document> it = con.getCollection(COLLECTION).find(d).iterator();
                TypeLoader loader = new TypeLoader(con);
                while (it.hasNext()) {
                    DeviceInfo i = DeviceInfo.makeDevInfo(loader, it.next());
                    if (i != null) {
                        dil.add(i);
                    }
                }
            } catch (Exception e) {
            }
        }
        return dil;
    }
}
