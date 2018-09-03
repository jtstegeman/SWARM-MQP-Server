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
@WebServlet(name = "API_DeviceType", urlPatterns = {"/api/type"})
public class API_DeviceType extends HttpServlet {

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
            long type = 0;
            try {
                type = Long.parseUnsignedLong(request.getParameter("type"), 16);
            } catch (Exception e) {
            }
            if (type == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            DeviceType tp = DeviceType.load(m, type);
            if (tp == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try (PrintWriter w = response.getWriter()) {
                response.setStatus(HttpServletResponse.SC_OK);
                w.println("status=$SUCCESS");
                for (ValueDef d : tp.getValueDefs()) {
                    w.println(d.getName() + "-fieldId=#" + d.getFieldId());
                    w.println(d.getName() + "-type=$" + d.getType().name());
                }
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
        MCon m = new MCon();
        if (Authorizer.authorizeUser(m, request)) {
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
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

}
