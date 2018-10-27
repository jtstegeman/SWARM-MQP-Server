/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.rover;

import com.wpi.swarm.node.*;
import static com.wpi.swarm.node.Node.bytesToHex;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author jtste
 */
public class Rover {

    public long id = 0;
    public String name = null;
    public String owner = null;
    public double latitude = Double.NaN;
    public double longitude = Double.NaN;
    public byte[] key = null;
    public int state = -1;
    public long time = 0;
    public ObjectId currentNextStep = null;

    public static Document buildDoc(Rover rover) {
        if (rover == null) {
            return null;
        }
        Document doc = new Document();
        if (rover.id != 0) {
            doc.put("_id", rover.id);
        }
        if (rover.time != 0) {
            doc.put("time", rover.time);
        }
        if (rover.name != null) {
            doc.put("name", rover.name);
        }
        if (rover.owner != null) {
            doc.put("owner", rover.owner);
        }
        if (rover.state > -1) {
            doc.put("state", rover.state);
        }
        if (rover.key != null) {
            doc.put("key", bytesToHex(Arrays.copyOf(rover.key, 8)));
        }
        if (Double.isFinite(rover.latitude)) {
            doc.put("lat", rover.latitude);
        }
        if (Double.isFinite(rover.longitude)) {
            doc.put("lng", rover.longitude);
        }
        if (rover.currentNextStep != null) {
            doc.put("next", rover.currentNextStep);
        }
        return doc;
    }

    public static Rover buildRover(Document doc) {
        if (doc == null) {
            return null;
        }
        Rover rover = new Rover();
        try {
            if (doc.containsKey("_id")) {
                rover.id = doc.getLong("_id");
            } else if (doc.containsKey("rId")) {
                rover.id = doc.getLong("rId");
            }
            if (doc.containsKey("time")) {
                rover.time = doc.getLong("time");
            }
            if (doc.containsKey("name")) {
                rover.name = doc.getString("name");
            }
            if (doc.containsKey("owner")) {
                rover.owner = doc.getString("owner");
            }
            if (doc.containsKey("state")) {
                rover.state = doc.getInteger("state");
            }
            if (doc.containsKey("key")) {
                rover.key = NodeController.getKey(doc.getString("key"));
            }
            if (doc.containsKey("lat")) {
                rover.latitude = doc.getDouble("lat");
            }
            if (doc.containsKey("lng")) {
                rover.longitude = doc.getDouble("lng");
            }
            if (doc.containsKey("next")) {
                rover.currentNextStep = doc.getObjectId("next");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return rover;
    }

    public static JsonObjectBuilder toJson(Rover node) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        if (node.id != 0) {
            json.add("id", node.id);
        }
        if (node.time != 0) {
            json.add("time", node.time);
        }
        if (node.name != null) {
            json.add("name", node.name);
        }
        if (node.owner != null) {
            json.add("owner", node.owner);
        }
        if (node.state > -1) {
            json.add("state", node.state);
        }
        if (node.key != null) {
            json.add("key", bytesToHex(Arrays.copyOf(node.key, 8)));
        }
        if (Double.isFinite(node.latitude)) {
            json.add("latitude", node.latitude);
        }
        if (Double.isFinite(node.longitude)) {
            json.add("longitude", node.longitude);
        }
        if (node.currentNextStep != null) {
            json.add("next", node.currentNextStep.toHexString());
        }
        return json;
    }

}
