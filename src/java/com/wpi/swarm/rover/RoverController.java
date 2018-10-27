/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.rover;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Indexes;
import com.wpi.swarm.mongo.MCon;
import com.wpi.swarm.mongo.MCounter;
import static com.wpi.swarm.node.Node.bytesToHex;
import static com.wpi.swarm.node.NodeController.getKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author jtste
 */
public class RoverController {

    protected final MCon con;

    static {
        MCon c = new MCon();
        c.getCollection("rovers").createIndex(Indexes.ascending("owner"));
        c.getCollection("rover_log").createIndex(Indexes.ascending("rId"));
        c.getCollection("rover_log").createIndex(Indexes.ascending("time"));
    }

    public RoverController(MCon con) {
        this.con = con;
    }

    public RoverController() {
        this(new MCon());
    }

    private boolean logState(Rover node) {
        if (node == null || node.id == 0 || node.key == null) {
            return false;
        }
        node.time = System.currentTimeMillis();
        Document d = Rover.buildDoc(node);
        if (d != null) {
            try {
                d.put("rId", node.id);
                d.remove("_id");
                con.getCollection("rover_log").insertOne(d);
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public Rover createRover(String owner, String name, double lat, double lng) {
        try {
            MCounter dCounter = new MCounter("dev_ids");
            long id = dCounter.nextCount();
            Rover node = new Rover();
            node.id = id;
            node.name = name;
            node.owner = owner;
            node.key = new byte[8];
            SecureRandom r = new SecureRandom();
            r.nextBytes(node.key);
            node.latitude = lat;
            node.longitude = lng;
            node.currentNextStep = null;
            node.state = 0;
            Document d = Rover.buildDoc(node);
            if (d != null) {
                con.getCollection("rovers").insertOne(d);
                logState(node);
                return node;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean deleteRover(long id, String key) {
        return this.deleteRover(id, getKey(key));
    }

    public boolean deleteRover(long id, byte[] key) {
        if (key == null) {
            return false;
        }
        try {
            return con.getCollection("rovers").deleteOne(new Document("_id", id).append("key", bytesToHex(key))).getDeletedCount() != 0;
        } catch (Exception e) {
        }
        return false;
    }

    public Rover getRover(long id, String key) {
        return this.getRover(id, getKey(key));
    }

    public Rover getRover(long id, byte[] key) {
        if (key == null) {
            return null;
        }
        try {
            return Rover.buildRover(con.getCollection("rovers").find(new Document("_id", id).append("key", bytesToHex(key))).first());
        } catch (Exception e) {
        }
        return null;
    }

    public List<Rover> getRoversForOwner(String owner) {
        if (owner == null) {
            return new ArrayList<>();
        }
        try {
            List<Rover> rovers = new ArrayList<>();
            MongoCursor<Document> it = con.getCollection("rovers").find(new Document("owner", owner)).iterator();
            try {
                while (it.hasNext()) {
                    Rover n = Rover.buildRover(it.next());
                    if (n != null) {
                        rovers.add(n);
                    }
                }
            } catch (Exception ex) {
            }
            it.close();
            return rovers;
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    public boolean updateRover(long id, String key, Rover state) {
        return this.updateRover(id, getKey(key), state);
    }

    public boolean updateRover(long id, byte[] key, Rover state) {
        if (key == null || state == null) {
            return false;
        }
        try {
            Document up = Rover.buildDoc(state);
            if (up == null) {
                return false;
            }
            up = new Document("$set", up);
            boolean r = con.getCollection("rovers").updateOne(new Document("_id", id).append("key", bytesToHex(key)), up).getMatchedCount() != 0;
            if (r) {
                state.id = id;
                state.key = key;
                logState(state);
            }
            return r;
        } catch (Exception e) {
        }
        return false;
    }

    public List<Rover> getRoverDataInRange(long id, String key, long after, long before) {
        return this.getRoverDataInRange(id, getKey(key), after, before);
    }

    public List<Rover> getRoverDataInRange(long id, byte[] key, long after, long before) {
        if (key == null) {
            return new ArrayList<>();
        }
        try {
            List<Rover> rovers = new ArrayList<>();
            Document search = new Document("rId", id).append("key", bytesToHex(key));
            Document ts = new Document();
            if (after != 0) {
                ts.append("$gte", after);
            }
            if (before != 0) {
                ts.append("$lte", before);
            }
            if (!ts.isEmpty()) {
                search.append("time", ts);
            }
            MongoCursor<Document> it = con.getCollection("rover_log").find(search).iterator();
            try {
                while (it.hasNext()) {
                    Rover n = Rover.buildRover(it.next());
                    if (n != null) {
                        rovers.add(n);
                    }
                }
            } catch (Exception ex) {
            }
            it.close();
            return rovers;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
