/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.openautomaker.base.services.printing;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author ianhudson
 */
public class PrintService extends Service<Void>
{
    @Override
    protected Task<Void> createTask()
    {
       
       return null; 
    }

    @Override
    protected void succeeded()
    {
        super.succeeded();
    }    
}
