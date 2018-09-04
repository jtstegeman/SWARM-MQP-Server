/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.auth;

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
        return Authorizer.authorizeDevice(con, req) || Authorizer.authorizeUser(con, req);
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
            return User.authUserPwd(con, user, temp) != null;
        }
        return true;
    }

    public static boolean authorizeDevice(MCon con, HttpServletRequest req) {
        return req.getParameter("key") != null;
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
                            resp.addCookie(new Cookie("swarmUser", r[0] + "." + temp));
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
                resp.addCookie(new Cookie("swarmUser", user + "." + temp));
                return true;
            }
        } else if (user != null && pass != null) {
            String t = User.authUserPwd(con, user, temp);
            if (t != null) {
                resp.addCookie(new Cookie("swarmUser", user + "." + temp));
                return true;
            }
        }
        return true;
    }
}
