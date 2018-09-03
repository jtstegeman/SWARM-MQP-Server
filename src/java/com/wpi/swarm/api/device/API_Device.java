/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.api.device;

import com.wpi.swarm.auth.Authorizer;
import com.wpi.swarm.device.DeviceController;
import com.wpi.swarm.device.DeviceInfo;
import com.wpi.swarm.device.DeviceType;
import com.wpi.swarm.device.DeviceType.ValueDef;
import com.wpi.swarm.device.DeviceType.ValueDef.ValueType;
import com.wpi.swarm.mongo.MCon;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jtste
 */
@WebServlet(name = "API_Device", urlPatterns = {"/api/device"})
public class API_Device extends HttpServlet {

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
        MCon m = new MCon();
        if (Authorizer.authorize(m, request)) {
            long id = 0;
            String key = null;
            try {
                id = Long.parseUnsignedLong(request.getParameter("id"), 16);
            } catch (Exception e) {
            }
            try {
                key = request.getParameter("key");
            } catch (Exception e) {
            }
            if (id == 0 || key == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            DeviceController dc = new DeviceController(m);
            DeviceInfo dev = dc.getLatestDevice(id, key);
            if (dev == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try (PrintWriter w = response.getWriter()) {
                response.setStatus(HttpServletResponse.SC_OK);
                w.println("status=$SUCCESS");
                w.println("id=#" + Long.toUnsignedString(dev.getId(), 16));
                w.println("key=$" + dev.getKeyString());
                w.println("owner=$" + dev.getOwner());
                w.println("name=$" + dev.getName());
                w.println("type=#" + Long.toUnsignedString(dev.getType(), 16));
                w.println("lat=#" + dev.getLatitude());
                w.println("lng=#" + dev.getLongitude());
                for (Entry<String, Double> e : dev.getNumbers().entrySet()) {
                    if (e.getKey() != null && e.getValue() != null) {
                        w.println(e.getKey() + "=#" + Double.toString(e.getValue()));
                    }
                }
                for (Entry<String, String> e : dev.getStrings().entrySet()) {
                    if (e.getKey() != null && e.getValue() != null) {
                        w.println(e.getKey() + "=$" + e.getValue());
                    }
                }
            }
        }
        else{
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
        MCon m = new MCon();
        if (Authorizer.authorize(m, request)) {
            long id = 0;
            long type = 0;
            String key = null;
            try {
                id = Long.parseUnsignedLong(request.getParameter("id"), 16);
            } catch (Exception e) {
            }
            try {
                type = Long.parseUnsignedLong(request.getParameter("type"), 16);
            } catch (Exception e) {
            }
            try {
                key = request.getParameter("key");
            } catch (Exception e) {
            }
            if (id == 0 || type == 0 || key == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            DeviceType devTyp = DeviceType.load(m, type);
            if (devTyp == null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }
            DeviceController dc = new DeviceController(m);
            DeviceInfo dev = new DeviceInfo();
            dev.clearChangeLog();

            for (String s : request.getParameterMap().keySet()) {
                ValueDef def = devTyp.getDef(s);
                if (def != null) {
                    try {
                        if (def.getType() == ValueType.NUMBER) {
                            dev.setNumber(s, Double.parseDouble(request.getParameter(s)));
                        } else if (def.getType() == ValueType.STRING) {
                            dev.setString(s, Objects.toString(request.getParameter(s), ""));
                        }
                    } catch (Exception e) {
                    }
                }
            }

            if (request.getParameter("lat") != null) {
                try {
                    dev.setLatitude(Double.parseDouble(request.getParameter("new_lat")));
                } catch (Exception e) {
                }
            }
            if (request.getParameter("lng") != null) {
                try {
                    dev.setLongitude(Double.parseDouble(request.getParameter("new_lng")));
                } catch (Exception e) {
                }
            }

            if (!dc.updateDevice(id, key, dev)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }

            try (PrintWriter w = response.getWriter()) {
                response.setStatus(HttpServletResponse.SC_OK);
                w.println("status=$SUCCESS");
                w.println("id=#" + Long.toUnsignedString(dev.getId(), 16));
                w.println("key=$" + dev.getKeyString());
                w.println("owner=$" + dev.getOwner());
                w.println("name=$" + dev.getName());
                w.println("type=#" + Long.toUnsignedString(dev.getType(), 16));
                w.println("lat=#" + dev.getLatitude());
                w.println("lng=#" + dev.getLongitude());
                for (Entry<String, Double> e : dev.getNumbers().entrySet()) {
                    if (e.getKey() != null && e.getValue() != null) {
                        w.println(e.getKey() + "=#" + Double.toString(e.getValue()));
                    }
                }
                for (Entry<String, String> e : dev.getStrings().entrySet()) {
                    if (e.getKey() != null && e.getValue() != null) {
                        w.println(e.getKey() + "=$" + e.getValue());
                    }
                }
            }
        }
        else{
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

}
