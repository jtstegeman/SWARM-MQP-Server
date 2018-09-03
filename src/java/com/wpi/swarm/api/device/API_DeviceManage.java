/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.api.device;

import com.wpi.swarm.auth.Authorizer;
import com.wpi.swarm.device.DeviceController;
import com.wpi.swarm.device.DeviceInfo;
import com.wpi.swarm.mongo.MCon;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jtste
 */
@WebServlet(name = "API_DeviceManage", urlPatterns = {"/api/device/manage"})
public class API_DeviceManage extends HttpServlet {

    protected void act(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MCon m = new MCon();
        if (Authorizer.authorizeUser(m, request)) {
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
            DeviceController dc = new DeviceController(m);
            String action = request.getParameter("action");
            String owner = request.getParameter("owner");
            String name = request.getParameter("name");
            DeviceInfo dev = null;
            long type = 0;
            try {
                type = Long.parseUnsignedLong(request.getParameter("type"), 16);
            } catch (Exception e) {
            }
            if ("create".equalsIgnoreCase(action)) {
                if (owner == null || name == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                dev = dc.createDevice(owner, name, type, key, 0, 0);
                if (dev == null) {
                    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    return;
                }
            } else if ("delete".equalsIgnoreCase(action)) {
                if (id == 0 || key == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                if (!dc.deleteDevice(id, key)) {
                    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    return;
                }
                try (PrintWriter w = response.getWriter()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    w.println("status=SUCCESS");
                }
                return;
            } else if ("edit".equalsIgnoreCase(action)) {
                if (id == 0 || key == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                dev = new DeviceInfo();
                if (request.getParameter("new_key")!=null){
                    dev.setKey(request.getParameter("new_key"));
                }
                if (request.getParameter("new_owner")!=null){
                    dev.setOwner(request.getParameter("new_owner"));
                }
                if (request.getParameter("new_name")!=null){
                    dev.setName(request.getParameter("new_name"));
                }
                if (request.getParameter("new_lat")!=null){
                    try{
                    dev.setLatitude(Double.parseDouble(request.getParameter("new_lat")));
                    }catch (Exception e){}
                }
                if (request.getParameter("new_lng")!=null){
                    try{
                    dev.setLongitude(Double.parseDouble(request.getParameter("new_lng")));
                    }catch (Exception e){}
                }
                if (request.getParameter("new_type")!=null){
                    try{
                    dev.setType(Long.parseUnsignedLong(request.getParameter("new_type"),16));
                    }catch (Exception e){}
                }
                if (!dc.updateDevice(id, key, dev)) {
                    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    return;
                }
                dev = dc.getLatestDevice(dev);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

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
        act(request, response);
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
        act(request, response);
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
