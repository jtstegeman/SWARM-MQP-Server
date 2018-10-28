/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.rover;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author jtste
 */
public class RoverCmd {

    public ObjectId id = null;
    public long roverId = 0;
    public double latitude = Double.NaN;
    public double longitude = Double.NaN;
    public int cmd = -1;

    public static Document buildDocument(RoverCmd cmd) {
        if (cmd == null) {
            return null;
        }
        Document doc = new Document();
        if (cmd.id != null) {
            doc.append("_id", cmd.id);
        }
        if (cmd.roverId != 0) {
            doc.append("rId", cmd.roverId);
        }
        if (Double.isFinite(cmd.latitude)) {
            doc.append("lat", cmd.latitude);
        }
        if (Double.isFinite(cmd.longitude)) {
            doc.append("lng", cmd.longitude);
        }
        if (cmd.cmd > -1) {
            doc.append("cmd", cmd.cmd);
        }
        return doc;
    }

    public static RoverCmd buildCmd(Document doc) {
        if (doc == null) {
            return null;
        }
        RoverCmd cmd = new RoverCmd();
        if (doc.containsKey("_id")) {
            cmd.id = doc.getObjectId("_id");
        }
        if (doc.containsKey("rId")) {
            cmd.roverId = doc.getLong("rId");
        }
        if (doc.containsKey("cmd")) {
            cmd.cmd = doc.getInteger("cmd");
        }
        if (doc.containsKey("lat")) {
            cmd.latitude = doc.getDouble("lat");
        }
        if (doc.containsKey("lng")) {
            cmd.longitude = doc.getDouble("lng");
        }
        return cmd;
    }
    
    public static JsonObjectBuilder toJson(RoverCmd cmd) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("id", cmd.id.toHexString());
        json.add("latitude", cmd.latitude);
        json.add("longitude", cmd.longitude);
        json.add("cmd", cmd.cmd);
        json.add("rover", cmd.roverId);
        return json;
    }
}
