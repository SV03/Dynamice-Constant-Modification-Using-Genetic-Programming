/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gp;
import ec.*;
import ec.app.regression.*;
import ec.gp.*;
import ec.gp.koza.KozaFitness;
import ec.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author sarath
 */
public class MyERC extends ERC{
    //Used to store the ERC values 
    public double value;
    //Used for Hill Climber
    public double energy; // store away the adjusted fitness in to this variable 
    EvolutionState minState; // store the first state from the EvolutionState
    double minEnergy; // store away the minimum adjusted fitness from the kozafitness 
    KozaFitness fitness;
    //Used for Simulated Annealing - Building up from the Hill Climber
    double decayRate; //the rate at which the energy of the climber decreases
    double initTemp; // the initial temperature used for the boltzmann value 
    
    public void mutateERC(final EvolutionState state, final int thread, double initTemp, double decayRate){
        
        double v;
        int iteration = 100;

//         // This replicates the... and adds Gaussian Nosie to the ERC
//        do v = value + state.random[thread].nextGaussian() * 0.01; //mutates node and applies a gussian noise to it by using the java.io.util.nextGaussian() method
//        while( v < 0.0 && v <= 1.0  ); // this restricition keeps within 0.0 and 1.0
//        value = v;         
        
        // This replaces the ERC selected for mutation with randomly generated ERC value
//        resetNode(state,thread); //--> resets all nodes to a new random ERC
        
        
        //Building a Hill Climber -after 11 run of the problem we had one fully successful 
//        EvolutionState init_state;
//        init_state = state;
//        energy = fitness.standardizedFitness();
//        minEnergy = energy;
//
//        for (int i=0; i< iteration; i++){
//            if (i % 100 ==  0){
//                System.out.print("min"+ "\t" + "energy");
//            }
//            v = state.random[thread].nextGaussian() * 0.01; //replace nextGaussian() with nextInt() - this does not choose a normally distributed value
//            double nextEnergy = fitness.adjustedFitness();
//            if (nextEnergy <= energy){
//                energy= nextEnergy;
//                if (nextEnergy < minEnergy){
//                    minEnergy = fitness.adjustedFitness();
//                }
//                  
//                value = v;
//            } 
//            
//        }
        
/*Prob(accepting uphill move) ~ 1 - exp(deltaE / kT))

A parameter T is also used to determine this probability.  It is analogous to temperature in an annealing system.  At higher values of T, uphill moves are more likely to occur.  
As T tends to zero, they become more and more unlikely, until the algorithm behaves more or less like hill-climbing.  In a typical SA optimization, T starts high and is gradually decreased according to an “annealing schedule”. 
The parameter k is some constant that relates temperature to energy (in nature it is Boltzmann’s constant.)*/

        // Simulated Annealing Code - this is built off the hillclimber made early in the code -best result was 5 generations follwed by one generated in 29
        EvolutionState init_state;
        init_state = state;
        energy = fitness.standardizedFitness();
        minEnergy = energy;
        this.initTemp = initTemp;
        this.decayRate = decayRate;
////        
        Random random = new Random();
        double temperature = initTemp;
        for (int i=0; i< iteration; i++){
            /*if (i % 100 ==  0){
                System.out.print("min"+ "\t" + "energy");
            }*/
            v = value + state.random[thread].nextGaussian() * 0.01;
            double nextEnergy = fitness.adjustedFitness();
            if (nextEnergy <= energy || random.nextDouble() < Math.exp(energy - nextEnergy)/ temperature){
                energy= nextEnergy;
                
                if (nextEnergy < minEnergy){
                    minEnergy = fitness.adjustedFitness();
                    
                }
               v = value;   
                
            }else {
                temperature *= decayRate;
            } 
            value = v;
        }
        
        ///////////////////////////////////////////////////////////////////////////////////////////
        // Goldberg Algorithm Implementation
//        do v = value + state.random[thread].nextGaussian() * state.random[thread].nextGaussian(); 
//        while( v < 0.0 && v <= 1.0  ); 
//        value = v;  
        
        // Guassian Newton Algorithm Implementation -- DID NOT WORK -- STOPPPED WORKING ON IT 
       // v= value + state.random[thread].nextGaussian();
        ///////////////////////////////////////////////////////////////////////////////////////////
        
        // Tabu Search 
        //ArrayList<double> tabuList;
        
        
        //-- Random implementation based on the fitness and standard adjustment for each of the generations

//         v = value + (state.random[thread].nextGaussian() * state.random[thread].nextGaussian()) *0.01; // the current value is taken and added to 2 randomly genertated guassiam values to be then reduce to 1% of its 
//         for (int i = 0; iteration > i; i++){
//             if (fitness.standardizedFitness() > fitness.adjustedFitness()){ // compare the adjusted old futness with adjusted new fitness 
//                  v = value + state.random[thread].nextGaussian();
//                 if(fitness.adjustedFitness()>fitness.standardizedFitness()){ //compare adjusted fitness with the orginnal fitness level 
//                     value = fitness.adjustedFitness() + value + state.random[thread].nextGaussian(); // if the adjusted value is greater than the orginal standard value then assign the value to the adjustment
//                 }/
//             }else{
//                 v = value - state.random[thread].nextGaussian();
//                 
//             }

//             value = v;
//         }
         
         
         
         
         
        
        
    }
    
//    public void crossoverERC(EvolutionState state, int thread){
//        //crossover for the ERC
//    }
    
    //This is not used, anymore in the implementation if you wish to use it remove the 
    @Override
    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextDouble();
        //value = state.random[thread].nextDouble() * 2 - 1.0;
    }

    @Override // This checks the node from the class and the generated ERC from MyERC are the same. 
    public boolean nodeEquals(GPNode node) {
        { return (node.getClass() == this.getClass() && ((MyERC)node).value == value); }
    }

    @Override
    public String toStringForHumans() { return " " + value; }
    
    @Override
    public String encode() { return Code.encode(value); }

    //
    @Override
    public boolean decode(DecodeReturn dret) {
        int pos = dret.pos;
        String data = dret.data;
        Code.decode(dret);
        if (dret.type != DecodeReturn.T_DOUBLE) // this restore and signal error.
        { dret.data = data; dret.pos = pos; return false; }
        value = dret.d;
    return true;
}
    //Altered the hashing into a more suitable hash which is more effcient
    @Override
    public int nodeHashCode()
        {
        long l = Double.doubleToLongBits(value); // convert the double into long bits 
        int iUpper = (int)(l & 0x00000000FFFFFFFF); 
        int iLower = (int)(l >>> 32);   
        return this.getClass().hashCode() + iUpper + iLower; //
        }

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        
        RegressionData rd = ((RegressionData)(input));
        rd.x = value;
        // ((MyData)data).val = value; -- ignore this line//
        
    }
    
}
