/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.api.rover;

import com.wpi.swarm.api.rover.*;
import com.wpi.swarm.auth.Authorizer;
import com.wpi.swarm.mongo.MCon;
import com.wpi.swarm.rover.Rover;
import com.wpi.swarm.rover.RoverController;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
@WebServlet(name = "API_Rover_Create", urlPatterns = {"/api/rover/create"})
public class API_Rover_Create extends HttpServlet {

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
        if (Authorizer.authorizeUser(m, request)) {
            double lat = 0;
            try {
                lat = Double.parseDouble(request.getParameter("latitude"));
            } catch (Exception e) {
            }
            double lng = 0;
            try {
                lng = Double.parseDouble(request.getParameter("longitude"));
            } catch (Exception e) {
            }
            String name = request.getParameter("name");
            RoverController nc = new RoverController();
            System.out.println(lat);
            System.out.println(lng);
            Rover n = nc.createRover(Authorizer.getUsername(request), name, lat, lng);
            if (n == null) {
                obj.add("status", "err");
            } else {
                obj.add("status", "success");
                obj.add("data", Rover.toJson(n));
            }
        } else {
            obj.add("status", "err");
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
        this.doGet(request, response);
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
