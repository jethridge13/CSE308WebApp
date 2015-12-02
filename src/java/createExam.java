/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Joshua
 */
public class createExam extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try {
                String str = request.getQueryString();
                System.out.println(str);
                String beginDate = str.substring(str.indexOf("=") + 1, str.indexOf("&"));
                System.out.println(beginDate);
                str = str.substring(str.indexOf("&") + 1);
                String endDate = str.substring(str.indexOf("=") + 1, str.indexOf("&"));
                str = str.substring(str.indexOf("&") + 1);
                System.out.println(endDate);
                System.out.println(str);
                String section = str.substring(str.indexOf("=") + 1, str.indexOf("&"));
                str = str.substring(str.indexOf("&") + 1);
                System.out.println(section);
                String students = str.substring(str.indexOf("=") + 1, str.indexOf("&"));
                str = str.substring(str.indexOf("&") + 1);
                System.out.println(students);
                String duration = str.substring(str.indexOf("=") + 1, str.indexOf("&"));
                str = str.substring(str.indexOf("&") + 1);
                System.out.println(duration);
                String classID = str.substring(str.indexOf("=") + 1, str.indexOf("&"));
                System.out.println(classID);
                str = str.substring(str.indexOf("&") + 1);
                String profID = str.substring(str.indexOf("=") + 1);
                System.out.println(classID);

                int sectionN = Integer.parseInt(section);
                int dur = Integer.parseInt(duration);
                int studentsN = Integer.parseInt(students);

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                Date begin = formatter.parse(beginDate);
                Date end = formatter.parse(endDate);
                

                Instructor ins = Instructor.getInstructor(profID);
                
                System.out.println(begin.getTime());
                System.out.println(end.getTime());
                ins.requestTest(classID, sectionN, 1, new Timestamp(begin.getTime()),
                        new Timestamp(end.getTime()),dur, studentsN);
            } catch (ParseException | SQLException ex) {
                Logger.getLogger(createExam.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        processRequest(request, response);
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
        processRequest(request, response);
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
