/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.api.device;

import com.google.gson.Gson;
import com.wpi.swarm.auth.Authorizer;
import com.wpi.swarm.device.DeviceController;
import com.wpi.swarm.device.DeviceInfo;
import com.wpi.swarm.device.DeviceType;
import com.wpi.swarm.device.DeviceType.ValueDef;
import com.wpi.swarm.device.DeviceType.ValueDef.ValueType;
import com.wpi.swarm.mongo.MCon;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
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
@WebServlet(name = "API_Device_JSON", urlPatterns = {"/api/json/device"})
public class API_Device_JSON extends HttpServlet {

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
            JDR in = null;
            try {
                in = new Gson().fromJson(request.getReader(), JDR.class);
            } catch (Exception e) {
            }
            if (in == null || in.getId() == 0 || in.getKey() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            DeviceController dc = new DeviceController(m);
            DeviceInfo dev = dc.getLatestDevice(in.getId(), in.getKey());
            if (dev == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try (PrintWriter w = response.getWriter()) {
                response.setStatus(HttpServletResponse.SC_OK);
                w.println(Json.createObjectBuilder().add("status", "SUCCESS").add("data", DeviceInfo.toJson(dev)).build().toString());
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
        if (Authorizer.authorize(m, request)) {
            JDR in = null;
            try {
                in = new Gson().fromJson(request.getReader(), JDR.class);
            } catch (Exception e) {
            }
            if (in == null || in.getId() == 0 || in.getKey() == null || in.getType() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            DeviceType devTyp = DeviceType.load(m, in.getType());
            if (devTyp == null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }
            DeviceController dc = new DeviceController(m);
            DeviceInfo dev = new DeviceInfo();
            dev.clearChangeLog();

            if (in.nums != null && !in.nums.isEmpty()) {
                for (String s : in.nums.keySet()) {
                    ValueDef def = devTyp.getDef(s);
                    if (def != null) {
                        try {
                            if (def.getType() == ValueType.NUMBER) {
                                dev.setNumber(s, in.nums.getOrDefault(def, 0.0));
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            if (in.strs != null && !in.strs.isEmpty()) {
                for (String s : in.strs.keySet()) {
                    ValueDef def = devTyp.getDef(s);
                    if (def != null) {
                        try {
                            if (def.getType() == ValueType.STRING) {
                                dev.setString(s, in.strs.getOrDefault(def, ""));
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            if (in.lat != null) {
                dev.setLatitude(in.lat);
            }
            if (in.lng != null) {
                dev.setLatitude(in.lng);
            }

            if (!dc.updateDevice(in.getId(), in.getKey(), dev)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }

            try (PrintWriter w = response.getWriter()) {
                response.setStatus(HttpServletResponse.SC_OK);
                w.println(Json.createObjectBuilder().add("status", "SUCCESS").add("data", DeviceInfo.toJson(dev)).build().toString());
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

    static final class JDR {

        String key = null;
        String type = null;
        String id = null;
        Double lat = null;
        Double lng = null;
        Map<String, Double> nums = null;
        Map<String, String> strs = null;

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

        long getId() {
            if (id == null) {
                return 0;
            }
            try {
                return Long.parseUnsignedLong(id, 16);
            } catch (Exception e) {
            }
            return 0;
        }

        String getKey() {
            return key;
        }
    }
}
