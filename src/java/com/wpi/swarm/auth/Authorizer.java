/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.auth;

import com.wpi.swarm.mongo.MCon;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jtste
 */
public class Authorizer {
    public static boolean authorize(MCon con, HttpServletRequest req){
        return Authorizer.authorizeDevice(con, req) || Authorizer.authorizeUser(con, req);
    }
    public static boolean authorizeUser(MCon con, HttpServletRequest req){
        return true;
    }
    public static boolean authorizeDevice(MCon con, HttpServletRequest req){
        return req.getParameter("key")!=null;
    }
    
    public static String getUser(HttpServletRequest req){
        return null;
    }
}
