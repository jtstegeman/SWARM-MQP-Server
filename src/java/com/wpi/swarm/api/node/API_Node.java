/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.api.node;

import com.google.gson.Gson;
import com.wpi.swarm.auth.Authorizer;
import com.wpi.swarm.device.DeviceController;
import com.wpi.swarm.device.DeviceInfo;
import com.wpi.swarm.device.DeviceType;
import com.wpi.swarm.device.DeviceType.ValueDef;
import com.wpi.swarm.device.DeviceType.ValueDef.ValueType;
import com.wpi.swarm.mongo.MCon;
import com.wpi.swarm.node.Node;
import com.wpi.swarm.node.NodeController;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jtste
 */
@WebServlet(name = "API_Node", urlPatterns = {"/api/node"})
public class API_Node extends HttpServlet {

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
        JsonObjectBuilder obj = Json.createObjectBuilder();
        MCon m = new MCon();
        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
        }
        String key = request.getParameter("key");
        NodeController nc = new NodeController();
        Node n = nc.getNode(id, key);
        if (n == null) {
            obj.add("status", "err");
        } else {
            obj.add("status", "success");
            obj.add("data", Node.toJson(n));
        }
        try (PrintWriter w = response.getWriter()) {
            response.setStatus(HttpServletResponse.SC_OK);
            w.println(obj.build().toString());
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
        JsonObjectBuilder obj = Json.createObjectBuilder();
        MCon m = new MCon();
        long id = 0;
        Node state = new Node();
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
        }
        String key = request.getParameter("key");
        try {
            state.latitude = Double.parseDouble(request.getParameter("latitude"));
        } catch (Exception e) {
        }
        try {
            state.longitude = Double.parseDouble(request.getParameter("longitude"));
        } catch (Exception e) {
        }
        try {
            state.airQuality = Double.parseDouble(request.getParameter("air"));
        } catch (Exception e) {
        }
        try {
            state.temperature = Double.parseDouble(request.getParameter("temperature"));
        } catch (Exception e) {
        }try {
            state.humidity = Double.parseDouble(request.getParameter("humidity"));
        } catch (Exception e) {
        }
        try {
            state.uv = Double.parseDouble(request.getParameter("uv"));
        } catch (Exception e) {
        }
        try {
            state.ir = Double.parseDouble(request.getParameter("ir"));
        } catch (Exception e) {
        }
        try {
            state.visible = Double.parseDouble(request.getParameter("visible"));
        } catch (Exception e) {
        }
        try {
            state.battery = Double.parseDouble(request.getParameter("battery"));
        } catch (Exception e) {
        }
        
        try {
            state.name = request.getParameter("name");
        } catch (Exception e) {
        }
        try {
            state.key = NodeController.getKey(request.getParameter("nKey"));
        } catch (Exception e) {
        }
        NodeController nc = new NodeController();
        
        if (!nc.updateNode(id, key,state)) {
            obj.add("status", "err");
        } else {
            obj.add("status", "success");
        }
        try (PrintWriter w = response.getWriter()) {
            response.setStatus(HttpServletResponse.SC_OK);
            w.println(obj.build().toString());
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
