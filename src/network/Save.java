/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author java3
 */
public class Save extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            String dkey = request.getParameter("secretkey");
            
            System.out.println("secret Key : " + dkey);
            HttpSession user = request.getSession();
            String uid = user.getAttribute("uid").toString();
            String ssowner = user.getAttribute("ssowner").toString();
            String uname = user.getAttribute("uname").toString();
            String id = user.getAttribute("idd").toString();
            System.out.println("UID : " + uid + "User Name : " + uname);

            Connection con = DbConnection.getConnection();
            Statement st1 = con.createStatement();
            Statement st = con.createStatement();
            
          ResultSet  rs3 = st1.executeQuery("Select * from revocation where uname='" + uname + "' AND oname='" + ssowner + "'");
            if (rs3.next()) {
                response.sendRedirect("files.jsp?revoke=Your_revoked");
            }else{
                ResultSet rt = st.executeQuery("select * from fileupload where secret_key='" + dkey + "' AND id='" + id + "'");
                if (rt.next()) {
                    String fname = rt.getString("filename");
                    String fid = rt.getString("id");
                    InputStream is = rt.getAsciiStream("content");
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuffer sb = new StringBuffer();
                    String temp = null;

                    while ((temp = br.readLine()) != null) {
                        sb.append(temp);

                    }
                    Decryption de = new Decryption();
                    String str = de.decrypt(sb.toString(), dkey);

                    response.setHeader("Content-Disposition", "attachment;filename=\"" + fname + "\"");
                    out.write(str.toString());
                    out.close();
                    int i = st1.executeUpdate("insert into download (uid, uname, fid, fname, time_)values('" + uid + "','" + uname + "','" + fid + "','" + fname + "',now())");

                    if (i != 0) {
                        // response.sendRedirect("download.jsp");
                    } else {
                        System.out.println("error  while updating information...");
                    }
                } else {
                    response.sendRedirect("files.jsp?ms=secret_key_or_file_not_matched");

                }
            }
            }  catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
     * Handles the HTTP
     * <code>POST</code> method.
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
