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
import com.wpi.swarm.device.DeviceLogEntry;
import com.wpi.swarm.device.DeviceType;
import com.wpi.swarm.device.DeviceType.ValueDef;
import com.wpi.swarm.device.DeviceType.ValueDef.ValueType;
import com.wpi.swarm.mongo.MCon;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jtste
 */
@WebServlet(name = "API_DeviceHistory_JSON", urlPatterns = {"/api/json/device/history"})
public class API_DeviceHistory_JSON extends HttpServlet {

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
            JDHR in = null;
            try {
                in = new Gson().fromJson(request.getReader(), JDHR.class);
                if (in == null) {
                    in = new JDHR();
                    in.id = request.getParameter("id");
                    in.type = request.getParameter("type");
                    in.key = request.getParameter("key");
                    in.since = request.getParameter("since");
                    in.before = request.getParameter("before");
                }
            } catch (Exception e) {
            }
            if (in == null || in.getId() == 0 || in.getKey() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            DeviceController dc = new DeviceController(m);
            if (dc.getLatestDevice(in.getId(), in.getKey())==null){ // check key validity
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            List<DeviceLogEntry> hist = dc.getDeviceHistory(in.getId(), in.getSince(), in.getBefore());
            if (hist == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try (PrintWriter w = response.getWriter()) {
                response.setStatus(HttpServletResponse.SC_OK);
                JsonArrayBuilder arr = Json.createArrayBuilder();
                for (DeviceLogEntry e : hist) {
                    arr.add(DeviceLogEntry.toJson(e));
                }
                w.println(Json.createObjectBuilder().add("status", "SUCCESS").add("data", arr).build().toString());
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
        doGet(request, response);
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

    static final class JDHR {

        String key = null;
        String type = null;
        String id = null;
        String before = null;
        String since = null;

        long getType() {
            if (type == null) {
                return 0;
            }
            try {
                return Long.parseUnsignedLong(type);
            } catch (Exception e) {
            }
            return 0;
        }

        long getId() {
            if (id == null) {
                return 0;
            }
            try {
                return Long.parseUnsignedLong(id);
            } catch (Exception e) {
            }
            return 0;
        }

        String getKey() {
            return key;
        }

        long getSince() {
            if (since == null) {
                return System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7; // past week
            }
            try {
                return Long.parseUnsignedLong(since);
            } catch (Exception e) {
            }
            return 0;
        }

        long getBefore() {
            if (before == null) {
                return this.getBefore() + 1000 * 60 * 60 * 24 * 7; // past week
            }
            try {
                return Long.parseUnsignedLong(before);
            } catch (Exception e) {
            }
            return 0;
        }
    }
}
