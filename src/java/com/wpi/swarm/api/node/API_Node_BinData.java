/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wpi.swarm.api.node;

import com.wpi.swarm.mongo.MCon;
import com.wpi.swarm.node.Node;
import com.wpi.swarm.node.NodeController;
import java.io.IOException;
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
@WebServlet(name = "API_Node_BinData", urlPatterns = {"/api/node/binary"})
public class API_Node_BinData extends HttpServlet {

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
        response.setContentType("application/octet-stream");
        MCon m = new MCon();
        try (ServletInputStream in = request.getInputStream()) {
            int v = in.read();
            int index = 0;
            int temp = 0;
            Node node = new Node();
            node.key = new byte[8];
            while (v != -1) {
                switch (index) {
                    case 0:
                        if (v != 0xAA) {
                            index = 99;
                        }
                        break;
                    case 1:
                        if (v != 0xAA) {
                            index = 99;
                        }
                        break;
                    case 2:
                        node.id = v & 0xFF;
                        break;
                    case 3:
                        node.id <<= 8;
                        node.id |= v & 0xFF;
                        break;
                    case 4:
                        node.id <<= 8;
                        node.id |= v & 0xFF;
                        break;
                    case 5:
                        node.id <<= 8;
                        node.id |= v & 0xFF;
                        break;
                    case 6:
                        node.id <<= 8;
                        node.id |= v & 0xFF;
                        break;
                    case 7:
                        node.id <<= 8;
                        node.id |= v & 0xFF;
                        break;
                    case 8:
                        node.id <<= 8;
                        node.id |= v & 0xFF;
                        break;
                    case 9:
                        node.id <<= 8;
                        node.id |= v & 0xFF;
                        break;

                    case 10:
                        node.key[0] = (byte) v;
                        break;
                    case 11:
                        node.key[1] = (byte) v;
                        break;
                    case 12:
                        node.key[2] = (byte) v;
                        break;
                    case 13:
                        node.key[3] = (byte) v;
                        break;
                    case 14:
                        node.key[4] = (byte) v;
                        break;
                    case 15:
                        node.key[5] = (byte) v;
                        break;
                    case 16:
                        node.key[6] = (byte) v;
                        break;
                    case 17:
                        node.key[7] = (byte) v;
                        break;

                    case 18:
                        temp = v & 0xFF;
                        break;
                    case 19:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 20:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 21:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        node.airQuality = Float.intBitsToFloat(temp);
                        break;

                    case 22:
                        temp = v & 0xFF;
                        break;
                    case 23:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 24:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 25:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        node.temperature = Float.intBitsToFloat(temp);
                        break;

                    case 26:
                        temp = v & 0xFF;
                        break;
                    case 27:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 28:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 29:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        node.humidity = Float.intBitsToFloat(temp);
                        break;

                    case 30:
                        temp = v & 0xFF;
                        break;
                    case 31:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 32:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 33:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        node.uv = Float.intBitsToFloat(temp);
                        break;

                    case 34:
                        temp = v & 0xFF;
                        break;
                    case 35:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 36:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 37:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        node.ir = Float.intBitsToFloat(temp);
                        break;

                    case 38:
                        temp = v & 0xFF;
                        break;
                    case 39:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 40:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 41:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        node.visible = Float.intBitsToFloat(temp);
                        break;

                    case 42:
                        temp = v & 0xFF;
                        break;
                    case 43:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 44:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        break;
                    case 45:
                        temp <<= 8;
                        temp |= v & 0xFF;
                        node.battery = Float.intBitsToFloat(temp);
                        break;
                    case 46:
                        break;

                    default:
                        index = 99;
                        break;
                }
                index++;
                v = in.read();
                if (index > 99) {
                    break;
                }
            }
            if (index != 46 && index != 47) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            NodeController c = new NodeController();
            c.updateNode(node.id, node.key, node);
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
