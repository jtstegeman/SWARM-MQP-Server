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
import java.io.InputStream;
import java.io.OutputStream;
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
@WebServlet(name = "API_RoverCurrent_Bin", urlPatterns = {"/api/rover/cur/bin"})
public class API_RoverCurrent_Bin extends HttpServlet {

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
        try(InputStream in = request.getInputStream()){
            int v = in.read();
            int index = 0;
            int act=0;
            long id=0;
            float lat=0;
            float lng=0;
            int temp=0;
            byte[] key=new byte[8];
            byte[] ida=new byte[8];
            while (v != -1) {
                switch (index) {
                    case 0:
                        if (v != 0xBB) {
                            index = 99;
                        }
                        break;
                    case 1:
                        if (v != 0xBB) {
                            index = 99;
                        }
                        break;
                    case 2:
                        id = v & 0xFF;
                        ida[0] = (byte)v;
                        break;
                    case 3:
                        id <<= 8;
                        id |= v & 0xFF;
                        ida[1] = (byte)v;
                        break;
                    case 4:
                        id <<= 8;
                        id |= v & 0xFF;
                        ida[2] = (byte)v;
                        break;
                    case 5:
                        id <<= 8;
                        id |= v & 0xFF;
                        ida[3] = (byte)v;
                        break;
                    case 6:
                        id <<= 8;
                        id |= v & 0xFF;
                        ida[4] = (byte)v;
                        break;
                    case 7:
                        id <<= 8;
                        id |= v & 0xFF;
                        ida[5] = (byte)v;
                        break;
                    case 8:
                        id <<= 8;
                        id |= v & 0xFF;
                        ida[6] = (byte)v;
                        break;
                    case 9:
                        id <<= 8;
                        id |= v & 0xFF;
                        ida[7] = (byte)v;
                        break;

                    case 10:
                        key[0] = (byte) v;
                        break;
                    case 11:
                        key[1] = (byte) v;
                        break;
                    case 12:
                        key[2] = (byte) v;
                        break;
                    case 13:
                        key[3] = (byte) v;
                        break;
                    case 14:
                        key[4] = (byte) v;
                        break;
                    case 15:
                        key[5] = (byte) v;
                        break;
                    case 16:
                        key[6] = (byte) v;
                        break;
                    case 17:
                        key[7] = (byte) v;
                        break;
                    case 18:
                        act = v;
                        break;
                        
                    case 19:
                        temp = v&0xFF;
                        break;
                    case 20:
                        temp<<=8;
                        temp |= v&0xFF;
                        break;
                    case 21:
                        temp<<=8;
                        temp |= v&0xFF;
                        break;
                    case 22:
                        temp<<=8;
                        temp |= v&0xFF;
                        lat = Float.intBitsToFloat(temp);
                        break;
                    
                    case 23:
                        temp = v&0xFF;
                        break;
                    case 24:
                        temp<<=8;
                        temp |= v&0xFF;
                        break;
                    case 25:
                        temp<<=8;
                        temp |= v&0xFF;
                        break;
                    case 26:
                        temp<<=8;
                        temp |= v&0xFF;
                        lng = Float.intBitsToFloat(temp);
                        break;
                }
                index++;
                v = in.read();
                if (index>99)
                    break;
            }
            if (index == 27 || index==28){
                RoverController rc = new RoverController();
                Rover r = new Rover();
                r.latitude = lat;
                r.longitude = lng;
                System.out.println(r);
                rc.updateRover(id, key, r);
                index = 19;
            }
            if (index == 19 || index==20){
                RoverController rc = new RoverController();
                RoverCmd cmd = rc.getCurrentRoverCmd(id);
                if (cmd!=null && act==2){ // advance to next
                    rc.deleteRoverCmd(cmd.id.toHexString());
                    cmd = rc.getCurrentRoverCmd(id);
                }
                if (cmd!=null){
                   System.out.println(cmd);
                   try (OutputStream out = response.getOutputStream()){
                       out.write(0xBC);
                       out.write(0xBC);
                       out.write(ida);
                       temp = Float.floatToRawIntBits((float)cmd.latitude);
                       byte[] ta = new byte[4];
                       ta[0] = (byte)(temp&0xFF);
                       ta[1] = (byte)((temp>>8)&0xFF);
                       ta[2] = (byte)((temp>>16)&0xFF);
                       ta[3] = (byte)((temp>>24)&0xFF);
                       out.write(ta);
                       temp = Float.floatToRawIntBits((float)cmd.longitude);
                       ta[0] = (byte)(temp&0xFF);
                       ta[1] = (byte)((temp>>8)&0xFF);
                       ta[2] = (byte)((temp>>16)&0xFF);
                       ta[3] = (byte)((temp>>24)&0xFF);
                       out.write(ta);
                       out.write(cmd.cmd);
                   }
                }
            }
        }
        catch (Exception e){}
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
