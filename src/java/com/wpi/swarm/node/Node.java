/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.node;

import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.bson.Document;
import org.bson.types.Binary;

/**
 *
 * @author jtste
 */
public class Node {

    public static final int STATE_NOT_DEPLOYED = 1;
    public static final int STATE_DEPLOYED = 2;
    public static final int STATE_DECOMISSIONED = 0;

    public long id = 0;
    public String name = null;
    public String owner = null;
    public double latitude = Double.NaN;
    public double longitude = Double.NaN;
    public byte[] key = null;
    public int state = -1;
    public long time = 0;

    public double airQuality = Double.NaN;
    public double humidity = Double.NaN;
    public double temperature = Double.NaN;
    public double uv = Double.NaN;
    public double ir = Double.NaN;
    public double visible = Double.NaN;
    public double battery = Double.NaN;

    public static Document buildDoc(Node node) {
        if (node == null) {
            return null;
        }
        Document doc = new Document();
        if (node.id != 0) {
            doc.put("_id", node.id);
        }
        if (node.time != 0) {
            doc.put("time", node.time);
        }
        if (node.name != null) {
            doc.put("name", node.name);
        }
        if (node.owner != null) {
            doc.put("owner", node.owner);
        }
        if (node.state > -1) {
            doc.put("state", node.state);
        }
        if (node.key != null) {
            doc.put("key", Node.bytesToHex(Arrays.copyOf(node.key, 8)));
        }
        if (Double.isFinite(node.latitude)) {
            doc.put("lat", node.latitude);
        }
        if (Double.isFinite(node.longitude)) {
            doc.put("lng", node.longitude);
        }

        if (Double.isFinite(node.airQuality)) {
            doc.put("aQ", node.airQuality);
        }
        if (Double.isFinite(node.temperature)) {
            doc.put("temp", node.temperature);
        }
        if (Double.isFinite(node.humidity)) {
            doc.put("humid", node.humidity);
        }
        if (Double.isFinite(node.uv)) {
            doc.put("uv", node.uv);
        }
        if (Double.isFinite(node.ir)) {
            doc.put("ir", node.ir);
        }
        if (Double.isFinite(node.visible)) {
            doc.put("visible", node.visible);
        }
        if (Double.isFinite(node.battery)) {
            doc.put("bat", node.battery);
        }
        return doc;
    }

    public static Node buildNode(Document doc) {
        if (doc == null) {
            return null;
        }
        Node node = new Node();
        try {
            if (doc.containsKey("dId")) {
                node.id = doc.getLong("dId");
            } else if (doc.containsKey("_id")) {
                node.id = doc.getLong("_id");
            }
            if (doc.containsKey("time")) {
                node.time = doc.getLong("time");
            }
            if (doc.containsKey("name")) {
                node.name = doc.getString("name");
            }
            if (doc.containsKey("owner")) {
                node.owner = doc.getString("owner");
            }
            if (doc.containsKey("state")) {
                node.state = doc.getInteger("state");
            }
            if (doc.containsKey("key")) {
                node.key = NodeController.getKey(doc.getString("key"));
            }
            if (doc.containsKey("lat")) {
                node.latitude = doc.getDouble("lat");
            }
            if (doc.containsKey("lng")) {
                node.longitude = doc.getDouble("lng");
            }

            if (doc.containsKey("aQ")) {
                node.airQuality = doc.getDouble("aQ");
            }
            if (doc.containsKey("temp")) {
                node.temperature = doc.getDouble("temp");
            }
            if (doc.containsKey("humid")) {
                node.humidity = doc.getDouble("humid");
            }
            if (doc.containsKey("uv")) {
                node.uv = doc.getDouble("uv");
            }
            if (doc.containsKey("ir")) {
                node.ir = doc.getDouble("ir");
            }
            if (doc.containsKey("visible")) {
                node.visible = doc.getDouble("visible");
            }
            if (doc.containsKey("bat")) {
                node.battery = doc.getDouble("bat");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return node;
    }

    public static JsonObjectBuilder toJson(Node node) {
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

        if (Double.isFinite(node.airQuality)) {
            json.add("air", node.airQuality);
        }
        if (Double.isFinite(node.temperature)) {
            json.add("temperature", node.temperature);
        }
        if (Double.isFinite(node.humidity)) {
            json.add("humidity", node.humidity);
        }
        if (Double.isFinite(node.uv)) {
            json.add("uv", node.uv);
        }
        if (Double.isFinite(node.ir)) {
            json.add("ir", node.ir);
        }
        if (Double.isFinite(node.visible)) {
            json.add("visible", node.visible);
        }
        if (Double.isFinite(node.battery)) {
            json.add("battery", node.battery);
        }
        return json;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[(v >> 4) & 0xF];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public String toString() {
        return "Node{" + "id=" + id + ", name=" + name + ", owner=" + owner + ", latitude=" + latitude + ", longitude=" + longitude + ", key=" + Arrays.toString(key) + ":" + bytesToHex(key) + ", state=" + state + ", time=" + time + ", airQuality=" + airQuality + ", humidity=" + humidity + ", temperature=" + temperature + ", uv=" + uv + ", ir=" + ir + ", visible=" + visible + ", battery=" + battery + '}';
    }

}
