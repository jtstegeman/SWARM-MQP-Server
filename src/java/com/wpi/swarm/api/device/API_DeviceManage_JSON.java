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
import com.wpi.swarm.mongo.MCon;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "API_DeviceManage_JSON", urlPatterns = {"/api/json/device/manage"})
public class API_DeviceManage_JSON extends HttpServlet {

    protected void act(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        MCon m = new MCon();
        if (Authorizer.authorizeUser(m, request)) {
            JDMR in = null;
            try {
                in = new Gson().fromJson(request.getReader(), JDMR.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (in==null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            DeviceController dc = new DeviceController(m);
            DeviceInfo dev = null;
            if ("create".equalsIgnoreCase(in.action)) {
                if (in.new_owner == null || in.new_name == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                dev = dc.createDevice(in.new_owner, in.new_name, in.getNewType(), in.getNewKey(), 0.1, 0.1);
                if (dev == null) {
                    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    return;
                }
            } else if ("delete".equalsIgnoreCase(in.action)) {
                if (in.getId() == 0 || in.getKey() == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                if (!dc.deleteDevice(in.getId(), in.getKey())) {
                    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    return;
                }
                try (PrintWriter w = response.getWriter()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                }
                return;
            } else if ("edit".equalsIgnoreCase(in.action)) {
                if (in.getId() == 0 || in.getKey() == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                dev = new DeviceInfo();
                if (in.new_key!=null){
                    dev.setKey(in.new_key);
                }
                if (in.new_owner!=null){
                    dev.setOwner(in.new_owner);
                }
                if (in.new_name!=null){
                    dev.setName(in.new_name);
                }
                if (in.new_lat!=null){
                    dev.setLatitude(in.new_lat);
                }
                if (in.new_lng!=null){
                    dev.setLatitude(in.new_lng);
                }
                if (in.getNewType()!=0){
                    dev.setType(in.getNewType());
                }
                if (!dc.updateDevice(in.getId(), in.key, dev)) {
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
                w.println(Json.createObjectBuilder().add("status", "SUCCESS").add("data", DeviceInfo.toJson(dev)).build().toString());
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

    static class JDMR{
        String id = null;
        String key = null;
        String action = null;
        String new_key = null;
        String new_type = null;
        String new_name = null;
        String new_owner = null;
        Double new_lat = null;
        Double new_lng = null;
        
        long getNewType() {
            if (new_type == null) {
                return 0;
            }
            try {
                return Long.parseUnsignedLong(new_type, 16);
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
        String getNewKey(){
            if (new_key==null){
                return DeviceInfo.getRandomKeyString();
            }
            return new_key;
        }

        @Override
        public String toString() {
            return "JDMR{" + "id=" + id + ", key=" + key + ", action=" + action + ", new_key=" + new_key + ", new_type=" + new_type + ", new_name=" + new_name + ", new_owner=" + new_owner + ", new_lat=" + new_lat + ", new_lng=" + new_lng + '}';
        }
        
    }
}
