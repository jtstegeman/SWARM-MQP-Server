/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.api.rover;

import com.wpi.swarm.mongo.MCon;
import com.wpi.swarm.node.NodeController;
import com.wpi.swarm.rover.Rover;
import com.wpi.swarm.rover.RoverCmd;
import com.wpi.swarm.rover.RoverController;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "API_RoverCmd", urlPatterns = {"/api/rover/cmd"})
public class API_RoverCmd extends HttpServlet {

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
        String id = request.getParameter("id");
        RoverController nc = new RoverController();
        RoverCmd r = nc.getRoverCmd(id);
        if (r == null) {
            obj.add("status", "err");
        } else {
            obj.add("status", "success");
            obj.add("data", RoverCmd.toJson(r));
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
        String id = request.getParameter("id");
        RoverCmd cmd = new RoverCmd();
        try {
            cmd.latitude = Double.parseDouble(request.getParameter("latitude"));
        } catch (Exception e) {
        }
        try {
            cmd.longitude = Double.parseDouble(request.getParameter("longitude"));
        } catch (Exception e) {
        }
        try {
            cmd.cmd = Integer.parseInt(request.getParameter("cmd"));
        } catch (Exception e) {
        }
        RoverController nc = new RoverController();

        if (!nc.updateRoverCmd(id, cmd)) {
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
