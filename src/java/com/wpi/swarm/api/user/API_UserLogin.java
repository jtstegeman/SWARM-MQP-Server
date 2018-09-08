/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.api.user;

import com.wpi.swarm.api.device.*;
import com.wpi.swarm.auth.Authorizer;
import com.wpi.swarm.device.DeviceController;
import com.wpi.swarm.device.DeviceInfo;
import com.wpi.swarm.device.DeviceType;
import com.wpi.swarm.device.DeviceType.ValueDef;
import com.wpi.swarm.device.DeviceType.ValueDef.ValueType;
import com.wpi.swarm.mongo.MCon;
import com.wpi.swarm.user.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.Objects;
import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jtste
 */
@WebServlet(name = "API_UserLogin", urlPatterns = {"/api/json/user/login"})
public class API_UserLogin extends HttpServlet {

    // <editor-fold >
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        MCon m = new MCon();
        if (Authorizer.authorizeUser(m, request)){
            response.setStatus(HttpServletResponse.SC_OK);
            String uName = Authorizer.getUsername(request);
            try (PrintWriter w = response.getWriter()) {
                w.println(Json.createObjectBuilder().add("status", "SUCCESS").add("name", uName).build().toString());
            }
        }
        else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter w = response.getWriter()) {
                w.println(Json.createObjectBuilder().add("status", "FAIL").build().toString());
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        MCon m = new MCon();
        if (Authorizer.loginUser(m, request, response)){
            response.setStatus(HttpServletResponse.SC_OK);
            try (PrintWriter w = response.getWriter()) {
                w.println(Json.createObjectBuilder().add("status", "SUCCESS").build().toString());
            }
        }
        else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter w = response.getWriter()) {
                w.println(Json.createObjectBuilder().add("status", "FAIL").build().toString());
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
