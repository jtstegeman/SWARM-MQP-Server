/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.api.device;

import com.google.gson.Gson;
import com.wpi.swarm.api.device.API_DeviceType_JSON.JDTR.VDef;
import com.wpi.swarm.auth.Authorizer;
import com.wpi.swarm.device.DeviceType;
import com.wpi.swarm.device.DeviceType.ValueDef;
import com.wpi.swarm.device.DeviceType.ValueDef.ValueType;
import com.wpi.swarm.mongo.MCon;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
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
@WebServlet(name = "API_DeviceType_JSON", urlPatterns = {"/api/json/type"})
public class API_DeviceType_JSON extends HttpServlet {

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
        if (Authorizer.authorize(m, request)) {
            JDTR in = null;
            try {
                in = new Gson().fromJson(request.getReader(), JDTR.class);
            } catch (Exception e) {
            }
            if (in == null || in.getType() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            DeviceType tp = DeviceType.load(m, in.getType());
            if (tp == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try (PrintWriter w = response.getWriter()) {
                response.setStatus(HttpServletResponse.SC_OK);
                w.println(Json.createObjectBuilder().add("status", "SUCCESS").add("data", DeviceType.toJson(tp)).build().toString());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
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
        if (Authorizer.authorizeUser(m, request)) {
            JDTR in = null;
            try {
                in = new Gson().fromJson(request.getReader(), JDTR.class);
            } catch (Exception e) {
            }
            if (in == null || in.getType() == 0 || in.creator == null || in.defs == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            DeviceType tp = new DeviceType(in.getType(), in.creator);
            for (Entry<String, VDef> e : in.defs.entrySet()) {
                if (e.getValue().getType() != null && e.getValue().fieldId > -1) {
                    tp.addDef(new ValueDef(e.getKey(), e.getValue().getType(), e.getValue().fieldId));
                }
            }
            if (DeviceType.update(m, tp)) {
                try (PrintWriter w = response.getWriter()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    w.println(Json.createObjectBuilder().add("status", "SUCCESS").add("data", DeviceType.toJson(tp)).build().toString());
                }
            } else {
                if (DeviceType.create(m, tp)) {
                    try (PrintWriter w = response.getWriter()) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        w.println(Json.createObjectBuilder().add("status", "SUCCESS").add("data", DeviceType.toJson(tp)).build().toString());
                    }
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
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

    static class JDTR {

        String type = null;
        String creator = null;
        Map<String, VDef> defs = null;

        long getType() {
            if (type == null) {
                return 0;
            }
            try {
                return Long.parseUnsignedLong(type, 16);
            } catch (Exception e) {
            }
            return 0;
        }

        static class VDef {

            int fieldId;
            String type;

            ValueType getType() {
                if (type != null) {
                    try {
                        return ValueType.valueOf(type.toUpperCase());
                    } catch (Exception e) {
                    }
                }
                return null;
            }
        }
    }
}
