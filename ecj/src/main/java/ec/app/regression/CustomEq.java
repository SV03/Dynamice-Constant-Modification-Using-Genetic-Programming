/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.app.regression;

/**
 *
 * @author sarath
 */
public class CustomEq extends Regression{
        public double func(double x)
        { return x*x*x*x*x*x - 2.0*x*x*x*x + x*x; }
}

