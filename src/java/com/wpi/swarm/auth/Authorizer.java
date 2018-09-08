/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.auth;

import com.wpi.swarm.device.DeviceController;
import com.wpi.swarm.mongo.MCon;
import com.wpi.swarm.user.User;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jtste
 */
public class Authorizer {

    public static boolean authorize(MCon con, HttpServletRequest req) {
        if (Authorizer.authorizeDevice(con, req))
            return true;
        return Authorizer.authorizeUser(con, req);
    }

    public static boolean authorizeUser(MCon con, HttpServletRequest req) {
        Cookie[] cks = req.getCookies();
        if (cks != null) {
            for (Cookie c : cks) {
                if ("swarmUser".equals(c.getName())) {
                    String[] r = c.getValue().split("\\.");
                    if (r != null && r.length == 2) {
                        return User.authUserTemp(con, r[0], r[1]) != null;
                    }
                }
            }
        }
        String user = req.getParameter("user");
        String pass = req.getParameter("pass");
        String temp = req.getParameter("temp");
        if (user != null && temp != null) {
            return User.authUserTemp(con, user, temp) != null;
        } else if (user != null && pass != null) {
            return User.authUserPwd(con, user, pass) != null;
        }
        return false;
    }

    public static boolean authorizeDevice(MCon con, HttpServletRequest req) {
        long id = 0;
        String key = null;
        try {
            id = Long.parseUnsignedLong(req.getParameter("id"), 16);
        } catch (Exception e) {
        }
        try {
            key = req.getParameter("key");
        } catch (Exception e) {
        }
        if (id == 0 || key == null) {
            return false;
        }
        DeviceController c = new DeviceController();
        return c.getLatestDevice(id, key)!=null;
    }

    public static String getUsername(HttpServletRequest req) {
        Cookie[] cks = req.getCookies();
        if (cks != null) {
            for (Cookie c : cks) {
                if ("swarmUser".equals(c.getName())) {
                    String[] r = c.getValue().split("\\.");
                    if (r != null && r.length == 2) {
                        return r[0];
                    }
                }
            }
        }
        return req.getParameter("user");
    }

    public static boolean loginUser(MCon con, HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cks = req.getCookies();
        if (cks != null) {
            for (Cookie c : cks) {
                if ("swarmUser".equals(c.getName())) {
                    String[] r = c.getValue().split("\\.");
                    if (r != null && r.length == 2) {
                        String temp = User.authUserTemp(con, r[0], r[1]);
                        if (temp != null) {
                            Cookie ck = new Cookie("swarmUser", r[0] + "." + temp);
                            ck.setMaxAge(864000);
                            ck.setHttpOnly(true);
                            ck.setPath("/");
                            resp.addCookie(ck);
                            return true;
                        }
                    }
                }
            }
        }
        String user = req.getParameter("user");
        String pass = req.getParameter("pass");
        String temp = req.getParameter("temp");
        if (user != null && temp != null) {
            String t = User.authUserTemp(con, user, temp);
            if (t != null) {
                Cookie ck = new Cookie("swarmUser", user + "." + t);
                            ck.setMaxAge(864000);
                            ck.setHttpOnly(true);
                            ck.setPath("/");
                            resp.addCookie(ck);
                resp.addCookie(ck);
                return true;
            }
        } else if (user != null && pass != null) {
            String t = User.authUserPwd(con, user, pass);
            if (t != null) {
                Cookie ck = new Cookie("swarmUser", user + "." + t);
                            ck.setMaxAge(864000);
                            ck.setHttpOnly(true);
                            ck.setPath("/");
                            resp.addCookie(ck);
                resp.addCookie(ck);
                return true;
            }
        }
        return false;
    }
}
