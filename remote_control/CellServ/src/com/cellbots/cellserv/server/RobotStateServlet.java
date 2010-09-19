package com.cellbots.cellserv.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import com.cellbots.CellbotProtos;
import com.cellbots.CellbotProtos.ControllerState;
import com.cellbots.CellbotProtos.PhoneState;
import com.cellbots.SchemaCellbotProtos;
import com.dyuproject.protostuff.JsonIOUtil;
import com.dyuproject.protostuff.Schema;

public class RobotStateServlet extends HttpServlet
{

  /**
   * 
   */
  private static final long serialVersionUID = 6703781028562576421L;
  
  private static boolean useNumericFormat = false;

  public String getServletInfo()
  {
    return "Servlet for handeling communication with phone";
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
  {
    Schema<com.cellbots.CellbotProtos.PhoneState> schema = new SchemaCellbotProtos.PhoneState.MessageSchema();

    PhoneState ps = StateHolder.getInstance("").getPhoneState();

    byte[] bytes;
    if (ps != null)
    {
      bytes =   JsonIOUtil.toByteArray(ps, schema, useNumericFormat);
      res.getOutputStream().write(bytes);
    }

  }

  /**
   * Write survey results to output file in response to the POSTed form. Write a
   * "thank you" to the client.
   */
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
  {
    // first, set the "content type" header of the response
    // res.setContentType("text/html");

    try
    {
      CellbotProtos.PhoneState state = CellbotProtos.PhoneState.parseFrom(req.getInputStream());
      
      
      String botID = "";
      if(state.hasBotID())
      {
        botID = state.getBotID();
      }
      
      StateHolder.getInstance(botID).setPhoneState(state);
      
      

      if (StateHolder.getInstance(botID).newControllerStateAvailble())
      {
        System.out.println("writing new controller msg");
        ControllerState cs = StateHolder.getInstance("").getControllerState();
       
        res.getOutputStream().write(cs.toByteArray());
      }

    }
    catch (IOException e)
    {
      e.printStackTrace();
      // toClient.println("A problem occured: could not write file: "+path +
      // "Please try again.");
    }

  }

}