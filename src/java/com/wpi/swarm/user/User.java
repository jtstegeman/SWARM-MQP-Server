/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.user;

import com.mongodb.client.MongoCursor;
import com.wpi.swarm.mongo.MCon;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.bson.Document;

/**
 *
 * @author jtste
 */
public class User {

    public static final String COLLECTION = "users";

    private static final SecureRandom rnd = new SecureRandom();
    protected String username = null;
    protected String tempAuth = null;
    protected String hashedPassword = null;
    protected long exp = 0;
    protected String recovery = null;

    public User(String username) {
        this.username = username;
    }

    public static String resetPassword(MCon con, String user, String recovery) {
        if (user == null || recovery == null) {
            return null;
        }
        String hR = hashPWD(recovery, user);
        if (hR == null) {
            return null;
        }
        Document d = new Document("_id", user.replace(".", "_")).append("rec", hR);
        MongoCursor<Document> it = con.getCollection(COLLECTION).find(d).iterator();
        if (it.hasNext()) {
            Document up = new Document();
            String pwd = newTemp();
            String hP = hashPWD(pwd, user);
            if (hP == null) {
                return null;
            }
            up.put("pass", hP);
            if (con.getCollection(COLLECTION).updateOne(d, new Document("$set", up)).getModifiedCount() != 0) {
                return pwd;
            }
        }
        return null;
    }

    public static String changePassword(MCon con, String user, String password, String newPass) {
        if (user == null || password == null || newPass == null) {
            return null;
        }
        String hP = hashPWD(password, user);
        if (hP == null) {
            return null;
        }
        Document d = new Document("_id", user.replace(".", "_")).append("rec", hP);
        MongoCursor<Document> it = con.getCollection(COLLECTION).find(d).iterator();
        if (it.hasNext()) {
            Document up = new Document();
            hP = hashPWD(newPass, user);
            if (hP == null) {
                return null;
            }
            up.put("pass", hP);
            if (con.getCollection(COLLECTION).updateOne(d, new Document("$set", up)).getModifiedCount() != 0) {
                return newPass;
            }
        }
        return null;
    }

    public static boolean createUser(MCon con, String user, String password, String recovery) {
        if (user == null || password == null || recovery == null) {
            return false;
        }
        String hP = hashPWD(password, user);
        String hR = hashPWD(recovery, user);
        if (hP == null || hR == null) {
            return false;
        }
        try {
            Document d = new Document("_id", user.replace(".", "_")).append("temp", "").append("exp", 0L).append("pass", hP).append("rec", hR);
            con.getCollection(COLLECTION).insertOne(d);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static String authUserTemp(MCon con, String user, String tempKey) {
        if (user == null || tempKey == null) {
            return null;
        }
        Document d = new Document("_id", user.replace(".", "_")).append("temp", tempKey);
        MongoCursor<Document> it = con.getCollection(COLLECTION).find(d).iterator();
        if (it.hasNext()) {
            Document u = it.next();
            Long exp = u.getLong("exp");
            if (exp != null && exp.longValue() > System.currentTimeMillis()) {
                String temp = u.getString("temp");
                if (temp == null || exp.longValue() < System.currentTimeMillis() + 1800000) { // key expired
                    Document up = new Document();
                    temp = newTemp();
                    up.put("exp", System.currentTimeMillis() + 86400000);
                    up.put("temp", temp);
                    if (con.getCollection(COLLECTION).updateOne(d, new Document("$set", up)).getModifiedCount() != 0) {
                        return temp;
                    }
                } else {
                    return temp;
                }
            }
        }
        return null;
    }

    public static String authUserPwd(MCon con, String user, String password) {
        if (user == null || password == null) {
            return null;
        }
        String hP = hashPWD(password, user);
        if (hP == null) {
            return null;
        }
        Document d = new Document("_id", user.replace(".", "_")).append("pass", hP);
        MongoCursor<Document> it = con.getCollection(COLLECTION).find(d).iterator();
        if (it.hasNext()) {
            Document u = it.next();
            Long exp = u.getLong("exp");
            String temp = u.getString("temp");
            if (exp == null || temp == null || exp.longValue() < System.currentTimeMillis() + 1800000) { // key expired
                Document up = new Document();
                temp = newTemp();
                up.put("exp", System.currentTimeMillis() + 86400000);
                up.put("temp", temp);
                if (con.getCollection(COLLECTION).updateOne(d, new Document("$set", up)).getModifiedCount() != 0) {
                    return temp;
                }
            } else {
                return temp;
            }
        }
        return null;
    }

    private static String hashPWD(String pwd, String salt) {
        if (pwd == null || salt == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((pwd + salt).getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException ex) {
            return Base64.getUrlEncoder().encodeToString(pwd.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String newTemp() {
        byte[] tk = new byte[32];
        rnd.nextBytes(tk);
        return Base64.getUrlEncoder().encodeToString(tk);
    }
}
