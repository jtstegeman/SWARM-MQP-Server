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
import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jtste
 */
@WebServlet(name = "API_Device_BINARY", urlPatterns = {"/api/binary/device"})
public class API_Device_BINARY extends HttpServlet {

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
        System.out.println("Bin");
        response.setContentType("application/octet-stream");
        MCon m = new MCon();
        try (ServletInputStream in = request.getInputStream()) {
            int v = in.read();
            int state = 0;
            long id = 0;
            byte[] key = new byte[8];
            DeviceInfo inf = null;
            DeviceType type = null;
            ValueDef vdef = null;
            int count = 0;
            int nval = 0;
            String sval = "";
            DeviceController c = new DeviceController(m);
            while (v != -1) {
                if (state < 0) {
                    break;
                }
                switch (state) {
                    case 0:
                        if (v != 0xAA) {
                            state = -1;
                        } else {
                            state = 1;
                        }
                        break;
                    case 1:
                        if (v != 0xAA) {
                            state = -1;
                        } else {
                            state = 2;
                        }
                        break;

                    case 2:
                        id = v;
                        state = 3;
                        break;
                    case 3:
                        id <<= 8;
                        id |= v;
                        state = 4;
                        break;
                    case 4:
                        id <<= 8;
                        id |= v;
                        state = 5;
                        break;
                    case 5:
                        id <<= 8;
                        id |= v;
                        state = 6;
                        break;
                    case 6:
                        id <<= 8;
                        id |= v;
                        state = 7;
                        break;
                    case 7:
                        id <<= 8;
                        id |= v;
                        state = 8;
                        break;
                    case 8:
                        id <<= 8;
                        id |= v;
                        state = 9;
                        break;
                    case 9:
                        id <<= 8;
                        id |= v;
                        state = 10;
                        break;

                    case 10:
                        key[0] = (byte) v;
                        state = 11;
                        break;
                    case 11:
                        key[1] = (byte) v;
                        state = 12;
                        break;
                    case 12:
                        key[2] = (byte) v;
                        state = 13;
                        break;
                    case 13:
                        key[3] = (byte) v;
                        state = 14;
                        break;
                    case 14:
                        key[4] = (byte) v;
                        state = 15;
                        break;
                    case 15:
                        key[5] = (byte) v;
                        state = 16;
                        break;
                    case 16:
                        key[6] = (byte) v;
                        state = 17;
                        break;
                    case 17:
                        key[7] = (byte) v;
                        state = 18;
                        inf = c.getLatestDevice(id, key);
                        if (inf != null) {
                            type = DeviceType.load(m, inf.getType());
                            if (type != null) {
                                inf = new DeviceInfo();
                                state = 18;
                            } else {
                                state = -3;
                            }
                        } else {
                            state = -2;
                        }
                        break;

                    case 18:
                        if (type == null) {
                            state = -4;
                            break;
                        }
                        vdef = type.getDef(v);
                        if (vdef != null && vdef.getType() == ValueType.NUMBER) {
                            state = 19;
                            count = 0;
                        }
                        if (vdef != null && vdef.getType() == ValueType.STRING) {
                            state = 20;
                            count = 0;
                        }
                        break;
                    case 19: // number
                        if (count == 0) {
                            nval = v;
                            count++;
                        } else {
                            nval <<= 8;
                            nval |= v;
                            count++;
                        }
                        if (count == 4) {
                            if (inf != null && vdef != null) {
                                inf.setNumber(vdef.getName(), (double) Float.intBitsToFloat(nval));
                            }
                            state = 18;
                        }
                        break;
                    case 20: // string
                        if (count == 0) {
                            sval = "";
                        }
                        if (v == 0) {
                            if (inf != null && vdef != null) {
                                inf.setString(vdef.getName(), sval);
                            }
                            state = 18;
                        } else {
                            count++;
                            sval += (char) v;
                        }
                        break;
                }
                v = in.read();
            }
            if (state != 18 || inf == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if (inf.changed()) {
                c.updateDevice(id, key, inf);
            }
            response.setStatus(HttpServletResponse.SC_OK);
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
}
