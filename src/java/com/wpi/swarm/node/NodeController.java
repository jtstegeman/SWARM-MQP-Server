/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.node;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Indexes;
import com.wpi.swarm.mongo.MCon;
import com.wpi.swarm.mongo.MCounter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import static jdk.nashorn.internal.runtime.Debug.id;
import org.bson.Document;
import org.bson.types.Binary;

/**
 *
 * @author jtste
 */
public class NodeController {

    protected final MCon con;

    static {
        MCon c = new MCon();
        c.getCollection("nodes").createIndex(Indexes.ascending("owner"));
        c.getCollection("node_log").createIndex(Indexes.ascending("dId"));
        c.getCollection("node_log").createIndex(Indexes.ascending("time"));
    }

    public NodeController(MCon con) {
        this.con = con;
    }

    public NodeController() {
        this(new MCon());
    }

    public static byte[] getKey(String key) {
        if (key != null) {
            try {
                char[] chars = key.toCharArray();
                byte[] k = new byte[chars.length % 2 + chars.length / 2];
                for (int i = 0; i < chars.length; i++) {
                    k[i / 2] |= (byte) ((Character.digit(chars[i], 16) & 0xF) << (4 * (1 - (i % 2))));
                }
                return k;
            } catch (Exception e) {
            }
        }
        return new byte[0];
    }

    private boolean logState(Node node) {
        if (node == null || node.id == 0 || node.key == null) {
            return false;
        }
        node.time = System.currentTimeMillis();
        Document d = Node.buildDoc(node);
        if (d != null) {
            try {
                d.put("dId", node.id);
                d.remove("_id");
                con.getCollection("node_log").insertOne(d);
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public Node createNode(String owner, String name, double lat, double lng) {
        try {
            MCounter dCounter = new MCounter("dev_ids");
            long id = dCounter.nextCount();
            Node node = new Node();
            node.id = id;
            node.name = name;
            node.owner = owner;
            node.key = new byte[8];
            SecureRandom r = new SecureRandom();
            r.nextBytes(node.key);
            node.latitude = lat;
            node.longitude = lng;
            node.airQuality = 0;
            node.battery = 0;
            node.humidity = 0;
            node.ir = 0;
            node.temperature = 0;
            node.uv = 0;
            node.visible = 0;
            node.state = Node.STATE_NOT_DEPLOYED;
            Document d = Node.buildDoc(node);
            if (d != null) {
                con.getCollection("nodes").insertOne(d);
                logState(node);
                return node;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean deleteNode(long id, String key) {
        return this.deleteNode(id, getKey(key));
    }

    public boolean deleteNode(long id, byte[] key) {
        if (key == null) {
            return false;
        }
        try {
            return con.getCollection("nodes").deleteOne(new Document("_id", id).append("key", Node.bytesToHex(key))).getDeletedCount() != 0;
        } catch (Exception e) {
        }
        return false;
    }

    public Node getNode(long id, String key) {
        return this.getNode(id, getKey(key));
    }

    public Node getNode(long id, byte[] key) {
        if (key == null) {
            return null;
        }
        try {
            return Node.buildNode(con.getCollection("nodes").find(new Document("_id", id).append("key", Node.bytesToHex(key))).first());
        } catch (Exception e) {
        }
        return null;
    }

    public List<Node> getNodesForOwner(String owner) {
        if (owner == null) {
            return new ArrayList<>();
        }
        try {
            List<Node> nodes = new ArrayList<>();
            MongoCursor<Document> it = con.getCollection("nodes").find(new Document("owner", owner)).iterator();
            try {
                while (it.hasNext()) {
                    Node n = Node.buildNode(it.next());
                    if (n != null) {
                        nodes.add(n);
                    }
                }
            } catch (Exception ex) {
            }
            it.close();
            return nodes;
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    public boolean updateNode(long id, String key, Node state) {
        return this.updateNode(id, getKey(key), state);
    }

    public boolean updateNode(long id, byte[] key, Node state) {
        if (key == null || state == null) {
            return false;
        }
        try {
            Document up = Node.buildDoc(state);
            if (up == null) {
                return false;
            }
            up = new Document("$set", up);
            boolean r = con.getCollection("nodes").updateOne(new Document("_id", id).append("key", Node.bytesToHex(key)), up).getMatchedCount() != 0;
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

    public List<Node> getNodeDataInRange(long id, String key, long after, long before) {
        return this.getNodeDataInRange(id, getKey(key), after, before);
    }

    public List<Node> getNodeDataInRange(long id, byte[] key, long after, long before) {
        if (key == null) {
            return new ArrayList<>();
        }
        try {
            List<Node> nodes = new ArrayList<>();
            Document search = new Document("dId", id).append("key", Node.bytesToHex(key));
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
            MongoCursor<Document> it = con.getCollection("node_log").find(search).iterator();
            try {
                while (it.hasNext()) {
                    Node n = Node.buildNode(it.next());
                    if (n != null) {
                        nodes.add(n);
                    }
                }
            } catch (Exception ex) {
            }
            it.close();
            return nodes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
